package com.hjg.pngj.chunks;

import java.io.OutputStream;

import com.hjg.pngj.ImageInfo;
import com.hjg.pngj.PngjExceptionInternal;

/**
 * Represents a instance of a PNG chunk.
 * <p>
 * See <a href="http://www.libpng.org/pub/png/spec/1.2/PNG-Chunks.html">http://www
 * .libpng.org/pub/png/spec/1.2/PNG-Chunks .html</a> </a>
 * <p>
 * Concrete classes should extend {@link PngChunkSingle} or {@link PngChunkMultiple}
 * <p>
 * Note that some methods/fields are type-specific (getOrderingConstraint(), allowsMultiple()),<br>
 * some are 'almost' type-specific (id,crit,pub,safe; the exception is PngUKNOWN), <br>
 * and the rest are instance-specific
 */
public abstract class PngChunk {

  /**
   * Chunk-id: 4 letters
   */
  public final String id;
  /**
   * Autocomputed at creation time
   */
  public final boolean crit, pub, safe;

  protected final ImageInfo imgInfo;

  protected ChunkRaw raw;

  private boolean priority = false; // For writing. Queued chunks with high priority will be written
                                    // as soon as
  // possible

  protected int chunkGroup = -1; // chunk group where it was read or writen

  /**
   * Possible ordering constraint for a PngChunk type -only relevant for ancillary chunks. Theoretically, there could be
   * more general constraints, but these cover the constraints for standard chunks.
   */
  public enum ChunkOrderingConstraint {
    /**
     * no ordering constraint
     */
    NONE,
    /**
     * Must go before PLTE (and hence, also before IDAT)
     */
    BEFORE_PLTE_AND_IDAT,
    /**
     * Must go after PLTE (if exists) but before IDAT
     */
    AFTER_PLTE_BEFORE_IDAT,
    /**
     * Must go after PLTE (and it must exist) but before IDAT
     */
    AFTER_PLTE_BEFORE_IDAT_PLTE_REQUIRED,
    /**
     * Must before IDAT (before or after PLTE)
     */
    BEFORE_IDAT,
    /**
     * After IDAT (this restriction does not apply to the standard PNG chunks)
     */
    AFTER_IDAT,
    /**
     * Does not apply
     */
    NA;

    public boolean mustGoBeforePLTE() {
      return this == BEFORE_PLTE_AND_IDAT;
    }

    public boolean mustGoBeforeIDAT() {
      return this == BEFORE_IDAT || this == BEFORE_PLTE_AND_IDAT || this == AFTER_PLTE_BEFORE_IDAT;
    }

    /**
     * after pallete, if exists
     */
    public boolean mustGoAfterPLTE() {
      return this == AFTER_PLTE_BEFORE_IDAT || this == AFTER_PLTE_BEFORE_IDAT_PLTE_REQUIRED;
    }

    public boolean mustGoAfterIDAT() {
      return this == AFTER_IDAT;
    }

    public boolean isOk(int currentChunkGroup, boolean hasplte) {
      if (this == NONE)
        return true;
      else if (this == BEFORE_IDAT)
        return currentChunkGroup < ChunksList.CHUNK_GROUP_4_IDAT;
      else if (this == BEFORE_PLTE_AND_IDAT)
        return currentChunkGroup < ChunksList.CHUNK_GROUP_2_PLTE;
      else if (this == AFTER_PLTE_BEFORE_IDAT)
        return hasplte ? currentChunkGroup < ChunksList.CHUNK_GROUP_4_IDAT
            : (currentChunkGroup < ChunksList.CHUNK_GROUP_4_IDAT && currentChunkGroup > ChunksList.CHUNK_GROUP_2_PLTE);
      else if (this == AFTER_IDAT)
        return currentChunkGroup > ChunksList.CHUNK_GROUP_4_IDAT;
      return false;
    }
  }

  public PngChunk(String id, ImageInfo imgInfo) {
    this.id = id;
    this.imgInfo = imgInfo;
    this.crit = ChunkHelper.isCritical(id);
    this.pub = ChunkHelper.isPublic(id);
    this.safe = ChunkHelper.isSafeToCopy(id);
  }

  protected final ChunkRaw createEmptyChunk(int len, boolean alloc) {
    ChunkRaw c = new ChunkRaw(len, ChunkHelper.toBytes(id), alloc);
    return c;
  }

  /**
   * In which "chunkGroup" (see {@link ChunksList}for definition) this chunks instance was read or written.
   * <p>
   * -1 if not read or written (eg, queued)
   */
  final public int getChunkGroup() {
    return chunkGroup;
  }

  /**
   * @see #getChunkGroup()
   */
  final void setChunkGroup(int chunkGroup) {
    this.chunkGroup = chunkGroup;
  }

  public boolean hasPriority() {
    return priority;
  }

  public void setPriority(boolean priority) {
    this.priority = priority;
  }

  final void write(OutputStream os) {
    if (raw == null || raw.data == null)
      raw = createRawChunk();
    if (raw == null)
      throw new PngjExceptionInternal("null chunk ! creation failed for " + this);
    raw.writeChunk(os);
  }

  /**
   * Creates the physical chunk. This is used when writing (serialization). Each particular chunk class implements its
   * own logic.
   * 
   * @return A newly allocated and filled raw chunk
   */
  public abstract ChunkRaw createRawChunk();

  /**
   * Parses raw chunk and fill inside data. This is used when reading (deserialization). Each particular chunk class
   * implements its own logic.
   */
  protected abstract void parseFromRaw(ChunkRaw c);

  /**
   * See {@link PngChunkMultiple} and {@link PngChunkSingle}
   * 
   * @return true if PNG accepts multiple chunks of this class
   */
  protected abstract boolean allowsMultiple();

  public ChunkRaw getRaw() {
    return raw;
  }

  void setRaw(ChunkRaw raw) {
    this.raw = raw;
  }

  /**
   * @see ChunkRaw#len
   */
  public int getLen() {
    return raw != null ? raw.len : -1;
  }

  /**
   * @see ChunkRaw#getOffset()
   */
  public long getOffset() {
    return raw != null ? raw.getOffset() : -1;
  }

  /**
   * This signals that the raw chunk (serialized data) as invalid, so that it's regenerated on write. This should be
   * called for the (infrequent) case of chunks that were copied from a PngReader and we want to manually modify it.
   */
  public void invalidateRawData() {
    raw = null;
  }

  /**
   * see {@link ChunkOrderingConstraint}
   */
  public abstract ChunkOrderingConstraint getOrderingConstraint();

  @Override
  public String toString() {
    return "chunk id= " + id + " (len=" + getLen() + " offset=" + getOffset() + ")";
  }

}
