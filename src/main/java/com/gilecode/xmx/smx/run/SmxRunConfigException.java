package com.gilecode.xmx.smx.run;

/**
 * Thrown to indicate that SMX/XMX cannot be started due to configuration issues.
 */
public class SmxRunConfigException extends Exception {

    public SmxRunConfigException() {
    }

    public SmxRunConfigException(String message) {
        super(message);
    }

    public SmxRunConfigException(String message, Throwable cause) {
        super(message, cause);
    }
}
