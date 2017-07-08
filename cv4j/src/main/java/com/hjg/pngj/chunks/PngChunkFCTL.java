package com.hjg.pngj.chunks;

import com.hjg.pngj.ImageInfo;
import com.hjg.pngj.PngHelperInternal;

/**
 * fcTL chunk. For APGN, not PGN standard
 * <p>
 * see https://wiki.mozilla.org/APNG_Specification#.60fcTL.60:_The_Frame_Control_Chunk
 * <p>
 */
public class PngChunkFCTL extends PngChunkMultiple {
  public final static String ID = "fcTL";

  public final static byte APNG_DISPOSE_OP_NONE = 0;
  public final static byte APNG_DISPOSE_OP_BACKGROUND = 1;
  public final static byte APNG_DISPOSE_OP_PREVIOUS = 2;
  public final static byte APNG_BLEND_OP_SOURCE = 0;
  public final static byte APNG_BLEND_OP_OVER = 1;

  private int seqNum;
  private int width, height, xOff, yOff;
  private int delayNum, delayDen;
  private byte disposeOp, blendOp;

  public PngChunkFCTL(ImageInfo info) {
    super(ID, info);
  }

  public ImageInfo getEquivImageInfo() {
    return new ImageInfo(width, height, imgInfo.bitDepth, imgInfo.alpha, imgInfo.greyscale,
        imgInfo.indexed);
  }

  @Override
  public ChunkOrderingConstraint getOrderingConstraint() {
    return ChunkOrderingConstraint.NONE;
  }

  @Override
  public ChunkRaw createRawChunk() {
    ChunkRaw c = createEmptyChunk(8, true);
    int off = 0;
    PngHelperInternal.writeInt4tobytes(seqNum, c.data, off);
    off += 4;
    PngHelperInternal.writeInt4tobytes(width, c.data, off);
    off += 4;
    PngHelperInternal.writeInt4tobytes(height, c.data, off);
    off += 4;
    PngHelperInternal.writeInt4tobytes(xOff, c.data, off);
    off += 4;
    PngHelperInternal.writeInt4tobytes(yOff, c.data, off);
    off += 4;
    PngHelperInternal.writeInt2tobytes(delayNum, c.data, off);
    off += 2;
    PngHelperInternal.writeInt2tobytes(delayDen, c.data, off);
    off += 2;
    c.data[off] = disposeOp;
    off += 1;
    c.data[off] = blendOp;
    return c;
  }

  @Override
  public void parseFromRaw(ChunkRaw chunk) {
    int off = 0;
    seqNum = PngHelperInternal.readInt4fromBytes(chunk.data, off);
    off += 4;
    width = PngHelperInternal.readInt4fromBytes(chunk.data, off);
    off += 4;
    height = PngHelperInternal.readInt4fromBytes(chunk.data, off);
    off += 4;
    xOff = PngHelperInternal.readInt4fromBytes(chunk.data, off);
    off += 4;
    yOff = PngHelperInternal.readInt4fromBytes(chunk.data, off);
    off += 4;
    delayNum = PngHelperInternal.readInt2fromBytes(chunk.data, off);
    off += 2;
    delayDen = PngHelperInternal.readInt2fromBytes(chunk.data, off);
    off += 2;
    disposeOp = chunk.data[off];
    off += 1;
    blendOp = chunk.data[off];
  }

  public int getSeqNum() {
    return seqNum;
  }

  public void setSeqNum(int seqNum) {
    this.seqNum = seqNum;
  }

  public int getWidth() {
    return width;
  }

  public void setWidth(int width) {
    this.width = width;
  }

  public int getHeight() {
    return height;
  }

  public void setHeight(int height) {
    this.height = height;
  }

  public int getxOff() {
    return xOff;
  }

  public void setxOff(int xOff) {
    this.xOff = xOff;
  }

  public int getyOff() {
    return yOff;
  }

  public void setyOff(int yOff) {
    this.yOff = yOff;
  }

  public int getDelayNum() {
    return delayNum;
  }

  public void setDelayNum(int delayNum) {
    this.delayNum = delayNum;
  }

  public int getDelayDen() {
    return delayDen;
  }

  public void setDelayDen(int delayDen) {
    this.delayDen = delayDen;
  }

  public byte getDisposeOp() {
    return disposeOp;
  }

  public void setDisposeOp(byte disposeOp) {
    this.disposeOp = disposeOp;
  }

  public byte getBlendOp() {
    return blendOp;
  }

  public void setBlendOp(byte blendOp) {
    this.blendOp = blendOp;
  }

}
