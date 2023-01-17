package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dtos.ShowDTO;
import errorhandling.API_Exception;
import errorhandling.GenericExceptionMapper;
import facades.ShowFacade;
import security.Permission;
import utils.EMF_Creator;
import utils.GsonLocalDate;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author lam@cphbusiness.dk
 */
@Path("show")
public class ShowResource {
    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();
    public static final ShowFacade SHOW_FACADE = ShowFacade.getShowFacade(EMF);
    //private static final Gson GSON = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new GsonLocalDateTime()).setPrettyPrinting().create();
    private static final Gson GSON = new GsonBuilder().registerTypeAdapter(LocalDate.class, new GsonLocalDate()).setPrettyPrinting().create();



    @POST
    @RolesAllowed({Permission.Types.ADMIN})
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/new")
    public Response createNewShow(String content) throws API_Exception {
        String name, duration, location;
        String startDate, startTime;
        try {
            JsonObject json = JsonParser.parseString(content).getAsJsonObject();
            name = json.get("name").getAsString();
            duration = json.get("duration").getAsString();
            location = json.get("location").getAsString();
            startDate = json.get("startDate").getAsString();
            startTime = json.get("startTime").getAsString();
        } catch (Exception e) {
            throw new API_Exception("Malformed JSON Supplied",400,e);
        }

        try {
            ShowDTO show = SHOW_FACADE.createShow(name, duration, location, LocalDate.parse(startDate), startTime);
            return Response.ok(GSON.toJson(show)).build();

        } catch (Exception ex) {
            Logger.getLogger(GenericExceptionMapper.class.getName()).log(Level.SEVERE, null, ex);
        }
        throw new API_Exception("Failed to create a new Show!");
    }

    @GET
    @Path("/get")
    @PermitAll
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllShows() {
        List<ShowDTO> list = SHOW_FACADE.getAllShows();
        return Response.ok().entity(GSON.toJson(list)).header(MediaType.CHARSET_PARAMETER, StandardCharsets.UTF_8.name()).build();
    }
    @GET
    @Path("/get/{id}")
    @RolesAllowed({Permission.Types.USER})
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllShowsForGuestWithID(@PathParam("id") Integer id) {
        List<ShowDTO> list = SHOW_FACADE.getAllShowsForGuestWithID(id);
        return Response.ok().entity(GSON.toJson(list)).header(MediaType.CHARSET_PARAMETER, StandardCharsets.UTF_8.name()).build();
    }
    @GET
    @Path("/{id}")
    @PermitAll
    @Produces(MediaType.APPLICATION_JSON)
    public Response getShowByID(@PathParam("id") Integer id) {
        ShowDTO show = SHOW_FACADE.getShowByID(id);
        return Response.ok().entity(GSON.toJson(show)).header(MediaType.CHARSET_PARAMETER, StandardCharsets.UTF_8.name()).build();
    }


    @PUT
    @Path("/{showId}/{guestID}")
    @RolesAllowed({Permission.Types.ADMIN})
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response signUpToShow( @PathParam("showId") Integer showId, @PathParam("guestID") Integer guestID) throws API_Exception {
        try {
            SHOW_FACADE.signUpToShow(showId, guestID);
            return Response.ok().entity(GSON.toJson("Signed guest with ID: "+guestID+" up to show with ID: "+showId)).header(MediaType.CHARSET_PARAMETER, StandardCharsets.UTF_8.name()).build();

        } catch (Exception ex) {
            Logger.getLogger(GenericExceptionMapper.class.getName()).log(Level.SEVERE, null, ex);
        }
        throw new API_Exception("Failed to update ShowRegistration!");
    }
    @PUT
    @Path("/{id}")
    @RolesAllowed({Permission.Types.ADMIN})
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response update( @PathParam("id") Integer id, String content) throws API_Exception {
        String name, duration, location;
        String startDate, startTime;
        try {
            JsonObject json = JsonParser.parseString(content).getAsJsonObject();
            name = json.get("name").getAsString();
            duration = json.get("duration").getAsString();
            location = json.get("location").getAsString();
            startDate = json.get("startDate").getAsString();
            startTime = json.get("startTime").getAsString();
        } catch (Exception e) {
            throw new API_Exception("Malformed JSON Supplied",400,e);
        }

        try {
            SHOW_FACADE.updateShow(id, name, duration, location, LocalDate.parse(startDate), startTime);
            return Response.ok().entity(GSON.toJson("Updated Show with ID: "+id)).header(MediaType.CHARSET_PARAMETER, StandardCharsets.UTF_8.name()).build();

        } catch (Exception ex) {
            Logger.getLogger(GenericExceptionMapper.class.getName()).log(Level.SEVERE, null, ex);
        }
        throw new API_Exception("Failed to update Show!");
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed({Permission.Types.ADMIN})
    @Produces({MediaType.APPLICATION_JSON})
    public Response delete(@PathParam("id") Integer id) {
        SHOW_FACADE.deleteShow(id);
        return Response.ok().entity(GSON.toJson("Deleted Show with ID: "+id)).header(MediaType.CHARSET_PARAMETER, StandardCharsets.UTF_8.name()).build();
    }


}