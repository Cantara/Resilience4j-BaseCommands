package no.cantara.base.command;

import junit.framework.TestCase;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;

public class BaseHttpGetCommandTest extends TestCase {

    private int port = 6776;
    URI baseUri = URI.create("http://www.vg.no/");
    String groupKey = "test";

    @Test
    public void testMockedGetCommand() throws IOException, InterruptedException {
        String jsonMockedResponse = "{\"status\":\"ok\"}";

        BaseHttpGetResilience4jCommand baseHttpGetResilience4jCommand = new BaseHttpGetResilience4jCommand(baseUri, groupKey)
                .withMockedResponse(200, jsonMockedResponse);
        assertTrue(jsonMockedResponse.equalsIgnoreCase(baseHttpGetResilience4jCommand.getAsJson()));
        assertTrue((baseHttpGetResilience4jCommand.getHttpStatus() == 200));

        baseHttpGetResilience4jCommand = new BaseHttpGetResilience4jCommand(baseUri, groupKey)
                .withMockedResponse(400, jsonMockedResponse);
        assertTrue((baseHttpGetResilience4jCommand.getHttpStatus() == 400));
    }


    @Test
    public void shouldPassVerification() throws IOException, InterruptedException {

        String expectedJsonresponse = "{\"hei\":\"du\"}";
        BaseHttpGetResilience4jCommand getCommand = new BaseHttpGetResilience4jCommand(URI.create("http://localhost:" + port + "/demo"), "test").withMockedResponse(200, expectedJsonresponse);
        String response = getCommand.getAsJson();
        assertNotNull(response);
        assertTrue(getCommand.getHttpStatus() == 200);
        assertTrue(response.equalsIgnoreCase(expectedJsonresponse));
    }


}