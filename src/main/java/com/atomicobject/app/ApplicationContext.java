package com.atomicobject.app;

import com.atomicobject.app.modules.ApplicationInjectionModule;
import com.atomicobject.app.modules.ApplicationServletModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Stage;

public class ApplicationContext {

    private final Injector injector;

    public ApplicationContext() {
        this(getApplicationInjectionModule(), new ApplicationServletModule());
    }

    private static Module getApplicationInjectionModule() {
        Class injectionModule = ApplicationInjectionModule.class;
        try {
            String property = System.getProperty("application.injection.module");
            if (property != null) {
                injectionModule = Class.forName(property);
            }
        } catch (ClassNotFoundException ignored) {
            ignored.printStackTrace();
        }
        try {
            return (Module) injectionModule.newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public ApplicationContext(Module... modules) {
        injector = Guice.createInjector(Stage.PRODUCTION, modules);
    }

    public Injector getInjector() {
        return injector;
    }
}
