package no.cantara.base.command;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockserver.client.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.Header;
import org.slf4j.Logger;

import javax.json.Json;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpResponse;
import java.util.UUID;

import static java.lang.String.format;
import static org.junit.Assert.*;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.matchers.Times.exactly;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.mockserver.model.StringBody.exact;
import static org.slf4j.LoggerFactory.getLogger;

//import io.restassured.RestAssured.*;
//        import io.restassured.matcher.RestAssuredMatchers.*;
//        import org.hamcrest.Matchers.*;

public class BaseHttpPostResilience4jCommandTest {
    private static final Logger log = getLogger(BaseHttpPostResilience4jCommandTest.class);
    private static final String CREATED_ID = "789123";

    private CommandProxy commandProxy;
    private static ClientAndServer server;

    @BeforeClass
    public static void startServer() {
        server = startClientAndServer(1081);
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
    public void successfullPost() throws InterruptedException, UnsuccesfulStatusCodeException, IOException {
        String path = "/create";
        String dynamicId = UUID.randomUUID().toString();
        String requestBody = jsonBody(dynamicId);
        createExpectationPost(path, requestBody);
        BaseHttpPostResilience4jCommand postCommand = new BaseHttpPostResilience4jCommand(URI.create(baseUrl() + path), "test");
        postCommand.setBody(requestBody);
        HttpResponse<String> response = (HttpResponse<String>) commandProxy.run(postCommand);
        log.info("Response: {}", response);
        assertNotNull(response);
        assertEquals(201, response.statusCode());
        assertTrue(response.body().contains(CREATED_ID));
    }
    private void createExpectationPost(String path, String requestBody) {
        new MockServerClient("127.0.0.1", 1081)
                .when(
                        request()
                                .withMethod("POST")
                                .withPath(path)
                                .withHeader("Content-Type", "application/json; charset=utf-8")
                                .withBody(exact(requestBody)),
                        exactly(1))
                .respond(
                        response()
                                .withStatusCode(201)
                                .withHeaders(
                                        new Header("Content-Type", "application/json; charset=utf-8"))
                                .withBody("{ \"id\": \"" + CREATED_ID + "\" }")
//                                .withDelay(TimeUnit.SECONDS,1)
                );
    }
    private void createExpectationForMissingAuthHeader(String path, String requestBody) {
        new MockServerClient("127.0.0.1", 1081)
                .when(
                        request()
                                .withMethod("POST")
                                .withPath(path)
                                .withHeader("Content-type", "application/json; charset=utf-8")
                                .withBody(exact(requestBody)),
                        exactly(1))
                .respond(
                        response()
                                .withStatusCode(401)
                                .withHeaders(
                                        new Header("Content-Type", "application/json; charset=utf-8"),
                                        new Header("Cache-Control", "public, max-age=86400"))
                                .withBody("{ message: 'missing bearer token' }")
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