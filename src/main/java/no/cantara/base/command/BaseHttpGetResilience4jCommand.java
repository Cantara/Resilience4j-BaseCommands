package no.cantara.base.command;

import no.cantara.base.commands.BaseCommand;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.Set;

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
     * @param extraHeaders  add extra http headers to the command
     */
    protected BaseHttpGetResilience4jCommand(URI baseUri, String groupKey, Map<String,String> extraHeaders) {
        this(baseUri, groupKey, BaseCommand.DEFAULT_TIMEOUT, extraHeaders);
    }

    /**
     * @param baseUri  URI to run get from
     * @param groupKey group your commands into logical enteties for monitoring
     * @param timeout  timeout in milliseconds.
     */
    protected BaseHttpGetResilience4jCommand(URI baseUri, String groupKey, int timeout) {
        this(baseUri, groupKey, timeout, null);
    }


    /**
     * @param baseUri  URI to run get from
     * @param groupKey group your commands into logical enteties for monitoring
     * @param timeout  timeout in milliseconds.
     * @param extraHeaders  add extra http headers to the command
     */
    protected BaseHttpGetResilience4jCommand(URI baseUri, String groupKey, int timeout, Map<String,String> extraHeaders) {
        super(baseUri, groupKey, timeout, extraHeaders);
        client = HttpClient.newBuilder().build();
        initializeCircuitBreaker();
        this.baseUri = baseUri;
    }

    private void initializeCircuitBreaker() {

    }

    public String getAsJson() throws IOException, InterruptedException, UnsuccesfulStatusCodeException {
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
    public int getHttpStatus() throws IOException, InterruptedException, UnsuccesfulStatusCodeException {
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
                .GET();
        if (getHeadersAsArray().length > 0) {
            builder = builder.headers(getHeadersAsArray());
        }
        httpRequest = builder.build();

        try {
            log.debug("URI: {}", buildUri());
            response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            log.warn("IOException when trying to get from {}. Reason {}", buildUri(), e.getMessage());
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            log.warn("Interupted when trying to get from {}. Reason {}", buildUri(), e.getMessage());
            throw new RuntimeException(e);
        }

        log.debug("Response: {}", response);
        if (response != null && response instanceof HttpResponse) {
            log.debug("Status: {}, Body: {}", response.statusCode(), response.body());

            if (successfulStatusCodes().contains(response.statusCode())) {
                return response;
            } else {
                final String errorMessage = "UnsuccesfulStatusCode: Statuscode " + response.statusCode() + " is not in successful set of [" + successfulStatusCodes() + "]. Http body is: " + response.body();
                log.warn(errorMessage);
                throw new RuntimeException(new UnsuccesfulStatusCodeException(response.statusCode(), errorMessage));
            }
        }
        throw new RuntimeException(new IOException("Expected HttpResponse"));
    }

    @Override
    protected String getBody() throws InterruptedException, IOException, UnsuccesfulStatusCodeException {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(buildUri())
                .GET();
        if (getHeadersAsArray().length > 0) {
            builder = builder.headers(getHeadersAsArray());
        }
        httpRequest = builder.build();

        try {
            response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            log.warn("IOException when trying to get from {}. Reason {}", buildUri(), e.getMessage());
            throw e;
        } catch (InterruptedException e) {
            log.warn("InterruptedException when trying to get from {}. Reason {}", buildUri(), e.getMessage());
            throw e;
        }

        log.debug("Response: {}", response);
        if (response != null && response instanceof HttpResponse) {
            log.debug("Status: {}, Body: {}", response.statusCode(), response.body());

            if (successfulStatusCodes().contains(response.statusCode())) {
                return response.body();
            } else {
                final String errorMessage = "UnsuccesfulStatusCode: Statuscode " + response.statusCode() + " is not in successful set of [" + successfulStatusCodes() + "]. Http body is: " + response.body();
                log.warn(errorMessage);
                throw new UnsuccesfulStatusCodeException(response.statusCode(), errorMessage);
            }
        }
        throw new IOException("Expected HttpResponse");
    }

    @Override
    protected URI buildUri() {
        return baseUri;
    }

    @Override
    protected String buildAuthorization() {
        return "Bearer 12345";
    }

    protected Set<Integer> successfulStatusCodes() {
        return Set.of(200);
    }
}
