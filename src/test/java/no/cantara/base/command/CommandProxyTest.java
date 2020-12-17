package no.cantara.base.command;

import org.junit.Before;
import org.junit.Test;

import java.net.URI;

import static org.junit.Assert.*;

public class CommandProxyTest {

    private CommandProxy commandProxy;

    @Before
    public void setUp() {
        commandProxy = new CommandProxy();
    }

    @Test
    public void getCommand() {
//        BaseResilience4jCommand backendService = new BaseHttpGetResilience4jCommand(URI.create(baseUrl() + path), "test", 50);
        BaseResilience4jCommand getCommand = new BaseHttpGetResilience4jCommand(URI.create("https://www.vg.no/"), "test", 50);
        Object result = commandProxy.run(getCommand);
        assertNotNull(result);
    }
}