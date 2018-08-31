package service;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import logical.ManagerService;
import net.sf.json.JSONObject;



@Path("user")
public class UserInfo {
    //TODO: Handle with authentication.
    /**
     * Method handling HTTP GET requests to get
     * basic information of a user. The returned object will
     * be sent to the client as "application/json"
     * media type.
     *
     * @return String that will be returned as a application/json response.
     */
    @GET @Path("/basic/{info}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getBasic(@PathParam("info") String userID) {
        return JSONObject.fromObject(
                new ManagerService().getBasic(userID)
        ).toString();
    }

    /**
     * Method handling HTTP POST requests to get
     * all the labels of a user. The returned object will
     * be sent to the client as "application/json"
     * media type.
     *
     * @return String that will be returned as a application/json response.
     */
    @GET @Path("/label/{info}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getLabel(@PathParam("info") String userID) {
        return JSONObject.fromObject(
                new ManagerService().getLabel(userID)
        ).toString();
    }

    /**
     * Method handling HTTP POST requests to get
     * loan history of a user. The returned object will
     * be sent to the client as "application/json"
     * media type.
     *
     * @return String that will be returned as a application/json response.
     */
    @GET @Path("/loanhistory/{info}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getLoanHistory(@PathParam("info") String userID) {
        return JSONObject.fromObject(
                new ManagerService().getLoanHistory(userID)
        ).toString();
    }

    /**
     * Method handling HTTP POST requests to get
     * comsume history of a user. The returned object will
     * be sent to the client as "application/json"
     * media type.
     *
     * @return String that will be returned as a application/json response.
     */
    @GET @Path("/consumehistory/{info}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getConsumeHistory(@PathParam("info") String userID) {
        return JSONObject.fromObject(
                new ManagerService().getBureauHistory(userID)
        ).toString();
    }
}
