package service;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import net.sf.json.JSONObject;
import logical.*;

@Path("/bank")
public class BankInfo {
    //TODO: Handle with authentication.
    /**
     * Method handling HTTP GET requests to get overview
     * information of bank. The returned object will
     * be sent to the client as "application/json"
     * media type.
     *
     * @return String that will be returned as a application/json response.
     */
    @GET @Path("/overview")
    @Produces(MediaType.APPLICATION_JSON)
    public String getOverview() {
        return JSONObject.fromObject(
                new ManagerService().getOverview()
        ).toString();
    }

    /**
     * Method handling HTTP GET requests to get price and number
     * of loan applications of last 7 days.
     * @return String that will be returned as a application/json response.
     */
    @GET @Path("/history")
    @Produces(MediaType.APPLICATION_JSON)
    public String getHistory(){
        return JSONObject.fromObject(
                new ManagerService().get7Day()
        ).toString();
    }

    /**
     * Method handling HTTP GET requests to get Top5
     * important features an accepted application has.
     * @return String that will be returned as a application/json response.
     */
    @GET @Path("/reasons")
    @Produces(MediaType.APPLICATION_JSON)
    public String getReasons(){
        return JSONObject.fromObject(
                new ManagerService().getReasons()
        ).toString();
    }

    /**
     * Method handling HTTP GET requests to get proportions of each kind of
     * people.
     * @return String that will be returned as a application/json response.
     */
    @GET @Path("/proportion")
    @Produces(MediaType.APPLICATION_JSON)
    public String getProportion(){
        return JSONObject.fromObject(
                new ManagerService().getProportion()
        ).toString();
    }
}
