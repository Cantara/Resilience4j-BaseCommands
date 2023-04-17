package no.cantara.base.command.commands;

import no.cantara.base.command.BaseHttpGetResilience4jCommand;

import java.net.URI;
import java.util.Map;
import java.util.Set;

public class InheritGetCommandForTesting extends BaseHttpGetResilience4jCommand {
    private final static String PATH = "/inherit-get-command-for-testing/";
    private final static String GROUP_KEY = "GetCommand";
    private String id;
    private String bearerToken;

    protected InheritGetCommandForTesting(URI baseUri, Map<String,String> extraHeaders) {
        super(baseUri, GROUP_KEY, extraHeaders);
    }

    public InheritGetCommandForTesting(URI baseUri, String bearerToken, String id) {
        this(baseUri, null);
        this.bearerToken = bearerToken;
        this.id = id;
    }

    public InheritGetCommandForTesting(URI baseUri, String bearerToken, String id, Map<String,String> extraHeaders) {
        this(baseUri, extraHeaders);
        this.bearerToken = bearerToken;
        this.id = id;
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
    public Set<Integer> successfulStatusCodes() {
        return Set.of(200,204);
    }
}
