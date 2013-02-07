package com.atomicobject.app;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.DefaultClientConfig;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;

public class GitHubAPIImpl implements GitHubAPI {

    private final WebResource resource;
    private final JacksonJsonProvider provider;

    @Inject
    public GitHubAPIImpl(JacksonJsonProvider provider) {
        this.provider = provider;
        DefaultClientConfig config = new DefaultClientConfig();
        resource = Client
                .create(config)
                .resource(UriBuilder.fromUri("https://api.github.com").build());

    }

    @Override
    @SuppressWarnings("unchecked")
    public List<String> followersFor(String gitHubHandle) {
        List<LinkedHashMap> list = toObject(List.class, resource.path("/users/" + gitHubHandle + "/followers").get(String.class));
        return Lists.transform(list, new Function<LinkedHashMap, String>() {
            @Override
            public String apply(LinkedHashMap linkedHashMap) {
                return (String) linkedHashMap.get("login");
            }
        });
    }


    protected <T> T toObject(Class<T> aClass, String data) {
        try {
            return provider.locateMapper(aClass, MediaType.APPLICATION_JSON_TYPE).reader(aClass).readValue(data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
