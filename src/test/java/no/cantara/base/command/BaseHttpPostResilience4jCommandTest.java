package no.cantara.base.command;

import com.xebialabs.restito.server.StubServer;
import io.restassured.RestAssured;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.http.HttpResponse;

import static org.slf4j.LoggerFactory.getLogger;

//import io.restassured.RestAssured.*;
//        import io.restassured.matcher.RestAssuredMatchers.*;
//        import org.hamcrest.Matchers.*;

public class BaseHttpPostResilience4jCommandTest {
    private static final Logger log = getLogger(BaseHttpPostResilience4jCommandTest.class);
    private StubServer server;
    private BaseHttpPostResilience4jCommand postCommand;
    private int port;
    private URI uri;

    @Before
    public void start() {
        server = new StubServer().run();
        RestAssured.port = server.getPort();
        this.port = server.getPort();
        uri = URI.create("http://localhost:" + port );

    }

    @Test
    public void shouldPassVerification() throws UnsupportedEncodingException {
        postCommand = new BaseHttpPostResilience4jCommand(URI.create("https://www.vg.no/"), "test");
        HttpResponse<String> response = postCommand.run();
        log.info("Response: {}", response);
        /** Todo
        // Restito
        whenHttp(server).
                match(get("/demo.json")).
                then(status(HttpStatus.OK_200),stringContent("{\"hei\":\"du\"}"));

        // Rest-assured
//        BaseHttpGetResilience4jCommand getCommand = new BaseHttpGetResilience4jCommand(URI.create("http://localhost:" + port + "/demo"), "test");
//        String response = getCommand.getAsJson();
//        assertNotNull(response);
        given().
                when().
                get("/demo.json").
                then().
                assertThat().
                body("hei",equalTo("du"));
//        get("/events?id=390").then().statusCode(200).assertThat()
//                .body("data.leagueId", equalTo(35));
//        expect().statusCode(200).body(StringContains("{\"hei\":\"du\"}")).when().get("/demo");

        // Restito
        verifyHttp(server).once(
                method(Method.GET),
                uri("/demo.json")
        );
         */
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