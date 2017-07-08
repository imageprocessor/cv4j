package com.hjg.pngj;

/**
 * This interface is just for the sake of unifying some methods of {@link ImageLineHelper} that can use both
 * {@link ImageLineInt} or {@link ImageLineByte}. It's not very useful outside that, and the user should not rely much
 * on this.
 */
public interface IImageLineArray {
  public ImageInfo getImageInfo();

  public FilterType getFilterType();

  /**
   * length of array (should correspond to samples)
   */
  public int getSize();

  /**
   * Get i-th element of array (for 0 to size-1). The meaning of this is type dependent. For ImageLineInt and
   * ImageLineByte is the sample value.
   */
  public int getElem(int i);
}
