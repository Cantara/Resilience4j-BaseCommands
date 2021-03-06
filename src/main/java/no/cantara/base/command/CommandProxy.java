package no.cantara.base.command;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

public class CommandProxy {
    private static final Logger log = getLogger(CommandProxy.class);
    private final CircuitBreakerRegistry registry;

    public CommandProxy() {
        CircuitBreakerConfig config = getConfig();
        registry = CircuitBreakerRegistry.of(config);

    }

    public Object run(BaseResilience4jCommand command) {
        String groupKey = command.getGroupKey();
        CircuitBreaker circuitBreaker = registry.circuitBreaker(groupKey);
        Object result = circuitBreaker.executeSupplier(command::run);
        log.trace("Result of CommandProxy::Run : {}", result);
        return result;
    }

    protected CircuitBreakerConfig getConfig() {
        return CircuitBreakerConfig.ofDefaults();
    }
    public CircuitBreakerRegistry getRegistry() {
        return registry;
    }
}
