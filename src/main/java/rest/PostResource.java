package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.SignedJWT;
import errorhandling.API_Exception;
import facades.PostFacade;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import javax.annotation.security.RolesAllowed;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import security.SharedSecret;
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
    public String getCategoryPosts(@PathParam("cat") String cat) {
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
    public String addPost(@Context HttpHeaders headers, String jsonString) throws API_Exception, AuthenticationException, IllegalAccessException, JOSEException, ParseException {
        try {
            String userName = checkUser(headers);
            JsonObject json = JsonParser.parseString(jsonString).getAsJsonObject();
            String postContent = json.get("content").getAsString();
            String category = json.get("category").getAsString();
            POST_FACADE.addPost(postContent, userName, category);
        } catch (API_Exception e) {
            return e.getMessage();
        }
        return "{\"msg\": \"Post Created\"}";
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("userpost/{name}")
    public String getUserPost(@PathParam("name") String name) {
        try {
            ArrayList posts = POST_FACADE.getUserPosts(name);
            return gson.toJson(posts);
        } catch (JsonSyntaxException e) {
            return e.getMessage();
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("add-comment")
    @RolesAllowed("user")
    public String addComment(@Context HttpHeaders headers, String jsonString) throws API_Exception, AuthenticationException, IllegalAccessException, JOSEException, ParseException {
        try {
            String userName = checkUser(headers);
            JsonObject json = JsonParser.parseString(jsonString).getAsJsonObject();
            String comContent = json.get("comContent").getAsString();
            Long postID = json.get("postID").getAsLong();
            POST_FACADE.addComment(userName, comContent, postID);
        } catch (API_Exception e) {
            return e.getMessage();
        }
        return "{\"msg\": \"Comment Created\"}";
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("all-comments/{postID}")
    public String getAllComments(@PathParam("postID") Long postID) {
        try {
            ArrayList posts = POST_FACADE.getAllComments(postID);
            return gson.toJson(posts);
        } catch (JsonSyntaxException e) {
            return e.getMessage();
        }
    }

    private String checkUser(@Context HttpHeaders headers) throws JOSEException, ParseException, AuthenticationException {
        String token = headers.getRequestHeader("x-access-token").get(0);
        SignedJWT signedJWT = SignedJWT.parse(token);
        //Is it a valid token (generated with our shared key)
        JWSVerifier verifier = new MACVerifier(SharedSecret.getSharedKey());
        if (signedJWT.verify(verifier)) {
            if (new Date().getTime() > signedJWT.getJWTClaimsSet().getExpirationTime().getTime()) {
                throw new AuthenticationException("Your Token is no longer valid");
            }
            String username = signedJWT.getJWTClaimsSet().getClaim("username").toString();
            return username;
        } else {
            throw new JOSEException("User could not be extracted from token");
        }
    }
}
