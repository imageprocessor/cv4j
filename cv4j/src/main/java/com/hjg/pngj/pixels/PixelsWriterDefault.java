package com.hjg.pngj.pixels;

import java.util.Arrays;

import com.hjg.pngj.FilterType;
import com.hjg.pngj.ImageInfo;
import com.hjg.pngj.PngjOutputException;

/**
 * Default implementation of PixelsWriter, with fixed filters and also adaptive strategies.
 */
public class PixelsWriterDefault extends PixelsWriter {
  /** current raw row */
  protected byte[] rowb;
  /** previous raw row */
  protected byte[] rowbprev;
  /** buffer for filtered row */
  protected byte[] rowbfilter;

  /** evaluates different filters, for adaptive strategy */
  protected FiltersPerformance filtersPerformance;

  /** currently concrete selected filter type */
  protected FilterType curfilterType;

  /** parameters for adaptive strategy */
  protected int adaptMaxSkip; // set in initParams, does not change
  protected int adaptSkipIncreaseSinceRow; // set in initParams, does not change
  protected double adaptSkipIncreaseFactor; // set in initParams, does not change
  protected int adaptNextRow = 0;

  public PixelsWriterDefault(ImageInfo imgInfo) {
    super(imgInfo);
    filtersPerformance = new FiltersPerformance(imgInfo);
  }

  @Override
  protected void initParams() {
    super.initParams();

    if (rowb == null || rowb.length < buflen)
      rowb = new byte[buflen];
    if (rowbfilter == null || rowbfilter.length < buflen)
      rowbfilter = new byte[buflen];
    if (rowbprev == null || rowbprev.length < buflen)
      rowbprev = new byte[buflen];
    else
      Arrays.fill(rowbprev, (byte) 0);

    // if adaptative but too few rows or columns, use default
    if (imgInfo.cols < 3 && !FilterType.isValidStandard(filterType))
      filterType = FilterType.FILTER_DEFAULT;
    if (imgInfo.rows < 3 && !FilterType.isValidStandard(filterType))
      filterType = FilterType.FILTER_DEFAULT;

    if (imgInfo.getTotalPixels() <= 1024 && !FilterType.isValidStandard(filterType))
      filterType = getDefaultFilter();

    if (FilterType.isAdaptive(filterType)) {
      // adaptCurSkip = 0;
      adaptNextRow = 0;
      if (filterType == FilterType.FILTER_ADAPTIVE_FAST) {
        adaptMaxSkip = 200;
        adaptSkipIncreaseSinceRow = 3;
        adaptSkipIncreaseFactor = 1 / 4.0; // skip ~ row/3
      } else if (filterType == FilterType.FILTER_ADAPTIVE_MEDIUM) {
        adaptMaxSkip = 8;
        adaptSkipIncreaseSinceRow = 32;
        adaptSkipIncreaseFactor = 1 / 80.0;
      } else if (filterType == FilterType.FILTER_ADAPTIVE_FULL) {
        adaptMaxSkip = 0;
        adaptSkipIncreaseSinceRow = 128;
        adaptSkipIncreaseFactor = 1 / 120.0;
      } else
        throw new PngjOutputException("bad filter " + filterType);
    }
  }

  @Override
  protected void filterAndWrite(final byte[] rowb) {
    if (rowb != this.rowb)
      throw new RuntimeException("??"); // we rely on this
    decideCurFilterType();
    byte[] filtered = filterRowWithFilterType(curfilterType, rowb, rowbprev, rowbfilter);
    sendToCompressedStream(filtered);
    // swap rowb <-> rowbprev
    byte[] aux = this.rowb;
    this.rowb = rowbprev;
    rowbprev = aux;
  }

  protected void decideCurFilterType() {
    // decide the real filter and store in curfilterType
    if (FilterType.isValidStandard(getFilterType())) {
      curfilterType = getFilterType();
    } else if (getFilterType() == FilterType.FILTER_PRESERVE) {
      curfilterType = FilterType.getByVal(rowb[0]);
    } else if (getFilterType() == FilterType.FILTER_CYCLIC) {
      curfilterType = FilterType.getByVal(currentRow % 5);
    } else if (getFilterType() == FilterType.FILTER_DEFAULT) {
      setFilterType(getDefaultFilter());
      curfilterType = getFilterType(); // this could be done once
    } else if (FilterType.isAdaptive(getFilterType())) {// adaptive
      if (currentRow == adaptNextRow) {
        for (FilterType ftype : FilterType.getAllStandard())
          filtersPerformance.updateFromRaw(ftype, rowb, rowbprev, currentRow);
        curfilterType = filtersPerformance.getPreferred();
        int skip =
            (currentRow >= adaptSkipIncreaseSinceRow ? (int) Math
                .round((currentRow - adaptSkipIncreaseSinceRow) * adaptSkipIncreaseFactor) : 0);
        if (skip > adaptMaxSkip)
          skip = adaptMaxSkip;
        if (currentRow == 0)
          skip = 0;
        adaptNextRow = currentRow + 1 + skip;
      }
    } else {
      throw new PngjOutputException("not implemented filter: " + getFilterType());
    }
    if (currentRow == 0 && curfilterType != FilterType.FILTER_NONE
        && curfilterType != FilterType.FILTER_SUB)
      curfilterType = FilterType.FILTER_SUB; // first row should always be none or sub
  }

  @Override
  public byte[] getRowb() {
    if (!initdone)
      init();
    return rowb;
  }

  @Override
  public void close() {
    super.close();
  }

  /**
   * Only for adaptive strategies. See {@link FiltersPerformance#setPreferenceForNone(double)}
   */
  public void setPreferenceForNone(double preferenceForNone) {
    filtersPerformance.setPreferenceForNone(preferenceForNone);
  }

  /**
   * Only for adaptive strategies. See {@link FiltersPerformance#tuneMemory(double)}
   */
  public void tuneMemory(double m) {
    filtersPerformance.tuneMemory(m);
  }

  /**
   * Only for adaptive strategies. See {@link FiltersPerformance#setFilterWeights(double[])}
   */
  public void setFilterWeights(double[] weights) {
    filtersPerformance.setFilterWeights(weights);
  }

}
