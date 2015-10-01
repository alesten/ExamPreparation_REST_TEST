/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.cphbusiness.rest;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dk.cphbusiness.entity.Facade;
import dk.cphbusiness.entity.ProjectUser;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.PathParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * REST Web Service
 *
 * @author sofus
 */
@Path("users")
public class ProjectUserRestSerice {

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of RestService
     */
    Gson gson;

    public ProjectUserRestSerice() {
        gson = new GsonBuilder().setPrettyPrinting().setFieldNamingPolicy(FieldNamingPolicy.IDENTITY).create();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createUser(String user) {
        ProjectUser u = gson.fromJson(user, ProjectUser.class);
        Facade.createUser(u);
        return Response.status(Response.Status.CREATED).type(MediaType.APPLICATION_JSON).entity(gson.toJson(u)).build();
    }

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUsers() {
        return Response.status(Response.Status.OK).entity(gson.toJson(Facade.getUsers())).build();
    }
    @DELETE
    @Path("{id}")
    public Response deleteUser(@PathParam("id") String id){
    Facade.deleteUser(new Long(id));
    return Response.status(Response.Status.OK).build();
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{id}")
    public Response getUser(@PathParam("id") String id){
        return Response.status(Response.Status.OK).entity(gson.toJson(Facade.findUser(new Long(id)))).build(); 
    }
    
    

}
