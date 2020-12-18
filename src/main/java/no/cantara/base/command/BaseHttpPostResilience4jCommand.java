package no.cantara.base.command;

import org.slf4j.Logger;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.slf4j.LoggerFactory.getLogger;

public class BaseHttpPostResilience4jCommand extends BaseResilience4jCommand {
    private static final Logger log = getLogger(BaseHttpPostResilience4jCommand.class);

    private HttpResponse<String> response = null;
    private String body = null;
    private String authorization = null;

    protected BaseHttpPostResilience4jCommand(URI baseUri, String groupKey) {
        super(baseUri, groupKey);
    }

    @Override
    protected HttpResponse<String> run() {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(buildUri())
                .header("Content-Type", "application/json; charset=utf-8")
                .POST(HttpRequest.BodyPublishers.ofString(getBody()));
        if (buildAuthorization() != null) {
            builder = builder.header("Authorization", buildAuthorization());
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
