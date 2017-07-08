package com.hjg.pngj;

import ar.com.hjg.pngj.chunks.ChunkRaw;
import ar.com.hjg.pngj.chunks.PngChunk;

/**
 * Factory to create a {@link PngChunk} from a {@link ChunkRaw}.
 * <p>
 * Used by {@link PngReader}
 */
public interface IChunkFactory {

  /**
   * @param chunkRaw Chunk in raw form. Data can be null if it was skipped or processed directly (eg IDAT)
   * @param imgInfo Not normally necessary, but some chunks want this info
   * @return should never return null.
   */
  public PngChunk createChunk(ChunkRaw chunkRaw, ImageInfo imgInfo);

}
