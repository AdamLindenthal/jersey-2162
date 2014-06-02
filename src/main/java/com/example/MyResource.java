package com.example;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.logging.Logger;

/**
 */
@Path("rest")
public class MyResource {

    private final static Logger LOGGER = Logger.getLogger(MyResource.class.getName());

    @GET
    @Path("sleep/{time}")
    public Response sleep(@PathParam("time") long millis) {
       // LOGGER.info("Sleeping for: " + millis + " milliseconds");
//       System.out.println("In the resource method: sleeping for " + millis + " milliseconds");
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Response.noContent().build();
    }
}
