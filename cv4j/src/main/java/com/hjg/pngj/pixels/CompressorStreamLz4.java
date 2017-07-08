package com.hjg.pngj.pixels;

import java.util.zip.Deflater;

import com.hjg.pngj.IDatChunkWriter;
import com.hjg.pngj.PngjOutputException;

/**
 * This class uses a quick compressor to get a rough estimate of deflate compression ratio.
 * 
 * This just ignores the outputStream, and the deflater related parameters
 */
public class CompressorStreamLz4 extends CompressorStream {

  private final DeflaterEstimatorLz4 lz4;

  private byte[] buf; // lazily allocated, only if needed
  private final int buffer_size;
  // bufpos=bytes in buffer yet not compressed (bytesIn include this)
  private int inbuf = 0;

  private static final int MAX_BUFFER_SIZE = 16000;

  public CompressorStreamLz4(IDatChunkWriter os, int maxBlockLen, long totalLen) {
    super(os, maxBlockLen, totalLen);
    lz4 = new DeflaterEstimatorLz4();
    buffer_size = (int) (totalLen > MAX_BUFFER_SIZE ? MAX_BUFFER_SIZE : totalLen);
  }

  public CompressorStreamLz4(IDatChunkWriter os, int maxBlockLen, long totalLen, Deflater def) {
    this(os, maxBlockLen, totalLen);// edlfater ignored
  }

  public CompressorStreamLz4(IDatChunkWriter os, int maxBlockLen, long totalLen,
      int deflaterCompLevel, int deflaterStrategy) {
    this(os, maxBlockLen, totalLen); // paramters ignored
  }

  @Override
  public void mywrite(byte[] b, int off, int len) {
    if (len == 0)
      return;
    if (done || closed)
      throw new PngjOutputException("write beyond end of stream");
    bytesIn += len;
    while (len > 0) {
      if (inbuf == 0 && (len >= MAX_BUFFER_SIZE || bytesIn == totalbytes)) {
        // direct copy (buffer might be null or empty)
        bytesOut += lz4.compressEstim(b, off, len);
        len = 0;
      } else {
        if (buf == null)
          buf = new byte[buffer_size];
        int len1 = inbuf + len <= buffer_size ? len : buffer_size - inbuf; // to copy
        if (len1 > 0)
          System.arraycopy(b, off, buf, inbuf, len1);
        inbuf += len1;
        len -= len1;
        off += len1;
        if (inbuf == buffer_size)
          compressFromBuffer();
      }
    }
  }

  void compressFromBuffer() {
    if (inbuf > 0) {
      bytesOut += lz4.compressEstim(buf, 0, inbuf);
      inbuf = 0;
    }
  }

  @Override
  public void done() {
    if (!done) {
      compressFromBuffer();
      done = true;
    }
  }

  @Override
  public void close() {
    done();
    if (!closed) {
      super.close();
      buf = null;
    }
  }

  public void reset() {
    super.reset();
  }

}
