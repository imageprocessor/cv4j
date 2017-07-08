package com.hjg.pngj;

/**
 * General format-translated image line.
 * <p>
 * The methods from this interface provides translation from/to PNG raw unfiltered pixel data, for each image line. This
 * doesn't make any assumptions of underlying storage.
 * <p>
 * The user of this library will not normally use this methods, but instead will cast to a more concrete implementation,
 * as {@link ImageLineInt} or {@link ImageLineByte} with its methods for accessing the pixel values.
 */
public interface IImageLine {

  /**
   * Extract pixels from a raw unlfilterd PNG row. Len is the total amount of bytes in the array, including the first
   * byte (filter type)
   * 
   * Arguments offset and step (0 and 1 for non interlaced) are in PIXELS. It's guaranteed that when step==1 then
   * offset=0
   * 
   * Notice that when step!=1 the data is partial, this method will be called several times
   * 
   * Warning: the data in array 'raw' starts at position 0 and has 'len' consecutive bytes. 'offset' and 'step' refer to
   * the pixels in destination
   */
  void readFromPngRaw(byte[] raw, int len, int offset, int step);

  /**
   * This is called when the read for the line has been completed (eg for interlaced). It's called exactly once for each
   * line. This is provided in case the class needs to to some postprocessing.
   */
  void endReadFromPngRaw();

  /**
   * Writes the line to a PNG raw byte array, in the unfiltered PNG format Notice that the first byte is the filter
   * type, you should write it only if you know it.
   * 
   */
  void writeToPngRaw(byte[] raw);

}
