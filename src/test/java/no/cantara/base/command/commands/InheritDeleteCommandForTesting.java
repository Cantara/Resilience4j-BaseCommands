package no.cantara.base.command.commands;

import no.cantara.base.command.BaseHttpDeleteResilience4jCommand;
import no.cantara.base.command.BaseHttpGetResilience4jCommand;

import java.net.URI;
import java.util.Map;
import java.util.Set;

public class InheritDeleteCommandForTesting extends BaseHttpDeleteResilience4jCommand {
    private final static String PATH = "/inherit-delete-command-for-testing/";
    private final static String GROUP_KEY = "DeleteCommand";
    private String id;
    private String bearerToken;

    protected InheritDeleteCommandForTesting(URI baseUri, Map<String,String> extraHeaders) {
        super(baseUri, GROUP_KEY, extraHeaders);
    }

    public InheritDeleteCommandForTesting(URI baseUri, String bearerToken, String id) {
        this(baseUri, null);
        this.bearerToken = bearerToken;
        this.id = id;
    }

    public InheritDeleteCommandForTesting(URI baseUri, String bearerToken, String id, Map<String,String> extraHeaders) {
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
}
