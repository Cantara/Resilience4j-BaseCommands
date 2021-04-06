package no.cantara.base.command;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import no.cantara.base.commands.http.BaseHttpCommand;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;

public abstract class BaseResilience4jCommand extends BaseHttpCommand {
    CircuitBreaker circuitBreaker;

    public BaseResilience4jCommand(URI baseUri, String groupKey) {
        this(baseUri, groupKey, DEFAULT_TIMEOUT);
    }

    protected BaseResilience4jCommand(URI baseUri, String groupKey, int timeout) {
        super(baseUri, groupKey, timeout);
        client = HttpClient.newBuilder().build();
        initializeCircuitBreaker();
    }

    private void initializeCircuitBreaker() {
        CircuitBreakerConfig config = CircuitBreakerConfig.ofDefaults();
        CircuitBreakerRegistry registry = CircuitBreakerRegistry.of(config);
        circuitBreaker = registry.circuitBreaker(getGroupKey());
    }

    protected abstract Object run() throws InterruptedException, IOException, UnsuccesfulStatusCodeException;

    protected abstract String getBody() throws IOException, InterruptedException, UnsuccesfulStatusCodeException;

    @Override
    protected URI buildUri() {
        return getBaseUri();
    }

    protected abstract String buildAuthorization();
}
