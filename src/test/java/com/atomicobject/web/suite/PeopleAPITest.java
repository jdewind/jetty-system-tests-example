package com.atomicobject.web.suite;

import com.atomicobject.daos.PeopleDAO;
import com.atomicobject.entities.Person;
import com.atomicobject.web.helpers.WebTestHelper;
import com.sun.jersey.api.client.WebResource;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class PeopleAPITest extends WebTestHelper {
    private WebResource resource;

    @Before
    public void setUp() throws Throwable {
        resource = getClientForAPI("people");
        g(PeopleDAO.class).reset();
    }

    @Test
    public void can_get_all_of_the_people() throws Throwable {
        List<Person> people = GET_ALL(resource, Person.class);

        assertThat(people.size(), equalTo(2));

        assertThat(people.get(0).getId(), equalTo(1));
        assertThat(people.get(0).getGithubUsername(), equalTo("dewind"));

        assertThat(people.get(1).getId(), equalTo(2));
        assertThat(people.get(1).getGithubUsername(), equalTo("dcrosby42"));
    }

    @Test
    public void adding_a_user_is_reflected_in_the_api() throws Throwable {
        g(PeopleDAO.class).addPerson(new Person(3, "John", "Smith", "john.smith"));

        List<Person> people = GET_ALL(resource, Person.class);

        assertThat(people.size(), equalTo(3));
        assertThat(people.get(2).getId(), equalTo(3));
        assertThat(people.get(2).getGithubUsername(), equalTo("john.smith"));
    }

    @Test
    public void can_get_an_individual_person() throws Throwable {
        Person person = GET(resource.path("1"), Person.class);

        assertThat(person.getFirstName(), equalTo("Justin"));
        assertThat(person.getLastName(), equalTo("DeWind"));
        assertThat(person.getId(), equalTo(1));
        assertThat(person.getGithubUsername(), equalTo("dewind"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void can_get_followers_of_a_person() throws Throwable {
        List<String> followers = toObject(List.class, resource.path("1/followers").accept(MediaType.APPLICATION_JSON).get(String.class));

        assertThat(followers.get(0), equalTo("follower 1"));
        assertThat(followers.get(1), equalTo("follower 2"));
    }

}
