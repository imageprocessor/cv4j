package com.hjg.pngj.chunks;

import com.hjg.pngj.ImageInfo;
import com.hjg.pngj.PngHelperInternal;
import com.hjg.pngj.PngjException;

/**
 * tRNS chunk.
 * <p>
 * see http://www.w3.org/TR/PNG/#11tRNS
 * <p>
 * this chunk structure depends on the image type
 */
public class PngChunkTRNS extends PngChunkSingle {
  public final static String ID = ChunkHelper.tRNS;

  // http://www.w3.org/TR/PNG/#11tRNS

  // only one of these is meaningful, depending on the image type
  private int gray;
  private int red, green, blue;
  private int[] paletteAlpha = new int[] {};

  public PngChunkTRNS(ImageInfo info) {
    super(ID, info);
  }

  @Override
  public ChunkOrderingConstraint getOrderingConstraint() {
    return ChunkOrderingConstraint.AFTER_PLTE_BEFORE_IDAT;
  }

  @Override
  public ChunkRaw createRawChunk() {
    ChunkRaw c = null;
    if (imgInfo.greyscale) {
      c = createEmptyChunk(2, true);
      PngHelperInternal.writeInt2tobytes(gray, c.data, 0);
    } else if (imgInfo.indexed) {
      c = createEmptyChunk(paletteAlpha.length, true);
      for (int n = 0; n < c.len; n++) {
        c.data[n] = (byte) paletteAlpha[n];
      }
    } else {
      c = createEmptyChunk(6, true);
      PngHelperInternal.writeInt2tobytes(red, c.data, 0);
      PngHelperInternal.writeInt2tobytes(green, c.data, 0);
      PngHelperInternal.writeInt2tobytes(blue, c.data, 0);
    }
    return c;
  }

  @Override
  public void parseFromRaw(ChunkRaw c) {
    if (imgInfo.greyscale) {
      gray = PngHelperInternal.readInt2fromBytes(c.data, 0);
    } else if (imgInfo.indexed) {
      int nentries = c.data.length;
      paletteAlpha = new int[nentries];
      for (int n = 0; n < nentries; n++) {
        paletteAlpha[n] = (int) (c.data[n] & 0xff);
      }
    } else {
      red = PngHelperInternal.readInt2fromBytes(c.data, 0);
      green = PngHelperInternal.readInt2fromBytes(c.data, 2);
      blue = PngHelperInternal.readInt2fromBytes(c.data, 4);
    }
  }

  /**
   * Set rgb values
   * 
   */
  public void setRGB(int r, int g, int b) {
    if (imgInfo.greyscale || imgInfo.indexed)
      throw new PngjException("only rgb or rgba images support this");
    red = r;
    green = g;
    blue = b;
  }

  public int[] getRGB() {
    if (imgInfo.greyscale || imgInfo.indexed)
      throw new PngjException("only rgb or rgba images support this");
    return new int[] {red, green, blue};
  }

  public int getRGB888() {
    if (imgInfo.greyscale || imgInfo.indexed)
      throw new PngjException("only rgb or rgba images support this");
    return (red << 16) | (green << 8) | blue;
  }

  public void setGray(int g) {
    if (!imgInfo.greyscale)
      throw new PngjException("only grayscale images support this");
    gray = g;
  }

  public int getGray() {
    if (!imgInfo.greyscale)
      throw new PngjException("only grayscale images support this");
    return gray;
  }

  /**
   * Sets the length of the palette alpha. This should be followed by #setNentriesPalAlpha
   * 
   * @param idx index inside the table
   * @param val alpha value (0-255)
   */
  public void setEntryPalAlpha(int idx, int val) {
    paletteAlpha[idx] = val;
  }

  public void setNentriesPalAlpha(int len) {
    paletteAlpha = new int[len];
  }

  /**
   * WARNING: non deep copy. See also {@link #setNentriesPalAlpha(int)} {@link #setEntryPalAlpha(int, int)}
   */
  public void setPalAlpha(int[] palAlpha) {
    if (!imgInfo.indexed)
      throw new PngjException("only indexed images support this");
    paletteAlpha = palAlpha;
  }

  /**
   * WARNING: non deep copy
   */
  public int[] getPalletteAlpha() {
    return paletteAlpha;
  }

  /**
   * to use when only one pallete index is set as totally transparent
   */
  public void setIndexEntryAsTransparent(int palAlphaIndex) {
    if (!imgInfo.indexed)
      throw new PngjException("only indexed images support this");
    paletteAlpha = new int[] {palAlphaIndex + 1};
    for (int i = 0; i < palAlphaIndex; i++)
      paletteAlpha[i] = 255;
    paletteAlpha[palAlphaIndex] = 0;
  }


}
