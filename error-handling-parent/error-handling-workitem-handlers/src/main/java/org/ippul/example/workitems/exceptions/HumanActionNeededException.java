package org.ippul.example.workitems.exceptions;

import java.io.Serializable;

public class HumanActionNeededException extends RuntimeException implements Serializable {

    static final long serialVersionUID = -2401237225740132928L;

    public HumanActionNeededException() {
    }

    public HumanActionNeededException(String message) {
        super(message);
    }

    public HumanActionNeededException(String message, Throwable cause) {
        super(message, cause);
    }

    public HumanActionNeededException(Throwable cause) {
        super(cause);
    }

    public HumanActionNeededException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
