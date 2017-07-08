package com.hjg.pngj;

/**
 * Bytes consumer. Objects implementing this interface can act as bytes consumers, that are "fed" with bytes.
 */
public interface IBytesConsumer {
  /**
   * Eats some bytes, at most len.
   * <p>
   * Returns bytes actually consumed. A negative return value signals that the consumer is done, it refuses to eat more
   * bytes. This should only return 0 if len is 0
   */
  int consume(byte[] buf, int offset, int len);
}
