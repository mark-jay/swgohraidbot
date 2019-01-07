package org.markjay.exceptions;

import java.io.IOException;

/**
 * @author <a href="mailto:mark.jay.mk@gmail.com">mark jay</a>
 * @since 1/1/19 11:29 PM
 */
public class RaidInfoExtractionException extends Exception {
    public RaidInfoExtractionException() {
    }

    public RaidInfoExtractionException(String message) {
        super(message);
    }

    public RaidInfoExtractionException(String message, Throwable cause) {
        super(message, cause);
    }

    public RaidInfoExtractionException(Throwable cause) {
        super(cause);
    }

    public RaidInfoExtractionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
