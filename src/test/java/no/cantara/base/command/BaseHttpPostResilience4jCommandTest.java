package no.cantara.base.command;

import org.junit.*;
import org.mockserver.client.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.Header;
import org.slf4j.Logger;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.http.HttpResponse;

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

    @Ignore
    @Test
    public void shouldPassVerification() throws UnsupportedEncodingException {
        String path = "create";
        String requestBody = "{\"any\":\"thing\"}";
        createExpectationForMissingAuthHeader(path, requestBody);
        BaseResilience4jCommand postCommand = new BaseHttpPostResilience4jCommand(URI.create("https://www.vg.no/"), "test");
        HttpResponse<String> response = (HttpResponse<String>) commandProxy.run(postCommand);
        log.info("Response: {}", response);
    }

    private void createExpectationForMissingAuthHeader(String path, String requestBody) {
        new MockServerClient("127.0.0.1", 1080)
                .when(
                        request()
                                .withMethod("POST")
                                .withPath(path)
                                .withHeader("\"Content-type\", \"application/json\"")
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
}