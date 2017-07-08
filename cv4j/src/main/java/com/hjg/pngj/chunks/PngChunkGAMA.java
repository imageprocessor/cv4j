package com.hjg.pngj.chunks;

import com.hjg.pngj.ImageInfo;
import com.hjg.pngj.PngHelperInternal;
import com.hjg.pngj.PngjException;

/**
 * gAMA chunk.
 * <p>
 * see http://www.w3.org/TR/PNG/#11gAMA
 */
public class PngChunkGAMA extends PngChunkSingle {
  public final static String ID = ChunkHelper.gAMA;

  // http://www.w3.org/TR/PNG/#11gAMA
  private double gamma;

  public PngChunkGAMA(ImageInfo info) {
    super(ID, info);
  }

  @Override
  public ChunkOrderingConstraint getOrderingConstraint() {
    return ChunkOrderingConstraint.BEFORE_PLTE_AND_IDAT;
  }

  @Override
  public ChunkRaw createRawChunk() {
    ChunkRaw c = createEmptyChunk(4, true);
    int g = (int) (gamma * 100000 + 0.5);
    PngHelperInternal.writeInt4tobytes(g, c.data, 0);
    return c;
  }

  @Override
  public void parseFromRaw(ChunkRaw chunk) {
    if (chunk.len != 4)
      throw new PngjException("bad chunk " + chunk);
    int g = PngHelperInternal.readInt4fromBytes(chunk.data, 0);
    gamma = ((double) g) / 100000.0;
  }

  public double getGamma() {
    return gamma;
  }

  public void setGamma(double gamma) {
    this.gamma = gamma;
  }

}
