package com.hjg.pngj;

import java.util.HashMap;

/**
 * Internal PNG predictor filter type
 * 
 * Negative values are pseudo types, actually global strategies for writing, that (can) result on different real filters
 * for different rows
 */
public enum FilterType {
  /**
   * No filter.
   */
  FILTER_NONE(0),
  /**
   * SUB filter (uses same row)
   */
  FILTER_SUB(1),
  /**
   * UP filter (uses previous row)
   */
  FILTER_UP(2),
  /**
   * AVERAGE filter
   */
  FILTER_AVERAGE(3),
  /**
   * PAETH predictor
   */
  FILTER_PAETH(4),
  /**
   * Default strategy: select one of the standard filters depending on global image parameters
   */
  FILTER_DEFAULT(-1),
  /**
   * @deprecated use #FILTER_ADAPTIVE_FAST
   */
  FILTER_AGGRESSIVE(-2),
  /**
   * @deprecated use #FILTER_ADAPTIVE_MEDIUM or #FILTER_ADAPTIVE_FULL
   */
  FILTER_VERYAGGRESSIVE(-4),
  /**
   * Adaptative strategy, sampling each row, or almost
   */
  FILTER_ADAPTIVE_FULL(-4),
  /**
   * Adaptive strategy, skippping some rows
   */
  FILTER_ADAPTIVE_MEDIUM(-3), // samples about 1/4 row
  /**
   * Adaptative strategy, skipping many rows - more speed
   */
  FILTER_ADAPTIVE_FAST(-2), // samples each 8 or 16 rows
  /**
   * Experimental
   */
  FILTER_SUPER_ADAPTIVE(-10), //
  /**
   * Preserves the filter passed in original row.
   */
  FILTER_PRESERVE(-40),
  /**
   * Uses all fiters, one for lines, cyciclally. Only for tests.
   */
  FILTER_CYCLIC(-50),
  /**
   * Not specified, placeholder for unknown or NA filters.
   */
  FILTER_UNKNOWN(-100);

  public final int val;

  private FilterType(int val) {
    this.val = val;
  }

  private static HashMap<Integer, FilterType> byVal;

  static {
    byVal = new HashMap<Integer, FilterType>();
    for (FilterType ft : values()) {
      byVal.put(ft.val, ft);
    }
  }

  public static FilterType getByVal(int i) {
    return byVal.get(i);
  }

  /** only considers standard */
  public static boolean isValidStandard(int i) {
    return i >= 0 && i <= 4;
  }

  public static boolean isValidStandard(FilterType fy) {
    return fy != null && isValidStandard(fy.val);
  }

  public static boolean isAdaptive(FilterType fy) {
    return fy.val <= -2 && fy.val >= -4;
  }

  /**
   * Returns all "standard" filters
   */
  public static FilterType[] getAllStandard() {
    return new FilterType[] {FILTER_NONE, FILTER_SUB, FILTER_UP, FILTER_AVERAGE, FILTER_PAETH};
  }

  public static FilterType[] getAllStandardNoneLast() {
    return new FilterType[] {FILTER_SUB, FILTER_UP, FILTER_AVERAGE, FILTER_PAETH, FILTER_NONE};
  }

  public static FilterType[] getAllStandardExceptNone() {
    return new FilterType[] {FILTER_SUB, FILTER_UP, FILTER_AVERAGE, FILTER_PAETH};
  }

  static FilterType[] getAllStandardForFirstRow() {
    return new FilterType[] {FILTER_SUB, FILTER_NONE};
  }

}
