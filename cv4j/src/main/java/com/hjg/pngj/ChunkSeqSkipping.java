package com.hjg.pngj;

import java.util.ArrayList;
import java.util.List;

import ar.com.hjg.pngj.ChunkReader.ChunkReaderMode;
import ar.com.hjg.pngj.chunks.ChunkRaw;

/**
 * This simple reader skips all chunks contents and stores the chunkRaw in a list. Useful to read chunks structure.
 * 
 * Optionally the contents might be processed. This doesn't distinguish IDAT chunks
 */
public class ChunkSeqSkipping extends ChunkSeqReader {

  private List<ChunkRaw> chunks = new ArrayList<ChunkRaw>();
  private boolean skip = true;

  /**
   * @param skipAll if true, contents will be truly skipped, and CRC will not be computed
   */
  public ChunkSeqSkipping(boolean skipAll) {
    super(true);
    skip = skipAll;
  }

  public ChunkSeqSkipping() {
    this(true);
  }

  protected ChunkReader createChunkReaderForNewChunk(String id, int len, long offset, boolean skip) {
    return new ChunkReader(len, id, offset, skip ? ChunkReaderMode.SKIP : ChunkReaderMode.PROCESS) {
      @Override
      protected void chunkDone() {
        postProcessChunk(this);
      }

      @Override
      protected void processData(int offsetinChhunk, byte[] buf, int off, int len) {
        processChunkContent(getChunkRaw(), offsetinChhunk, buf, off, len);
      }
    };
  }

  protected void processChunkContent(ChunkRaw chunkRaw, int offsetinChhunk, byte[] buf, int off,
      int len) {
    // does nothing
  }

  @Override
  protected void postProcessChunk(ChunkReader chunkR) {
    super.postProcessChunk(chunkR);
    chunks.add(chunkR.getChunkRaw());
  }

  @Override
  protected boolean shouldSkipContent(int len, String id) {
    return skip;
  }

  @Override
  protected boolean isIdatKind(String id) {
    return false;
  }

  public List<ChunkRaw> getChunks() {
    return chunks;
  }

}
