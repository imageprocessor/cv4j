package com.hjg.pngj.chunks;

import com.hjg.pngj.ImageInfo;
import com.hjg.pngj.PngHelperInternal;

/**
 * acTL chunk. For APGN, not PGN standard
 * <p>
 * see https://wiki.mozilla.org/APNG_Specification#.60acTL.60:_The_Animation_Control_Chunk
 * <p>
 */
public class PngChunkACTL extends PngChunkSingle {
  public final static String ID = "acTL";
  private int numFrames;
  private int numPlays;


  public PngChunkACTL(ImageInfo info) {
    super(ID, info);
  }

  @Override
  public ChunkOrderingConstraint getOrderingConstraint() {
    return ChunkOrderingConstraint.BEFORE_IDAT;
  }

  @Override
  public ChunkRaw createRawChunk() {
    ChunkRaw c = createEmptyChunk(8, true);
    PngHelperInternal.writeInt4tobytes((int) numFrames, c.data, 0);
    PngHelperInternal.writeInt4tobytes((int) numPlays, c.data, 4);
    return c;
  }

  @Override
  public void parseFromRaw(ChunkRaw chunk) {
    numFrames = PngHelperInternal.readInt4fromBytes(chunk.data, 0);
    numPlays = PngHelperInternal.readInt4fromBytes(chunk.data, 4);
  }

  public int getNumFrames() {
    return numFrames;
  }

  public void setNumFrames(int numFrames) {
    this.numFrames = numFrames;
  }

  public int getNumPlays() {
    return numPlays;
  }

  public void setNumPlays(int numPlays) {
    this.numPlays = numPlays;
  }



}
