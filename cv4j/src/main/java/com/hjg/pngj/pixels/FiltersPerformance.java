package com.hjg.pngj.pixels;

import java.util.Arrays;

import com.hjg.pngj.FilterType;
import com.hjg.pngj.ImageInfo;
import com.hjg.pngj.PngHelperInternal;
import com.hjg.pngj.PngjExceptionInternal;

/** for use in adaptative strategy */
public class FiltersPerformance {

  private final ImageInfo iminfo;
  private double memoryA = 0.7; // empirical (not very critical: 0.72)
  private int lastrow = -1;
  private double[] absum = new double[5];// depending on the strategy not all values might be
                                         // computed for all
  private double[] entropy = new double[5];
  private double[] cost = new double[5];
  private int[] histog = new int[256]; // temporary, not normalized
  private int lastprefered = -1;
  private boolean initdone = false;
  private double preferenceForNone = 1.0; // higher gives more preference to NONE

  // this values are empirical (montecarlo), for RGB8 images with entropy estimator for NONE and
  // memory=0.7
  // DONT MODIFY THIS
  public static final double[] FILTER_WEIGHTS_DEFAULT = {0.73, 1.03, 0.97, 1.11, 1.22}; // lower is
                                                                                        // better!

  private double[] filter_weights = new double[] {-1, -1, -1, -1, -1};

  private final static double LOG2NI = -1.0 / Math.log(2.0);

  public FiltersPerformance(ImageInfo imgInfo) {
    this.iminfo = imgInfo;
  }

  private void init() {
    if (filter_weights[0] < 0) {// has not been set from outside
      System.arraycopy(FILTER_WEIGHTS_DEFAULT, 0, filter_weights, 0, 5);
      double wNone = filter_weights[0];
      if (iminfo.bitDepth == 16)
        wNone = 1.2;
      else if (iminfo.alpha)
        wNone = 0.8;
      else if (iminfo.indexed || iminfo.bitDepth < 8)
        wNone = 0.4; // we prefer NONE strongly
      wNone /= preferenceForNone;
      filter_weights[0] = wNone;
    }
    Arrays.fill(cost, 1.0);
    initdone = true;
  }

  public void updateFromFiltered(FilterType ftype, byte[] rowff, int rown) {
    updateFromRawOrFiltered(ftype, rowff, null, null, rown);
  }

  /** alternative: computes statistic without filtering */
  public void updateFromRaw(FilterType ftype, byte[] rowb, byte[] rowbprev, int rown) {
    updateFromRawOrFiltered(ftype, null, rowb, rowbprev, rown);
  }

  private void updateFromRawOrFiltered(FilterType ftype, byte[] rowff, byte[] rowb,
      byte[] rowbprev, int rown) {
    if (!initdone)
      init();
    if (rown != lastrow) {
      Arrays.fill(absum, Double.NaN);
      Arrays.fill(entropy, Double.NaN);
    }
    lastrow = rown;
    if (rowff != null)
      computeHistogram(rowff);
    else
      computeHistogramForFilter(ftype, rowb, rowbprev);
    if (ftype == FilterType.FILTER_NONE)
      entropy[ftype.val] = computeEntropyFromHistogram();
    else
      absum[ftype.val] = computeAbsFromHistogram();
  }

  /* WARNING: this is not idempotent, call it just once per cycle (sigh) */
  public FilterType getPreferred() {
    int fi = 0;
    double vali = Double.MAX_VALUE, val = 0; // lower wins
    for (int i = 0; i < 5; i++) {
      if (!Double.isNaN(absum[i])) {
        val = absum[i];
      } else if (!Double.isNaN(entropy[i])) {
        val = (Math.pow(2.0, entropy[i]) - 1.0) * 0.5;
      } else
        continue;
      val *= filter_weights[i];
      val = cost[i] * memoryA + (1 - memoryA) * val;
      cost[i] = val;
      if (val < vali) {
        vali = val;
        fi = i;
      }
    }
    lastprefered = fi;
    return FilterType.getByVal(lastprefered);
  }

  public final void computeHistogramForFilter(FilterType filterType, byte[] rowb, byte[] rowbprev) {
    Arrays.fill(histog, 0);
    int i, j, imax = iminfo.bytesPerRow;
    switch (filterType) {
      case FILTER_NONE:
        for (i = 1; i <= imax; i++)
          histog[rowb[i] & 0xFF]++;
        break;
      case FILTER_PAETH:
        for (i = 1; i <= imax; i++)
          histog[PngHelperInternal.filterRowPaeth(rowb[i], 0, rowbprev[i] & 0xFF, 0)]++;
        for (j = 1, i = iminfo.bytesPixel + 1; i <= imax; i++, j++)
          histog[PngHelperInternal.filterRowPaeth(rowb[i], rowb[j] & 0xFF, rowbprev[i] & 0xFF,
              rowbprev[j] & 0xFF)]++;
        break;
      case FILTER_SUB:
        for (i = 1; i <= iminfo.bytesPixel; i++)
          histog[rowb[i] & 0xFF]++;
        for (j = 1, i = iminfo.bytesPixel + 1; i <= imax; i++, j++)
          histog[(rowb[i] - rowb[j]) & 0xFF]++;
        break;
      case FILTER_UP:
        for (i = 1; i <= iminfo.bytesPerRow; i++)
          histog[(rowb[i] - rowbprev[i]) & 0xFF]++;
        break;
      case FILTER_AVERAGE:
        for (i = 1; i <= iminfo.bytesPixel; i++)
          histog[((rowb[i] & 0xFF) - ((rowbprev[i] & 0xFF)) / 2) & 0xFF]++;
        for (j = 1, i = iminfo.bytesPixel + 1; i <= imax; i++, j++)
          histog[((rowb[i] & 0xFF) - ((rowbprev[i] & 0xFF) + (rowb[j] & 0xFF)) / 2) & 0xFF]++;
        break;
      default:
        throw new PngjExceptionInternal("Bad filter:" + filterType);
    }
  }

  public void computeHistogram(byte[] rowff) {
    Arrays.fill(histog, 0);
    for (int i = 1; i < iminfo.bytesPerRow; i++)
      histog[rowff[i] & 0xFF]++;
  }

  public double computeAbsFromHistogram() {
    int s = 0;
    for (int i = 1; i < 128; i++)
      s += histog[i] * i;
    for (int i = 128, j = 128; j > 0; i++, j--)
      s += histog[i] * j;
    return s / (double) iminfo.bytesPerRow;
  }

  public final double computeEntropyFromHistogram() {
    double s = 1.0 / iminfo.bytesPerRow;
    double ls = Math.log(s);

    double h = 0;
    for (int x : histog) {
      if (x > 0)
        h += (Math.log(x) + ls) * x;
    }
    h *= s * LOG2NI;
    if (h < 0.0)
      h = 0.0;
    return h;
  }

  /**
   * If larger than 1.0, NONE will be more prefered. This must be called before init
   * 
   * @param preferenceForNone around 1.0 (default: 1.0)
   */
  public void setPreferenceForNone(double preferenceForNone) {
    this.preferenceForNone = preferenceForNone;
  }

  /**
   * Values greater than 1.0 (towards infinite) increase the memory towards 1. Values smaller than 1.0 (towards zero)
   * decreases the memory .
   * 
   */
  public void tuneMemory(double m) {
    if (m == 0)
      memoryA = 0.0;
    else
      memoryA = Math.pow(memoryA, 1.0 / m);
  }

  /**
   * To set manually the filter weights. This is not recommended, unless you know what you are doing. Setting this
   * ignores preferenceForNone and omits some heuristics
   * 
   * @param weights Five doubles around 1.0, one for each filter type. Lower is preferered
   */
  public void setFilterWeights(double[] weights) {
    System.arraycopy(weights, 0, filter_weights, 0, 5);
  }
}
