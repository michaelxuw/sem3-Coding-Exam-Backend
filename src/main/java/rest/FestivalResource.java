package rest;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import dtos.FestivalDTO;
import errorhandling.API_Exception;
import errorhandling.GenericExceptionMapper;
import facades.FestivalFacade;
import security.Permission;
import utils.EMF_Creator;
import utils.GsonLocalDate;
import utils.GsonLocalDateTime;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.*;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author lam@cphbusiness.dk
 */
@Path("festival")
public class FestivalResource {
    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();
    public static final FestivalFacade FESTIVAL_FACADE = FestivalFacade.getFestivalFacade(EMF);
    //private static final Gson GSON = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new GsonLocalDateTime()).setPrettyPrinting().create();
    private static final Gson GSON = new GsonBuilder().registerTypeAdapter(LocalDate.class, new GsonLocalDate()).setPrettyPrinting().create();



    @POST
    @RolesAllowed({Permission.Types.ADMIN})
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/new")
    public Response createNewFestival(String content) throws API_Exception {
        String name, city;
        String startDate, duration;
        try {
            JsonObject json = JsonParser.parseString(content).getAsJsonObject();
            name = json.get("name").getAsString();
            city = json.get("city").getAsString();
            startDate = json.get("startDate").getAsString();
            duration = json.get("duration").getAsString();

        } catch (Exception e) {
            throw new API_Exception("Malformed JSON Supplied",400,e);
        }

        try {
            FestivalDTO festival = FESTIVAL_FACADE.createFestival(name, city, LocalDate.parse(startDate), duration);
            System.out.println("StartDate for created festival is: "+festival.getStartDate());
            return Response.ok(GSON.toJson(festival)).build();

        } catch (Exception ex) {
            Logger.getLogger(GenericExceptionMapper.class.getName()).log(Level.SEVERE, null, ex);
        }
        throw new API_Exception("Failed to create a new Festival!");
    }

    @GET
    @Path("/get")
    @PermitAll
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllFestivals() {
        List<FestivalDTO> list = FESTIVAL_FACADE.getAllFestivals();
//        System.out.println("GSON toJson is: "+GSON.toJson(list));
        return Response.ok().entity(GSON.toJson(list)).header(MediaType.CHARSET_PARAMETER, StandardCharsets.UTF_8.name()).build();
    }
    @GET
    @Path("/getRelevant")
//    @PermitAll
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllRelevantFestivals() {
        List<FestivalDTO> list = FESTIVAL_FACADE.getAllRelevantFestivals();
        return Response.ok().entity(GSON.toJson(list)).header(MediaType.CHARSET_PARAMETER, StandardCharsets.UTF_8.name()).build();
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed({Permission.Types.ADMIN})
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response update( @PathParam("id") Integer id, String content) throws API_Exception {
        String name, city;
        String startDate, duration;
        List<Integer> guests;
        try {
            JsonObject json = JsonParser.parseString(content).getAsJsonObject();
            name = json.get("name").getAsString();
            city = json.get("city").getAsString();
            startDate = json.get("startDate").getAsString();
            duration = json.get("duration").getAsString();
            Type listType = new TypeToken<LinkedList<Integer>>() {}.getType();
            guests = new Gson().fromJson(json.get("guestIDs").getAsJsonArray(), listType);

        } catch (Exception e) {
            throw new API_Exception("Malformed JSON Supplied",400,e);
        }

        try {
            System.out.println("Duration is: "+duration);
            FESTIVAL_FACADE.updateFestival(id, name, city, LocalDate.parse(startDate), duration, guests);
            return Response.ok().entity(GSON.toJson("Updated Festival with ID: "+id)).header(MediaType.CHARSET_PARAMETER, StandardCharsets.UTF_8.name()).build();

        } catch (Exception ex) {
            Logger.getLogger(GenericExceptionMapper.class.getName()).log(Level.SEVERE, null, ex);
        }
        throw new API_Exception("Failed to update Festival!");
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed({Permission.Types.ADMIN})
    @Produces({MediaType.APPLICATION_JSON})
    public Response delete(@PathParam("id") Integer id) {
        FESTIVAL_FACADE.deleteFestival(id);
        return Response.ok().entity(GSON.toJson("Deleted Festival with ID: "+id)).header(MediaType.CHARSET_PARAMETER, StandardCharsets.UTF_8.name()).build();
    }


}