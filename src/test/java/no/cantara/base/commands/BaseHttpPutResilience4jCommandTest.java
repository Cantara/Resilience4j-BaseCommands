package no.cantara.base.commands;

import com.xebialabs.restito.server.StubServer;
import io.restassured.RestAssured;
import no.cantara.base.command.BaseHttpPostResilience4jCommand;
import org.junit.Before;

import java.net.URI;


public class BaseHttpPutResilience4jCommandTest {
    private StubServer server;
    private BaseHttpPostResilience4jCommand baseHttpPostResilience4jCommand;
    private int port;
    private URI uri;

    @Before
    public void start() {
        server = new StubServer().run();
        RestAssured.port = server.getPort();
        this.port = server.getPort();
        uri = URI.create("http://localhost:" + port );

    }

    /*
    @Test
    public void testDoPostCommandJson() throws Exception {

        // Restito
        whenHttp(server).
//                match(contentType("applicatin/json"))
        match(post("/postJson"), withHeader("CONTENT-TYPE", MediaType.APPLICATION_JSON)).
                then(status(HttpStatus.OK_200), stringContent("Updated Ok") );


        baseHttpPostResilience4jCommand = new BaseHttpPostResilience4jCommand(uri, "test") {
            @Override
            protected String getTargetPath() {
                return "/postJson";
            }

            @Override
            protected String getJsonBody() {
                return "{\"test\": \"value\"}";
            }
        };

        String response = (String) baseHttpPostResilience4jCommand.();
        assertEquals("Updated Ok", response);

    }

    @Test
    public void testDoPostCommandFormParams() throws Exception {
        // Restito
        whenHttp(server).
//                match(contentType("applicatin/json"))
        match(post("/postForm"), withHeader("CONTENT-TYPE", MediaType.APPLICATION_FORM_URLENCODED +"; charset=UTF-8")).
//        match(post("/postForm")).
                then(status(HttpStatus.OK_200), stringContent("Updated Ok") );


        Map<String,String> formParams = new HashMap<>();
        formParams.put("test", "value");
        baseHttpPostResilience4jCommand = new BaseHttpPostResilience4jCommand(uri, "test") {
            @Override
            protected String getTargetPath() {
                return "/postForm";
            }

            @Override
            protected Map<String, String> getFormParameters() {
                return formParams;
            }
        };

        String response = (String) baseHttpPostResilience4jCommand.doPostCommand();
        assertEquals("Updated Ok", response);

    }

    @After
    public void stop() {
        server.stop();
    }

    @Test
    public void shouldPassVerification() throws UnsupportedEncodingException {
        // Restito
        whenHttp(server).
                match(get("/demo")).
                then(status(HttpStatus.OK_200));

        // Rest-assured
        expect().statusCode(200).when().get("/demo");

        // Restito
        verifyHttp(server).once(
                method(Method.GET),
                uri("/demo")
        );
    }


     */

}