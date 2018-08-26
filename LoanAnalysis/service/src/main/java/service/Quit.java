package service;

import authen.Authenticator;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("quit")
public class Quit {
    /**
     * Method handling HTTP POST requests to sign out
     * the system. The returned object will
     * be sent to the client as "application/json"
     * media type.
     *
     * @return VOID
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public void quit(@FormParam("token") String token) {
        Authenticator.cancel(token);
    }
}
