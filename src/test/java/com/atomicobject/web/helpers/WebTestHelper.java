package com.atomicobject.web.helpers;

import com.atomicobject.app.ApplicationServletContextListener;
import com.atomicobject.helpers.TestInjectionModule;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.inject.Injector;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import org.apache.commons.io.IOUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.bio.SocketConnector;
import org.mortbay.jetty.deployer.WebAppDeployer;
import org.mortbay.jetty.handler.ContextHandlerCollection;
import org.mortbay.jetty.plus.webapp.Configuration;
import org.mortbay.jetty.plus.webapp.EnvConfiguration;
import org.mortbay.jetty.webapp.JettyWebXmlConfiguration;
import org.mortbay.jetty.webapp.TagLibConfiguration;
import org.mortbay.jetty.webapp.WebAppContext;
import org.mortbay.jetty.webapp.WebInfConfiguration;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.EventListener;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import static org.junit.Assert.fail;

public class WebTestHelper {

    public static final String PROJECT_NAME = "jetty-systest-example";
    public static final String CONTEXT_PATH = "";
    public static final String PROJECT_VERSION = "1.0";

    public static final int WEBSERVER_PORT = 1245;
    public static final String BASE_URL = "http://localhost:" + WEBSERVER_PORT + CONTEXT_PATH;
    private static final int MAX_DEPTH_FOR_POM = 4;

    protected static Server server;
    protected static boolean setupSuite;
    private static WebAppContext context;
    private static WebAppDeployer webAppDeployer;
    private static Injector injector;

    private static Boolean startedFromSuite = false;

    public Injector getGuiceInjector() {
        return getInjector();
    }

    public static void buildWarAndStartJettyFromSuite() throws Throwable {
        buildWarAndStartJetty();
        startedFromSuite = true;
    }

    @BeforeClass
    public static void buildWarAndStartJetty() throws Throwable {
        System.setProperty("application.injection.module", TestInjectionModule.class.getName());
        if (!setupSuite) {
            buildWar();
            startJetty();
            setupSuite = true;
        }
    }

    @AfterClass
    public static void stopJettyIfNotStartedFromSuite() throws Exception {
        if (!startedFromSuite) {
            stopJetty();
        }
        System.clearProperty("application.injection.module");
    }

    public static void buildWar() throws Throwable {
        Process process = Runtime.getRuntime().exec("mvn package -Dmaven.test.skip=true");
        process.waitFor();
        if (process.exitValue() != 0) {
            StringWriter output = new StringWriter();
            IOUtils.copy(process.getErrorStream(), output);
            fail("Could not build package: " + output.toString());
        }
    }

    protected static String projectRoot() {
        String contextPath = Thread.currentThread().getContextClassLoader().getResource(".").getPath();
        File file = new File(contextPath + "pom.xml");
        String up = ".." + File.separator;
        int currentDepth = 0;
        while (!file.exists() && currentDepth < MAX_DEPTH_FOR_POM) {
            file = new File(contextPath + up + "pom.xml");
            up += ".." + File.separator;
            currentDepth++;
        }
        return file.getParent();
    }

    public static Injector getInjector() {
        return injector;
    }

    public static void startJetty() throws Exception {
        configureServerProperties();

        configureServer();

        configureWebAppContext();

        configureWebAppDeployer();

        server.addLifeCycle(webAppDeployer);
        server.addHandler(context);
        server.start();

        while (server.isStarting()) {
            Thread.sleep(250);
        }

        findInjector();
    }

    private static void findInjector() {
        EventListener[] eventListeners = context.getServletContext().getContextHandler().getEventListeners();
        for (EventListener eventListener : eventListeners) {
            if (eventListener instanceof ApplicationServletContextListener) {
                injector = ((ApplicationServletContextListener) eventListener).backDoorToInjector();
                break;
            }
        }
    }

    private static void configureServer() {
        server = new Server();
        SocketConnector connector = new SocketConnector();
        connector.setPort(WEBSERVER_PORT);
        server.setConnectors(new Connector[]{connector});
    }

    private static void configureWebAppDeployer() {
        webAppDeployer = new WebAppDeployer();
        webAppDeployer.setConfigurationClasses(new String[]{
                WebInfConfiguration.class.getName(),
                EnvConfiguration.class.getName(),
                Configuration.class.getName(),
                JettyWebXmlConfiguration.class.getName(),
                TagLibConfiguration.class.getName()
        });

        ContextHandlerCollection contexts = new ContextHandlerCollection();
        webAppDeployer.setWebAppDir(projectRoot() + "/target");
        webAppDeployer.setContexts(contexts);
    }

    private static void configureWebAppContext() {
        context = new WebAppContext();
        context.setClassLoader(Thread.currentThread().getContextClassLoader());
        context.setServer(server);
        context.setContextPath(CONTEXT_PATH);
        context.setWar(projectRoot() + "/target/" + PROJECT_NAME + "-" + PROJECT_VERSION + "-SNAPSHOT");
    }

    private static void configureServerProperties() {
        System.setProperty("java.naming.factory.url.pkgs", "org.mortbay.naming");
        System.setProperty("java.naming.factory.initial", "org.mortbay.naming.InitialContextFactory");
    }


    public static void stopJetty() throws Exception {
        if (server != null && server.isRunning()) {
            server.stop();
        }
    }

    protected <T> T g(Class<T> type) {
        return getInjector().getInstance(type);
    }

    protected WebResource getClientForAPI(String relativeURL) {
        DefaultClientConfig config = new DefaultClientConfig();
        return Client
                .create(config)
                .resource(UriBuilder.fromUri(BASE_URL + "/api/v1/" + relativeURL).build());
    }

    protected HashMap toHashMap(String content) throws IOException {
        return new ObjectMapper().readValue(content, HashMap.class);
    }

    protected String toJSON(Object object) throws IOException {
        return g(JacksonJsonProvider.class).locateMapper(object.getClass(), MediaType.APPLICATION_JSON_TYPE).writeValueAsString(object);
    }

    //
    // Helpers
    //

    protected <T> T toObject(Class<T> aClass, String data) throws Throwable {
        return g(JacksonJsonProvider.class).locateMapper(aClass, MediaType.APPLICATION_JSON_TYPE).reader(aClass).readValue(data);
    }

    protected void DELETE(WebResource uri) {
        uri.accept(MediaType.APPLICATION_JSON_TYPE).delete();
    }

    protected <T> T PUT(WebResource webResource, Class<T> entityClass, Object requestEntity) throws Throwable {
        return toObject(entityClass, webResource.accept(MediaType.APPLICATION_JSON_TYPE).type(MediaType.APPLICATION_JSON_TYPE).put(String.class, toJSON(requestEntity)));
    }

    @SuppressWarnings("unchecked")
    protected <T> List<T> GET_ALL(WebResource resource, final Class<T> aClass) throws Throwable {
        List<LinkedHashMap> listOfHashes = toObject(List.class, resource.accept(MediaType.APPLICATION_JSON).get(String.class));
        return Lists.transform(listOfHashes, new Function<LinkedHashMap, T>() {
            @Override
            public T apply(LinkedHashMap linkedHashMap) {
                try {
                    String json = g(JacksonJsonProvider.class).locateMapper(LinkedHashMap.class, MediaType.APPLICATION_JSON_TYPE).writeValueAsString(linkedHashMap);
                    return toObject(aClass, json);
                } catch (Throwable throwable) {
                    throw new RuntimeException("Failed to transform object", throwable);
                }
            }
        });
    }

    protected <T> T GET(WebResource webResource, Class<T> aClass) throws Throwable {
        return toObject(aClass, webResource.accept(MediaType.APPLICATION_JSON).get(String.class));
    }

    protected <T> T POST(WebResource resource, Class<T> aClass, Object requestEntity) throws Throwable{
        return toObject(aClass, resource.type(MediaType.APPLICATION_JSON_TYPE).post(String.class, toJSON(requestEntity)));
    }
}
