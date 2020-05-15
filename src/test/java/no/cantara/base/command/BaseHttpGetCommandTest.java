package no.cantara.base.command;

import junit.framework.TestCase;
import org.junit.Test;

import java.net.URI;

public class BaseHttpGetCommandTest extends TestCase {

    URI baseUri = URI.create("http://www.vg.no/");
    String groupKey = "test";

    @Test
    public void testMockedCommand() {
        String jsonMockedResponse = "{\"status\":\"ok\"}";

        BaseHttpGetResilience4jCommand baseHttpGetResilience4jCommand = new BaseHttpGetResilience4jCommand(baseUri, groupKey)
                .withMockedResponse(200, jsonMockedResponse);
        assertTrue(jsonMockedResponse.equalsIgnoreCase(baseHttpGetResilience4jCommand.getAsJson()));
        assertTrue((baseHttpGetResilience4jCommand.getHttpStatus() == 200));

        baseHttpGetResilience4jCommand = new BaseHttpGetResilience4jCommand(baseUri, groupKey)
                .withMockedResponse(400, jsonMockedResponse);
        assertTrue((baseHttpGetResilience4jCommand.getHttpStatus() == 400));
    }

}