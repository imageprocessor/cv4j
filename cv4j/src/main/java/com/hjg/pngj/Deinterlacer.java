package com.hjg.pngj;

public class Deinterlacer {
  final ImageInfo imi;
  private int pass; // 1-7
  private int rows, cols;
  int dY, dX, oY, oX; // current step and offset (in pixels)
  int oXsamples, dXsamples; // step in samples

  // current row in the virtual subsampled image; this increments (by 1) from 0 to rows/dy 7 times
  private int currRowSubimg;
  // in the real image, this will cycle from 0 to im.rows in different steps, 7 times
  private int currRowReal;
  private int currRowSeq; // not counting empty rows

  int totalRows;
  private boolean ended = false;

  public Deinterlacer(ImageInfo iminfo) {
    this.imi = iminfo;
    pass = 0;
    currRowSubimg = -1;
    currRowReal = -1;
    currRowSeq = 0;
    ended = false;
    totalRows = 0; // lazy compute
    setPass(1);
    setRow(0);
  }

  /** this refers to the row currRowSubimg */
  private void setRow(int n) { // This should be called only intercally, in sequential order
    currRowSubimg = n;
    currRowReal = n * dY + oY;
    if (currRowReal < 0 || currRowReal >= imi.rows)
      throw new PngjExceptionInternal("bad row - this should not happen");
  }

  /** Skips passes with no rows. Return false is no more rows */
  boolean nextRow() {
    currRowSeq++;
    if (rows == 0 || currRowSubimg >= rows - 1) { // next pass
      if (pass == 7) {
        ended = true;
        return false;
      }
      setPass(pass + 1);
      if (rows == 0) {
        currRowSeq--;
        return nextRow();
      }
      setRow(0);
    } else {
      setRow(currRowSubimg + 1);
    }
    return true;
  }

  boolean isEnded() {
    return ended;
  }

  void setPass(int p) {
    if (this.pass == p)
      return;
    pass = p;
    byte[] pp = paramsForPass(p);// dx,dy,ox,oy
    dX = pp[0];
    dY = pp[1];
    oX = pp[2];
    oY = pp[3];
    rows = imi.rows > oY ? (imi.rows + dY - 1 - oY) / dY : 0;
    cols = imi.cols > oX ? (imi.cols + dX - 1 - oX) / dX : 0;
    if (cols == 0)
      rows = 0; // well, really...
    dXsamples = dX * imi.channels;
    oXsamples = oX * imi.channels;
  }

  static byte[] paramsForPass(final int p) {// dx,dy,ox,oy
    switch (p) {
      case 1:
        return new byte[] {8, 8, 0, 0};
      case 2:
        return new byte[] {8, 8, 4, 0};
      case 3:
        return new byte[] {4, 8, 0, 4};
      case 4:
        return new byte[] {4, 4, 2, 0};
      case 5:
        return new byte[] {2, 4, 0, 2};
      case 6:
        return new byte[] {2, 2, 1, 0};
      case 7:
        return new byte[] {1, 2, 0, 1};
      default:
        throw new PngjExceptionInternal("bad interlace pass" + p);
    }
  }

  /**
   * current row number inside the "sub image"
   */
  int getCurrRowSubimg() {
    return currRowSubimg;
  }

  /**
   * current row number inside the "real image"
   */
  int getCurrRowReal() {
    return currRowReal;
  }

  /**
   * current pass number (1-7)
   */
  int getPass() {
    return pass;
  }

  /**
   * How many rows has the current pass?
   **/
  int getRows() {
    return rows;
  }

  /**
   * How many columns (pixels) are there in the current row
   */
  int getCols() {
    return cols;
  }

  public int getPixelsToRead() {
    return getCols();
  }

  public int getBytesToRead() { // not including filter byte
    return (imi.bitspPixel * getPixelsToRead() + 7) / 8;
  }

  public int getdY() {
    return dY;
  }

  /*
   * in pixels
   */
  public int getdX() {
    return dX;
  }

  public int getoY() {
    return oY;
  }

  /*
   * in pixels
   */
  public int getoX() {
    return oX;
  }

  public int getTotalRows() {
    if (totalRows == 0) { // lazy compute
      for (int p = 1; p <= 7; p++) {
        byte[] pp = paramsForPass(p); // dx dy ox oy
        int rows = imi.rows > pp[3] ? (imi.rows + pp[1] - 1 - pp[3]) / pp[1] : 0;
        int cols = imi.cols > pp[2] ? (imi.cols + pp[0] - 1 - pp[2]) / pp[0] : 0;
        if (rows > 0 && cols > 0)
          totalRows += rows;
      }
    }
    return totalRows;
  }

  /**
   * total unfiltered bytes in the image, including the filter byte
   */
  public long getTotalRawBytes() { // including the filter byte
    long bytes = 0;
    for (int p = 1; p <= 7; p++) {
      byte[] pp = paramsForPass(p); // dx dy ox oy
      int rows = imi.rows > pp[3] ? (imi.rows + pp[1] - 1 - pp[3]) / pp[1] : 0;
      int cols = imi.cols > pp[2] ? (imi.cols + pp[0] - 1 - pp[2]) / pp[0] : 0;
      int bytesr = (imi.bitspPixel * cols + 7) / 8; // without filter byte
      if (rows > 0 && cols > 0)
        bytes += rows * (1 + (long) bytesr);
    }
    return bytes;
  }

  public int getCurrRowSeq() {
    return currRowSeq;
  }

}
