package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.SignedJWT;
import entities.User;
import errorhandling.API_Exception;
import facades.UserFacade;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import javax.annotation.security.RolesAllowed;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import security.SharedSecret;
import security.errorhandling.AuthenticationException;
import utils.EMF_Creator;

@Path("user")
public class UserResource {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private Gson gson = new Gson();

    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();
    public static final UserFacade USER_FACADE = UserFacade.getUserFacade(EMF);

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
    public String allUsers() {

        EntityManager em = EMF.createEntityManager();
        try {
            TypedQuery<User> query = em.createQuery("select u from User u", entities.User.class);
            List<User> users = query.getResultList();
            return "[" + users.size() + "]";
        } finally {
            em.close();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("user")
    @RolesAllowed("user")
    public String getFromUser() {
        String thisuser = securityContext.getUserPrincipal().getName();
        return "{\"msg\": \"Hello to User: " + thisuser + "\"}";
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Path("change-password")
    @RolesAllowed("user")
    public String changePassword(@Context HttpHeaders headers, String jsonString) throws API_Exception, AuthenticationException, IllegalAccessException, JOSEException, ParseException {
        JsonObject json = JsonParser.parseString(jsonString).getAsJsonObject();
        String userName = checkUser(headers);
        String password = json.get("password").getAsString();
        String newPassword1 = json.get("newPassword1").getAsString();
        String newPassword2 = json.get("newPassword2").getAsString();
        USER_FACADE.changePassword(userName, password, newPassword1, newPassword2);
        return "{\"msg\": \"Password changed\"}";
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
