package com.hjg.pngj.chunks;

import com.hjg.pngj.ImageInfo;

/**
 * IDAT chunk.
 * <p>
 * see http://www.w3.org/TR/PNG/#11IDAT
 * <p>
 * This is dummy placeholder - we write/read this chunk (actually several) by special code.
 */
public class PngChunkIDAT extends PngChunkMultiple {
  public final static String ID = ChunkHelper.IDAT;

  // http://www.w3.org/TR/PNG/#11IDAT
  public PngChunkIDAT(ImageInfo i) {
    super(ID, i);
  }

  @Override
  public ChunkOrderingConstraint getOrderingConstraint() {
    return ChunkOrderingConstraint.NA;
  }

  @Override
  public ChunkRaw createRawChunk() {// does nothing
    return null;
  }

  @Override
  public void parseFromRaw(ChunkRaw c) { // does nothing
  }

}
