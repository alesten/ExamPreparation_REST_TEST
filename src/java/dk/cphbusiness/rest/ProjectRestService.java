/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.cphbusiness.rest;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dk.cphbusiness.entity.Facade;
import dk.cphbusiness.entity.Project;
import dk.cphbusiness.entity.ProjectUser;
import exception.ProjectNotFoundException;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import static javax.ws.rs.core.HttpHeaders.LINK;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * REST Web Service
 *
 * @author sofus
 */
@Path("projects")
public class ProjectRestService {

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of RestService
     */
    Gson gson;

    public ProjectRestService() {
        gson = new GsonBuilder().setPrettyPrinting().setFieldNamingPolicy(FieldNamingPolicy.IDENTITY).create();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProjects() {
        JsonArray out = new JsonArray();
        JsonObject juser = new JsonObject();
        List<Project> projects = Facade.getProjects();
        System.out.println(projects.size());
        for (Project project : projects) {
            juser = makeProject(project);

            out.add(juser);
        }
        return Response.status(Response.Status.OK).entity(out.toString()).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{id}")
    public Response getProject(@PathParam("id") String id) throws ProjectNotFoundException{
        Project project = Facade.findProject(new Long(id));
        if(project == null)
            throw new ProjectNotFoundException("Project with the given ID is not in the database");
        return Response.ok(makeProject(project).toString(), MediaType.APPLICATION_JSON).build();
    }
    
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createProject(String json){
        Project p = gson.fromJson(json, Project.class);
        p.setCreated(Date.from(Instant.now()));
        p.setLastModified(Date.from(Instant.now()));
        Facade.createProject(p);
        return Response.status(Response.Status.CREATED).type(MediaType.APPLICATION_JSON).entity(makeProject(p).toString()).build();
    }
    
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{projectId}/{userId}")
    public Response assignUserToProject(@PathParam("projectId") String projectId, @PathParam("userId") String userId){
        new Facade().assignUserToProject(new Long(projectId), new Long(userId));
        Project project = Facade.findProject(new Long(projectId));
        return Response.ok(makeProject(project).toString(), MediaType.APPLICATION_JSON).build();
    }
    
    private JsonObject makeProject(Project project) {
        JsonObject jProjects = new JsonObject();
        jProjects.addProperty("id", project.getId());
        jProjects.addProperty("name", project.getName());
        jProjects.addProperty("description", project.getDescription());
        jProjects.addProperty("created", project.getCreated().toString());
        jProjects.addProperty("lastModified", project.getLastModified().toString());
        JsonArray jUsers = new JsonArray();
        JsonObject user;
        for (ProjectUser pUser : project.getProjectUsers()) {
            user = new JsonObject();
            user.addProperty("username", pUser.getUserName());
            user.addProperty("id", pUser.getId());
            jUsers.add(user);
        }
        jProjects.add("projectUsers", jUsers);
        return jProjects;
    }
}
