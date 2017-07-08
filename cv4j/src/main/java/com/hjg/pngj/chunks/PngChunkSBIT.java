package com.hjg.pngj.chunks;

import com.hjg.pngj.ImageInfo;
import com.hjg.pngj.PngHelperInternal;
import com.hjg.pngj.PngjException;

/**
 * sBIT chunk.
 * <p>
 * see http://www.w3.org/TR/PNG/#11sBIT
 * <p>
 * this chunk structure depends on the image type
 */
public class PngChunkSBIT extends PngChunkSingle {
  public final static String ID = ChunkHelper.sBIT;
  // http://www.w3.org/TR/PNG/#11sBIT

  // significant bits
  private int graysb, alphasb;
  private int redsb, greensb, bluesb;

  public PngChunkSBIT(ImageInfo info) {
    super(ID, info);
  }

  @Override
  public ChunkOrderingConstraint getOrderingConstraint() {
    return ChunkOrderingConstraint.BEFORE_PLTE_AND_IDAT;
  }

  private int getCLen() {
    int len = imgInfo.greyscale ? 1 : 3;
    if (imgInfo.alpha)
      len += 1;
    return len;
  }

  @Override
  public void parseFromRaw(ChunkRaw c) {
    if (c.len != getCLen())
      throw new PngjException("bad chunk length " + c);
    if (imgInfo.greyscale) {
      graysb = PngHelperInternal.readInt1fromByte(c.data, 0);
      if (imgInfo.alpha)
        alphasb = PngHelperInternal.readInt1fromByte(c.data, 1);
    } else {
      redsb = PngHelperInternal.readInt1fromByte(c.data, 0);
      greensb = PngHelperInternal.readInt1fromByte(c.data, 1);
      bluesb = PngHelperInternal.readInt1fromByte(c.data, 2);
      if (imgInfo.alpha)
        alphasb = PngHelperInternal.readInt1fromByte(c.data, 3);
    }
  }

  @Override
  public ChunkRaw createRawChunk() {
    ChunkRaw c = null;
    c = createEmptyChunk(getCLen(), true);
    if (imgInfo.greyscale) {
      c.data[0] = (byte) graysb;
      if (imgInfo.alpha)
        c.data[1] = (byte) alphasb;
    } else {
      c.data[0] = (byte) redsb;
      c.data[1] = (byte) greensb;
      c.data[2] = (byte) bluesb;
      if (imgInfo.alpha)
        c.data[3] = (byte) alphasb;
    }
    return c;
  }

  public void setGraysb(int gray) {
    if (!imgInfo.greyscale)
      throw new PngjException("only greyscale images support this");
    graysb = gray;
  }

  public int getGraysb() {
    if (!imgInfo.greyscale)
      throw new PngjException("only greyscale images support this");
    return graysb;
  }

  public void setAlphasb(int a) {
    if (!imgInfo.alpha)
      throw new PngjException("only images with alpha support this");
    alphasb = a;
  }

  public int getAlphasb() {
    if (!imgInfo.alpha)
      throw new PngjException("only images with alpha support this");
    return alphasb;
  }

  /**
   * Set rgb values
   * 
   */
  public void setRGB(int r, int g, int b) {
    if (imgInfo.greyscale || imgInfo.indexed)
      throw new PngjException("only rgb or rgba images support this");
    redsb = r;
    greensb = g;
    bluesb = b;
  }

  public int[] getRGB() {
    if (imgInfo.greyscale || imgInfo.indexed)
      throw new PngjException("only rgb or rgba images support this");
    return new int[] {redsb, greensb, bluesb};
  }
}
