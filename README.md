![GitHub tag (latest SemVer)](https://img.shields.io/github/v/tag/Cantara/Resilience4j-BaseCommands) [![Build Status](https://jenkins.entraos.io/buildStatus/icon?job=Resilience4j-BaseCommands)](https://jenkins.entraos.io/view/Build%20Monitor/job/Resilience4j-BaseCommands/) ![GitHub commit activity](https://img.shields.io/github/commit-activity/m/Cantara/Resilience4j-BaseCommands/?foo=bar) [![Project Status: Active – The project has reached a stable, usable state and is being actively developed.](http://www.repostatus.org/badges/latest/active.svg)](http://www.repostatus.org/#active) [![Known Vulnerabilities](https://snyk.io/test/github/Cantara/Resilience4j-BaseCommands/badge.svg)](https://snyk.io/test/github/Cantara/Resilience4j-BaseCommands)

 

# Resilience4j-BaseCommands
Basic Commands for HTTP using CircuitBreaker with Resilience4j

 * BaseHttpGetResilience4jCommand
 * BaseHttpPostResilience4jCommand
 * BaseHttpPutResilience4jCommand
 * BaseResilience4jCommand
 
 
 # Example, stubbed junit test
 
 public class BaseHttpGetCommandTest extends TestCase {

    private int port = 6776;
    URI baseUri = URI.create("http://www.vg.no/");
    String groupKey = "test";

    @Test
    public void testMockedGetCommand() {
        String jsonMockedResponse = "{\"status\":\"ok\"}";

        BaseHttpGetResilience4jCommand baseHttpGetResilience4jCommand = new BaseHttpGetResilience4jCommand(baseUri, groupKey)
                .withMockedResponse(200, jsonMockedResponse);
        assertTrue(jsonMockedResponse.equalsIgnoreCase(baseHttpGetResilience4jCommand.getAsJson()));
        assertTrue((baseHttpGetResilience4jCommand.getHttpStatus() == 200));

        baseHttpGetResilience4jCommand = new BaseHttpGetResilience4jCommand(baseUri, groupKey)
                .withMockedResponse(400, jsonMockedResponse);
        assertTrue((baseHttpGetResilience4jCommand.getHttpStatus() == 400));
    }
 
