package com.hjg.pngj;

/**
 * Generic exception for this library. It's a RuntimeException (unchecked)
 */
public class PngjException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public PngjException(String message, Throwable cause) {
    super(message, cause);
  }

  public PngjException(String message) {
    super(message);
  }

  public PngjException(Throwable cause) {
    super(cause);
  }
}
