package com.hjg.pngj;

/**
 * Image Line factory.
 */
public interface IImageLineFactory<T extends IImageLine> {
  public T createImageLine(ImageInfo iminfo);
}
