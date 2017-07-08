package com.hjg.pngj;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import ar.com.hjg.pngj.chunks.PngChunk;
import ar.com.hjg.pngj.chunks.PngChunkACTL;
import ar.com.hjg.pngj.chunks.PngChunkFCTL;
import ar.com.hjg.pngj.chunks.PngChunkFDAT;
import ar.com.hjg.pngj.chunks.PngChunkIDAT;

/**
 */
public class PngReaderApng extends PngReaderByte {

  public PngReaderApng(File file) {
    super(file);
    dontSkipChunk(PngChunkFCTL.ID);
  }

  public PngReaderApng(InputStream inputStream) {
    super(inputStream);
    dontSkipChunk(PngChunkFCTL.ID);
  }

  private Boolean apngKind = null;
  private boolean firsIdatApngFrame = false;
  protected PngChunkACTL actlChunk; // null if not APNG
  private PngChunkFCTL fctlChunk; // current (null for the pseudo still frame)

  /**
   * Current frame number (reading or read). First animated frame is 0. Frame -1 represents the IDAT (default image)
   * when it's not part of the animation
   */
  protected int frameNum = -1; // incremented after each fctl finding

  public boolean isApng() {
    if (apngKind == null) {
      // this triggers the loading of first chunks;
      actlChunk = (PngChunkACTL) getChunksList().getById1(PngChunkACTL.ID); // null if not apng
      apngKind = actlChunk != null;
      firsIdatApngFrame = fctlChunk != null;

    }
    return apngKind.booleanValue();
  }


  public void advanceToFrame(int frame) {
    if (frame < frameNum)
      throw new PngjInputException("Cannot go backwards");
    if (frame >= getApngNumFrames())
      throw new PngjInputException("Frame out of range " + frame);
    if (frame > frameNum) {
      addChunkToSkip(PngChunkIDAT.ID);
      addChunkToSkip(PngChunkFDAT.ID);
      if (chunkseq.getIdatSet() != null && !chunkseq.getIdatSet().isDone())
        chunkseq.getIdatSet().done(); // seems to be necessary sometimes (we should check this)
      while (frameNum < frame & !chunkseq.isDone())
        if (streamFeeder.feed(chunkseq) <= 0)
          break;
    }
    if (frame == frameNum) { // prepare to read rows. at this point we have a new
      dontSkipChunk(PngChunkIDAT.ID);
      dontSkipChunk(PngChunkFDAT.ID);
      rowNum = -1;
      imlinesSet = null;// force recreation (this is slightly dirty)
      // seek the next IDAT/fDAT - TODO: set the expected sequence number
      while (!chunkseq.isDone() && !chunkseq.getCurChunkReader().isFromDeflatedSet())
        if (streamFeeder.feed(chunkseq) <= 0)
          break;
    } else {
      throw new PngjInputException("unexpected error seeking from frame " + frame);
    }
  }

  /**
   * True if it has a default image (IDAT) that is not part of the animation. In that case, we consider it as a
   * pseudo-frame (number -1)
   */
  public boolean hasExtraStillImage() {
    return isApng() && !firsIdatApngFrame;
  }

  /**
   * Only counts true animation frames.
   */
  public int getApngNumFrames() {
    if (isApng())
      return actlChunk.getNumFrames();
    else
      return 0;
  }

  /**
   * 0 if it's to been played infinitely. -1 if not APNG
   */
  public int getApngNumPlays() {
    if (isApng())
      return actlChunk.getNumPlays();
    else
      return -1;
  }

  @Override
  public IImageLine readRow() {
    // TODO Auto-generated method stub
    return super.readRow();
  }

  @Override
  public boolean hasMoreRows() {
    // TODO Auto-generated method stub
    return super.hasMoreRows();
  }

  @Override
  public IImageLine readRow(int nrow) {
    // TODO Auto-generated method stub
    return super.readRow(nrow);
  }

  @Override
  public IImageLineSet<? extends IImageLine> readRows() {
    // TODO Auto-generated method stub
    return super.readRows();
  }

  @Override
  public IImageLineSet<? extends IImageLine> readRows(int nRows, int rowOffset, int rowStep) {
    // TODO Auto-generated method stub
    return super.readRows(nRows, rowOffset, rowStep);
  }

  @Override
  public void readSkippingAllRows() {
    // TODO Auto-generated method stub
    super.readSkippingAllRows();
  }

  @Override
  protected ChunkSeqReaderPng createChunkSeqReader() {
    ChunkSeqReaderPng cr = new ChunkSeqReaderPng(false) {

      @Override
      public boolean shouldSkipContent(int len, String id) {
        return super.shouldSkipContent(len, id);
      }

      @Override
      protected boolean isIdatKind(String id) {
        return id.equals(PngChunkIDAT.ID) || id.equals(PngChunkFDAT.ID);
      }

      @Override
      protected DeflatedChunksSet createIdatSet(String id) {
        IdatSet ids = new IdatSet(id, getCurImgInfo(), deinterlacer);
        ids.setCallbackMode(callbackMode);
        return ids;
      }


      @Override
      protected void startNewChunk(int len, String id, long offset) {
        super.startNewChunk(len, id, offset);
      }

      @Override
      protected void postProcessChunk(ChunkReader chunkR) {
        super.postProcessChunk(chunkR);
        if (chunkR.getChunkRaw().id.equals(PngChunkFCTL.ID)) {
          frameNum++;
          List<PngChunk> chunkslist = chunkseq.getChunks();
          fctlChunk = (PngChunkFCTL) chunkslist.get(chunkslist.size() - 1);
          // as this is slightly dirty, we check
          if (chunkR.getChunkRaw().getOffset() != fctlChunk.getRaw().getOffset())
            throw new PngjInputException("something went wrong");
          ImageInfo frameInfo = fctlChunk.getEquivImageInfo();
          getChunkseq().updateCurImgInfo(frameInfo);
        }
      }

      @Override
      protected boolean countChunkTypeAsAncillary(String id) {
        // we don't count fdat as ancillary data
        return super.countChunkTypeAsAncillary(id) && !id.equals(id.equals(PngChunkFDAT.ID));
      }

    };
    return cr;
  }

  /**
   * @see #frameNum
   */
  public int getFrameNum() {
    return frameNum;
  }

  @Override
  public void end() {
    // TODO Auto-generated method stub
    super.end();
  }

  public PngChunkFCTL getFctl() {
    return fctlChunk;
  }



}
