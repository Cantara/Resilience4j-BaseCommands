package no.cantara.base.command;

import no.cantara.base.command.commands.InheritDeleteCommandForTesting;
import no.cantara.base.command.commands.InheritGetCommandForTesting;
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

import static java.lang.String.format;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.matchers.Times.exactly;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.slf4j.LoggerFactory.getLogger;

public class InheritDeleteCommandForTestingTest {
    private static final Logger log = getLogger(InheritDeleteCommandForTestingTest.class);
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
    public void deleteCommand() throws InterruptedException, UnsuccesfulStatusCodeException, IOException {
        String id = "98765";
        String path = format("/inherit-delete-command-for-testing/%s", id);

        //Expect
        createExpectationForDelete(path, jsonBody(id));

        URI deleteUri = URI.create(baseUrl());
        log.info("Delete from: {}", deleteUri);
        //Execute
        InheritDeleteCommandForTesting deleteCommand = new InheritDeleteCommandForTesting(deleteUri, "fake-token", id);
        Object result = commandProxy.run(deleteCommand);
        assertNotNull(result);
        //Verify
        assertEquals(200, ((HttpResponse)result).statusCode());
    }


    @Test
    public void deleteCommand_withExtraHeaders() throws InterruptedException, UnsuccesfulStatusCodeException, IOException {
        String id = "98765";
        String path = format("/inherit-delete-command-for-testing/%s", id);

        //Expect
        createExpectationForDelete_withExtraHeaders(path, jsonBody(id));

        URI deleteUri = URI.create(baseUrl());
        log.info("Delete from: {}", deleteUri);

        // Extre headers we want to add to the request
        Map<String,String> extraHeaders = Map.of("FDO_HEADER","foo","BAR_HEADER","bar");

        //Execute
        InheritDeleteCommandForTesting deleteCommand = new InheritDeleteCommandForTesting(deleteUri, "fake-token", id, extraHeaders);
        Object result = commandProxy.run(deleteCommand);
        assertNotNull(result);
        //Verify
        assertEquals(200, ((HttpResponse)result).statusCode());
        log.info("Response (extra headers): {}", ((HttpResponse<?>) result).body());
    }


    private void createExpectationForDelete(String path, String returnBody) {
        new MockServerClient("127.0.0.1", 1080)
                .when(
                        request()
                                .withMethod("DELETE")
                                .withPath(path)
                                .withHeaders(
                                    new Header("Authorization", "Bearer fake-token")),
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


    private void createExpectationForDelete_withExtraHeaders(String path, String returnBody) {
        new MockServerClient("127.0.0.1", 1080)
                .when(
                        request()
                                .withMethod("DELETE")
                                .withPath(path)
                                .withHeaders(
                                        new Header("FDO_HEADER", "foo"),
                                        new Header("BAR_HEADER", "bar"),
                                        new Header("Authorization", "Bearer fake-token")),
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

    private String jsonBody(String dynamicId) {
        String jsonString = Json.createObjectBuilder()
                .add("id", dynamicId)
                .add("ok", true)
                .build()
                .toString();
        return jsonString;
    }

    private String baseUrl() {
        return format("http://localhost:%d", server.getPort());
    }

}