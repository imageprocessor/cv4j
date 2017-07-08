package com.hjg.pngj.chunks;

import com.hjg.pngj.ImageInfo;
import com.hjg.pngj.PngHelperInternal;
import com.hjg.pngj.PngjException;

/**
 * fdAT chunk. For APGN, not PGN standard
 * <p>
 * see https://wiki.mozilla.org/APNG_Specification#.60fdAT.60:_The_Frame_Data_Chunk
 * <p>
 * This implementation does not support buffering, this should be not managed similar to a IDAT chunk
 * 
 */
public class PngChunkFDAT extends PngChunkMultiple {
  public final static String ID = "fdAT";
  private int seqNum;
  private byte[] buffer; // normally not allocated - if so, it's the raw data, so it includes the 4bytes seqNum
  int datalen; // length of idat data, excluding seqNUm (= chunk.len-4)

  public PngChunkFDAT(ImageInfo info) {
    super(ID, info);
  }

  @Override
  public ChunkOrderingConstraint getOrderingConstraint() {
    return ChunkOrderingConstraint.AFTER_IDAT;
  }

  @Override
  public ChunkRaw createRawChunk() {
    if (buffer == null)
      throw new PngjException("not buffered");
    ChunkRaw c = createEmptyChunk(datalen + 4, false);
    c.data = buffer; // shallow copy!
    return c;
  }

  @Override
  public void parseFromRaw(ChunkRaw chunk) {
    seqNum = PngHelperInternal.readInt4fromBytes(chunk.data, 0);
    datalen = chunk.len - 4;
    buffer = chunk.data;
  }

  public int getSeqNum() {
    return seqNum;
  }

  public void setSeqNum(int seqNum) {
    this.seqNum = seqNum;
  }

  public byte[] getBuffer() {
    return buffer;
  }

  public void setBuffer(byte[] buffer) {
    this.buffer = buffer;
  }

  public int getDatalen() {
    return datalen;
  }

  public void setDatalen(int datalen) {
    this.datalen = datalen;
  }

}
