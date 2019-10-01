package com.cloudnc.interview;

import org.glassfish.jersey.spi.Contract;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

@Contract
@Path("/primalities")
public final class PrimalityResource {
    @Path("{base10Number}")
    @GET
    public Response getPrimality(@PathParam("base10Number") final String base10Number) {
        return Response.serverError()
                       .entity("Not implemented yet")
                       .build();
    }
}
