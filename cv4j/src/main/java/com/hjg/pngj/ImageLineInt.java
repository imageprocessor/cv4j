package com.hjg.pngj;

/**
 * Represents an image line, integer format (one integer by sample). See {@link #scanline} to understand the format.
 */
public class ImageLineInt implements IImageLine, IImageLineArray {
  public final ImageInfo imgInfo;

  /**
   * The 'scanline' is an array of integers, corresponds to an image line (row).
   * <p>
   * Each <code>int</code> is a "sample" (one for channel), (0-255 or 0-65535) in the corresponding PNG sequence:
   * <code>R G B R G B...</code> or <code>R G B A R G B A...</tt> 
   * or <code>g g g ...</code> or <code>i i i</code> (palette index)
   * <p>
   * For bitdepth=1/2/4 the value is not scaled (hence, eg, if bitdepth=2 the range will be 0-4)
   * <p>
   * To convert a indexed line to RGB values, see
   * {@link ImageLineHelper#palette2rgb(ImageLineInt, ar.com.hjg.pngj.chunks.PngChunkPLTE, int[])} (you can't do the
   * reverse)
   */
  protected final int[] scanline;

  /**
   * number of elements in the scanline
   */
  protected final int size;

  /**
   * informational ; only filled by the reader. not meaningful for interlaced
   */
  protected FilterType filterType = FilterType.FILTER_UNKNOWN;

  /**
   * @param imgInfo Inmutable ImageInfo, basic parameters of the image we are reading or writing
   */
  public ImageLineInt(ImageInfo imgInfo) {
    this(imgInfo, null);
  }

  /**
   * @param imgInfo Inmutable ImageInfo, basic parameters of the image we are reading or writing
   * @param sci prealocated buffer (can be null)
   */
  public ImageLineInt(ImageInfo imgInfo, int[] sci) {
    this.imgInfo = imgInfo;
    filterType = FilterType.FILTER_UNKNOWN;
    size = imgInfo.samplesPerRow;
    scanline = sci != null && sci.length >= size ? sci : new int[size];
  }

  /**
   * Helper method, returns a default factory for this object
   * 
   */
  public static IImageLineFactory<ImageLineInt> getFactory() {
    return new IImageLineFactory<ImageLineInt>() {
      public ImageLineInt createImageLine(ImageInfo iminfo) {
        return new ImageLineInt(iminfo);
      }
    };
  }

  public FilterType getFilterType() {
    return filterType;
  }

  /**
   * This should rarely be used by client code. Only relevant if FilterPreserve==true
   */
  public void setFilterType(FilterType ft) {
    filterType = ft;
  }

  /**
   * Basic info
   */
  public String toString() {
    return " cols=" + imgInfo.cols + " bpc=" + imgInfo.bitDepth + " size=" + scanline.length;
  }

  public void readFromPngRaw(byte[] raw, final int len, final int offset, final int step) {
    setFilterType(FilterType.getByVal(raw[0]));
    int len1 = len - 1;
    int step1 = (step - 1) * imgInfo.channels;
    if (imgInfo.bitDepth == 8) {
      if (step == 1) {// 8bispp non-interlaced: most important case, should be optimized
        for (int i = 0; i < size; i++) {
          scanline[i] = (raw[i + 1] & 0xff);
        }
      } else {// 8bispp interlaced
        for (int s = 1, c = 0, i = offset * imgInfo.channels; s <= len1; s++, i++) {
          scanline[i] = (raw[s] & 0xff);
          c++;
          if (c == imgInfo.channels) {
            c = 0;
            i += step1;
          }
        }
      }
    } else if (imgInfo.bitDepth == 16) {
      if (step == 1) {// 16bispp non-interlaced
        for (int i = 0, s = 1; i < size; i++) {
          scanline[i] = ((raw[s++] & 0xFF) << 8) | (raw[s++] & 0xFF); // 16 bitspc
        }
      } else {
        for (int s = 1, c = 0, i = offset != 0 ? offset * imgInfo.channels : 0; s <= len1; s++, i++) {
          scanline[i] = ((raw[s++] & 0xFF) << 8) | (raw[s] & 0xFF); // 16 bitspc
          c++;
          if (c == imgInfo.channels) {
            c = 0;
            i += step1;
          }
        }
      }
    } else { // packed formats
      int mask0, mask, shi, bd;
      bd = imgInfo.bitDepth;
      mask0 = ImageLineHelper.getMaskForPackedFormats(bd);
      for (int i = offset * imgInfo.channels, r = 1, c = 0; r < len; r++) {
        mask = mask0;
        shi = 8 - bd;
        do {
          scanline[i++] = (raw[r] & mask) >> shi;
          mask >>= bd;
          shi -= bd;
          c++;
          if (c == imgInfo.channels) {
            c = 0;
            i += step1;
          }
        } while (mask != 0 && i < size);
      }
    }
  }

  public void writeToPngRaw(byte[] raw) {
    raw[0] = (byte) filterType.val;
    if (imgInfo.bitDepth == 8) {
      for (int i = 0; i < size; i++) {
        raw[i + 1] = (byte) scanline[i];
      }
    } else if (imgInfo.bitDepth == 16) {
      for (int i = 0, s = 1; i < size; i++) {
        raw[s++] = (byte) (scanline[i] >> 8);
        raw[s++] = (byte) (scanline[i] & 0xff);
      }
    } else { // packed formats
      int shi, bd, v;
      bd = imgInfo.bitDepth;
      shi = 8 - bd;
      v = 0;
      for (int i = 0, r = 1; i < size; i++) {
        v |= (scanline[i] << shi);
        shi -= bd;
        if (shi < 0 || i == size - 1) {
          raw[r++] = (byte) v;
          shi = 8 - bd;
          v = 0;
        }
      }
    }
  }

  /**
   * Does nothing in this implementation
   */
  public void endReadFromPngRaw() {

  }

  /**
   * @see #size
   */
  public int getSize() {
    return size;
  }

  public int getElem(int i) {
    return scanline[i];
  }

  /**
   * @return see {@link #scanline}
   */
  public int[] getScanline() {
    return scanline;
  }

  public ImageInfo getImageInfo() {
    return imgInfo;
  }
}
