package com.hjg.pngj;

/**
 * Exception thrown by bad CRC check
 */
public class PngjBadCrcException extends PngjInputException {
  private static final long serialVersionUID = 1L;

  public PngjBadCrcException(String message, Throwable cause) {
    super(message, cause);
  }

  public PngjBadCrcException(String message) {
    super(message);
  }

  public PngjBadCrcException(Throwable cause) {
    super(cause);
  }
}
