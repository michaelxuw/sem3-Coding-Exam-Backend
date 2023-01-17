package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import dtos.FestivalDTO;
import dtos.GuestDTO;
import errorhandling.API_Exception;
import errorhandling.GenericExceptionMapper;
import facades.FestivalFacade;
import facades.GuestFacade;
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
@Path("guest")
public class GuestResource {
    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();
    public static final GuestFacade GUEST_FACADE = GuestFacade.getGuestFacade(EMF);
    //private static final Gson GSON = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new GsonLocalDateTime()).setPrettyPrinting().create();
    private static final Gson GSON = new GsonBuilder().registerTypeAdapter(LocalDate.class, new GsonLocalDate()).setPrettyPrinting().create();



    @POST
    @RolesAllowed({Permission.Types.ADMIN})
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/")
    public Response createNewGuest(String content) throws API_Exception {
        String name, phone, email, status;
        Integer accountID;
        try {
            JsonObject json = JsonParser.parseString(content).getAsJsonObject();
            name = json.get("name").getAsString();
            phone = json.get("phone").getAsString();
            email = json.get("email").getAsString();
            status = json.get("status").getAsString();
            accountID = json.get("accountID").getAsInt();

        } catch (Exception e) {
            throw new API_Exception("Malformed JSON Supplied",400,e);
        }

        try {
            GuestDTO guest = GUEST_FACADE.createGuest(name, phone, email, status, accountID);
            return Response.ok(GSON.toJson(guest)).build();

        } catch (Exception ex) {
            Logger.getLogger(GenericExceptionMapper.class.getName()).log(Level.SEVERE, null, ex);
        }
        throw new API_Exception("Failed to create a new Guest!");
    }

    @GET
    @Path("/")
    @RolesAllowed({Permission.Types.ADMIN})
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllGuests() {
        List<GuestDTO> list = GUEST_FACADE.getAllGuests();
        return Response.ok().entity(GSON.toJson(list)).header(MediaType.CHARSET_PARAMETER, StandardCharsets.UTF_8.name()).build();
    }
    @GET
    @Path("/{id}")
    @PermitAll
    @Produces(MediaType.APPLICATION_JSON)
    public Response getGuestByID(@PathParam("id") Integer id) {
        GuestDTO guest = GUEST_FACADE.getGuestByID(id);
        return Response.ok().entity(GSON.toJson(guest)).header(MediaType.CHARSET_PARAMETER, StandardCharsets.UTF_8.name()).build();
    }


//    @PUT
//    @Path("/{id}")
//    @RolesAllowed({Permission.Types.ADMIN})
//    @Produces({MediaType.APPLICATION_JSON})
//    @Consumes({MediaType.APPLICATION_JSON})
//    public Response update(@PathParam("id") Integer id, String content) throws API_Exception {
//        String name, city;
//        String startDate, duration;
//        Integer guest;
//        List<Integer> guests;
//        try {
//            JsonObject json = JsonParser.parseString(content).getAsJsonObject();
//            name = json.get("name").getAsString();
//            city = json.get("city").getAsString();
//            startDate = json.get("startDate").getAsString();
//            duration = json.get("duration").getAsString();
//            guest = json.get("guest").getAsInt();
//            Type listType = new TypeToken<LinkedList<Integer>>() {}.getType();
//            guests = new Gson().fromJson(json.get("guests").getAsJsonArray(), listType);
//
//        } catch (Exception e) {
//            throw new API_Exception("Malformed JSON Supplied",400,e);
//        }
//
//        try {
//            FESTIVAL_FACADE.updateFestival(id, name, city, LocalDate.parse(startDate), duration, guest, guests);
//            return Response.ok().build();
//
//        } catch (Exception ex) {
//            Logger.getLogger(GenericExceptionMapper.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        throw new API_Exception("Failed to update Festival!");
//    }
//
//    @DELETE
//    @Path("/{id}")
//    @RolesAllowed({Permission.Types.ADMIN})
//    @Produces({MediaType.APPLICATION_JSON})
//    public Response delete(@PathParam("id") Integer id) {
//        FESTIVAL_FACADE.deleteFestival(id);
//        return Response.ok().build();
//    }


}