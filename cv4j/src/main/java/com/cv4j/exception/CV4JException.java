package com.cv4j.exception;

/**
 * Created by Tony Shen on 2017/3/5.
 */

public class CV4JException extends RuntimeException {

    private static final long serialVersionUID = -2565764903880816387L;

    public CV4JException(String message) {
        super(message);
    }

    public CV4JException(Throwable throwable) {
        super(throwable);
    }

    public CV4JException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
