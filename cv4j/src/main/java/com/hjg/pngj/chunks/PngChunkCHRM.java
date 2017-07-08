package com.hjg.pngj.chunks;

import com.hjg.pngj.ImageInfo;
import com.hjg.pngj.PngHelperInternal;
import com.hjg.pngj.PngjException;

/**
 * cHRM chunk.
 * <p>
 * see http://www.w3.org/TR/PNG/#11cHRM
 */
public class PngChunkCHRM extends PngChunkSingle {
  public final static String ID = ChunkHelper.cHRM;

  // http://www.w3.org/TR/PNG/#11cHRM
  private double whitex, whitey;
  private double redx, redy;
  private double greenx, greeny;
  private double bluex, bluey;

  public PngChunkCHRM(ImageInfo info) {
    super(ID, info);
  }

  @Override
  public ChunkOrderingConstraint getOrderingConstraint() {
    return ChunkOrderingConstraint.AFTER_PLTE_BEFORE_IDAT;
  }

  @Override
  public ChunkRaw createRawChunk() {
    ChunkRaw c = null;
    c = createEmptyChunk(32, true);
    PngHelperInternal.writeInt4tobytes(PngHelperInternal.doubleToInt100000(whitex), c.data, 0);
    PngHelperInternal.writeInt4tobytes(PngHelperInternal.doubleToInt100000(whitey), c.data, 4);
    PngHelperInternal.writeInt4tobytes(PngHelperInternal.doubleToInt100000(redx), c.data, 8);
    PngHelperInternal.writeInt4tobytes(PngHelperInternal.doubleToInt100000(redy), c.data, 12);
    PngHelperInternal.writeInt4tobytes(PngHelperInternal.doubleToInt100000(greenx), c.data, 16);
    PngHelperInternal.writeInt4tobytes(PngHelperInternal.doubleToInt100000(greeny), c.data, 20);
    PngHelperInternal.writeInt4tobytes(PngHelperInternal.doubleToInt100000(bluex), c.data, 24);
    PngHelperInternal.writeInt4tobytes(PngHelperInternal.doubleToInt100000(bluey), c.data, 28);
    return c;
  }

  @Override
  public void parseFromRaw(ChunkRaw c) {
    if (c.len != 32)
      throw new PngjException("bad chunk " + c);
    whitex = PngHelperInternal.intToDouble100000(PngHelperInternal.readInt4fromBytes(c.data, 0));
    whitey = PngHelperInternal.intToDouble100000(PngHelperInternal.readInt4fromBytes(c.data, 4));
    redx = PngHelperInternal.intToDouble100000(PngHelperInternal.readInt4fromBytes(c.data, 8));
    redy = PngHelperInternal.intToDouble100000(PngHelperInternal.readInt4fromBytes(c.data, 12));
    greenx = PngHelperInternal.intToDouble100000(PngHelperInternal.readInt4fromBytes(c.data, 16));
    greeny = PngHelperInternal.intToDouble100000(PngHelperInternal.readInt4fromBytes(c.data, 20));
    bluex = PngHelperInternal.intToDouble100000(PngHelperInternal.readInt4fromBytes(c.data, 24));
    bluey = PngHelperInternal.intToDouble100000(PngHelperInternal.readInt4fromBytes(c.data, 28));
  }

  public void setChromaticities(double whitex, double whitey, double redx, double redy,
      double greenx, double greeny, double bluex, double bluey) {
    this.whitex = whitex;
    this.redx = redx;
    this.greenx = greenx;
    this.bluex = bluex;
    this.whitey = whitey;
    this.redy = redy;
    this.greeny = greeny;
    this.bluey = bluey;
  }

  public double[] getChromaticities() {
    return new double[] {whitex, whitey, redx, redy, greenx, greeny, bluex, bluey};
  }

}
