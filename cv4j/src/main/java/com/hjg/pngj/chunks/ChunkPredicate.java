package com.hjg.pngj.chunks;

/**
 * Decides if another chunk "matches", according to some criterion
 */
public interface ChunkPredicate {
  /**
   * The other chunk matches with this one
   * 
   * @param chunk
   * @return true if match
   */
  boolean match(PngChunk chunk);
}
