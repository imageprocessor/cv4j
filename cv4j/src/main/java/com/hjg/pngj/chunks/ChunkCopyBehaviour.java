package com.hjg.pngj.chunks;

import com.hjg.pngj.ImageInfo;
import com.hjg.pngj.PngReader;
import com.hjg.pngj.PngWriter;

/**
 * Chunk copy policy to apply when copyng from a {@link PngReader} to a {@link PngWriter}.
 * <p>
 * The constants are bit-masks, they can be OR-ed
 * <p>
 * Reference: <a href="http://www.w3.org/TR/PNG/#14">http://www.w3.org/TR/PNG/#14</a> <br>
 */
public class ChunkCopyBehaviour {

  /** Don't copy anything */
  public static final int COPY_NONE = 0;

  /** copy the palette */
  public static final int COPY_PALETTE = 1;

  /** copy all 'safe to copy' chunks */
  public static final int COPY_ALL_SAFE = 1 << 2;

  /**
   * copy all, including palette
   */
  public static final int COPY_ALL = 1 << 3; // includes palette!
  /**
   * Copy PHYS chunk (physical resolution)
   */
  public static final int COPY_PHYS = 1 << 4; // dpi
  /**
   * Copy al textual chunks.
   */
  public static final int COPY_TEXTUAL = 1 << 5; // all textual types
  /**
   * Copy TRNS chunk
   */
  public static final int COPY_TRANSPARENCY = 1 << 6; //
  /**
   * Copy unknown chunks (unknown by our factory)
   */
  public static final int COPY_UNKNOWN = 1 << 7; // all unknown (by the factory!)
  /**
   * Copy almost all: excepts only HIST (histogram) TIME and TEXTUAL chunks
   */
  public static final int COPY_ALMOSTALL = 1 << 8;

  private static boolean maskMatch(int v, int mask) {
    return (v & mask) != 0;
  }

  /**
   * Creates a predicate equivalent to the copy mask
   * <p>
   * Given a copy mask (see static fields) and the ImageInfo of the target PNG, returns a predicate that tells if a
   * chunk should be copied.
   * <p>
   * This is a handy helper method, you can also create and set your own predicate
   */
  public static ChunkPredicate createPredicate(final int copyFromMask, final ImageInfo imgInfo) {
    return new ChunkPredicate() {
      public boolean match(PngChunk chunk) {
        if (chunk.crit) {
          if (chunk.id.equals(ChunkHelper.PLTE)) {
            if (imgInfo.indexed && maskMatch(copyFromMask, ChunkCopyBehaviour.COPY_PALETTE))
              return true;
            if (!imgInfo.greyscale && maskMatch(copyFromMask, ChunkCopyBehaviour.COPY_ALL))
              return true;
          }
        } else { // ancillary
          boolean text = (chunk instanceof PngChunkTextVar);
          boolean safe = chunk.safe;
          // notice that these if are not exclusive
          if (maskMatch(copyFromMask, ChunkCopyBehaviour.COPY_ALL))
            return true;
          if (safe && maskMatch(copyFromMask, ChunkCopyBehaviour.COPY_ALL_SAFE))
            return true;
          if (chunk.id.equals(ChunkHelper.tRNS)
              && maskMatch(copyFromMask, ChunkCopyBehaviour.COPY_TRANSPARENCY))
            return true;
          if (chunk.id.equals(ChunkHelper.pHYs)
              && maskMatch(copyFromMask, ChunkCopyBehaviour.COPY_PHYS))
            return true;
          if (text && maskMatch(copyFromMask, ChunkCopyBehaviour.COPY_TEXTUAL))
            return true;
          if (maskMatch(copyFromMask, ChunkCopyBehaviour.COPY_ALMOSTALL)
              && !(ChunkHelper.isUnknown(chunk) || text || chunk.id.equals(ChunkHelper.hIST) || chunk.id
                  .equals(ChunkHelper.tIME)))
            return true;
          if (maskMatch(copyFromMask, ChunkCopyBehaviour.COPY_UNKNOWN)
              && ChunkHelper.isUnknown(chunk))
            return true;
        }
        return false;
      }

    };
  }
}
