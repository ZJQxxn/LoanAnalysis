package service;


import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import authen.Authenticator;
import logical.AdminService;
import util.StatusCode;

@Path("myaccount")
public class MyAccount {
    /**
     * Method handling HTTP POST requests to sign up
     * the system. The returned object will
     * be sent to the client as "application/json"
     * media type.
     *
     * @return String that will be returned as a application/json response.
     */
    @POST @Path("/add")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String signUp(@FormParam("username") String username,
                           @FormParam("password") String password,
                         @FormParam("token") String token) {
        //Authentic
        if (!Authenticator.authenticate(token,1)){
            return String.format("{\"result\":%d}", StatusCode.UNAUTHORIZED);
        }
        Boolean res=new AdminService().signUp(username,password);
        if(!res){
            return String.format("{\"result\":%d}", StatusCode.INCORRECT);
        }
        else{
            return String.format("{\"result\":%d}", StatusCode.CORRECT);
        }
    }

    /**
     * Method handling HTTP POST requests to
     * cancel an account. The returned object will
     * be sent to the client as "application/json"
     * media type.
     *
     * @return String that will be returned as a application/json response.
     */
    @POST @Path("/cancel")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String cancel(@FormParam("username") String username,
                         @FormParam("password") String password,
                         @PathParam("token") String token) {
        //Authentic
        if (!Authenticator.authenticate(token,1)){
            return String.format("{\"result\":%d}", StatusCode.UNAUTHORIZED);
        }
        Boolean res=new AdminService().cancel(username,password);
        if(!res){
            return String.format("{\"result\":%d}", StatusCode.INCORRECT);
        }
        else{
            return String.format("{\"result\":%d}", StatusCode.CORRECT);
        }
    }
}
