package no.cantara.base.command;

import no.cantara.base.commands.BaseCommand;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;

public class BaseHttpDeleteResilience4jCommand extends BaseResilience4jCommand {
    private static final Logger log = getLogger(BaseHttpDeleteResilience4jCommand.class);
    protected HttpClient client;
    private HttpRequest httpRequest;
    private URI baseUri;
    private HttpResponse<String> response = null;

    /**
     * @param baseUri  URI to run get from
     * @param groupKey group your commands into logical entities for monitoring
     */
    protected BaseHttpDeleteResilience4jCommand(URI baseUri, String groupKey) {
        this(baseUri, groupKey, BaseCommand.DEFAULT_TIMEOUT);
    }


    /**
     * @param baseUri  URI to run get from
     * @param groupKey group your commands into logical entities for monitoring
     * @param extraHeaders  add extra http headers to the command
     */
    protected BaseHttpDeleteResilience4jCommand(URI baseUri, String groupKey, Map<String,String> extraHeaders) {
        this(baseUri, groupKey, BaseCommand.DEFAULT_TIMEOUT, extraHeaders);
    }

    /**
     * @param baseUri  URI to run get from
     * @param groupKey group your commands into logical entities for monitoring
     * @param timeout  timeout in milliseconds.
     */
    protected BaseHttpDeleteResilience4jCommand(URI baseUri, String groupKey, int timeout) {
        this(baseUri, groupKey, timeout, null);
    }

    /**
     * @param baseUri  URI to run get from
     * @param groupKey group your commands into logical entities for monitoring
     * @param timeout  timeout in milliseconds.
     * @param extraHeaders  add extra http headers to the command
     */
    protected BaseHttpDeleteResilience4jCommand(URI baseUri, String groupKey, int timeout, Map<String,String> extraHeaders) {
        super(baseUri, groupKey, timeout, extraHeaders);
        client = HttpClient.newBuilder().build();
        initializeCircuitBreaker();
        this.baseUri = baseUri;
    }

    private void initializeCircuitBreaker() {

    }

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
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(buildUri())
                .DELETE();
        if (getHeaders().length > 0) {
            builder = builder.headers(getHeaders());
        }
        httpRequest = builder.build();

        try {
            log.info("URI: {}", buildUri());
            response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            log.debug("IOException when trying to get from {}. Reason {}", buildUri(), e.getMessage());
        } catch (InterruptedException e) {
            log.debug("Interupted when trying to get from {}. Reason {}", buildUri(), e.getMessage());
        }

        log.info("Response: {}", response);
        if (response != null && response instanceof HttpResponse) {
            log.info("Status: {}, Body: {}", response.statusCode(), response.body());
        }
        return response;

    }


    @Override
    protected String getBody() {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(buildUri())
                .GET();
        if (getHeaders().length > 0) {
            builder = builder.headers(getHeaders());
        }
        httpRequest = builder.build();

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
