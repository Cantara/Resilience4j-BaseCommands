package no.cantara.base.command;

import org.junit.*;
import org.mockserver.client.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.Header;
import org.mockserver.verify.VerificationTimes;
import org.slf4j.Logger;

import javax.json.Json;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpResponse;
import java.util.concurrent.TimeUnit;

import static java.lang.String.format;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.matchers.Times.exactly;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.mockserver.model.StringBody.exact;
import static org.slf4j.LoggerFactory.getLogger;

public class CommandProxyTest {
    private static final Logger log = getLogger(CommandProxyTest.class);
    public static final int PORT = 1082;
    public static final String GROUP_KEY = "test";

    private CommandProxy commandProxy;
    private static ClientAndServer server;
    private String dynamicId;
    private String path;

    @BeforeClass
    public static void startServer() {
        server = startClientAndServer(PORT);
    }
    @Before
    public void setUp() {
        commandProxy = new CommandProxy();
        dynamicId = "12345";
    }

    @AfterClass
    public static void stopServer() {
        server.stop();
    }

    @Test
    public void expectErrorCodesMoreThan500ToCountAsFailed()  {
        path = format("/demo/%s", dynamicId);

        //Expect
        createExpectationForGet(path, 500, "Internal Server Erro");

        URI getFrom = URI.create(baseUrl() + path);
        log.info("Get from: {}", getFrom);
        //Execute
        BaseResilience4jCommand getCommand = new BaseHttpGetResilience4jCommand(getFrom, GROUP_KEY, 50);
        try {
            commandProxy.run(getCommand);
            Assert.fail();
        } catch (UnsuccesfulStatusCodeException e) {
            assertEquals(500, e.getStatusCode());
            final int numberOfFailedCalls = commandProxy.getRegistry().circuitBreaker(GROUP_KEY).getMetrics().getNumberOfFailedCalls();
            assertEquals(1, numberOfFailedCalls);
        } catch (IOException | InterruptedException e) {
            Assert.fail();
        }

    }


    @Test
    public void expectErrorCodesLessThan500NotToCountAsFailed()  {
        path = format("/demo/%s", dynamicId);

        //Expect
        createExpectationForGet(path, 404, null);

        URI getFrom = URI.create(baseUrl() + path);
        log.info("Get from: {}", getFrom);
        //Execute
        BaseResilience4jCommand getCommand = new BaseHttpGetResilience4jCommand(getFrom, GROUP_KEY, 50);
        try {
            commandProxy.run(getCommand);
            Assert.fail();
        } catch (UnsuccesfulStatusCodeException e) {
            assertEquals(404, e.getStatusCode());
            final int numberOfFailedCalls = commandProxy.getRegistry().circuitBreaker(GROUP_KEY).getMetrics().getNumberOfFailedCalls();
            assertEquals(0, numberOfFailedCalls);
        } catch (IOException | InterruptedException e) {
            Assert.fail();
        }

    }

    @Test
    public void getCommand() throws InterruptedException, UnsuccesfulStatusCodeException, IOException {
        String dynamicId = "12345";
        String path = format("/demo/%s", dynamicId);

        //Expect
        createExpectationForGet(path, 200, jsonBody(dynamicId));

        URI getFrom = URI.create(baseUrl() + path);
        log.info("Get from: {}", getFrom);
        //Execute
        BaseResilience4jCommand getCommand = new BaseHttpGetResilience4jCommand(getFrom, "test", 50);
        Object result = commandProxy.run(getCommand);
        assertNotNull(result);
        //Verify
        assertEquals(200, ((HttpResponse)result).statusCode());
    }

    private void createExpectationForGet(String path, int status, String returnBody) {
        new MockServerClient("127.0.0.1", PORT)
                .when(
                        request()
                                .withMethod("GET")
                                .withPath(path),
                        exactly(1))
                .respond(
                        response()
                                .withStatusCode(status)
                                .withHeaders(
                                        new Header("Content-Type", "application/json; charset=utf-8"),
                                        new Header("Cache-Control", "public, max-age=86400"))
                                 .withBody(returnBody)
//                                .withDelay(TimeUnit.SECONDS,1)
                );
    }
    private void createExpectationForInvalidAuth() {
        new MockServerClient("127.0.0.1", PORT)
                .when(
                        request()
                                .withMethod("POST")
                                .withPath("/validate")
                                .withHeader("\"Content-type\", \"application/json\"")
                                .withBody(exact("{username: 'foo', password: 'bar'}")),
                        exactly(1))
                .respond(
                        response()
                                .withStatusCode(401)
                                .withHeaders(
                                        new Header("Content-Type", "application/json; charset=utf-8"),
                                        new Header("Cache-Control", "public, max-age=86400"))
                                .withBody("{ message: 'incorrect username and password combination' }")
                                .withDelay(TimeUnit.SECONDS,1)
                );
    }

    private void verifyPostRequest() {
        new MockServerClient("localhost", PORT).verify(
                request()
                        .withMethod("POST")
                        .withPath("/validate")
                        .withBody(exact("{username: 'foo', password: 'bar'}")),
                VerificationTimes.exactly(1)
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