package no.cantara.base.command.commands;

import no.cantara.base.command.BaseHttpGetResilience4jCommand;
import no.cantara.base.command.BaseHttpPutResilience4jCommand;

import java.net.URI;
import java.util.Map;
import java.util.Set;

public class InheritPutCommandForTesting extends BaseHttpPutResilience4jCommand {
    private final static String PATH = "/inherit-put-command-for-testing/";
    private final static String GROUP_KEY = "PutCommand";
    private String body;
    private String id;
    private String bearerToken;

    protected InheritPutCommandForTesting(URI baseUri, Map<String,String> extraHeaders) {
        super(baseUri, GROUP_KEY, extraHeaders);
    }

    public InheritPutCommandForTesting(URI baseUri, String bearerToken, String id, String jsonBody) {
        this(baseUri, null);
        this.bearerToken = bearerToken;
        this.id = id;
        this.body = jsonBody;
    }

    public InheritPutCommandForTesting(URI baseUri, String bearerToken, String id, String jsonBody, Map<String,String> extraHeaders) {
        this(baseUri, extraHeaders);
        this.bearerToken = bearerToken;
        this.id = id;
        this.body = jsonBody;
    }

    @Override
    protected URI buildUri() {
        return URI.create(getBaseUri().toString() + PATH + id);
    }

    @Override
    protected String buildAuthorization() {
        return "Bearer " + this.bearerToken;
    }

    @Override
    protected String getBody() {
        return this.body;
    }
}
