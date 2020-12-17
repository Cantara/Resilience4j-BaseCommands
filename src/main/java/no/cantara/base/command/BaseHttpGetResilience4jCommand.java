package no.cantara.base.command;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import no.cantara.base.commands.BaseCommand;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.function.Function;

import static org.slf4j.LoggerFactory.getLogger;

public class BaseHttpGetResilience4jCommand extends BaseResilience4jCommand {
    private static final Logger log = getLogger(BaseHttpGetResilience4jCommand.class);
    protected HttpClient client;
    private HttpRequest httpRequest;
    private Function<HttpRequest, HttpResponse> decorated;
    private CircuitBreaker circuitBreaker;
    private URI baseUri;
    private HttpResponse<String> response = null;

    /**
     * @param baseUri  URI to run get from
     * @param groupKey group your commands into logical enteties for monitoring
     */
    protected BaseHttpGetResilience4jCommand(URI baseUri, String groupKey) {
        this(baseUri, groupKey, BaseCommand.DEFAULT_TIMEOUT);
    }

    /**
     * @param baseUri  URI to run get from
     * @param groupKey group your commands into logical enteties for monitoring
     * @param timeout  timeout in milliseconds.
     */
    protected BaseHttpGetResilience4jCommand(URI baseUri, String groupKey, int timeout) {
        super(baseUri, groupKey, timeout);
        client = HttpClient.newBuilder().build();
        initializeCircuitBreaker();
        this.baseUri = baseUri;
    }

    private void initializeCircuitBreaker() {
        CircuitBreakerConfig config = CircuitBreakerConfig.ofDefaults();
        CircuitBreakerRegistry registry = CircuitBreakerRegistry.of(config);
        circuitBreaker = registry.circuitBreaker(getGroupKey());
    }

    //https://www.baeldung.com/java-9-http-client
    //https://gssachdeva.wordpress.com/2015/09/02/java-8-lambda-expression-for-design-patterns-command-design-pattern/
    public String getAsJson() {
        String json = null;
        if (isMocked) {
            json = mockedResponseData;
        } else {
            HttpResponse<String> response = run();
            json = response.body();
        }
        return json;
    }

    /**
     * When you expect eg. 204
     *
     * @return http status only
     */
    public int getHttpStatus() {
        if (isMocked) {
            return mockedStatusCode;
        } else {
            int statusCode = -1;
            if (response == null) {
                response = run();
            }
            statusCode = response.statusCode();
            return statusCode;
        }
    }

    //Should we impose String body on HttpCommands?
    @Override
    protected HttpResponse<String> run() {
        httpRequest = HttpRequest.newBuilder()
                .header("Authorization", buildAuthorization())
                .uri(buildUri())
                .GET()
                .build();
//        decorated = CircuitBreaker.decorateFunction(circuitBreaker, httpRequest -> {
        try {
            log.info("URI: {}", buildUri());
            response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            log.debug("IOException when trying to get from {}. Reason {}", buildUri(), e.getMessage());
        } catch (InterruptedException e) {
            log.debug("Interupted when trying to get from {}. Reason {}", buildUri(), e.getMessage());
        }
//            return null;
//        });

//        HttpResponse<String> response = decorated.apply(httpRequest);
        log.info("Response: {}", response);
        if (response != null && response instanceof HttpResponse) {
            log.info("Status: {}, Body: {}", response.statusCode(), response.body());
        }
        return response;

    }

    @Override
    protected String getBody() {
        httpRequest = HttpRequest.newBuilder()
                .header("Authorization", buildAuthorization())
                .uri(buildUri())
                .GET()
                .build();
//        decorated = CircuitBreaker.decorateFunction(circuitBreaker, httpRequest -> {
        try {
            response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            log.debug("IOException when trying to get from {}. Reason {}", buildUri(), e.getMessage());
        } catch (InterruptedException e) {
            log.debug("Interupted when trying to get from {}. Reason {}", buildUri(), e.getMessage());
        }
        return response.body();
    }

    @Override
    protected URI buildUri() {
        return baseUri;
    }

    @Override
    protected String buildAuthorization() {
        return "Bearer 12345";
    }
}
