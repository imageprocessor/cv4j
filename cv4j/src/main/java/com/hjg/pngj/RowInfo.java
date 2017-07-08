package com.hjg.pngj;

/**
 * Packs information of current row. Only used internally
 */
class RowInfo {
  public final ImageInfo imgInfo;
  public final Deinterlacer deinterlacer;
  public final boolean imode; // Interlaced
  int dY, dX, oY, oX; // current step and offset (in pixels)
  int rowNseq; // row number (from 0) in sequential read order
  int rowNreal; // row number in the real image
  int rowNsubImg; // current row in the virtual subsampled image; this increments (by 1) from 0 to
                  // rows/dy 7 times
  int rowsSubImg, colsSubImg; // size of current subimage , in pixels
  int bytesRow;
  int pass; // 1-7
  byte[] buf; // non-deep copy
  int buflen; // valid bytes in buffer (include filter byte)

  public RowInfo(ImageInfo imgInfo, Deinterlacer deinterlacer) {
    this.imgInfo = imgInfo;
    this.deinterlacer = deinterlacer;
    this.imode = deinterlacer != null;
  }

  void update(int rowseq) {
    rowNseq = rowseq;
    if (imode) {
      pass = deinterlacer.getPass();
      dX = deinterlacer.dX;
      dY = deinterlacer.dY;
      oX = deinterlacer.oX;
      oY = deinterlacer.oY;
      rowNreal = deinterlacer.getCurrRowReal();
      rowNsubImg = deinterlacer.getCurrRowSubimg();
      rowsSubImg = deinterlacer.getRows();
      colsSubImg = deinterlacer.getCols();
      bytesRow = (imgInfo.bitspPixel * colsSubImg + 7) / 8;
    } else {
      pass = 1;
      dX = dY = 1;
      oX = oY = 0;
      rowNreal = rowNsubImg = rowseq;
      rowsSubImg = imgInfo.rows;
      colsSubImg = imgInfo.cols;
      bytesRow = imgInfo.bytesPerRow;
    }
  }

  void updateBuf(byte[] buf, int buflen) {
    this.buf = buf;
    this.buflen = buflen;
  }
}
