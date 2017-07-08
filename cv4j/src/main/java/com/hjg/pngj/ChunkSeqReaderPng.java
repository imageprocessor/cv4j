package com.hjg.pngj;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ar.com.hjg.pngj.ChunkReader.ChunkReaderMode;
import ar.com.hjg.pngj.chunks.ChunkFactory;
import ar.com.hjg.pngj.chunks.ChunkHelper;
import ar.com.hjg.pngj.chunks.ChunkLoadBehaviour;
import ar.com.hjg.pngj.chunks.ChunksList;
import ar.com.hjg.pngj.chunks.PngChunk;
import ar.com.hjg.pngj.chunks.PngChunkIDAT;
import ar.com.hjg.pngj.chunks.PngChunkIEND;
import ar.com.hjg.pngj.chunks.PngChunkIHDR;
import ar.com.hjg.pngj.chunks.PngChunkPLTE;

/**
 * Adds to ChunkSeqReader the storing of PngChunk, with a PngFactory, and imageInfo + deinterlacer.
 * <p>
 * Most usual PNG reading should use this class, or a {@link PngReader}, which is a thin wrapper over this.
 */
public class ChunkSeqReaderPng extends ChunkSeqReader {

  protected ImageInfo imageInfo; // initialized at parsing the IHDR
  protected ImageInfo curImageInfo; // can vary, for apng
  protected Deinterlacer deinterlacer;
  protected int currentChunkGroup = -1;

  /**
   * All chunks, but some of them can have the buffer empty (IDAT and skipped)
   */
  protected ChunksList chunksList = null;
  protected final boolean callbackMode;
  private long bytesAncChunksLoaded = 0; // bytes loaded from buffered chunks non-critical chunks (data only)

  private boolean checkCrc = true;

  // --- parameters to be set prior to reading ---
  private boolean includeNonBufferedChunks = false;

  private Set<String> chunksToSkip = new HashSet<String>();
  private long maxTotalBytesRead = 0;
  private long skipChunkMaxSize = 0;
  private long maxBytesMetadata = 0;
  private IChunkFactory chunkFactory;
  private ChunkLoadBehaviour chunkLoadBehaviour = ChunkLoadBehaviour.LOAD_CHUNK_ALWAYS;

  public ChunkSeqReaderPng(boolean callbackMode) {
    super();
    this.callbackMode = callbackMode;
    chunkFactory = new ChunkFactory(); // default factory
  }

  private void updateAndCheckChunkGroup(String id) {
    if (id.equals(PngChunkIHDR.ID)) { // IDHR
      if (currentChunkGroup < 0)
        currentChunkGroup = ChunksList.CHUNK_GROUP_0_IDHR;
      else
        throw new PngjInputException("unexpected chunk " + id);
    } else if (id.equals(PngChunkPLTE.ID)) { // PLTE
      if ((currentChunkGroup == ChunksList.CHUNK_GROUP_0_IDHR || currentChunkGroup == ChunksList.CHUNK_GROUP_1_AFTERIDHR))
        currentChunkGroup = ChunksList.CHUNK_GROUP_2_PLTE;
      else
        throw new PngjInputException("unexpected chunk " + id);
    } else if (id.equals(PngChunkIDAT.ID)) { // IDAT (no necessarily the first)
      if ((currentChunkGroup >= ChunksList.CHUNK_GROUP_0_IDHR && currentChunkGroup <= ChunksList.CHUNK_GROUP_4_IDAT))
        currentChunkGroup = ChunksList.CHUNK_GROUP_4_IDAT;
      else
        throw new PngjInputException("unexpected chunk " + id);
    } else if (id.equals(PngChunkIEND.ID)) { // END
      if ((currentChunkGroup >= ChunksList.CHUNK_GROUP_4_IDAT))
        currentChunkGroup = ChunksList.CHUNK_GROUP_6_END;
      else
        throw new PngjInputException("unexpected chunk " + id);
    } else { // ancillary
      if (currentChunkGroup <= ChunksList.CHUNK_GROUP_1_AFTERIDHR)
        currentChunkGroup = ChunksList.CHUNK_GROUP_1_AFTERIDHR;
      else if (currentChunkGroup <= ChunksList.CHUNK_GROUP_3_AFTERPLTE)
        currentChunkGroup = ChunksList.CHUNK_GROUP_3_AFTERPLTE;
      else
        currentChunkGroup = ChunksList.CHUNK_GROUP_5_AFTERIDAT;
    }
  }

  @Override
  public boolean shouldSkipContent(int len, String id) {
    if (super.shouldSkipContent(len, id))
      return true;
    if (ChunkHelper.isCritical(id))
      return false;// critical chunks are never skipped
    if (maxTotalBytesRead > 0 && len + getBytesCount() > maxTotalBytesRead)
      throw new PngjInputException("Maximum total bytes to read exceeeded: " + maxTotalBytesRead
          + " offset:" + getBytesCount() + " len=" + len);
    if (chunksToSkip.contains(id))
      return true; // specific skip
    if (skipChunkMaxSize > 0 && len > skipChunkMaxSize)
      return true; // too big chunk
    if (maxBytesMetadata > 0 && len > maxBytesMetadata - bytesAncChunksLoaded)
      return true; // too much ancillary chunks loaded
    switch (chunkLoadBehaviour) {
      case LOAD_CHUNK_IF_SAFE:
        if (!ChunkHelper.isSafeToCopy(id))
          return true;
        break;
      case LOAD_CHUNK_NEVER:
        return true;
      default:
        break;
    }
    return false;
  }

  public long getBytesChunksLoaded() {
    return bytesAncChunksLoaded;
  }

  public int getCurrentChunkGroup() {
    return currentChunkGroup;
  }

  public void setChunksToSkip(String... chunksToSkip) {
    this.chunksToSkip.clear();
    for (String c : chunksToSkip)
      this.chunksToSkip.add(c);
  }

  public void addChunkToSkip(String chunkToSkip) {
    this.chunksToSkip.add(chunkToSkip);
  }

  public void dontSkipChunk(String chunkToSkip) {
    this.chunksToSkip.remove(chunkToSkip);
  }

  public boolean firstChunksNotYetRead() {
    return getCurrentChunkGroup() < ChunksList.CHUNK_GROUP_4_IDAT;
  }

  @Override
  protected void postProcessChunk(ChunkReader chunkR) {
    super.postProcessChunk(chunkR);
    if (chunkR.getChunkRaw().id.equals(PngChunkIHDR.ID)) {
      PngChunkIHDR ch = new PngChunkIHDR(null);
      ch.parseFromRaw(chunkR.getChunkRaw());
      imageInfo = ch.createImageInfo();
      curImageInfo = imageInfo;
      if (ch.isInterlaced())
        deinterlacer = new Deinterlacer(curImageInfo);
      chunksList = new ChunksList(imageInfo);
    }
    if (chunkR.mode == ChunkReaderMode.BUFFER && countChunkTypeAsAncillary(chunkR.getChunkRaw().id)) {
      bytesAncChunksLoaded += chunkR.getChunkRaw().len;
    }
    if (chunkR.mode == ChunkReaderMode.BUFFER || includeNonBufferedChunks) {
      PngChunk chunk = chunkFactory.createChunk(chunkR.getChunkRaw(), getImageInfo());
      chunksList.appendReadChunk(chunk, currentChunkGroup);
    }
    if (isDone()) {
      processEndPng();
    }
  }

  protected boolean countChunkTypeAsAncillary(String id) {
    return !ChunkHelper.isCritical(id);
  }

  @Override
  protected DeflatedChunksSet createIdatSet(String id) {
    IdatSet ids = new IdatSet(id, getCurImgInfo(), deinterlacer);
    ids.setCallbackMode(callbackMode);
    return ids;
  }

  public IdatSet getIdatSet() {
    DeflatedChunksSet c = getCurReaderDeflatedSet();
    return c instanceof IdatSet ? (IdatSet) c : null;
  }

  @Override
  protected boolean isIdatKind(String id) {
    return id.equals(PngChunkIDAT.ID);
  }

  @Override
  public int consume(byte[] buf, int off, int len) {
    return super.consume(buf, off, len);
  }

  /**
   * sets a custom chunk factory. This is typically called with a custom class extends ChunkFactory, to adds custom
   * chunks to the default well-know ones
   * 
   * @param chunkFactory
   */
  public void setChunkFactory(IChunkFactory chunkFactory) {
    this.chunkFactory = chunkFactory;
  }

  /**
   * Things to be done after IEND processing. This is not called if prematurely closed.
   */
  protected void processEndPng() {
    // nothing to do
  }

  public ImageInfo getImageInfo() {
    return imageInfo;
  }

  public boolean isInterlaced() {
    return deinterlacer != null;
  }

  public Deinterlacer getDeinterlacer() {
    return deinterlacer;
  }

  @Override
  protected void startNewChunk(int len, String id, long offset) {
    updateAndCheckChunkGroup(id);
    super.startNewChunk(len, id, offset);
  }

  @Override
  public void close() {
    if (currentChunkGroup != ChunksList.CHUNK_GROUP_6_END)// this could only happen if forced close
      currentChunkGroup = ChunksList.CHUNK_GROUP_6_END;
    super.close();
  }

  public List<PngChunk> getChunks() {
    return chunksList.getChunks();
  }

  public void setMaxTotalBytesRead(long maxTotalBytesRead) {
    this.maxTotalBytesRead = maxTotalBytesRead;
  }

  public long getSkipChunkMaxSize() {
    return skipChunkMaxSize;
  }

  public void setSkipChunkMaxSize(long skipChunkMaxSize) {
    this.skipChunkMaxSize = skipChunkMaxSize;
  }

  public long getMaxBytesMetadata() {
    return maxBytesMetadata;
  }

  public void setMaxBytesMetadata(long maxBytesMetadata) {
    this.maxBytesMetadata = maxBytesMetadata;
  }

  public long getMaxTotalBytesRead() {
    return maxTotalBytesRead;
  }

  @Override
  protected boolean shouldCheckCrc(int len, String id) {
    return checkCrc;
  }

  public boolean isCheckCrc() {
    return checkCrc;
  }

  public void setCheckCrc(boolean checkCrc) {
    this.checkCrc = checkCrc;
  }

  public boolean isCallbackMode() {
    return callbackMode;
  }

  public Set<String> getChunksToSkip() {
    return chunksToSkip;
  }

  public void setChunkLoadBehaviour(ChunkLoadBehaviour chunkLoadBehaviour) {
    this.chunkLoadBehaviour = chunkLoadBehaviour;
  }

  public ImageInfo getCurImgInfo() {
    return curImageInfo;
  }

  public void updateCurImgInfo(ImageInfo iminfo) {
    if (!iminfo.equals(curImageInfo)) {
      curImageInfo = iminfo;
    }
    if (deinterlacer != null)
      deinterlacer = new Deinterlacer(curImageInfo); // we could reset it, but...
  }

  /**
   * If true, the chunks with no data (because skipped or because processed like IDAT-type) are still stored in the
   * PngChunks list, which might be more informative.
   * 
   * Setting this to false saves a few bytes
   * 
   * Default: false
   * 
   * @param includeNonBufferedChunks
   */
  public void setIncludeNonBufferedChunks(boolean includeNonBufferedChunks) {
    this.includeNonBufferedChunks = includeNonBufferedChunks;
  }



}
