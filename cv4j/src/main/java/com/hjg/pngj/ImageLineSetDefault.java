package com.hjg.pngj;

import java.util.ArrayList;
import java.util.List;

/**
 * Default implementation of {@link IImageLineSet}.
 * <P>
 * Supports all modes: single cursor, full rows, or partial. This should not be used for
 */
public abstract class ImageLineSetDefault<T extends IImageLine> implements IImageLineSet<T> {

  protected final ImageInfo imgInfo;
  private final boolean singleCursor;
  private final int nlines, offset, step;
  protected List<T> imageLines; // null if single cursor
  protected T imageLine; // null unless single cursor
  protected int currentRow = -1; // only relevant (and not much) for cursor

  public ImageLineSetDefault(ImageInfo imgInfo, final boolean singleCursor, final int nlinesx,
      final int noffsetx, final int stepx) {
    this.imgInfo = imgInfo;
    this.singleCursor = singleCursor;
    if (singleCursor) {
      this.nlines = 1; // we store only one line, no matter how many will be read
      offset = 0;
      this.step = 1;// don't matter
    } else {
      this.nlines = nlinesx; // note that it can also be 1
      offset = noffsetx;
      this.step = stepx;// don't matter
    }
    createImageLines();
  }

  private void createImageLines() {
    if (singleCursor)
      imageLine = createImageLine();
    else {
      imageLines = new ArrayList<T>();
      for (int i = 0; i < nlines; i++)
        imageLines.add(createImageLine());
    }
  }

  protected abstract T createImageLine();

  /**
   * Retrieves the image line
   * <p>
   * Warning: the argument is the row number in the original image
   * <p>
   * If this is a cursor, no check is done, always the same row is returned
   */
  public T getImageLine(int n) {
    currentRow = n;
    if (singleCursor)
      return imageLine;
    else {
      int r = imageRowToMatrixRowStrict(n);
      if (r < 0)
        throw new PngjException("Invalid row number");
      return imageLines.get(r);
    }
  }

  /**
   * does not check for valid range
   */
  public T getImageLineRawNum(int r) {
    if (singleCursor)
      return imageLine;
    else
      return imageLines.get(r);
  }

  /**
   * True if the set contains this image line
   * <p>
   * Warning: the argument is the row number in the original image
   * <p>
   * If this works as cursor, this returns true only if that is the number of its "current" line
   */
  public boolean hasImageLine(int n) {
    return singleCursor ? currentRow == n : imageRowToMatrixRowStrict(n) >= 0;
  }

  /**
   * How many lines does this object contain?
   */
  public int size() {
    return nlines;
  }

  /**
   * Same as {@link #imageRowToMatrixRow(int)}, but returns negative if invalid
   */
  public int imageRowToMatrixRowStrict(int imrow) {
    imrow -= offset;
    int mrow = imrow >= 0 && (step == 1 || imrow % step == 0) ? imrow / step : -1;
    return mrow < nlines ? mrow : -1;
  }

  /**
   * Converts from matrix row number (0 : nRows-1) to image row number
   * 
   * @param mrow Matrix row number
   * @return Image row number. Returns trash if mrow is invalid
   */
  public int matrixRowToImageRow(int mrow) {
    return mrow * step + offset;
  }

  /**
   * Converts from real image row to this object row number.
   * <p>
   * Warning: this always returns a valid matrix row (clamping on 0 : nrows-1, and rounding down)
   * <p>
   * Eg: rowOffset=4,rowStep=2 imageRowToMatrixRow(17) returns 6 , imageRowToMatrixRow(1) returns 0
   */
  public int imageRowToMatrixRow(int imrow) {
    int r = (imrow - offset) / step;
    return r < 0 ? 0 : (r < nlines ? r : nlines - 1);
  }

  /** utility function, given a factory for one line, returns a factory for a set */
  public static <T extends IImageLine> IImageLineSetFactory<T> createImageLineSetFactoryFromImageLineFactory(
      final IImageLineFactory<T> ifactory) { // ugly method must have ugly name. don't let this intimidate you
    return new IImageLineSetFactory<T>() {
      public IImageLineSet<T> create(final ImageInfo iminfo, boolean singleCursor, int nlines,
          int noffset, int step) {
        return new ImageLineSetDefault<T>(iminfo, singleCursor, nlines, noffset, step) {
          @Override
          protected T createImageLine() {
            return ifactory.createImageLine(iminfo);
          }
        };
      };
    };
  }

  /** utility function, returns default factory for {@link ImageLineInt} */
  public static IImageLineSetFactory<ImageLineInt> getFactoryInt() {
    return createImageLineSetFactoryFromImageLineFactory(ImageLineInt.getFactory());
  }

  /** utility function, returns default factory for {@link ImageLineByte} */
  public static IImageLineSetFactory<ImageLineByte> getFactoryByte() {
    return createImageLineSetFactoryFromImageLineFactory(ImageLineByte.getFactory());
  }
}
