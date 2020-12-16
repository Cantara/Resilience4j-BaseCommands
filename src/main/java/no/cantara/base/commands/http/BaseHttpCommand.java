package no.cantara.base.commands.http;


import no.cantara.base.commands.BaseCommand;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.function.Function;

public abstract class BaseHttpCommand<HttpResponse> extends BaseCommand {

    protected HttpClient client;
    protected HttpRequest httpRequest;
    protected Function<HttpRequest, java.net.http.HttpResponse> decorated;

    private final URI baseUri;

    protected BaseHttpCommand(URI baseUri, String groupKey) {
        this(baseUri, groupKey, BaseCommand.DEFAULT_TIMEOUT);
    }

    protected BaseHttpCommand(URI baseUri, String groupKey, int timeout) {
        super(groupKey,timeout);
        this.baseUri = baseUri;
    }
    protected URI getBaseUri() {
        return baseUri;
    }

    protected abstract HttpResponse run();

    protected abstract URI buildUri();

    protected abstract String buildAuthorization();
}
