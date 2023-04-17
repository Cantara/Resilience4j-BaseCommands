package no.cantara.base.command;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import no.cantara.base.commands.http.BaseHttpCommand;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.util.*;

public abstract class BaseResilience4jCommand extends BaseHttpCommand {
    CircuitBreaker circuitBreaker;
    List<String> headers = new ArrayList<>();


    public BaseResilience4jCommand(URI baseUri, String groupKey) {
        this(baseUri, groupKey, DEFAULT_TIMEOUT);
    }

    public BaseResilience4jCommand(URI baseUri, String groupKey, Map<String,String> extraHeaders) {
        this(baseUri, groupKey, DEFAULT_TIMEOUT, extraHeaders);
    }

    protected BaseResilience4jCommand(URI baseUri, String groupKey, int timeout) {
        super(baseUri, groupKey, timeout);
        client = HttpClient.newBuilder().build();
        initializeCircuitBreaker();
    }

    public BaseResilience4jCommand(URI baseUri, String groupKey, int timeout, Map<String, String> extraHeaders) {
        this(baseUri, groupKey, timeout);
        if (Objects.nonNull(extraHeaders)) {
            headers.addAll(convertHeadersMapToHeadersArray(extraHeaders));
        }
    }

    private void initializeCircuitBreaker() {
        CircuitBreakerConfig config = CircuitBreakerConfig.ofDefaults();
        CircuitBreakerRegistry registry = CircuitBreakerRegistry.of(config);
        circuitBreaker = registry.circuitBreaker(getGroupKey());
    }

    protected abstract Object run();

    protected abstract String getBody() throws IOException, InterruptedException, UnsuccesfulStatusCodeException;

    @Override
    protected URI buildUri() {
        return getBaseUri();
    }

    protected abstract String buildAuthorization();

    protected String[] getHeadersAsArray() {
        // Add Auth header if not already done
        if (Objects.nonNull(buildAuthorization()) && !headers.contains("Authorization")) {
            headers.add("Authorization");
            headers.add(buildAuthorization());
        }
        return headers.toArray(new String[0]);
    }

    private List<String> convertHeadersMapToHeadersArray(Map<String,String> headers) {
        List<String> headersArray = new ArrayList<>();
        headers.keySet()
                .forEach(key -> {
                    headersArray.add(key);
                    headersArray.add(headers.get(key));
                });
        return headersArray;
    }
}
