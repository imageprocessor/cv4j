package com.hjg.pngj;

import java.util.zip.Checksum;

/**
 * Simple immutable wrapper for basic image info.
 * <p>
 * Some parameters are redundant, but the constructor receives an 'orthogonal' subset.
 * <p>
 * ref: http://www.w3.org/TR/PNG/#11IHDR
 */
public class ImageInfo {

  /**
   * Absolute allowed maximum value for rows and cols (2^24 ~16 million). (bytesPerRow must fit in a 32bit integer,
   * though total amount of pixels not necessarily).
   */
  public static final int MAX_COLS_ROW = 16777216;

  /**
   * Cols= Image width, in pixels.
   */
  public final int cols;

  /**
   * Rows= Image height, in pixels
   */
  public final int rows;

  /**
   * Bits per sample (per channel) in the buffer (1-2-4-8-16). This is 8-16 for RGB/ARGB images, 1-2-4-8 for grayscale.
   * For indexed images, number of bits per palette index (1-2-4-8)
   */
  public final int bitDepth;

  /**
   * Number of channels, as used internally: 3 for RGB, 4 for RGBA, 2 for GA (gray with alpha), 1 for grayscale or
   * indexed.
   */
  public final int channels;

  /**
   * Flag: true if has alpha channel (RGBA/GA)
   */
  public final boolean alpha;

  /**
   * Flag: true if is grayscale (G/GA)
   */
  public final boolean greyscale;

  /**
   * Flag: true if image is indexed, i.e., it has a palette
   */
  public final boolean indexed;

  /**
   * Flag: true if image internally uses less than one byte per sample (bit depth 1-2-4)
   */
  public final boolean packed;

  /**
   * Bits used for each pixel in the buffer: channel * bitDepth
   */
  public final int bitspPixel;

  /**
   * rounded up value: this is only used internally for filter
   */
  public final int bytesPixel;

  /**
   * ceil(bitspp*cols/8) - does not include filter
   */
  public final int bytesPerRow;

  /**
   * Equals cols * channels
   */
  public final int samplesPerRow;

  /**
   * Amount of "packed samples" : when several samples are stored in a single byte (bitdepth 1,2 4) they are counted as
   * one "packed sample". This is less that samplesPerRow only when bitdepth is 1-2-4 (flag packed = true)
   * <p>
   * This equals the number of elements in the scanline array if working with packedMode=true
   * <p>
   * For internal use, client code should rarely access this.
   */
  public final int samplesPerRowPacked;

  private long totalPixels = -1; // lazy getter

  private long totalRawBytes = -1; // lazy getter

  /**
   * Short constructor: assumes truecolor (RGB/RGBA)
   */
  public ImageInfo(int cols, int rows, int bitdepth, boolean alpha) {
    this(cols, rows, bitdepth, alpha, false, false);
  }

  /**
   * Full constructor
   * 
   * @param cols Width in pixels
   * @param rows Height in pixels
   * @param bitdepth Bits per sample, in the buffer : 8-16 for RGB true color and greyscale
   * @param alpha Flag: has an alpha channel (RGBA or GA)
   * @param grayscale Flag: is gray scale (any bitdepth, with or without alpha)
   * @param indexed Flag: has palette
   */
  public ImageInfo(int cols, int rows, int bitdepth, boolean alpha, boolean grayscale,
      boolean indexed) {
    this.cols = cols;
    this.rows = rows;
    this.alpha = alpha;
    this.indexed = indexed;
    this.greyscale = grayscale;
    if (greyscale && indexed)
      throw new PngjException("palette and greyscale are mutually exclusive");
    this.channels = (grayscale || indexed) ? (alpha ? 2 : 1) : (alpha ? 4 : 3);
    // http://www.w3.org/TR/PNG/#11IHDR
    this.bitDepth = bitdepth;
    this.packed = bitdepth < 8;
    this.bitspPixel = (channels * this.bitDepth);
    this.bytesPixel = (bitspPixel + 7) / 8;
    this.bytesPerRow = (bitspPixel * cols + 7) / 8;
    this.samplesPerRow = channels * this.cols;
    this.samplesPerRowPacked = packed ? bytesPerRow : samplesPerRow;
    // several checks
    switch (this.bitDepth) {
      case 1:
      case 2:
      case 4:
        if (!(this.indexed || this.greyscale))
          throw new PngjException("only indexed or grayscale can have bitdepth=" + this.bitDepth);
        break;
      case 8:
        break;
      case 16:
        if (this.indexed)
          throw new PngjException("indexed can't have bitdepth=" + this.bitDepth);
        break;
      default:
        throw new PngjException("invalid bitdepth=" + this.bitDepth);
    }
    if (cols < 1 || cols > MAX_COLS_ROW)
      throw new PngjException("invalid cols=" + cols + " ???");
    if (rows < 1 || rows > MAX_COLS_ROW)
      throw new PngjException("invalid rows=" + rows + " ???");
    if (samplesPerRow < 1)
      throw new PngjException("invalid image parameters (overflow?)");
  }

  /**
   * returns a copy with different size
   * 
   * @param cols if non-positive, the original is used
   * @param rows if non-positive, the original is used
   * @return a new copy with the specified size and same properties
   */
  public ImageInfo withSize(int cols, int rows) {
    return new ImageInfo(cols > 0 ? cols : this.cols, rows > 0 ? rows : this.rows, this.bitDepth,
        this.alpha, this.greyscale, this.indexed);
  }

  public long getTotalPixels() {
    if (totalPixels < 0)
      totalPixels = cols * (long) rows;
    return totalPixels;
  }

  /**
   * Total uncompressed bytes in IDAT, including filter byte. This is not valid for interlaced.
   */
  public long getTotalRawBytes() {
    if (totalRawBytes < 0)
      totalRawBytes = (bytesPerRow + 1) * (long) rows;
    return totalRawBytes;
  }

  @Override
  public String toString() {
    return "ImageInfo [cols=" + cols + ", rows=" + rows + ", bitDepth=" + bitDepth + ", channels="
        + channels + ", alpha=" + alpha + ", greyscale=" + greyscale + ", indexed=" + indexed + "]";
  }

  /**
   * Brief info: COLSxROWS[dBITDEPTH][a][p][g] ( the default dBITDEPTH='d8' is ommited)
   **/
  public String toStringBrief() {
    return String.valueOf(cols) + "x" + rows + (bitDepth != 8 ? ("d" + bitDepth) : "")
        + (alpha ? "a" : "") + (indexed ? "p" : "") + (greyscale ? "g" : "");
  }

  public String toStringDetail() {
    return "ImageInfo [cols=" + cols + ", rows=" + rows + ", bitDepth=" + bitDepth + ", channels="
        + channels + ", bitspPixel=" + bitspPixel + ", bytesPixel=" + bytesPixel + ", bytesPerRow="
        + bytesPerRow + ", samplesPerRow=" + samplesPerRow + ", samplesPerRowP="
        + samplesPerRowPacked + ", alpha=" + alpha + ", greyscale=" + greyscale + ", indexed="
        + indexed + ", packed=" + packed + "]";
  }


  void updateCrc(Checksum crc) {
    crc.update((byte) rows);
    crc.update((byte) (rows >> 8));
    crc.update((byte) (rows >> 16));
    crc.update((byte) cols);
    crc.update((byte) (cols >> 8));
    crc.update((byte) (cols >> 16));
    crc.update((byte) (bitDepth));
    crc.update((byte) (indexed ? 1 : 2));
    crc.update((byte) (greyscale ? 3 : 4));
    crc.update((byte) (alpha ? 3 : 4));
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (alpha ? 1231 : 1237);
    result = prime * result + bitDepth;
    result = prime * result + cols;
    result = prime * result + (greyscale ? 1231 : 1237);
    result = prime * result + (indexed ? 1231 : 1237);
    result = prime * result + rows;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    ImageInfo other = (ImageInfo) obj;
    if (alpha != other.alpha)
      return false;
    if (bitDepth != other.bitDepth)
      return false;
    if (cols != other.cols)
      return false;
    if (greyscale != other.greyscale)
      return false;
    if (indexed != other.indexed)
      return false;
    if (rows != other.rows)
      return false;
    return true;
  }
}
