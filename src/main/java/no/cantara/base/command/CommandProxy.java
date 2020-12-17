package no.cantara.base.command;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

public class CommandProxy {
    private static final Logger log = getLogger(CommandProxy.class);

    private final CircuitBreaker circuitBreaker;

    public CommandProxy() {
        circuitBreaker = CircuitBreaker.ofDefaults("circuit-breaker");;
    }

    public Object run(BaseResilience4jCommand command) {
        Object result = circuitBreaker.executeSupplier(command::run);
        log.info("Result: {}", result);
        return result;
    }

}
