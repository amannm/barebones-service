package com.amannmalik.service.barebones.endpoint;

/**
 * Created by Amann on 8/13/2015.
 */


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Api(value = "/api/greeting", description = "Example API operations")
@Path("/greeting")
@Dependent
public class ExampleResource {

    @Inject
    private ExampleData data;

    @ApiOperation("Get a resource")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Resource has been returned"),
            @ApiResponse(code = 500, message = "An internal server error occurred")
    })
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getHelloWorld() {
        return data.getGreeting();
    }
}
