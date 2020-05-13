package no.cantara.base.command;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import no.cantara.base.commands.BaseCommand;
import no.cantara.base.commands.http.BaseHttpCommand;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.function.Function;

import static org.slf4j.LoggerFactory.getLogger;

public class BaseHttpPutResilience4jCommand extends BaseHttpCommand {
    private static final Logger log = getLogger(BaseHttpPutResilience4jCommand.class);
    protected HttpClient client;
    private HttpRequest httpRequest;
    private Function<HttpRequest, HttpResponse> decorated;
    private CircuitBreaker circuitBreaker;
    private URI baseUri;

    protected BaseHttpPutResilience4jCommand(URI baseUri, String groupKey) {
        this(baseUri, groupKey, BaseCommand.DEFAULT_TIMEOUT);
    }

    protected BaseHttpPutResilience4jCommand(URI baseUri, String groupKey, int timeout) {
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
        HttpResponse<String> response = run();
        json = response.body();
        return json;
    }

    //Should we impose String body on HttpCommands?
    @Override
    protected HttpResponse<String> run() {
        httpRequest = HttpRequest.newBuilder()
                .header("Authorization", buildAuthorization())
                .uri(buildUri())
                .GET()
                .build();
        decorated = CircuitBreaker.decorateFunction(circuitBreaker, httpRequest -> {
            try {
                return client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            } catch (IOException e) {
                log.debug("IOException when trying to get from {}. Reason {}", buildUri(), e.getMessage());
            } catch (InterruptedException e) {
                log.debug("Interupted when trying to get from {}. Reason {}", buildUri(), e.getMessage());
            }
            return null;
        });

        HttpResponse<String> response = decorated.apply(httpRequest);

        return response;
    }

    @Override
    protected URI buildUri() {
        return baseUri;
    }

    @Override
    protected String buildAuthorization() {
        return "";
    }
}
