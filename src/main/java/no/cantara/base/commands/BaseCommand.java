package no.cantara.base.commands;

public abstract class BaseCommand {
    public final static int DEFAULT_TIMEOUT = 2000;
    private final String groupKey;
    private final int timeout;

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
}
