package com.atomicobject.rest.resources;

import com.atomicobject.app.GitHubAPI;
import com.atomicobject.daos.PeopleDAO;
import com.atomicobject.entities.Person;
import com.atomicobject.rest.pojo.CountResponse;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import java.util.List;

@Path("people")
public class PeopleResource extends RESTResource {

    private final PeopleDAO peopleDAO;
    private final GitHubAPI gitHubAPI;

    @Inject
    public PeopleResource(PeopleDAO peopleDAO, GitHubAPI gitHubAPI) {
        this.peopleDAO = peopleDAO;
        this.gitHubAPI = gitHubAPI;
    }

    @GET
    public List<Person> all() {
        return peopleDAO.findAll();
    }

    @GET
    @Path("{id}")
    public Person person(@PathParam("id") Integer id) {
        return peopleDAO.findById(id);
    }

    @GET
    @Path("{id}/followers")
    public List<String> followers(@PathParam("id") Integer id) {
        Person person = peopleDAO.findById(id);
        return gitHubAPI.followersFor(person.getGithubUsername());
    }

    @GET
    @Path("count")
    public CountResponse count() {
        return new CountResponse(peopleDAO.count());
    }

}
