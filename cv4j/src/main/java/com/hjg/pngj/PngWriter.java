package com.hjg.pngj;

import java.io.File;
import java.io.OutputStream;
import java.util.List;

import com.hjg.pngj.chunks.ChunkCopyBehaviour;
import com.hjg.pngj.chunks.ChunkPredicate;
import com.hjg.pngj.chunks.ChunksList;
import com.hjg.pngj.chunks.ChunksListForWrite;
import com.hjg.pngj.chunks.PngChunk;
import com.hjg.pngj.chunks.PngChunkIEND;
import com.hjg.pngj.chunks.PngChunkIHDR;
import com.hjg.pngj.chunks.PngChunkPLTE;
import com.hjg.pngj.chunks.PngMetadata;
import com.hjg.pngj.pixels.PixelsWriter;
import com.hjg.pngj.pixels.PixelsWriterDefault;

/**
 * Writes a PNG image, line by line.
 */
public class PngWriter {

  public final ImageInfo imgInfo;

  /**
   * last writen row number, starting from 0
   */
  protected int rowNum = -1;

  private final ChunksListForWrite chunksList;

  private final PngMetadata metadata;

  /**
   * Current chunk grounp, (0-6) already written or currently writing (this is advanced when just starting to write the
   * new group, not when finalizing the previous)
   * <p>
   * see {@link ChunksList}
   */
  protected int currentChunkGroup = -1;

  private int passes = 1; // Some writes might require two passes (NOT USED STILL)
  private int currentpass = 0; // numbered from 1

  private boolean shouldCloseStream = true;

  private int idatMaxSize = 0; // 0=use default (PngIDatChunkOutputStream 64k)
  // private PngIDatChunkOutputStream datStream;

  protected PixelsWriter pixelsWriter;

  private final OutputStream os;

  private ChunkPredicate copyFromPredicate = null;
  private ChunksList copyFromList = null;

  protected StringBuilder debuginfo = new StringBuilder();

  /**
   * Opens a file for writing.
   * <p>
   * Sets shouldCloseStream=true. For more info see {@link #PngWriter(OutputStream, ImageInfo)}
   * 
   * @param file
   * @param imgInfo
   * @param allowoverwrite If false and file exists, an {@link PngjOutputException} is thrown
   */
  public PngWriter(File file, ImageInfo imgInfo, boolean allowoverwrite) {
    this(PngHelperInternal.ostreamFromFile(file, allowoverwrite), imgInfo);
    setShouldCloseStream(true);
  }

  /**
   * @see #PngWriter(File, ImageInfo, boolean) (overwrite=true)
   */
  public PngWriter(File file, ImageInfo imgInfo) {
    this(file, imgInfo, true);
  }

  /**
   * Constructs a new PngWriter from a output stream. After construction nothing is writen yet. You still can set some
   * parameters (compression, filters) and queue chunks before start writing the pixels.
   * <p>
   * 
   * @param outputStream Open stream for binary writing
   * @param imgInfo Basic image parameters
   */
  public PngWriter(OutputStream outputStream, ImageInfo imgInfo) {
    this.os = outputStream;
    this.imgInfo = imgInfo;
    // prealloc
    chunksList = new ChunksListForWrite(imgInfo);
    metadata = new PngMetadata(chunksList);
    pixelsWriter = createPixelsWriter(imgInfo);
    setCompLevel(9);
  }

  private void initIdat() { // this triggers the writing of first chunks
    pixelsWriter.setOs(this.os);
    pixelsWriter.setIdatMaxSize(idatMaxSize);
    writeSignatureAndIHDR();
    writeFirstChunks();
  }

  private void writeEndChunk() {
    currentChunkGroup = ChunksList.CHUNK_GROUP_6_END;
    PngChunkIEND c = new PngChunkIEND(imgInfo);
    c.createRawChunk().writeChunk(os);
    chunksList.getChunks().add(c);
  }

  private void writeFirstChunks() {
    if (currentChunkGroup >= ChunksList.CHUNK_GROUP_4_IDAT)
      return;
    int nw = 0;
    currentChunkGroup = ChunksList.CHUNK_GROUP_1_AFTERIDHR;
    queueChunksFromOther();
    nw = chunksList.writeChunks(os, currentChunkGroup);
    currentChunkGroup = ChunksList.CHUNK_GROUP_2_PLTE;
    nw = chunksList.writeChunks(os, currentChunkGroup);
    if (nw > 0 && imgInfo.greyscale)
      throw new PngjOutputException("cannot write palette for this format");
    if (nw == 0 && imgInfo.indexed)
      throw new PngjOutputException("missing palette");
    currentChunkGroup = ChunksList.CHUNK_GROUP_3_AFTERPLTE;
    nw = chunksList.writeChunks(os, currentChunkGroup);
  }

  private void writeLastChunks() { // not including end
    currentChunkGroup = ChunksList.CHUNK_GROUP_5_AFTERIDAT;
    queueChunksFromOther();
    chunksList.writeChunks(os, currentChunkGroup);
    // should not be unwriten chunks
    List<PngChunk> pending = chunksList.getQueuedChunks();
    if (!pending.isEmpty())
      throw new PngjOutputException(pending.size() + " chunks were not written! Eg: "
          + pending.get(0).toString());
  }

  /**
   * Write id signature and also "IHDR" chunk
   */
  private void writeSignatureAndIHDR() {
    PngHelperInternal.writeBytes(os, PngHelperInternal.getPngIdSignature()); // signature
    currentChunkGroup = ChunksList.CHUNK_GROUP_0_IDHR;
    PngChunkIHDR ihdr = new PngChunkIHDR(imgInfo);
    // http://www.libpng.org/pub/png/spec/1.2/PNG-Chunks.html
    ihdr.createRawChunk().writeChunk(os);
    chunksList.getChunks().add(ihdr);
  }

  private void queueChunksFromOther() {
    if (copyFromList == null || copyFromPredicate == null)
      return;
    boolean idatDone = currentChunkGroup >= ChunksList.CHUNK_GROUP_4_IDAT; // we assume this method is not either before
                                                                           // or after the IDAT writing, not in the
                                                                           // middle!
    for (PngChunk chunk : copyFromList.getChunks()) {
      if (chunk.getRaw().data == null)
        continue; // we cannot copy skipped chunks?
      int groupOri = chunk.getChunkGroup();
      if (groupOri <= ChunksList.CHUNK_GROUP_4_IDAT && idatDone)
        continue;
      if (groupOri >= ChunksList.CHUNK_GROUP_4_IDAT && !idatDone)
        continue;
      if (chunk.crit && !chunk.id.equals(PngChunkPLTE.ID))
        continue; // critical chunks (except perhaps PLTE) are never
      // copied
      boolean copy = copyFromPredicate.match(chunk);
      if (copy) {
        // but if the chunk is already queued or writen, it's ommited!
        if (chunksList.getEquivalent(chunk).isEmpty()
            && chunksList.getQueuedEquivalent(chunk).isEmpty()) {
          chunksList.queue(chunk);
        }
      }
    }
  }

/**
   * Queues an ancillary chunk for writing. 
   * <p>
   * If a "equivalent" chunk is already queued (see {@link ChunkHelper#equivalent(PngChunk, PngChunk)), this overwrites it. 
   * <p>
   * The chunk will be written as late as possible, unless the priority is set.
   * 
   * @param chunk
   */
  public void queueChunk(PngChunk chunk) {
    for (PngChunk other : chunksList.getQueuedEquivalent(chunk)) {
      getChunksList().removeChunk(other);
    }
    chunksList.queue(chunk);
  }

  /**
   * Sets an origin (typically from a {@link PngReader}) of Chunks to be copied. This should be called only once, before
   * starting writing the rows. It doesn't matter the current state of the PngReader reading, this is a live object and
   * what matters is that when the writer writes the pixels (IDAT) the reader has already read them, and that when the
   * writer ends, the reader is already ended (all this is very natural).
   * <p>
   * Apart from the copyMask, there is some addional heuristics:
   * <p>
   * - The chunks will be queued, but will be written as late as possible (unless you explicitly set priority=true)
   * <p>
   * - The chunk will not be queued if an "equivalent" chunk was already queued explicitly. And it will be overwriten
   * another is queued explicitly.
   * 
   * @param chunks
   * @param copyMask Some bitmask from {@link ChunkCopyBehaviour}
   * 
   * @see #copyChunksFrom(ChunksList, ChunkPredicate)
   */
  public void copyChunksFrom(ChunksList chunks, int copyMask) {
    copyChunksFrom(chunks, ChunkCopyBehaviour.createPredicate(copyMask, imgInfo));
  }

  /**
   * Copy all chunks from origin. See {@link #copyChunksFrom(ChunksList, int)} for more info
   */
  public void copyChunksFrom(ChunksList chunks) {
    copyChunksFrom(chunks, ChunkCopyBehaviour.COPY_ALL);
  }

  /**
   * Copy chunks from origin depending on some {@link ChunkPredicate}
   * 
   * @param chunks
   * @param predicate The chunks (ancillary or PLTE) will be copied if and only if predicate matches
   * 
   * @see #copyChunksFrom(ChunksList, int) for more info
   */
  public void copyChunksFrom(ChunksList chunks, ChunkPredicate predicate) {
    if (copyFromList != null && chunks != null)
      PngHelperInternal.LOGGER.warning("copyChunksFrom should only be called once");
    if (predicate == null)
      throw new PngjOutputException("copyChunksFrom requires a predicate");
    this.copyFromList = chunks;
    this.copyFromPredicate = predicate;
  }

  /**
   * Computes compressed size/raw size, approximate.
   * <p>
   * Actually: compressed size = total size of IDAT data , raw size = uncompressed pixel bytes = rows * (bytesPerRow +
   * 1).
   * 
   * This must be called after pngw.end()
   */
  public double computeCompressionRatio() {
    if (currentChunkGroup < ChunksList.CHUNK_GROUP_5_AFTERIDAT)
      throw new PngjOutputException("must be called after end()");
    return pixelsWriter.getCompression();
  }

  /**
   * Finalizes all the steps and closes the stream. This must be called after writing the lines. Idempotent
   */
  public void end() {
    if (rowNum != imgInfo.rows - 1 || !pixelsWriter.isDone())
      throw new PngjOutputException("all rows have not been written");
    try {
      if (pixelsWriter != null)
        pixelsWriter.close();
      if (currentChunkGroup < ChunksList.CHUNK_GROUP_5_AFTERIDAT)
        writeLastChunks();
      if (currentChunkGroup < ChunksList.CHUNK_GROUP_6_END)
        writeEndChunk();
    } finally {
      close();
    }
  }

  /**
   * Closes and releases resources
   * <p>
   * This is normally called internally from {@link #end()}, you should only call this for aborting the writing and
   * release resources (close the stream).
   * <p>
   * Idempotent and secure - never throws exceptions
   */
  public void close() {
    if (pixelsWriter != null)
      pixelsWriter.close();
    if (shouldCloseStream && os != null)
      try {
        os.close();
      } catch (Exception e) {
        PngHelperInternal.LOGGER.warning("Error closing writer " + e.toString());
      }
  }

  /**
   * returns the chunks list (queued and writen chunks)
   */
  public ChunksListForWrite getChunksList() {
    return chunksList;
  }

  /**
   * Retruns a high level wrapper over for metadata handling
   */
  public PngMetadata getMetadata() {
    return metadata;
  }

  /**
   * Sets internal prediction filter type, or strategy to choose it.
   * <p>
   * This must be called just after constructor, before starting writing.
   * <p>
   */
  public void setFilterType(FilterType filterType) {
    pixelsWriter.setFilterType(filterType);
  }

  /**
   * This is kept for backwards compatibility, now the PixelsWriter object should be used for setting
   * compression/filtering options
   * 
   * @see PixelsWriter#setCompressionFactor(double)
   * @param compLevel between 0 (no compression, max speed) and 9 (max compression)
   */
  public void setCompLevel(int complevel) {
    pixelsWriter.setDeflaterCompLevel(complevel);
  }

  /**
	 * 
	 */
  public void setFilterPreserve(boolean filterPreserve) {
    if (filterPreserve)
      pixelsWriter.setFilterType(FilterType.FILTER_PRESERVE);
    else if (pixelsWriter.getFilterType() == null)
      pixelsWriter.setFilterType(FilterType.FILTER_DEFAULT);
  }

  /**
   * Sets maximum size of IDAT fragments. Incrementing this from the default has very little effect on compression and
   * increments memory usage. You should rarely change this.
   * <p>
   * 
   * @param idatMaxSize default=0 : use defaultSize (32K)
   */
  public void setIdatMaxSize(int idatMaxSize) {
    this.idatMaxSize = idatMaxSize;
  }

  /**
   * If true, output stream will be closed after ending write
   * <p>
   * default=true
   */
  public void setShouldCloseStream(boolean shouldCloseStream) {
    this.shouldCloseStream = shouldCloseStream;
  }

  /**
   * Writes next row, does not check row number.
   * 
   * @param imgline
   */
  public void writeRow(IImageLine imgline) {
    writeRow(imgline, rowNum + 1);
  }

  /**
   * Writes the full set of row. The ImageLineSet should contain (allow to acces) imgInfo.rows
   */
  public void writeRows(IImageLineSet<? extends IImageLine> imglines) {
    for (int i = 0; i < imgInfo.rows; i++)
      writeRow(imglines.getImageLineRawNum(i));
  }

  public void writeRow(IImageLine imgline, int rownumber) {
    rowNum++;
    if (rowNum == imgInfo.rows)
      rowNum = 0;
    if (rownumber == imgInfo.rows)
      rownumber = 0;
    if (rownumber >= 0 && rowNum != rownumber)
      throw new PngjOutputException("rows must be written in order: expected:" + rowNum
          + " passed:" + rownumber);
    if (rowNum == 0)
      currentpass++;
    if (rownumber == 0 && currentpass == passes) {
      initIdat();
      currentChunkGroup = ChunksList.CHUNK_GROUP_4_IDAT; // we just begin writing IDAT
    }
    byte[] rowb = pixelsWriter.getRowb();
    imgline.writeToPngRaw(rowb);
    pixelsWriter.processRow(rowb);

  }

  /**
   * Utility method, uses internaly a ImageLineInt
   */
  public void writeRowInt(int[] buf) {
    writeRow(new ImageLineInt(imgInfo, buf));
  }

  /**
   * Factory method for pixels writer. This will be called once at the moment at start writing a set of IDAT chunks
   * (typically once in a normal PNG)
   * 
   * This should be overriden if custom filtering strategies are desired. Remember to release this with close()
   * 
   * @param imginfo Might be different than that of this object (eg: APNG with subimages)
   * @param os Output stream
   * @return new PixelsWriter. Don't forget to call close() when discarding it
   */
  protected PixelsWriter createPixelsWriter(ImageInfo imginfo) {
    PixelsWriterDefault pw = new PixelsWriterDefault(imginfo);
    return pw;
  }

  public final PixelsWriter getPixelsWriter() {
    return pixelsWriter;
  }

  public String getDebuginfo() {
    return debuginfo.toString();
  }

}
