package com.hjg.pngj.chunks;

import java.util.ArrayList;
import java.util.List;

import com.hjg.pngj.PngjException;

/**
 * We consider "image metadata" every info inside the image except for the most basic image info (IHDR chunk - ImageInfo
 * class) and the pixels values.
 * <p>
 * This includes the palette (if present) and all the ancillary chunks
 * <p>
 * This class provides a wrapper over the collection of chunks of a image (read or to write) and provides some high
 * level methods to access them
 */
public class PngMetadata {
  private final ChunksList chunkList;
  private final boolean readonly;

  public PngMetadata(ChunksList chunks) {
    this.chunkList = chunks;
    if (chunks instanceof ChunksListForWrite) {
      this.readonly = false;
    } else {
      this.readonly = true;
    }
  }

  /**
   * Queues the chunk at the writer
   * <p>
   * lazyOverwrite: if true, checks if there is a queued "equivalent" chunk and if so, overwrites it. However if that
   * not check for already written chunks.
   */
  public void queueChunk(final PngChunk c, boolean lazyOverwrite) {
    ChunksListForWrite cl = getChunkListW();
    if (readonly)
      throw new PngjException("cannot set chunk : readonly metadata");
    if (lazyOverwrite) {
      ChunkHelper.trimList(cl.getQueuedChunks(), new ChunkPredicate() {
        public boolean match(PngChunk c2) {
          return ChunkHelper.equivalent(c, c2);
        }
      });
    }
    cl.queue(c);
  }

  public void queueChunk(final PngChunk c) {
    queueChunk(c, true);
  }

  private ChunksListForWrite getChunkListW() {
    return (ChunksListForWrite) chunkList;
  }

  // ///// high level utility methods follow ////////////

  // //////////// DPI

  /**
   * returns -1 if not found or dimension unknown
   */
  public double[] getDpi() {
    PngChunk c = chunkList.getById1(ChunkHelper.pHYs, true);
    if (c == null)
      return new double[] {-1, -1};
    else
      return ((PngChunkPHYS) c).getAsDpi2();
  }

  public void setDpi(double x) {
    setDpi(x, x);
  }

  public void setDpi(double x, double y) {
    PngChunkPHYS c = new PngChunkPHYS(chunkList.imageInfo);
    c.setAsDpi2(x, y);
    queueChunk(c);
  }

  // //////////// TIME

  /**
   * Creates a time chunk with current time, less secsAgo seconds
   * <p>
   * 
   * @return Returns the created-queued chunk, just in case you want to examine or modify it
   */
  public PngChunkTIME setTimeNow(int secsAgo) {
    PngChunkTIME c = new PngChunkTIME(chunkList.imageInfo);
    c.setNow(secsAgo);
    queueChunk(c);
    return c;
  }

  public PngChunkTIME setTimeNow() {
    return setTimeNow(0);
  }

  /**
   * Creates a time chunk with diven date-time
   * <p>
   * 
   * @return Returns the created-queued chunk, just in case you want to examine or modify it
   */
  public PngChunkTIME setTimeYMDHMS(int yearx, int monx, int dayx, int hourx, int minx, int secx) {
    PngChunkTIME c = new PngChunkTIME(chunkList.imageInfo);
    c.setYMDHMS(yearx, monx, dayx, hourx, minx, secx);
    queueChunk(c, true);
    return c;
  }

  /**
   * null if not found
   */
  public PngChunkTIME getTime() {
    return (PngChunkTIME) chunkList.getById1(ChunkHelper.tIME);
  }

  public String getTimeAsString() {
    PngChunkTIME c = getTime();
    return c == null ? "" : c.getAsString();
  }

  // //////////// TEXT

  /**
   * Creates a text chunk and queue it.
   * <p>
   * 
   * @param k : key (latin1)
   * @param val (arbitrary, should be latin1 if useLatin1)
   * @param useLatin1
   * @param compress
   * @return Returns the created-queued chunks, just in case you want to examine, touch it
   */
  public PngChunkTextVar setText(String k, String val, boolean useLatin1, boolean compress) {
    if (compress && !useLatin1)
      throw new PngjException("cannot compress non latin text");
    PngChunkTextVar c;
    if (useLatin1) {
      if (compress) {
        c = new PngChunkZTXT(chunkList.imageInfo);
      } else {
        c = new PngChunkTEXT(chunkList.imageInfo);
      }
    } else {
      c = new PngChunkITXT(chunkList.imageInfo);
      ((PngChunkITXT) c).setLangtag(k); // we use the same orig tag (this is not quite right)
    }
    c.setKeyVal(k, val);
    queueChunk(c, true);
    return c;
  }

  public PngChunkTextVar setText(String k, String val) {
    return setText(k, val, false, false);
  }

  /**
   * gets all text chunks with a given key
   * <p>
   * returns null if not found
   * <p>
   * Warning: this does not check the "lang" key of iTxt
   */
  @SuppressWarnings("unchecked")
  public List<? extends PngChunkTextVar> getTxtsForKey(String k) {
    @SuppressWarnings("rawtypes")
    List c = new ArrayList();
    c.addAll(chunkList.getById(ChunkHelper.tEXt, k));
    c.addAll(chunkList.getById(ChunkHelper.zTXt, k));
    c.addAll(chunkList.getById(ChunkHelper.iTXt, k));
    return c;
  }

  /**
   * Returns empty if not found, concatenated (with newlines) if multiple! - and trimmed
   * <p>
   * Use getTxtsForKey() if you don't want this behaviour
   */
  public String getTxtForKey(String k) {
    List<? extends PngChunkTextVar> li = getTxtsForKey(k);
    if (li.isEmpty())
      return "";
    StringBuilder t = new StringBuilder();
    for (PngChunkTextVar c : li)
      t.append(c.getVal()).append("\n");
    return t.toString().trim();
  }

  /**
   * Returns the palette chunk, if present
   * 
   * @return null if not present
   */
  public PngChunkPLTE getPLTE() {
    return (PngChunkPLTE) chunkList.getById1(PngChunkPLTE.ID);
  }

  /**
   * Creates a new empty palette chunk, queues it for write and return it to the caller, who should fill its entries
   */
  public PngChunkPLTE createPLTEChunk() {
    PngChunkPLTE plte = new PngChunkPLTE(chunkList.imageInfo);
    queueChunk(plte);
    return plte;
  }

  /**
   * Returns the TRNS chunk, if present
   * 
   * @return null if not present
   */
  public PngChunkTRNS getTRNS() {
    return (PngChunkTRNS) chunkList.getById1(PngChunkTRNS.ID);
  }

  /**
   * Creates a new empty TRNS chunk, queues it for write and return it to the caller, who should fill its entries
   */
  public PngChunkTRNS createTRNSChunk() {
    PngChunkTRNS trns = new PngChunkTRNS(chunkList.imageInfo);
    queueChunk(trns);
    return trns;
  }

}
