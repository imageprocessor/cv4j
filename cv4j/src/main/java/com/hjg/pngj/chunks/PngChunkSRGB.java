package com.hjg.pngj.chunks;

import com.hjg.pngj.ImageInfo;
import com.hjg.pngj.PngHelperInternal;
import com.hjg.pngj.PngjException;

/**
 * sRGB chunk.
 * <p>
 * see http://www.w3.org/TR/PNG/#11sRGB
 */
public class PngChunkSRGB extends PngChunkSingle {
  public final static String ID = ChunkHelper.sRGB;

  // http://www.w3.org/TR/PNG/#11sRGB

  public static final int RENDER_INTENT_Perceptual = 0;
  public static final int RENDER_INTENT_Relative_colorimetric = 1;
  public static final int RENDER_INTENT_Saturation = 2;
  public static final int RENDER_INTENT_Absolute_colorimetric = 3;

  private int intent;

  public PngChunkSRGB(ImageInfo info) {
    super(ID, info);
  }

  @Override
  public ChunkOrderingConstraint getOrderingConstraint() {
    return ChunkOrderingConstraint.BEFORE_PLTE_AND_IDAT;
  }

  @Override
  public void parseFromRaw(ChunkRaw c) {
    if (c.len != 1)
      throw new PngjException("bad chunk length " + c);
    intent = PngHelperInternal.readInt1fromByte(c.data, 0);
  }

  @Override
  public ChunkRaw createRawChunk() {
    ChunkRaw c = null;
    c = createEmptyChunk(1, true);
    c.data[0] = (byte) intent;
    return c;
  }

  public int getIntent() {
    return intent;
  }

  public void setIntent(int intent) {
    this.intent = intent;
  }
}
