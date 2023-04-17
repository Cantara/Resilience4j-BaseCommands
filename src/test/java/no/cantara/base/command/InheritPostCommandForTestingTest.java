package no.cantara.base.command;

import no.cantara.base.command.commands.InheritPostCommandForTesting;
import no.cantara.base.command.commands.InheritPutCommandForTesting;
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
import java.util.Map;
import java.util.UUID;

import static java.lang.String.format;
import static org.junit.Assert.*;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.matchers.Times.exactly;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.mockserver.model.StringBody.exact;
import static org.slf4j.LoggerFactory.getLogger;

public class InheritPostCommandForTestingTest {
    private static final Logger log = getLogger(InheritPostCommandForTestingTest.class);
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
    public void successfullPut() throws InterruptedException, UnsuccesfulStatusCodeException, IOException {
        String token = UUID.randomUUID().toString();
        String id = UUID.randomUUID().toString();
        String path = format("/inherit-post-command-for-testing/%s", id);

        String requestBody = jsonBody(id);
        createExpectationPut(path, requestBody, id, token);

        InheritPostCommandForTesting putCommand = new InheritPostCommandForTesting(URI.create(baseUrl()), token,id, requestBody);
        HttpResponse<String> response = (HttpResponse<String>) commandProxy.run(putCommand);
        log.info("Response: {}", response);

        assertNotNull(response);
        assertEquals(201, response.statusCode());
        assertTrue(response.body().contains(id));
    }

    @Test
    public void successfullPost_withExtraHeaders() throws InterruptedException, UnsuccesfulStatusCodeException, IOException {
        String token = UUID.randomUUID().toString();
        String id = UUID.randomUUID().toString();
        String path = format("/inherit-post-command-for-testing/%s", id);

        String requestBody = jsonBody(id);
        createExpectationPut_withExtraHeaders(path, requestBody, id, token);
        Map<String,String> extraHeaders = Map.of("FDO_HEADER","foo","BAR_HEADER","bar");

        InheritPostCommandForTesting putCommand = new InheritPostCommandForTesting(URI.create(baseUrl()), token,id, requestBody,extraHeaders);
        HttpResponse<String> response = (HttpResponse<String>) commandProxy.run(putCommand);
        log.info("Response (extra headers): {}", response);

        assertNotNull(response);
        assertEquals(201, response.statusCode());
        assertTrue(response.body().contains(id));
    }

    private void createExpectationPut(String path, String requestBody, String id, String token) {
        new MockServerClient("127.0.0.1", 1081)
                .when(
                        request()
                                .withMethod("POST")
                                .withPath(path)
                                .withHeaders(
                                        new Header("Content-Type", "application/json; charset=utf-8"),
                                        new Header("Authorization", "Bearer "+token))
                                .withBody(exact(requestBody)),
                        exactly(1))
                .respond(
                        response()
                                .withStatusCode(201)
                                .withHeaders(
                                        new Header("Content-Type", "application/json; charset=utf-8"))
                                .withBody("{ \"id\": \"" + id + "\" }")
                );
    }

    private void createExpectationPut_withExtraHeaders(String path, String requestBody, String id, String token) {
        new MockServerClient("127.0.0.1", 1081)
                .when(
                        request()
                                .withMethod("POST")
                                .withPath(path)
                                .withHeaders(
                                        new Header("Content-Type", "application/json; charset=utf-8"),
                                        new Header("FDO_HEADER", "foo"),
                                        new Header("BAR_HEADER", "bar"),
                                        new Header("Authorization", "Bearer "+token))
                                .withBody(exact(requestBody)),
                        exactly(1))
                .respond(
                        response()
                                .withStatusCode(201)
                                .withHeaders(
                                        new Header("Content-Type", "application/json; charset=utf-8"))
                                .withBody("{ \"id\": \"" + id + "\" }")
                );
    }


    private String jsonBody(String id) {
        String jsonString = Json.createObjectBuilder()
                .add("id", id)
                .build()
                .toString();
        return jsonString;
    }

    private String baseUrl() {
        return format("http://localhost:%d", server.getPort());
    }

}