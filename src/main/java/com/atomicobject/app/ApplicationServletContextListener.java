package com.atomicobject.app;

import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;

import javax.servlet.ServletContextEvent;

public class ApplicationServletContextListener extends GuiceServletContextListener {
    private Injector injector;

    public void contextInitialized(ServletContextEvent sce) {
        injector = new ApplicationContext().getInjector();
    }

    @Override
    protected Injector getInjector() {
        return injector;
    }


    public Injector backDoorToInjector() {
        return injector;
    }

}
