package security;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import dtos.FestivalDTO;
import entities.Account;
import facades.AccountFacade;

import java.time.LocalDate;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import errorhandling.API_Exception;

import javax.annotation.security.PermitAll;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import security.errorhandling.AuthenticationException;
import errorhandling.GenericExceptionMapper;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.core.SecurityContext;

import utils.EMF_Creator;

@Path("login")
public class LoginEndpoint {

    public static final int TOKEN_EXPIRE_TIME = 1000 * 60 * 30; //30 min
    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();
    public static final AccountFacade ACCOUNT_FACADE = AccountFacade.getAccountFacade(EMF);

    @Context
    SecurityContext securityContext;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/new")
    public Response createAccount(String content) throws API_Exception {
        boolean isAdmin;
        String email, userPass, phone, name;
        try {
            JsonObject json = JsonParser.parseString(content).getAsJsonObject();
            isAdmin = json.get("isAdmin").getAsBoolean();
            email = json.get("email").getAsString();
            userPass = json.get("password").getAsString();
            phone = json.get("phone").getAsString();
            name = json.get("name").getAsString();
        } catch (Exception e) {
            throw new API_Exception("Malformed JSON Supplied",400,e);
        }
        try {
            Account account = ACCOUNT_FACADE.createAccount(isAdmin, email, userPass, phone, name);
            Response.ok().build();

        } catch (Exception ex) {
            Logger.getLogger(GenericExceptionMapper.class.getName()).log(Level.SEVERE, null, ex);
        }
        throw new API_Exception("Failed to create a new Account!");
    }
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(String jsonString) throws AuthenticationException, API_Exception {
        String email;
        String password;
        try {
            JsonObject json = JsonParser.parseString(jsonString).getAsJsonObject();
            email = json.get("email").getAsString();
            password = json.get("password").getAsString();
        } catch (Exception e) {
           throw new API_Exception("Malformed JSON Suplied",400,e);
        }

        try {
            System.out.println("email is: "+email+" and pass is:"+password);
            Account account = ACCOUNT_FACADE.getVeryfiedAccount(email, password);
            Permission permission;
            if(account.getIsAdmin()) {
                permission = Permission.ADMIN;
            } else {
                permission = Permission.USER;
            }
            String token = createToken(account, permission);
            JsonObject responseJson = new JsonObject();
            responseJson.addProperty("email", email);
            responseJson.addProperty("token", token);
            return Response.ok(new Gson().toJson(responseJson)).build();

        } catch (JOSEException | AuthenticationException ex) {
            if (ex instanceof AuthenticationException) {
                throw (AuthenticationException) ex;
            }
            Logger.getLogger(GenericExceptionMapper.class.getName()).log(Level.SEVERE, null, ex);
        }
        throw new AuthenticationException("Invalid email or password! Please try again");
    }

    private String createToken(Account account, Permission permission) throws JOSEException {

        StringBuilder res = new StringBuilder();
        String issuer = "semesterstartcode-dat3";

        JWSSigner signer = new MACSigner(SharedSecret.getSharedKey());
        Date date = new Date();
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(account.getEmail())
                .claim("ID", account.getId())
                .claim("pms", permission)
                .claim("email", account.getEmail())
                .claim("name", account.getName())
                .claim("phone", account.getPhone())
                .claim("issuer", issuer)
                .issueTime(date)
                .expirationTime(new Date(date.getTime() + TOKEN_EXPIRE_TIME))
                .build();
        SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);
        signedJWT.sign(signer);
        return signedJWT.serialize();
    }

    @HEAD
    @Path("validate")
    @PermitAll
    public Response validateToken() {
        return Response.ok().build();
    }
}
