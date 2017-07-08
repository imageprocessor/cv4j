package com.hjg.pngj.chunks;

import com.hjg.pngj.ImageInfo;

/**
 * PNG chunk type (abstract) that does not allow multiple instances in same image.
 */
public abstract class PngChunkSingle extends PngChunk {

  protected PngChunkSingle(String id, ImageInfo imgInfo) {
    super(id, imgInfo);
  }

  public final boolean allowsMultiple() {
    return false;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
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
    PngChunkSingle other = (PngChunkSingle) obj;
    if (id == null) {
      if (other.id != null)
        return false;
    } else if (!id.equals(other.id))
      return false;
    return true;
  }

}
