package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dtos.apiPictures;
import dtos.catDTO;
import dtos.weatherCatDTO;
import dtos.weatherDTO;
import utils.HttpUtils;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;

/**
 * @author lam@cphbusiness.dk
 */
@Path("imagesFromApis")
public class imageResource {

    @Context
    private UriInfo context;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getImageFromApiWithURLResponse(String content) throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Object apiUrl = gson.fromJson(content, Object.class);
        System.out.println(apiUrl.toString());
        String apiUrlString = apiUrl.toString().substring(5,apiUrl.toString().length()-1);
        System.out.println(apiUrlString);

        String res = HttpUtils.fetchData(apiUrlString);
        System.out.println("This is res: "+res);
        if (res == null) {
            String error = "could not GET from link: "+apiUrlString;
            return Response.status(400).entity(error).build();
        }

        apiPictures imageDTO = gson.fromJson(res, apiPictures.class);
        String imageUrl = "";
        if (imageDTO.getUrl() != null && imageDTO.getUrl().contains("jpg")) {
            imageUrl = imageDTO.getUrl();
        } else if (imageDTO.getImage() != null && imageDTO.getImage().contains("jpg")) {
            imageUrl = imageDTO.getImage();
        } else if (imageDTO.getFile() != null && imageDTO.getFile().contains("jpg")) {
            imageUrl = imageDTO.getFile();
        } else {
            imageUrl = "Could not get image, might be chunked encoding (which this endpoint can't handle)";
        }

        String result = gson.toJson(imageUrl);

        return Response.ok().entity(result).build();
    }


}