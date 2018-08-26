package service;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import logical.UserService;
import authen.Authenticator;
import util.StatusCode;

@Path("login")
public class LogIn {
    /**
     * Method handling HTTP POST requests to sign in
     * the system. The returned object will
     * be sent to the client as "application/json"
     * media type.
     *
     * @return String that will be returned as a application/json response.
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String logIn(@FormParam("username") String username,
                        @FormParam("password") String password,
                        @PathParam("privilege") Integer privilege) {
        Integer res=new UserService().signIn(username,password,privilege);
        if (res.equals(1)){
            String token=Integer.toString((username+password).hashCode());
            Authenticator.add(token,privilege);
            return String.format("{\"result\":%d}", StatusCode.CORRECT);
        }
        else if (res.equals(0))
        {
            return String.format("{\"result\":%d}", StatusCode.INCORRECT);
        }
        else{
            return String.format("{\"result\":%d}", StatusCode.UNAUTHORIZED);
        }
    }
}
