package com.hjg.pngj;

/**
 * Exception for anomalous internal problems (sort of asserts) that point to some issue with the library
 * 
 * @author Hernan J Gonzalez
 * 
 */
public class PngjExceptionInternal extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public PngjExceptionInternal(String message, Throwable cause) {
    super(message, cause);
  }

  public PngjExceptionInternal(String message) {
    super(message);
  }

  public PngjExceptionInternal(Throwable cause) {
    super(cause);
  }
}
