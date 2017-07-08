package com.hjg.pngj;

import java.io.File;
import java.io.InputStream;

/**
 * Trivial extension of {@link PngReader} that uses {@link ImageLineInt}.
 * <p>
 * In the current implementation this is quite dummy/redundant, because (for backward compatibility) PngReader already
 * uses a {@link ImageLineInt}.
 * <p>
 * The factory is set at construction time. Remember that this could still be changed at runtime.
 */
public class PngReaderInt extends PngReader {

  public PngReaderInt(File file) {
    super(file); // not necessary to set factory, PngReader already does that
  }

  public PngReaderInt(InputStream inputStream) {
    super(inputStream);
  }

  /**
   * Utility method that casts the IImageLine to a ImageLineInt
   * 
   * This only make sense for this concrete class
   * 
   */
  public ImageLineInt readRowInt() {
    IImageLine line = readRow();
    if (line instanceof ImageLineInt)
      return (ImageLineInt) line;
    else
      throw new PngjException("This is not a ImageLineInt : " + line.getClass());
  }
}
