package com.hjg.pngj.chunks;

import com.hjg.pngj.ImageInfo;
import com.hjg.pngj.PngjException;

/**
 * PLTE chunk.
 * <p>
 * see http://www.w3.org/TR/PNG/#11PLTE
 * <p>
 * Critical chunk
 */
public class PngChunkPLTE extends PngChunkSingle {
  public final static String ID = ChunkHelper.PLTE;

  // http://www.w3.org/TR/PNG/#11PLTE
  private int nentries = 0;
  /**
   * RGB8 packed in one integer
   */
  private int[] entries;

  public PngChunkPLTE(ImageInfo info) {
    super(ID, info);
  }

  @Override
  public ChunkOrderingConstraint getOrderingConstraint() {
    return ChunkOrderingConstraint.NA;
  }

  @Override
  public ChunkRaw createRawChunk() {
    int len = 3 * nentries;
    int[] rgb = new int[3];
    ChunkRaw c = createEmptyChunk(len, true);
    for (int n = 0, i = 0; n < nentries; n++) {
      getEntryRgb(n, rgb);
      c.data[i++] = (byte) rgb[0];
      c.data[i++] = (byte) rgb[1];
      c.data[i++] = (byte) rgb[2];
    }
    return c;
  }

  @Override
  public void parseFromRaw(ChunkRaw chunk) {
    setNentries(chunk.len / 3);
    for (int n = 0, i = 0; n < nentries; n++) {
      setEntry(n, (int) (chunk.data[i++] & 0xff), (int) (chunk.data[i++] & 0xff),
          (int) (chunk.data[i++] & 0xff));
    }
  }

  public void setNentries(int n) {
    nentries = n;
    if (nentries < 1 || nentries > 256)
      throw new PngjException("invalid pallette - nentries=" + nentries);
    if (entries == null || entries.length != nentries) { // alloc
      entries = new int[nentries];
    }
  }

  public int getNentries() {
    return nentries;
  }

  public void setEntry(int n, int r, int g, int b) {
    entries[n] = ((r << 16) | (g << 8) | b);
  }

  public int getEntry(int n) {
    return entries[n];
  }

  public void getEntryRgb(int n, int[] rgb) {
    getEntryRgb(n, rgb, 0);
  }

  public void getEntryRgb(int n, int[] rgb, int offset) {
    int v = entries[n];
    rgb[offset + 0] = ((v & 0xff0000) >> 16);
    rgb[offset + 1] = ((v & 0xff00) >> 8);
    rgb[offset + 2] = (v & 0xff);
  }

  public int minBitDepth() {
    if (nentries <= 2)
      return 1;
    else if (nentries <= 4)
      return 2;
    else if (nentries <= 16)
      return 4;
    else
      return 8;
  }

}
