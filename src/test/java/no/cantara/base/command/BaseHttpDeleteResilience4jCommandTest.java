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
import java.net.URI;
import java.net.http.HttpResponse;

import static java.lang.String.format;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.matchers.Times.exactly;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.slf4j.LoggerFactory.getLogger;

public class BaseHttpDeleteResilience4jCommandTest {
    private static final Logger log = getLogger(BaseHttpDeleteResilience4jCommandTest.class);
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
    public void deleteCommand() {
        String dynamicId = "12345";
        String path = format("/demo/%s", dynamicId);

        //Expect
        createExpectationForDelete(path, jsonBody(dynamicId));

        URI deleteUri = URI.create(baseUrl() + path);
        log.info("Delete from: {}", deleteUri);
        //Execute
        BaseResilience4jCommand getCommand = new BaseHttpDeleteResilience4jCommand(deleteUri, "test", 50);
        Object result = commandProxy.run(getCommand);
        assertNotNull(result);
        //Verify
        assertEquals(200, ((HttpResponse)result).statusCode());
    }

    private void createExpectationForDelete(String path, String returnBody) {
        new MockServerClient("127.0.0.1", 1080)
                .when(
                        request()
                                .withMethod("DELETE")
                                .withPath(path),
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