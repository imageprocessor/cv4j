package com.hjg.pngj.pixels;

import java.util.zip.Deflater;

import com.hjg.pngj.IDatChunkWriter;
import com.hjg.pngj.PngjOutputException;

/**
 * CompressorStream backed by a Deflater.
 * 
 * Note that the Deflater is not disposed after done, you should either recycle this with reset() or dispose it with
 * close()
 * 
 */
public class CompressorStreamDeflater extends CompressorStream {

  protected Deflater deflater;
  protected byte[] buf1; // temporary storage of compressed bytes: only used if idatWriter is null
  protected boolean deflaterIsOwn = true;

  /** if a deflater is passed, it must be already reset. It will not be released on close */
  public CompressorStreamDeflater(IDatChunkWriter idatCw, int maxBlockLen, long totalLen,
      Deflater def) {
    super(idatCw, maxBlockLen, totalLen);
    this.deflater = def == null ? new Deflater() : def;
    this.deflaterIsOwn = def == null;
  }

  public CompressorStreamDeflater(IDatChunkWriter idatCw, int maxBlockLen, long totalLen) {
    this(idatCw, maxBlockLen, totalLen, null);
  }

  public CompressorStreamDeflater(IDatChunkWriter idatCw, int maxBlockLen, long totalLen,
      int deflaterCompLevel, int deflaterStrategy) {
    this(idatCw, maxBlockLen, totalLen, new Deflater(deflaterCompLevel));
    this.deflaterIsOwn = true;
    deflater.setStrategy(deflaterStrategy);
  }

  @Override
  public void mywrite(byte[] data, int off, final int len) {
    if (deflater.finished() || done || closed)
      throw new PngjOutputException("write beyond end of stream");
    deflater.setInput(data, off, len);
    bytesIn += len;
    while (!deflater.needsInput())
      deflate();
  }

  protected void deflate() {
    byte[] buf;
    int off, n;
    if (idatChunkWriter != null) {
      buf = idatChunkWriter.getBuf();
      off = idatChunkWriter.getOffset();
      n = idatChunkWriter.getAvailLen();
    } else {
      if (buf1 == null)
        buf1 = new byte[4096];
      buf = buf1;
      off = 0;
      n = buf1.length;
    }
    int len = deflater.deflate(buf, off, n);
    if (len > 0) {
      if (idatChunkWriter != null)
        idatChunkWriter.incrementOffset(len);
      bytesOut += len;
    }
  }

  /** automatically called when done */
  @Override
  public void done() {
    if (done)
      return;
    if (!deflater.finished()) {
      deflater.finish();
      while (!deflater.finished())
        deflate();
    }
    done = true;
    if (idatChunkWriter != null)
      idatChunkWriter.close();
  }

  public void close() {
    done();
    try {
      if (deflaterIsOwn) {
        deflater.end();
      }
    } catch (Exception e) {
    }
    super.close();
  }

  @Override
  public void reset() {
    deflater.reset();
    super.reset();
  }

}
