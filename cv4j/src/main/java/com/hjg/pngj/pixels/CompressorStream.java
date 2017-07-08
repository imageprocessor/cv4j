package com.hjg.pngj.pixels;

import java.io.OutputStream;

import com.hjg.pngj.IDatChunkWriter;

/**
 * This is an OutputStream that compresses (via Deflater or a deflater-like object), and optionally passes the
 * compressed stream to another output stream.
 * 
 * It allows to compute in/out/ratio stats.
 * 
 * It works as a stream (similar to DeflaterOutputStream), but it's peculiar in that it expects that each writes has a
 * fixed length (other lenghts are accepted, but it's less efficient) and that the total amount of bytes is known (so it
 * can close itself, but it can also be closed on demand) In PNGJ use, the block is typically a row (including filter
 * byte).
 * 
 * We use this to do the real compression (with Deflate) but also to compute tentative estimators
 * 
 * If not closed, it can be recicled via reset()
 * 
 * 
 */
public abstract class CompressorStream extends OutputStream {

  protected IDatChunkWriter idatChunkWriter;
  public final int blockLen;
  public final long totalbytes;

  boolean closed = false;
  protected boolean done = false;
  protected long bytesIn = 0;
  protected long bytesOut = 0;
  protected int block = -1;

  /** optionally stores the first byte of each block (row) */
  private byte[] firstBytes;
  protected boolean storeFirstByte = false;

  /**
   * 
   * @param idatCw Can be null (if we are only interested in compute compression ratio)
   * @param blockLen Estimated maximum block length. If unknown, use -1.
   * @param totalbytes Expected total bytes to be fed. If unknown, use -1.
   */
  public CompressorStream(IDatChunkWriter idatCw, int blockLen, long totalbytes) {
    this.idatChunkWriter = idatCw;
    if (blockLen < 0)
      blockLen = 4096;
    if (totalbytes < 0)
      totalbytes = Long.MAX_VALUE;
    if (blockLen < 1 || totalbytes < 1)
      throw new RuntimeException(" maxBlockLen or totalLen invalid");
    this.blockLen = blockLen;
    this.totalbytes = totalbytes;
  }

  /** Releases resources. Idempotent. */
  @Override
  public void close() {
    done();
    if(idatChunkWriter!=null) idatChunkWriter.close();
    closed = true;
  }

  /**
   * Will be called automatically when the number of bytes reaches the total expected Can be also be called from
   * outside. This should set the flag done=true
   */
  public abstract void done();

  @Override
  public final void write(byte[] data) {
    write(data, 0, data.length);
  }

  @Override
  public final void write(byte[] data, int off, int len) {
    block++;
    if (len <= blockLen) { // normal case
      mywrite(data, off, len);
      if (storeFirstByte && block < firstBytes.length) {
        firstBytes[block] = data[off]; // only makes sense in this case
      }
    } else {
      while (len > 0) {
        mywrite(data, off, blockLen);
        off += blockLen;
        len -= blockLen;
      }
    }
    if (bytesIn >= totalbytes)
      done();

  }

  /**
   * same as write, but guarantedd to not exceed blockLen The implementation should update bytesOut and bytesInt but not
   * check for totalBytes
   */
  public abstract void mywrite(byte[] data, int off, int len);


  /**
   * compressed/raw. This should be called only when done
   */
  public final double getCompressionRatio() {
    return bytesOut == 0 ? 1.0 : bytesOut / (double) bytesIn;
  }

  /**
   * raw (input) bytes. This should be called only when done
   */
  public final long getBytesRaw() {
    return bytesIn;
  }

  /**
   * compressed (out) bytes. This should be called only when done
   */
  public final long getBytesCompressed() {
    return bytesOut;
  }

  public boolean isClosed() {
    return closed;
  }

  public boolean isDone() {
    return done;
  }

  public byte[] getFirstBytes() {
    return firstBytes;
  }

  public void setStoreFirstByte(boolean storeFirstByte, int nblocks) {
    this.storeFirstByte = storeFirstByte;
    if (this.storeFirstByte) {
      if (firstBytes == null || firstBytes.length < nblocks)
        firstBytes = new byte[nblocks];
    } else
      firstBytes = null;
  }

  public void reset() {
    done();
    bytesIn = 0;
    bytesOut = 0;
    block = -1;
    done = false;
  }

  @Override
  public void write(int i) { // should not be used
    write(new byte[] {(byte) i});
  }


}
