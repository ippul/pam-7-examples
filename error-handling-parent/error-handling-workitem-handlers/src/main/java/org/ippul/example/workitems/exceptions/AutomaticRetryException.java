package org.ippul.example.workitems.exceptions;

import java.io.Serializable;

public class AutomaticRetryException extends RuntimeException implements Serializable {

    static final long serialVersionUID = 7788954310937030838L;

    public AutomaticRetryException() {
    }

    public AutomaticRetryException(String message) {
        super(message);
    }

    public AutomaticRetryException(String message, Throwable cause) {
        super(message, cause);
    }

    public AutomaticRetryException(Throwable cause) {
        super(cause);
    }

    public AutomaticRetryException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
