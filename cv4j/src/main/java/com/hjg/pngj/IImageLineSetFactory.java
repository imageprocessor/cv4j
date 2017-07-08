package com.hjg.pngj;

/**
 * Factory of {@link IImageLineSet}, used by {@link PngReader}.
 * <p>
 * 
 * @param <T> Generic type of IImageLine
 */
public interface IImageLineSetFactory<T extends IImageLine> {
  /**
   * Creates a new {@link IImageLineSet}
   * 
   * If singleCursor=true, the caller will read and write one row fully at a time, in order (it'll never try to read out
   * of order lines), so the implementation can opt for allocate only one line.
   * 
   * @param imgInfo Image info
   * @param singleCursor : will read/write one row at a time
   * @param nlines : how many lines we plan to read
   * @param noffset : how many lines we want to skip from the original image (normally 0)
   * @param step : row step (normally 1)
   */
  public IImageLineSet<T> create(ImageInfo imgInfo, boolean singleCursor, int nlines, int noffset,
                                 int step);
}
