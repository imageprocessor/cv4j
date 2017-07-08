package com.hjg.pngj;

import java.io.OutputStream;

import ar.com.hjg.pngj.chunks.ChunkHelper;
import ar.com.hjg.pngj.chunks.ChunkRaw;

/**
 * Outputs a sequence of IDAT-like chunk, that is filled progressively until the max chunk length is reached (or until
 * flush())
 */
public class IDatChunkWriter {

  private static final int MAX_LEN_DEFAULT = 32768; // 32K rather arbitrary - data only

  private final OutputStream outputStream;
  private final int maxChunkLen;
  private byte[] buf;

  private int offset = 0;
  private int availLen;
  private long totalBytesWriten = 0; // including header+crc
  private int chunksWriten = 0;

  public IDatChunkWriter(OutputStream outputStream) {
    this(outputStream, 0);
  }

  public IDatChunkWriter(OutputStream outputStream, int maxChunkLength) {
    this.outputStream = outputStream;
    this.maxChunkLen = maxChunkLength > 0 ? maxChunkLength : MAX_LEN_DEFAULT;
    buf = new byte[maxChunkLen];
    availLen = maxChunkLen - offset;
    postReset();
  }

  public IDatChunkWriter(OutputStream outputStream, byte[] b) {
    this.outputStream = outputStream;
    this.buf = b != null ? b : new byte[MAX_LEN_DEFAULT];
    this.maxChunkLen = b.length;
    availLen = maxChunkLen - offset;
    postReset();
  }

  protected byte[] getChunkId() {
    return ChunkHelper.b_IDAT;
  }

  /**
   * Writes a chhunk if there is more than minLenToWrite.
   * 
   * This is normally called internally, but can be called explicitly to force flush.
   */
  public final void flush() {
    if (offset > 0 && offset >= minLenToWrite()) {
      ChunkRaw c = new ChunkRaw(offset, getChunkId(), false);
      c.data = buf;
      c.writeChunk(outputStream);
      totalBytesWriten += c.len + 12;
      chunksWriten++;
      offset = 0;
      availLen = maxChunkLen;
      postReset();
    }
  }

  public int getOffset() {
    return offset;
  }

  public int getAvailLen() {
    return availLen;
  }

  /** triggers an flush+reset if appropiate */
  public void incrementOffset(int n) {
    offset += n;
    availLen -= n;
    if (availLen < 0)
      throw new PngjOutputException("Anomalous situation");
    if (availLen == 0) {
      flush();
    }
  }

  /**
   * this should rarely be used, the normal way (to avoid double copying) is to get the buffer and write directly to it
   */
  public void write(byte[] b, int o, int len) {
    while (len > 0) {
      int n = len <= availLen ? len : availLen;
      System.arraycopy(b, o, buf, offset, n);
      incrementOffset(n);
      len -= n;
      o += n;
    }
  }

  /** this will be called after reset */
  protected void postReset() {
    // fdat could override this (and minLenToWrite) to add a prefix
  }

  protected int minLenToWrite() {
    return 1;
  }

  public void close() {
    flush();
    offset = 0;
    buf = null;
  }

  /**
   * You can write directly to this buffer, using {@link #getOffset()} and {@link #getAvailLen()}. You should call
   * {@link #incrementOffset(int)} inmediately after.
   * */
  public byte[] getBuf() {
    return buf;
  }

  public long getTotalBytesWriten() {
    return totalBytesWriten;
  }

  public int getChunksWriten() {
    return chunksWriten;
  }
}
