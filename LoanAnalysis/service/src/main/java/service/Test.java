package service;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

//packages to handle with JSON
import net.sf.json.JSONObject;

@Path("test")
public class Test {
    @GET @Path("unaothorize")
    @Produces(MediaType.APPLICATION_JSON)
    public String test(){
        throw new WebApplicationException(Response.Status.UNAUTHORIZED);
    }
}
