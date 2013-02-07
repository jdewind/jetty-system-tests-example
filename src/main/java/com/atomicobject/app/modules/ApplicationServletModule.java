package com.atomicobject.app.modules;

import com.atomicobject.rest.guice.RestModule;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.google.inject.Singleton;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;

public class ApplicationServletModule extends com.google.inject.servlet.ServletModule {

    public ApplicationServletModule() {
    }

    @Override
    protected void configureServlets() {
        bind(JacksonJsonProvider.class).in(Singleton.class);
        serve("/api/v1/*").with(GuiceContainer.class);
        install(new RestModule());
    }
}
