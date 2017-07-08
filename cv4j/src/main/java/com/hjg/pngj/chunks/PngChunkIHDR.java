package com.hjg.pngj.chunks;

import java.io.ByteArrayInputStream;

import com.hjg.pngj.ImageInfo;
import com.hjg.pngj.PngHelperInternal;
import com.hjg.pngj.PngjException;
import com.hjg.pngj.PngjInputException;

/**
 * IHDR chunk.
 * <p>
 * see http://www.w3.org/TR/PNG/#11IHDR
 * <p>
 * This is a special critical Chunk.
 */
public class PngChunkIHDR extends PngChunkSingle {
  public final static String ID = ChunkHelper.IHDR;

  private int cols;
  private int rows;
  private int bitspc;
  private int colormodel;
  private int compmeth;
  private int filmeth;
  private int interlaced;

  // http://www.w3.org/TR/PNG/#11IHDR
  //
  public PngChunkIHDR(ImageInfo info) { // argument is normally null here, if not null is used to fill the fields
    super(ID, info);
    if (info != null)
      fillFromInfo(info);
  }


  @Override
  public ChunkOrderingConstraint getOrderingConstraint() {
    return ChunkOrderingConstraint.NA;
  }

  @Override
  public ChunkRaw createRawChunk() {
    ChunkRaw c = new ChunkRaw(13, ChunkHelper.b_IHDR, true);
    int offset = 0;
    PngHelperInternal.writeInt4tobytes(cols, c.data, offset);
    offset += 4;
    PngHelperInternal.writeInt4tobytes(rows, c.data, offset);
    offset += 4;
    c.data[offset++] = (byte) bitspc;
    c.data[offset++] = (byte) colormodel;
    c.data[offset++] = (byte) compmeth;
    c.data[offset++] = (byte) filmeth;
    c.data[offset++] = (byte) interlaced;
    return c;
  }

  @Override
  public void parseFromRaw(ChunkRaw c) {
    if (c.len != 13)
      throw new PngjException("Bad IDHR len " + c.len);
    ByteArrayInputStream st = c.getAsByteStream();
    cols = PngHelperInternal.readInt4(st);
    rows = PngHelperInternal.readInt4(st);
    // bit depth: number of bits per channel
    bitspc = PngHelperInternal.readByte(st);
    colormodel = PngHelperInternal.readByte(st);
    compmeth = PngHelperInternal.readByte(st);
    filmeth = PngHelperInternal.readByte(st);
    interlaced = PngHelperInternal.readByte(st);
  }

  public int getCols() {
    return cols;
  }

  public void setCols(int cols) {
    this.cols = cols;
  }

  public int getRows() {
    return rows;
  }

  public void setRows(int rows) {
    this.rows = rows;
  }

  public int getBitspc() {
    return bitspc;
  }

  public void setBitspc(int bitspc) {
    this.bitspc = bitspc;
  }

  public int getColormodel() {
    return colormodel;
  }

  public void setColormodel(int colormodel) {
    this.colormodel = colormodel;
  }

  public int getCompmeth() {
    return compmeth;
  }

  public void setCompmeth(int compmeth) {
    this.compmeth = compmeth;
  }

  public int getFilmeth() {
    return filmeth;
  }

  public void setFilmeth(int filmeth) {
    this.filmeth = filmeth;
  }

  public int getInterlaced() {
    return interlaced;
  }

  public void setInterlaced(int interlaced) {
    this.interlaced = interlaced;
  }

  public boolean isInterlaced() {
    return getInterlaced() == 1;
  }

  public void fillFromInfo(ImageInfo info) {
    setCols(imgInfo.cols);
    setRows(imgInfo.rows);
    setBitspc(imgInfo.bitDepth);
    int colormodel = 0;
    if (imgInfo.alpha)
      colormodel += 0x04;
    if (imgInfo.indexed)
      colormodel += 0x01;
    if (!imgInfo.greyscale)
      colormodel += 0x02;
    setColormodel(colormodel);
    setCompmeth(0); // compression method 0=deflate
    setFilmeth(0); // filter method (0)
    setInterlaced(0); // we never interlace
  }

  /** throws PngInputException if unexpected values */
  public ImageInfo createImageInfo() {
    check();
    boolean alpha = (getColormodel() & 0x04) != 0;
    boolean palette = (getColormodel() & 0x01) != 0;
    boolean grayscale = (getColormodel() == 0 || getColormodel() == 4);
    // creates ImgInfo and imgLine, and allocates buffers
    return new ImageInfo(getCols(), getRows(), getBitspc(), alpha, grayscale, palette);
  }

  public void check() {
    if (cols < 1 || rows < 1 || compmeth != 0 || filmeth != 0)
      throw new PngjInputException("bad IHDR: col/row/compmethod/filmethod invalid");
    if (bitspc != 1 && bitspc != 2 && bitspc != 4 && bitspc != 8 && bitspc != 16)
      throw new PngjInputException("bad IHDR: bitdepth invalid");
    if (interlaced < 0 || interlaced > 1)
      throw new PngjInputException("bad IHDR: interlace invalid");
    switch (colormodel) {
      case 0:
        break;
      case 3:
        if (bitspc == 16)
          throw new PngjInputException("bad IHDR: bitdepth invalid");
        break;
      case 2:
      case 4:
      case 6:
        if (bitspc != 8 && bitspc != 16)
          throw new PngjInputException("bad IHDR: bitdepth invalid");
        break;
      default:
        throw new PngjInputException("bad IHDR: invalid colormodel");
    }
  }

}
