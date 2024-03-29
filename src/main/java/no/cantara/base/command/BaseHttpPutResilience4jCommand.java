package no.cantara.base.command;

import org.slf4j.Logger;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;

public abstract class BaseHttpPutResilience4jCommand extends BaseResilience4jCommand {
    private static final Logger log = getLogger(BaseHttpPutResilience4jCommand.class);

    private HttpResponse<String> response = null;
    private String body = null;
    private String authorization = null;

    protected BaseHttpPutResilience4jCommand(URI baseUri, String groupKey) {
        this(baseUri, groupKey, null);
    }
    protected BaseHttpPutResilience4jCommand(URI baseUri, String groupKey, Map<String,String> extraHeaders) {
        super(baseUri, groupKey, extraHeaders);
    }

    @Override
    protected HttpResponse<String> run() {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(buildUri())
                .header("Content-Type", "application/json; charset=utf-8")
                .PUT(HttpRequest.BodyPublishers.ofString(getBody()));
        if (getHeadersAsArray().length > 0) {
            builder = builder.headers(getHeadersAsArray());
        }
        httpRequest = builder.build();
        try {
            response =  client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            log.debug("IOException when trying to get from {}. Reason {}", buildUri(), e.getMessage());
        } catch (InterruptedException e) {
            log.debug("Interrupted when trying to get from {}. Reason {}", buildUri(), e.getMessage());
        }

        return response;
    }

    @Override
    protected URI buildUri() {
        return super.buildUri();
    }

    @Override
    protected String buildAuthorization() {
        return authorization;
    }

    void setBearerToken(String bearerToken) {
        this.authorization = "Bearer " + bearerToken;
    }

    @Override
    protected String getBody() {
        return body;
    }

    void setBody(String body) {
        this.body = body;
    }
}
