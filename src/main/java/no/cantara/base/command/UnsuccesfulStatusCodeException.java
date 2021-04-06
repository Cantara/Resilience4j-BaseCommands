package no.cantara.base.command;

public class UnsuccesfulStatusCodeException extends Exception {
    private int statusCode;

    public UnsuccesfulStatusCodeException(int statusCode, String message) {
        super(message);

        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
