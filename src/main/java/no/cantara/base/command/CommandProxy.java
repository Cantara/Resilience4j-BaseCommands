package no.cantara.base.command;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.http.HttpResponse;

import static org.slf4j.LoggerFactory.getLogger;

public class CommandProxy {
    private static final Logger log = getLogger(CommandProxy.class);
    private final CircuitBreakerRegistry registry;

    public CommandProxy() {
        CircuitBreakerConfig config = getConfig();
        registry = CircuitBreakerRegistry.of(config);
    }

    public Object run(BaseResilience4jCommand command) throws UnsuccesfulStatusCodeException, IOException, InterruptedException {
        String groupKey = command.getGroupKey();
        CircuitBreaker circuitBreaker = registry.circuitBreaker(groupKey);
        final HttpResponse<String> result;
        try {
            result = (HttpResponse<String>) circuitBreaker.executeSupplier(command::run);
        } catch (RuntimeException throwable) {
            final Throwable cause = throwable.getCause();
            if (cause instanceof UnsuccesfulStatusCodeException) {
                throw (UnsuccesfulStatusCodeException) cause;
            }
            if (cause instanceof IOException) {
                throw (IOException) cause;
            }
            if (cause instanceof InterruptedException) {
                throw (InterruptedException) cause;
            }
            log.warn("Unrecognized exception: {}", cause);
            log.warn("Original runtimeexception: {}", throwable);
            throw throwable;
        }

        log.trace("Result of CommandProxy::Run : {}", result);
        return result;
    }

    protected CircuitBreakerConfig getConfig() {
        final CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom()
                .recordException(throwable -> {
                    Throwable e = throwable.getCause();
                    return e instanceof UnsuccesfulStatusCodeException && ((UnsuccesfulStatusCodeException) e).getStatusCode() >= 500;
                })
                .recordExceptions(IOException.class, InterruptedException.class)
                .build();
        return circuitBreakerConfig;
    }

    public CircuitBreakerRegistry getRegistry() {
        return registry;
    }
}
