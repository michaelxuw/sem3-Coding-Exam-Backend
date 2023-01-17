package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dtos.FestivalDTO;
import errorhandling.API_Exception;
import errorhandling.GenericExceptionMapper;
import facades.FestivalFacade;
import security.Permission;
import utils.EMF_Creator;
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
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private static final Gson GSON = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new GsonLocalDateTime()).setPrettyPrinting().create();



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
            return Response.ok(GSON.toJson(festival)).build();

        } catch (Exception ex) {
            Logger.getLogger(GenericExceptionMapper.class.getName()).log(Level.SEVERE, null, ex);
        }
        throw new API_Exception("Failed to create a new FoocleSpot!");
    }
    @GET
    @Path("/get")
    @PermitAll
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllFestivals() {
        List<FestivalDTO> list = FESTIVAL_FACADE.getAllFestivals();
        return Response.ok().entity(GSON.toJson(list)).header(MediaType.CHARSET_PARAMETER, StandardCharsets.UTF_8.name()).build();
    }
    @GET
    @Path("/getRelevant")
    @PermitAll
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllRelevantFestivals() {
        List<FestivalDTO> list = FESTIVAL_FACADE.getAllRelevantFestivals();
        return Response.ok().entity(GSON.toJson(list)).header(MediaType.CHARSET_PARAMETER, StandardCharsets.UTF_8.name()).build();
    }


}