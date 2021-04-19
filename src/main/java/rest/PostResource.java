package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import dtos.PostDTO;
import entities.Post;
import entities.User;
import errorhandling.API_Exception;
import facades.PostFacade;
import facades.UserFacade;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.security.RolesAllowed;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import security.errorhandling.AuthenticationException;
import utils.EMF_Creator;

@Path("post")
public class PostResource {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private Gson gson = new Gson();

    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();
    public static final PostFacade POST_FACADE = PostFacade.getPostFacade(EMF);

    @Context
    private UriInfo context;

    @Context
    SecurityContext securityContext;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getInfoForAll() {
        return "{\"msg\":\"Hello anonymous\"}";
    }

    //Just to verify if the database is setup
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("all")
    public String allPosts() {
        return gson.toJson(POST_FACADE.getAllPosts());
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("user")
    @RolesAllowed("user")
    public String getFromUser() {
        String thisuser = securityContext.getUserPrincipal().getName();
        return "{\"msg\": \"Hello to User: " + thisuser + "\"}";
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("category/{cat}")
    public String getCategoryPosts(@PathParam("cat") String cat){
        try {
            ArrayList posts = POST_FACADE.getCategoryPosts(cat);
            return gson.toJson(posts);
        } catch (JsonSyntaxException e) {
            return e.getMessage();
        }
    }
    
    
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("add-post")
    @RolesAllowed("user")
    public String addPost(String jsonString) throws API_Exception, AuthenticationException {
        try {
            JsonObject json = JsonParser.parseString(jsonString).getAsJsonObject();
            String postContent = json.get("content").getAsString();
            String userName = json.get("username").getAsString();
            String category = json.get("category").getAsString();
            POST_FACADE.addPost(postContent, userName, category);
        } catch (API_Exception e) {
            return e.getMessage();
        }
        return "{\"msg\": \"Post Created\"}";
    }

}
