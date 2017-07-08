package com.hjg.pngj;

import java.io.IOException;
import java.io.InputStream;

/**
 * Reads bytes from an input stream, and feeds a IBytesConsumer.
 */
public class BufferedStreamFeeder {

  private InputStream stream;
  private byte[] buf;
  private int pendinglen; // bytes read and stored in buf that have not yet still been fed to
                          // IBytesConsumer
  private int offset;
  private boolean eof = false;
  private boolean closeStream = true;
  private boolean failIfNoFeed = false;

  private static final int DEFAULTSIZE = 8192;

  /** By default, the stream will be closed on close() */
  public BufferedStreamFeeder(InputStream is) {
    this(is, DEFAULTSIZE);
  }

  public BufferedStreamFeeder(InputStream is, int bufsize) {
    this.stream = is;
    buf = new byte[bufsize < 1 ? DEFAULTSIZE : bufsize];
  }

  /**
   * Returns inputstream
   * 
   * @return Input Stream from which bytes are read
   */
  public InputStream getStream() {
    return stream;
  }

  /**
   * Feeds bytes to the consumer <br>
   * Returns bytes actually consumed <br>
   * This should return 0 only if the stream is EOF or the consumer is done
   */
  public int feed(IBytesConsumer consumer) {
    return feed(consumer, Integer.MAX_VALUE);
  }

  /**
   * Feeds the consumer (with at most maxbytes) <br>
   * Returns 0 only if the stream is EOF (or maxbytes=0). Returns negative is the consumer is done.<br>
   * It can return less than maxbytes (that doesn't mean that the consumer or the input stream is done)
   */
  public int feed(IBytesConsumer consumer, int maxbytes) {
    if (pendinglen == 0)
      refillBuffer();
    int tofeed = maxbytes >= 0 && maxbytes < pendinglen ? maxbytes : pendinglen;
    int n = 0;
    if (tofeed > 0) {
      n = consumer.consume(buf, offset, tofeed);
      if (n > 0) {
        offset += n;
        pendinglen -= n;
      }
    }
    if (n < 1 && failIfNoFeed)
      throw new PngjInputException("Failed to feed bytes (premature ending?)");
    return n;
  }


  /**
   * Feeds as much bytes as it can to the consumer, in a loop. <br>
   * Returns bytes actually consumed <br>
   * This will stop when either the input stream is eof, or when the consumer refuses to eat more bytes. The caller can
   * distinguish both cases by calling {@link #hasMoreToFeed()}
   */
  public long feedAll(IBytesConsumer consumer) {
    long n = 0;
    while (hasMoreToFeed()) {
      int n1 = feed(consumer);
      if (n1 < 1)
        break;
      n += n1;
    }
    return n;
  }


  /**
   * Feeds exactly nbytes, retrying if necessary
   * 
   * @param consumer Consumer
   * @param nbytes Number of bytes
   * @return true if success, false otherwise (EOF on stream, or consumer is done)
   */
  public boolean feedFixed(IBytesConsumer consumer, int nbytes) {
    int remain = nbytes;
    while (remain > 0) {
      int n = feed(consumer, remain);
      if (n < 1)
        return false;
      remain -= n;
    }
    return true;
  }

  /**
   * If there are not pending bytes to be consumed tries to fill the buffer with bytes from the stream.
   */
  protected void refillBuffer() {
    if (pendinglen > 0 || eof)
      return; // only if not pending data
    try {
      // try to read
      offset = 0;
      pendinglen = stream.read(buf);
      if (pendinglen < 0) {
        close();
        return;
      } else
        return;
    } catch (IOException e) {
      throw new PngjInputException(e);
    }
  }

  /**
   * Returuns true if we have more data to fed the consumer. This internally tries to grabs more bytes from the stream
   * if necessary
   */
  public boolean hasMoreToFeed() {
    if (eof)
      return pendinglen > 0;
    else
      refillBuffer();
    return pendinglen > 0;
  }

  /**
   * @param closeStream If true, the underlying stream will be closed on when close() is called
   */
  public void setCloseStream(boolean closeStream) {
    this.closeStream = closeStream;
  }

  /**
   * Closes this object.
   * 
   * Sets EOF=true, and closes the stream if <tt>closeStream</tt> is true
   * 
   * This can be called internally, or from outside.
   * 
   * Idempotent, secure, never throws exception.
   **/
  public void close() {
    eof = true;
    buf = null;
    pendinglen = 0;
    offset = 0;
    if (stream != null && closeStream) {
      try {
        stream.close();
      } catch (Exception e) {
        // PngHelperInternal.LOGGER.log(Level.WARNING, "Exception closing stream", e);
      }
    }
    stream = null;
  }

  /**
   * Sets a new underlying inputstream. This allows to reuse this object. The old underlying is not closed and the state
   * is not reset (you should call close() previously if you want that)
   * 
   * @param is
   */
  public void setInputStream(InputStream is) { // to reuse this object
    this.stream = is;
    eof = false;
  }

  /**
   * @return EOF on stream, or close() was called
   */
  public boolean isEof() {
    return eof;
  }

  /**
   * If this flag is set (default: false), any call to feed() that returns zero (no byte feed) will throw an exception.
   * This is useful to be sure of avoid infinite loops in some scenarios.
   * 
   * @param failIfNoFeed
   */
  public void setFailIfNoFeed(boolean failIfNoFeed) {
    this.failIfNoFeed = failIfNoFeed;
  }
}
