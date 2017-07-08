package com.hjg.pngj;

import java.io.OutputStream;

public interface IPngWriterFactory {
  public PngWriter createPngWriter(OutputStream outputStream, ImageInfo imgInfo);
}
