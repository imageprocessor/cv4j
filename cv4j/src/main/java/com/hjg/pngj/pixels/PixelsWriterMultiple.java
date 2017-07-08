package com.hjg.pngj.pixels;

import java.util.LinkedList;
import java.util.zip.Deflater;

import com.hjg.pngj.FilterType;
import com.hjg.pngj.ImageInfo;

/** Special pixels writer for experimental super adaptive strategy */
public class PixelsWriterMultiple extends PixelsWriter {
  /**
   * unfiltered rowsperband elements, 0 is the current (rowb). This should include all rows of current band, plus one
   */
  protected LinkedList<byte[]> rows;

  /**
   * bank of compressor estimators, one for each filter and (perhaps) an adaptive strategy
   */
  protected CompressorStream[] filterBank = new CompressorStream[6];
  /**
   * stored filtered rows, one for each filter (0=none is not allocated but linked)
   */
  protected byte[][] filteredRows = new byte[5][];
  protected byte[] filteredRowTmp; //

  protected FiltersPerformance filtersPerf;
  protected int rowsPerBand = 0; // This is a 'nominal' size
  protected int rowsPerBandCurrent = 0; // lastRowInThisBand-firstRowInThisBand +1 : might be
  // smaller than rowsPerBand
  protected int rowInBand = -1;
  protected int bandNum = -1;
  protected int firstRowInThisBand, lastRowInThisBand;
  private boolean tryAdaptive = true;

  protected static final int HINT_MEMORY_DEFAULT_KB = 100;
  // we will consume about (not more than) this memory (in buffers, not counting the compressors)
  protected int hintMemoryKb = HINT_MEMORY_DEFAULT_KB;

  private int hintRowsPerBand = 1000; // default: very large number, can be changed

  private boolean useLz4 = true;

  public PixelsWriterMultiple(ImageInfo imgInfo) {
    super(imgInfo);
    filtersPerf = new FiltersPerformance(imgInfo);
    rows = new LinkedList<byte[]>();
    for (int i = 0; i < 2; i++)
      rows.add(new byte[buflen]); // we preallocate 2 rows (rowb and rowbprev)
    filteredRowTmp = new byte[buflen];
  }

  @Override
  protected void filterAndWrite(byte[] rowb) {
    if (!initdone)
      init();
    if (rowb != rows.get(0))
      throw new RuntimeException("?");
    setBandFromNewRown();
    byte[] rowbprev = rows.get(1);
    for (FilterType ftype : FilterType.getAllStandardNoneLast()) {
      // this has a special behaviour for NONE: filteredRows[0] is null, and the returned value is
      // rowb
      if (currentRow == 0 && ftype != FilterType.FILTER_NONE && ftype != FilterType.FILTER_SUB)
        continue;
      byte[] filtered = filterRowWithFilterType(ftype, rowb, rowbprev, filteredRows[ftype.val]);
      filterBank[ftype.val].write(filtered);
      if (currentRow == 0 && ftype == FilterType.FILTER_SUB) { // litle lie, only for first row
        filterBank[FilterType.FILTER_PAETH.val].write(filtered);
        filterBank[FilterType.FILTER_AVERAGE.val].write(filtered);
        filterBank[FilterType.FILTER_UP.val].write(filtered);
      }
      // adptive: report each filterted
      if (tryAdaptive) {
        filtersPerf.updateFromFiltered(ftype, filtered, currentRow);
      }
    }
    filteredRows[0] = rowb;
    if (tryAdaptive) {
      FilterType preferredAdaptive = filtersPerf.getPreferred();
      filterBank[5].write(filteredRows[preferredAdaptive.val]);
    }
    if (currentRow == lastRowInThisBand) {
      int best = getBestCompressor();
      // PngHelperInternal.debug("won: " + best + " (rows: " + firstRowInThisBand + ":" + lastRowInThisBand + ")");
      // if(currentRow>90&&currentRow<100)
      // PngHelperInternal.debug(String.format("row=%d ft=%s",currentRow,FilterType.getByVal(best)));
      byte[] filtersAdapt = filterBank[best].getFirstBytes();
      for (int r = firstRowInThisBand, i = 0, j = lastRowInThisBand - firstRowInThisBand; r <= lastRowInThisBand; r++, j--, i++) {
        int fti = filtersAdapt[i];
        byte[] filtered = null;
        if (r != lastRowInThisBand) {
          filtered =
              filterRowWithFilterType(FilterType.getByVal(fti), rows.get(j), rows.get(j + 1),
                  filteredRowTmp);
        } else { // no need to do this filtering, we already have it
          filtered = filteredRows[fti];
        }
        sendToCompressedStream(filtered);
      }
    }
    // rotate
    if (rows.size() > rowsPerBandCurrent) {
      rows.addFirst(rows.removeLast());
    } else
      rows.addFirst(new byte[buflen]);
  }

  @Override
  public byte[] getRowb() {
    return rows.get(0);
  }


  private void setBandFromNewRown() {
    boolean newBand = currentRow == 0 || currentRow > lastRowInThisBand;
    if (currentRow == 0)
      bandNum = -1;
    if (newBand) {
      bandNum++;
      rowInBand = 0;
    } else {
      rowInBand++;
    }
    if (newBand) {
      firstRowInThisBand = currentRow;
      lastRowInThisBand = firstRowInThisBand + rowsPerBand - 1;
      int lastRowInNextBand = firstRowInThisBand + 2 * rowsPerBand - 1;
      if (lastRowInNextBand >= imgInfo.rows) // hack:make this band bigger, so we don't have a small
                                             // last band
        lastRowInThisBand = imgInfo.rows - 1;
      rowsPerBandCurrent = 1 + lastRowInThisBand - firstRowInThisBand;
      tryAdaptive =
          rowsPerBandCurrent <= 3 || (rowsPerBandCurrent < 10 && imgInfo.bytesPerRow < 64) ? false
              : true;
      // rebuild bank
      rebuildFiltersBank();
    }
  }

  private void rebuildFiltersBank() {
    long bytesPerBandCurrent = rowsPerBandCurrent * (long) buflen;
    final int DEFLATER_COMP_LEVEL = 4;
    for (int i = 0; i <= 5; i++) {// one for each filter plus one adaptive
      CompressorStream cp = filterBank[i];
      if (cp == null || cp.totalbytes != bytesPerBandCurrent) {
        if (cp != null)
          cp.close();
        if (useLz4)
          cp = new CompressorStreamLz4(null, buflen, bytesPerBandCurrent);
        else
          cp =
              new CompressorStreamDeflater(null, buflen, bytesPerBandCurrent, DEFLATER_COMP_LEVEL,
                  Deflater.DEFAULT_STRATEGY);
        filterBank[i] = cp;
      } else {
        cp.reset();
      }
      cp.setStoreFirstByte(true, rowsPerBandCurrent); // TODO: only for adaptive?
    }
  }

  private int computeInitialRowsPerBand() {
    // memory (only buffers) ~ (r+1+5) * bytesPerRow
    int r = (int) ((hintMemoryKb * 1024.0) / (imgInfo.bytesPerRow + 1) - 5);
    if (r < 1)
      r = 1;
    if (hintRowsPerBand > 0 && r > hintRowsPerBand)
      r = hintRowsPerBand;
    if (r > imgInfo.rows)
      r = imgInfo.rows;
    if (r > 2 && r > imgInfo.rows / 8) { // redistribute more evenly
      int k = (imgInfo.rows + (r - 1)) / r;
      r = (imgInfo.rows + k / 2) / k;
    }
    // PngHelperInternal.debug("rows :" + r + "/" + imgInfo.rows);
    return r;
  }

  private int getBestCompressor() {
    double bestcr = Double.MAX_VALUE;
    int bestb = -1;
    for (int i = tryAdaptive ? 5 : 4; i >= 0; i--) {
      CompressorStream fb = filterBank[i];
      double cr = fb.getCompressionRatio();
      if (cr <= bestcr) { // dirty trick, here the equality gains for row 0, so that SUB is prefered
                          // over PAETH, UP, AVE...
        bestb = i;
        bestcr = cr;
      }
    }
    return bestb;
  }

  @Override
  protected void initParams() {
    super.initParams();
    // if adaptative but too few rows or columns, use default
    if (imgInfo.cols < 3 && !FilterType.isValidStandard(filterType))
      filterType = FilterType.FILTER_DEFAULT;
    if (imgInfo.rows < 3 && !FilterType.isValidStandard(filterType))
      filterType = FilterType.FILTER_DEFAULT;
    for (int i = 1; i <= 4; i++) { // element 0 is not allocated
      if (filteredRows[i] == null || filteredRows[i].length < buflen)
        filteredRows[i] = new byte[buflen];
    }
    if (rowsPerBand == 0)
      rowsPerBand = computeInitialRowsPerBand();
  }

  @Override
  public void close() {
    super.close();
    rows.clear();
    for (CompressorStream f : filterBank) {
      f.close();
    }
  }

  public void setHintMemoryKb(int hintMemoryKb) {
    this.hintMemoryKb =
        hintMemoryKb <= 0 ? HINT_MEMORY_DEFAULT_KB : (hintMemoryKb > 10000 ? 10000 : hintMemoryKb);
  }

  public void setHintRowsPerBand(int hintRowsPerBand) {
    this.hintRowsPerBand = hintRowsPerBand;
  }

  public void setUseLz4(boolean lz4) {
    this.useLz4 = lz4;
  }

  /** for tuning memory or other parameters */
  public FiltersPerformance getFiltersPerf() {
    return filtersPerf;
  }

  public void setTryAdaptive(boolean tryAdaptive) {
    this.tryAdaptive = tryAdaptive;
  }

}
