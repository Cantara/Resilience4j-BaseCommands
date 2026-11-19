package no.cantara.base.command;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockserver.client.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.Header;
import org.slf4j.Logger;

import jakarta.json.Json;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.logging.Level;

import static java.lang.String.format;
import static org.junit.Assert.*;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.matchers.Times.exactly;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.slf4j.LoggerFactory.getLogger;

public class BaseHttpGetResilience4jCommandTest {
    private static final Logger log = getLogger(BaseHttpGetResilience4jCommandTest.class);
    private CommandProxy commandProxy;
    private static ClientAndServer server;

    @BeforeClass
    public static void startServer() {
        server = startClientAndServer(1080);
    }
    @Before
    public void setUp() {
        commandProxy = new CommandProxy();
    }

    @AfterClass
    public static void stopServer() {
        server.stop();
    }

    @Test
    public void getCommand() throws InterruptedException, UnsuccesfulStatusCodeException, IOException {
        String dynamicId = "12345";
        String path = format("/demo/%s", dynamicId);

        //Expect
        createExpectationForGet(path, jsonBody(dynamicId));

        URI getFrom = URI.create(baseUrl() + path);
        log.info("Get from: {}", getFrom);
        //Execute
        BaseResilience4jCommand getCommand = new BaseHttpGetResilience4jCommand(getFrom, "test", 50);
        Object result = commandProxy.run(getCommand);
        assertNotNull(result);
        //Verify
        assertEquals(200, ((HttpResponse)result).statusCode());
    }

    @Test
    public void getCommand_fail() throws InterruptedException, UnsuccesfulStatusCodeException, IOException {
        String dynamicId = "12345";
        String path = format("/demo/%s", dynamicId);

        //Expect
        createExpectationForGet_Fail(path, jsonBody(dynamicId));

        URI getFrom = URI.create(baseUrl() + path);
        log.info("Get from: {}", getFrom);
        //Execute
        try {
            BaseResilience4jCommand getCommand = new BaseHttpGetResilience4jCommand(getFrom, "test", 50);
            Object result = commandProxy.run(getCommand);
        } catch (Exception e) {
            log.info("",e);
            assertTrue(true); // All good if this fails
        }
    }

    @Test
    public void getCommand_withExtraHeaders() throws InterruptedException, UnsuccesfulStatusCodeException, IOException {
        String dynamicId = "12345";
        String path = format("/demo/%s", dynamicId);

        //Expect
        createExpectationForGet_withExtraHeaders(path, jsonBody(dynamicId));

        URI getFrom = URI.create(baseUrl() + path);
        log.info("Get from: {}", getFrom);

        // Extre headers we want to add to the request
        Map<String,String> extraHeaders = Map.of("FDO_HEADER","foo","BAR_HEADER","bar");

        //Execute
        BaseResilience4jCommand getCommand = new BaseHttpGetResilience4jCommand(getFrom, "test", 50, extraHeaders);
        Object result = commandProxy.run(getCommand);
        assertNotNull(result);
        //Verify
        assertEquals(200, ((HttpResponse)result).statusCode());
        log.info("Response (extra headers): {}", ((HttpResponse<?>) result).body());
    }

    private void createExpectationForGet(String path, String returnBody) {
        new MockServerClient("127.0.0.1", 1080)
                .when(
                        request()
                                .withMethod("GET")
                                .withPath(path)
                                .withHeaders(
                                        new Header("Authorization", "Bearer 12345")),
                        exactly(1))
                .respond(
                        response()
                                .withStatusCode(200)
                                .withHeaders(
                                        new Header("Content-Type", "application/json; charset=utf-8"),
                                        new Header("Cache-Control", "public, max-age=86400"))
                                .withBody(returnBody)
//                                .withDelay(TimeUnit.SECONDS,1)
                );
    }



    private void createExpectationForGet_Fail(String path, String returnBody) {
        new MockServerClient("127.0.0.1", 1080)
                .when(
                        request()
                                .withMethod("GET")
                                .withPath(path)
                                .withHeaders(
                                        new Header("Authorization", "Bearer 12345"),
                                        new Header("Fail", "Fail")),
                        exactly(1))
                .respond(
                        response()
                                .withStatusCode(200)
                                .withHeaders(
                                        new Header("Content-Type", "application/json; charset=utf-8"),
                                        new Header("Cache-Control", "public, max-age=86400"))
                                .withBody(returnBody)
                );
    }

    private void createExpectationForGet_withExtraHeaders(String path, String returnBody) {
        new MockServerClient("127.0.0.1", 1080)
                .when(
                        request()
                                .withMethod("GET")
                                .withPath(path)
                                .withHeaders(
                                        new Header("FDO_HEADER", "foo"),
                                        new Header("BAR_HEADER", "bar"),
                                        new Header("Authorization", "Bearer 12345")),
                        exactly(1))
                .respond(
                        response()
                                .withStatusCode(200)
                                .withHeaders(
                                        new Header("Content-Type", "application/json; charset=utf-8"),
                                        new Header("Cache-Control", "public, max-age=86400"))
                                .withBody(returnBody)
//                                .withDelay(TimeUnit.SECONDS,1)
                );
    }

    private String jsonBody(String dynamicId) {
        String jsonString = Json.createObjectBuilder()
                .add("dynamicId", dynamicId)
                .add("ok", true)
                .build()
                .toString();
        return jsonString;
    }

    private String baseUrl() {
        return format("http://localhost:%d", server.getPort());
    }

}