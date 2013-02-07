package com.atomicobject.rest.guice;

import com.atomicobject.rest.resources.PeopleResource;
import com.google.inject.AbstractModule;

public class RestModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(PeopleResource.class);
    }
}
