package no.cantara.base.command;

import no.cantara.base.commands.http.BaseHttpCommand;

import java.net.URI;
import java.net.http.HttpResponse;

public class BaseHttpPostResilience4jCommand extends BaseHttpCommand {
    protected BaseHttpPostResilience4jCommand(URI baseUri, String groupKey) {
        super(baseUri, groupKey);
    }

    @Override
    protected HttpResponse<String> run() {
        return null;
    }

    @Override
    protected URI buildUri() {
        return null;
    }

    @Override
    protected String buildAuthorization() {
        return null;
    }
}
