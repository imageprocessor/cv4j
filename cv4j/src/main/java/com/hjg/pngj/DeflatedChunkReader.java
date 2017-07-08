package com.hjg.pngj;

import ar.com.hjg.pngj.chunks.PngChunkFDAT;

/**
 * 
 * Specialization of ChunkReader, for IDAT-like chunks. These chunks are part of a set of similar chunks (contiguos
 * normally, not necessariyl) which conforms a zlib stream
 */
public class DeflatedChunkReader extends ChunkReader {

  protected final DeflatedChunksSet deflatedChunksSet;
  protected boolean alsoBuffer = false;

  protected boolean skipBytes = false; // fDAT (APNG) skips 4 bytes)
  protected byte[] skippedBytes; // only for fDAT
  protected int seqNumExpected = -1; // only for fDAT

  public DeflatedChunkReader(int clen, String chunkid, boolean checkCrc, long offsetInPng,
      DeflatedChunksSet iDatSet) {
    super(clen, chunkid, offsetInPng, ChunkReaderMode.PROCESS);
    this.deflatedChunksSet = iDatSet;
    if (chunkid.equals(PngChunkFDAT.ID)) {
      skipBytes = true;
      skippedBytes = new byte[4];
    }
    iDatSet.appendNewChunk(this);
  }

  /**
   * Delegates to ChunkReaderDeflatedSet.processData()
   */
  @Override
  protected void processData(int offsetInchunk, byte[] buf, int off, int len) {
    if (skipBytes && offsetInchunk < 4) {// only for APNG (sigh)
      for (int oc = offsetInchunk; oc < 4 && len > 0; oc++, off++, len--)
        skippedBytes[oc] = buf[off];
    }
    if (len > 0) { // delegate to idatSet
      deflatedChunksSet.processBytes(buf, off, len);
      if (alsoBuffer) { // very rare!
        System.arraycopy(buf, off, getChunkRaw().data, read, len);
      }
    }
  }

  /**
   * only a stupid check for fDAT (I wonder how many APGN readers do this)
   */
  @Override
  protected void chunkDone() {
    if (skipBytes && getChunkRaw().id.equals(PngChunkFDAT.ID)) {
      if (seqNumExpected >= 0) {
        int seqNum = PngHelperInternal.readInt4fromBytes(skippedBytes, 0);
        if (seqNum != seqNumExpected)
          throw new PngjInputException("bad chunk sequence for fDAT chunk " + seqNum + " expected "
              + seqNumExpected);
      }
    }
  }

  @Override
  public boolean isFromDeflatedSet() {
    return true;
  }

  /**
   * In some rare cases you might want to also buffer the data?
   */
  public void setAlsoBuffer() {
    if (read > 0)
      throw new RuntimeException("too late");
    alsoBuffer = true;
    getChunkRaw().allocData();
  }

  /** only relevant for fDAT */
  public void setSeqNumExpected(int seqNumExpected) {
    this.seqNumExpected = seqNumExpected;
  }


}
