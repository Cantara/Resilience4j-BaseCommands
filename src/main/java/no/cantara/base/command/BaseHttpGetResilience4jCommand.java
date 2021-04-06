package no.cantara.base.command;

import no.cantara.base.commands.BaseCommand;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.slf4j.LoggerFactory.getLogger;

public class BaseHttpGetResilience4jCommand extends BaseResilience4jCommand {
    private static final Logger log = getLogger(BaseHttpGetResilience4jCommand.class);
    protected HttpClient client;
    private HttpRequest httpRequest;
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

    }

    public String getAsJson() throws IOException, InterruptedException {
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
    public int getHttpStatus() throws IOException, InterruptedException {
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
    protected HttpResponse<String> run() throws InterruptedException, IOException {
        httpRequest = HttpRequest.newBuilder()
                .header("Authorization", buildAuthorization())
                .uri(buildUri())
                .GET()
                .build();
//        decorated = CircuitBreaker.decorateFunction(circuitBreaker, httpRequest -> {
        try {
            log.debug("URI: {}", buildUri());
            response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            log.warn("IOException when trying to get from {}. Reason {}", buildUri(), e.getMessage());
            throw e;
        } catch (InterruptedException e) {
            log.warn("Interupted when trying to get from {}. Reason {}", buildUri(), e.getMessage());
            throw e;
        }
//            return null;
//        });

//        HttpResponse<String> response = decorated.apply(httpRequest);
        log.debug("Response: {}", response);
        if (response != null && response instanceof HttpResponse) {
            log.debug("Status: {}, Body: {}", response.statusCode(), response.body());
        }
        return response;

    }

    @Override
    protected String getBody() throws InterruptedException, IOException {
        httpRequest = HttpRequest.newBuilder()
                .header("Authorization", buildAuthorization())
                .uri(buildUri())
                .GET()
                .build();
//        decorated = CircuitBreaker.decorateFunction(circuitBreaker, httpRequest -> {
        try {
            response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            log.warn("IOException when trying to get from {}. Reason {}", buildUri(), e.getMessage());
            throw e;
        } catch (InterruptedException e) {
            log.warn("InterruptedException when trying to get from {}. Reason {}", buildUri(), e.getMessage());
            throw e;
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
