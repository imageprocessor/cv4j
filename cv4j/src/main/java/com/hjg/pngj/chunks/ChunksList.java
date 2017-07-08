package com.hjg.pngj.chunks;

import java.util.ArrayList;
import java.util.List;

import com.hjg.pngj.ImageInfo;
import com.hjg.pngj.PngjException;

/**
 * All chunks that form an image, read or to be written.
 * <p>
 * chunks include all chunks, but IDAT is a single pseudo chunk without data
 **/
public class ChunksList {
  // ref: http://www.w3.org/TR/PNG/#table53
  public static final int CHUNK_GROUP_0_IDHR = 0; // required - single
  public static final int CHUNK_GROUP_1_AFTERIDHR = 1; // optional - multiple
  public static final int CHUNK_GROUP_2_PLTE = 2; // optional - single
  public static final int CHUNK_GROUP_3_AFTERPLTE = 3; // optional - multple
  public static final int CHUNK_GROUP_4_IDAT = 4; // required (single pseudo chunk)
  public static final int CHUNK_GROUP_5_AFTERIDAT = 5; // optional - multple
  public static final int CHUNK_GROUP_6_END = 6; // only 1 chunk - requried

  /**
   * All chunks, read (or written)
   * 
   * But IDAT is a single pseudo chunk without data
   */
  List<PngChunk> chunks = new ArrayList<PngChunk>();
  // protected HashMap<String, List<PngChunk>> chunksById = new HashMap<String, List<PngChunk>>();
  // // does not include IDAT

  final ImageInfo imageInfo; // only required for writing

  boolean withPlte = false;

  public ChunksList(ImageInfo imfinfo) {
    this.imageInfo = imfinfo;
  }

  /**
   * WARNING: this does NOT return a copy, but the list itself. The called should not modify this directly! Don't use
   * this to manipulate the chunks.
   */
  public List<PngChunk> getChunks() {
    return chunks;
  }

  protected static List<PngChunk> getXById(final List<PngChunk> list, final String id,
      final String innerid) {
    if (innerid == null)
      return ChunkHelper.filterList(list, new ChunkPredicate() {
        public boolean match(PngChunk c) {
          return c.id.equals(id);
        }
      });
    else
      return ChunkHelper.filterList(list, new ChunkPredicate() {
        public boolean match(PngChunk c) {
          if (!c.id.equals(id))
            return false;
          if (c instanceof PngChunkTextVar && !((PngChunkTextVar) c).getKey().equals(innerid))
            return false;
          if (c instanceof PngChunkSPLT && !((PngChunkSPLT) c).getPalName().equals(innerid))
            return false;
          return true;
        }
      });
  }

  /**
   * Adds chunk in next position. This is used onyl by the pngReader
   */
  public void appendReadChunk(PngChunk chunk, int chunkGroup) {
    chunk.setChunkGroup(chunkGroup);
    chunks.add(chunk);
    if (chunk.id.equals(PngChunkPLTE.ID))
      withPlte = true;
  }

  /**
   * All chunks with this ID
   * 
   * @param id
   * @return List, empty if none
   */
  public List<? extends PngChunk> getById(final String id) {
    return getById(id, null);
  }

  /**
   * If innerid!=null and the chunk is PngChunkTextVar or PngChunkSPLT, it's filtered by that id
   * 
   * @param id
   * @return innerid Only used for text and SPLT chunks
   * @return List, empty if none
   */
  public List<? extends PngChunk> getById(final String id, final String innerid) {
    return getXById(chunks, id, innerid);
  }

  /**
   * Returns only one chunk
   * 
   * @param id
   * @return First chunk found, null if not found
   */
  public PngChunk getById1(final String id) {
    return getById1(id, false);
  }

  /**
   * Returns only one chunk or null if nothing found - does not include queued
   * <p>
   * If more than one chunk is found, then an exception is thrown (failifMultiple=true or chunk is single) or the last
   * one is returned (failifMultiple=false)
   **/
  public PngChunk getById1(final String id, final boolean failIfMultiple) {
    return getById1(id, null, failIfMultiple);
  }

  /**
   * Returns only one chunk or null if nothing found - does not include queued
   * <p>
   * If more than one chunk (after filtering by inner id) is found, then an exception is thrown (failifMultiple=true or
   * chunk is single) or the last one is returned (failifMultiple=false)
   **/
  public PngChunk getById1(final String id, final String innerid, final boolean failIfMultiple) {
    List<? extends PngChunk> list = getById(id, innerid);
    if (list.isEmpty())
      return null;
    if (list.size() > 1 && (failIfMultiple || !list.get(0).allowsMultiple()))
      throw new PngjException("unexpected multiple chunks id=" + id);
    return list.get(list.size() - 1);
  }

  /**
   * Finds all chunks "equivalent" to this one
   * 
   * @param c2
   * @return Empty if nothing found
   */
  public List<PngChunk> getEquivalent(final PngChunk c2) {
    return ChunkHelper.filterList(chunks, new ChunkPredicate() {
      public boolean match(PngChunk c) {
        return ChunkHelper.equivalent(c, c2);
      }
    });
  }

  public String toString() {
    return "ChunkList: read: " + chunks.size();
  }

  /**
   * for debugging
   */
  public String toStringFull() {
    StringBuilder sb = new StringBuilder(toString());
    sb.append("\n Read:\n");
    for (PngChunk chunk : chunks) {
      sb.append(chunk).append(" G=" + chunk.getChunkGroup() + "\n");
    }
    return sb.toString();
  }

}
