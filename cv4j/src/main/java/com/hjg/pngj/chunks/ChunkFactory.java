package com.hjg.pngj.chunks;

import com.hjg.pngj.IChunkFactory;
import com.hjg.pngj.ImageInfo;

/**
 * Default chunk factory.
 * <p>
 * The user that wants to parse custom chunks can extend {@link #createEmptyChunkExtended(String, ImageInfo)}
 */
public class ChunkFactory implements IChunkFactory {

  boolean parse;

  public ChunkFactory() {
    this(true);
  }

  public ChunkFactory(boolean parse) {
    this.parse = parse;
  }

  public final PngChunk createChunk(ChunkRaw chunkRaw, ImageInfo imgInfo) {
    PngChunk c = createEmptyChunkKnown(chunkRaw.id, imgInfo);
    if (c == null)
      c = createEmptyChunkExtended(chunkRaw.id, imgInfo);
    if (c == null)
      c = createEmptyChunkUnknown(chunkRaw.id, imgInfo);
    c.setRaw(chunkRaw);
    if (parse && chunkRaw.data != null)
      c.parseFromRaw(chunkRaw);
    return c;
  }

  protected final PngChunk createEmptyChunkKnown(String id, ImageInfo imgInfo) {
    if (id.equals(ChunkHelper.IDAT))
      return new PngChunkIDAT(imgInfo);
    if (id.equals(ChunkHelper.IHDR))
      return new PngChunkIHDR(imgInfo);
    if (id.equals(ChunkHelper.PLTE))
      return new PngChunkPLTE(imgInfo);
    if (id.equals(ChunkHelper.IEND))
      return new PngChunkIEND(imgInfo);
    if (id.equals(ChunkHelper.tEXt))
      return new PngChunkTEXT(imgInfo);
    if (id.equals(ChunkHelper.iTXt))
      return new PngChunkITXT(imgInfo);
    if (id.equals(ChunkHelper.zTXt))
      return new PngChunkZTXT(imgInfo);
    if (id.equals(ChunkHelper.bKGD))
      return new PngChunkBKGD(imgInfo);
    if (id.equals(ChunkHelper.gAMA))
      return new PngChunkGAMA(imgInfo);
    if (id.equals(ChunkHelper.pHYs))
      return new PngChunkPHYS(imgInfo);
    if (id.equals(ChunkHelper.iCCP))
      return new PngChunkICCP(imgInfo);
    if (id.equals(ChunkHelper.tIME))
      return new PngChunkTIME(imgInfo);
    if (id.equals(ChunkHelper.tRNS))
      return new PngChunkTRNS(imgInfo);
    if (id.equals(ChunkHelper.cHRM))
      return new PngChunkCHRM(imgInfo);
    if (id.equals(ChunkHelper.sBIT))
      return new PngChunkSBIT(imgInfo);
    if (id.equals(ChunkHelper.sRGB))
      return new PngChunkSRGB(imgInfo);
    if (id.equals(ChunkHelper.hIST))
      return new PngChunkHIST(imgInfo);
    if (id.equals(ChunkHelper.sPLT))
      return new PngChunkSPLT(imgInfo);
    // apng
    if (id.equals(PngChunkFDAT.ID))
      return new PngChunkFDAT(imgInfo);
    if (id.equals(PngChunkACTL.ID))
      return new PngChunkACTL(imgInfo);
    if (id.equals(PngChunkFCTL.ID))
      return new PngChunkFCTL(imgInfo);
    return null;
  }

  /**
   * This is used as last resort factory method.
   * <p>
   * It creates a {@link PngChunkUNKNOWN} chunk.
   */
  protected final PngChunk createEmptyChunkUnknown(String id, ImageInfo imgInfo) {
    return new PngChunkUNKNOWN(id, imgInfo);
  }

  /**
   * Factory for chunks that are not in the original PNG standard. This can be overriden (but dont forget to call this
   * also)
   * 
   * @param id Chunk id , 4 letters
   * @param imgInfo Usually not needed
   * @return null if chunk id not recognized
   */
  protected PngChunk createEmptyChunkExtended(String id, ImageInfo imgInfo) {
    if (id.equals(PngChunkOFFS.ID))
      return new PngChunkOFFS(imgInfo);
    if (id.equals(PngChunkSTER.ID))
      return new PngChunkSTER(imgInfo);
    return null; // extend!
  }

}
