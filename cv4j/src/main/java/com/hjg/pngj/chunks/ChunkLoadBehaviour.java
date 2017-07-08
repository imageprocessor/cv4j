package com.hjg.pngj.chunks;

/**
 * What to do with ancillary (non-critical) chunks when reading.
 * <p>
 * 
 */
public enum ChunkLoadBehaviour {
  /**
   * All non-critical chunks are skipped
   */
  LOAD_CHUNK_NEVER,
  /**
   * Load chunk if "safe to copy"
   */
  LOAD_CHUNK_IF_SAFE,
  /**
   * Load only most important chunk: TRNS
   */
  LOAD_CHUNK_MOST_IMPORTANT,
  /**
   * Load all chunks. <br>
   * Notice that other restrictions might apply, see PngReader.skipChunkMaxSize PngReader.skipChunkIds
   */
  LOAD_CHUNK_ALWAYS;
}
