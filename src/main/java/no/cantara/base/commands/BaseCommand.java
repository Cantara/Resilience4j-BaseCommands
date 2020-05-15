package no.cantara.base.commands;

public abstract class BaseCommand {
    public final static int DEFAULT_TIMEOUT = 2000;
    private final String groupKey;
    private final int timeout;

    protected int mockedStatusCode;
    protected String mockedResponseData;
    protected boolean isMocked = false;


    protected BaseCommand(String groupKey) {
        this(groupKey, DEFAULT_TIMEOUT);
    }

    protected BaseCommand(String groupKey, int timeout) {
        this.groupKey = groupKey;
        this.timeout = timeout;
    }

    public String getGroupKey() {
        return groupKey;
    }

    public int getTimeout() {
        return timeout;
    }

    //    @SuppressWarnings("unchecked")
    public <T> T withMockedResponse(int i, String jsonMockedResponse) {
        isMocked = true;
        mockedStatusCode = i;
        mockedResponseData = jsonMockedResponse;
        return (T) this;
    }

    ;
}
