package com.hjg.pngj;


/**
 * Set of {@link IImageLine} elements.
 * <p>
 * This is actually a "virtual" set, it can be implemented in several ways; for example
 * <ul>
 * <li>Cursor-like: stores only one line, which is implicitly moved when requested</li>
 * <li>All lines: all lines stored as an array of <tt>IImageLine</tt></li>
 * <li>
 * Subset of lines: eg, only first 3 lines, or odd numbered lines. Or a band of neighbours lines that is moved like a
 * cursor.</li>
 * The ImageLine that PngReader returns is hosted by a IImageLineSet (this abstraction allows the implementation to deal
 * with interlaced images cleanly) but the library user does not normally needs to know that (or rely on that), except
 * for the {@link PngReader#readRows()} method.
 * </ul>
 */
public interface IImageLineSet<T extends IImageLine> {

  /**
   * Asks for imageline corresponding to row <tt>n</tt> in the original image (zero based). This can trigger side
   * effects in this object (eg, advance a cursor, set current row number...) In some scenarios, this should be consider
   * as alias to (pseudocode) <tt>positionAtLine(n); getCurrentLine();</tt>
   * <p>
   * Throws exception if not available. The caller is supposed to know what he/she is doing
   **/
  public IImageLine getImageLine(int n);

  /**
   * Like {@link #getImageLine(int)} but uses the raw numbering inside the LineSet This makes little sense for a cursor
   * 
   * @param n Should normally go from 0 to {@link #size()}
   * @return
   */
  public IImageLine getImageLineRawNum(int n);


  /**
   * Returns true if the set contain row <tt>n</tt> (in the original image,zero based) currently allocated.
   * <p>
   * If it's a single-cursor, this should return true only if it's positioned there. (notice that hasImageLine(n) can
   * return false, but getImageLine(n) can be ok)
   * 
   **/
  public boolean hasImageLine(int n);

  /**
   * Internal size of allocated rows This is informational, it should rarely be important for the caller.
   **/
  public int size();

}
