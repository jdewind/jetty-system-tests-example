package com.atomicobject.rest.resources;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;

@Produces(MediaType.APPLICATION_JSON)
public class RESTResource {
    @Context
    protected Request request;

    @Context
    protected HttpServletResponse response;

    public Request getRequest() {
        return request;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

}
