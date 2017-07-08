package com.hjg.pngj;

import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

/**
 * A set of IDAT-like chunks which, concatenated, form a zlib stream.
 * <p>
 * The inflated stream is intented to be read as a sequence of "rows", of which the caller knows the lengths (not
 * necessary equal) and number.
 * <p>
 * Eg: For IDAT non-interlaced images, a row has bytesPerRow + 1 filter byte<br>
 * For interlaced images, the lengths are variable.
 * <p>
 * This class can work in sync (polled) mode or async (callback) mode. But for callback mode the method
 * processRowCallback() must be overriden
 * <p>
 * See {@link IdatSet}, which is mostly used and has a slightly simpler use.<br>
 * See <code>DeflatedChunkSetTest</code> for example of use.
 */
public class DeflatedChunksSet {

  protected byte[] row; // a "row" here means a raw (uncopressed filtered) part of the IDAT stream,
                        // normally a image row (or subimage row for interlaced) plus a filter byte
  private int rowfilled; // effective/valid length of row
  private int rowlen; // what amount of bytes is to be interpreted as a complete "row". can change
                      // (for interlaced)
  private int rown; // only coincide with image row if non-interlaced - incremented by
                    // setNextRowSize()

  /*
   * States WAITING_FOR_INPUT ROW_READY WORK_DONE TERMINATED
   * 
   * processBytes() is externally called, prohibited in READY (in DONE it's ignored)
   * 
   * WARNING: inflater.finished() != DONE (not enough, not neccesary) DONE means that we have already uncompressed all
   * the data of interest.
   * 
   * In non-callback mode, prepareForNextRow() is also externally called, in
   * 
   * Flow: - processBytes() calls inflateData() - inflateData() : if buffer is filled goes to READY else if !
   * inf.finished goes to WAITING else if any data goes to READY (incomplete data to be read) else goes to DONE - in
   * Callback mode, after going to READY, n=processCallback() is called and then prepareForNextRow(n) is called. - in
   * Polled mode, prepareForNextRow(n) must be called from outside (after checking state=READY) - prepareForNextRow(n)
   * goes to DONE if n==0 calls inflateData() again - end() goes to DONE
   */
  private enum State {
    WAITING_FOR_INPUT, // waiting for more input
    ROW_READY, // ready for consumption (might be less than fully filled), ephemeral for CALLBACK
               // mode
    WORK_DONE, // all data of interest has been read, but we might accept still more trailing chunks
               // (we'll ignore them)
    TERMINATED; // we are done, and also won't accept more IDAT chunks

    public boolean isDone() {
      return this == WORK_DONE || this == TERMINATED;
    } // the caller has already uncompressed all the data of interest or EOF

    public boolean isTerminated() {
      return this == TERMINATED;
    } // we dont accept more chunks
  }

  State state = State.WAITING_FOR_INPUT; // never null

  private Inflater inf;
  private final boolean infOwn; // true if we own the inflater (we created it)

  private DeflatedChunkReader curChunk;

  private boolean callbackMode = true;
  private long nBytesIn = 0; // count the total compressed bytes that have been fed
  private long nBytesOut = 0; // count the total uncompressed bytes
  int chunkNum = -1; // incremented at each new chunk start
  int firstChunqSeqNum = -1; // expected seq num for first chunk. used only for fDAT (APNG)

  /**
   * All IDAT-like chunks that form a same DeflatedChunksSet should have the same id
   */
  public final String chunkid;

  /**
   * @param initialRowLen Length in bytes of first "row" (see description)
   * @param maxRowLen Max length in bytes of "rows"
   * @param inflater Can be null. If not null, must be already reset (and it must be closed/released by caller!)
   */
  public DeflatedChunksSet(String chunkid, int initialRowLen, int maxRowLen, Inflater inflater,
      byte[] buffer) {
    this.chunkid = chunkid;
    this.rowlen = initialRowLen;
    if (initialRowLen < 1 || maxRowLen < initialRowLen)
      throw new PngjException("bad inital row len " + initialRowLen);
    if (inflater != null) {
      this.inf = inflater;
      infOwn = false;
    } else {
      this.inf = new Inflater();
      infOwn = true; // inflater is own, we will release on close()
    }
    this.row = buffer != null && buffer.length >= initialRowLen ? buffer : new byte[maxRowLen];
    rown = -1;
    this.state = State.WAITING_FOR_INPUT;
    try {
      prepareForNextRow(initialRowLen);
    } catch (RuntimeException e) {
      close();
      throw e;
    }
  }

  public DeflatedChunksSet(String chunkid, int initialRowLen, int maxRowLen) {
    this(chunkid, initialRowLen, maxRowLen, null, null);
  }

  protected void appendNewChunk(DeflatedChunkReader cr) {
    // all chunks must have same id
    if (!this.chunkid.equals(cr.getChunkRaw().id))
      throw new PngjInputException("Bad chunk inside IdatSet, id:" + cr.getChunkRaw().id
          + ", expected:" + this.chunkid);
    this.curChunk = cr;
    chunkNum++;
    if (firstChunqSeqNum >= 0)
      cr.setSeqNumExpected(chunkNum + firstChunqSeqNum);
  }

  /**
   * Feeds the inflater with the compressed bytes
   * 
   * In poll mode, the caller should not call repeatedly this, without consuming first, checking
   * isDataReadyForConsumer()
   * 
   * @param buf
   * @param off
   * @param len
   */
  protected void processBytes(byte[] buf, int off, int len) {
    nBytesIn += len;
    // PngHelperInternal.LOGGER.info("processing compressed bytes in chunkreader : " + len);
    if (len < 1 || state.isDone())
      return;
    if (state == State.ROW_READY)
      throw new PngjInputException("this should only be called if waitingForMoreInput");
    if (inf.needsDictionary() || !inf.needsInput())
      throw new RuntimeException("should not happen");
    inf.setInput(buf, off, len);
    // PngHelperInternal.debug("entering processs bytes, state=" + state +
    // " callback="+callbackMode);
    if (isCallbackMode()) {
      while (inflateData()) {
        int nextRowLen = processRowCallback();
        prepareForNextRow(nextRowLen);
        if (isDone())
          processDoneCallback();
      }
    } else
      inflateData();
  }

  /*
   * This never inflates more than one row This returns true if this has resulted in a row being ready and preprocessed
   * with preProcessRow (in callback mode, we should call immediately processRowCallback() and
   * prepareForNextRow(nextRowLen)
   */
  private boolean inflateData() {
    try {
      // PngHelperInternal.debug("entering inflateData bytes, state=" + state +
      // " callback="+callbackMode);
      if (state == State.ROW_READY)
        throw new PngjException("invalid state");// assert
      if (state.isDone())
        return false;
      int ninflated = 0;
      if (row == null || row.length < rowlen)
        row = new byte[rowlen]; // should not happen
      if (rowfilled < rowlen && !inf.finished()) {
        try {
          ninflated = inf.inflate(row, rowfilled, rowlen - rowfilled);
        } catch (DataFormatException e) {
          throw new PngjInputException("error decompressing zlib stream ", e);
        }
        rowfilled += ninflated;
        nBytesOut += ninflated;
      }
      State nextstate = null;
      if (rowfilled == rowlen)
        nextstate = State.ROW_READY; // complete row, process it
      else if (!inf.finished())
        nextstate = State.WAITING_FOR_INPUT;
      else if (rowfilled > 0)
        nextstate = State.ROW_READY; // complete row, process it
      else {
        nextstate = State.WORK_DONE; // eof, no more data
      }
      state = nextstate;
      if (state == State.ROW_READY) {
        preProcessRow();
        return true;
      }
    } catch (RuntimeException e) {
      close();
      throw e;
    }
    return false;
  }

  /**
   * Called automatically in all modes when a full row has been inflated.
   */
  protected void preProcessRow() {

  }

  /**
   * Callback, must be implemented in callbackMode
   * <p>
   * This should use {@link #getRowFilled()} and {@link #getInflatedRow()} to access the row.
   * <p>
   * Must return byes of next row, for next callback.
   */
  protected int processRowCallback() {
    throw new PngjInputException("not implemented");
  }

  /**
   * Callback, to be implemented in callbackMode
   * <p>
   * This will be called once to notify state done
   */
  protected void processDoneCallback() {}

  /**
   * Inflated buffer.
   * 
   * The effective length is given by {@link #getRowFilled()}
   */
  public byte[] getInflatedRow() {
    return row;
  }

  /**
   * Should be called after the previous row was processed
   * <p>
   * Pass 0 or negative to signal that we are done (not expecting more bytes)
   * <p>
   * This resets {@link #rowfilled}
   * <p>
   * The
   */
  public void prepareForNextRow(int len) {
    rowfilled = 0;
    rown++;
    if (len < 1) {
      rowlen = 0;
      done();
    } else if (inf.finished()) {
      rowlen = 0;
      done();
    } else {
      state = State.WAITING_FOR_INPUT;
      rowlen = len;
      if (!callbackMode)
        inflateData();
    }
  }

  /**
   * In this state, the object is waiting for more input to deflate.
   * <p>
   * Only in this state it's legal to feed this
   */
  public boolean isWaitingForMoreInput() {
    return state == State.WAITING_FOR_INPUT;
  }

  /**
   * In this state, the object is waiting the caller to retrieve inflated data
   * <p>
   * Effective length: see {@link #getRowFilled()}
   */
  public boolean isRowReady() {
    return state == State.ROW_READY;
  }

  /**
   * In this state, all relevant data has been uncompressed and retrieved (exceptionally, the reading has ended
   * prematurely).
   * <p>
   * We can still feed this object, but the bytes will be swallowed/ignored.
   */
  public boolean isDone() {
    return state.isDone();
  }

  public boolean isTerminated() {
    return state.isTerminated();
  }

  /**
   * This will be called by the owner to report us the next chunk to come. We can make our own internal changes and
   * checks. This returns true if we acknowledge the next chunk as part of this set
   */
  public boolean ackNextChunkId(String id) {
    if (state.isTerminated())
      return false;
    else if (id.equals(chunkid)) {
      return true;
    } else {
      if (!allowOtherChunksInBetween(id)) {
        if (state.isDone()) {
          if (!isTerminated())
            terminate();
          return false;
        } else {
          throw new PngjInputException("Unexpected chunk " + id + " while " + chunkid
              + " set is not done");
        }
      } else
        return true;
    }
  }

  protected void terminate() {
    close();
  }

  /**
   * This should be called when discarding this object, or for aborting. Secure, idempotent Don't use this just to
   * notify this object that it has no more work to do, see {@link #done()}
   * */
  public void close() {
    try {
      if (!state.isTerminated()) {
        state = State.TERMINATED;
      }
      if (infOwn && inf != null) {
        inf.end();// we end the Inflater only if we created it
        inf = null;
      }
    } catch (Exception e) {
    }
  }

  /**
   * Forces the DONE state, this object won't uncompress more data. It's still not terminated, it will accept more IDAT
   * chunks, but will ignore them.
   */
  public void done() {
    if (!isDone())
      state = State.WORK_DONE;
  }

  /**
   * Target size of the current row, including filter byte. <br>
   * should coincide (or be less than) with row.length
   */
  public int getRowLen() {
    return rowlen;
  }

  /** This the amount of valid bytes in the buffer */
  public int getRowFilled() {
    return rowfilled;
  }

  /**
   * Get current (last) row number.
   * <p>
   * This corresponds to the raw numeration of rows as seen by the deflater. Not the same as the real image row, if
   * interlaced.
   * 
   */
  public int getRown() {
    return rown;
  }

  /**
   * Some IDAT-like set can allow other chunks in between (APGN?).
   * <p>
   * Normally false.
   * 
   * @param id Id of the other chunk that appeared in middel of this set.
   * @return true if allowed
   */
  public boolean allowOtherChunksInBetween(String id) {
    return false;
  }

  /**
   * Callback mode = async processing
   */
  public boolean isCallbackMode() {
    return callbackMode;
  }

  public void setCallbackMode(boolean callbackMode) {
    this.callbackMode = callbackMode;
  }

  /** total number of bytes that have been fed to this object */
  public long getBytesIn() {
    return nBytesIn;
  }

  /** total number of bytes that have been uncompressed */
  public long getBytesOut() {
    return nBytesOut;
  }

  @Override
  public String toString() {
    StringBuilder sb =
        new StringBuilder("idatSet : " + curChunk.getChunkRaw().id + " state=" + state + " rows="
            + rown + " bytes=" + nBytesIn + "/" + nBytesOut);
    return sb.toString();
  }

}
