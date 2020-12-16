package no.cantara.base.command;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.slf4j.LoggerFactory.getLogger;

public class BaseHttpPostResilience4jCommand extends BaseResilience4jCommand {
    private static final Logger log = getLogger(BaseHttpPostResilience4jCommand.class);

    protected BaseHttpPostResilience4jCommand(URI baseUri, String groupKey) {
        super(baseUri, groupKey);
    }

    @Override
    protected HttpResponse<String> run() {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(buildUri())
                .GET();
        if (buildAuthorization() != null) {
            builder = builder.header("Authorization", buildAuthorization());
        }
        httpRequest = builder.build();
        decorated = CircuitBreaker.decorateFunction(circuitBreaker, httpRequest -> {
            try {
                return client.send((HttpRequest) httpRequest, HttpResponse.BodyHandlers.ofString());
            } catch (IOException e) {
                log.debug("IOException when trying to get from {}. Reason {}", buildUri(), e.getMessage());
            } catch (InterruptedException e) {
                log.debug("Interrupted when trying to get from {}. Reason {}", buildUri(), e.getMessage());
            }
            return null;
        });

        HttpResponse<String> response = (HttpResponse<String>) decorated.apply(httpRequest);

        return response;
    }

    @Override
    protected URI buildUri() {
        return super.buildUri();
    }

    @Override
    protected String buildAuthorization() {
        return null;
    }
}
