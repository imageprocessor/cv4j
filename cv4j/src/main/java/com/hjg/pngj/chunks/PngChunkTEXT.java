package com.hjg.pngj.chunks;

import com.hjg.pngj.ImageInfo;
import com.hjg.pngj.PngjException;

/**
 * tEXt chunk.
 * <p>
 * see http://www.w3.org/TR/PNG/#11tEXt
 */
public class PngChunkTEXT extends PngChunkTextVar {
  public final static String ID = ChunkHelper.tEXt;

  public PngChunkTEXT(ImageInfo info) {
    super(ID, info);
  }

  public PngChunkTEXT(ImageInfo info, String key, String val) {
    super(ID, info);
    setKeyVal(key, val);
  }

  @Override
  public ChunkRaw createRawChunk() {
    if (key == null || key.trim().length() == 0)
      throw new PngjException("Text chunk key must be non empty");
    byte[] b = ChunkHelper.toBytes(key + "\0" + val);
    ChunkRaw chunk = createEmptyChunk(b.length, false);
    chunk.data = b;
    return chunk;
  }

  @Override
  public void parseFromRaw(ChunkRaw c) {
    int i;
    for (i = 0; i < c.data.length; i++)
      if (c.data[i] == 0)
        break;
    key = ChunkHelper.toString(c.data, 0, i);
    i++;
    val = i < c.data.length ? ChunkHelper.toString(c.data, i, c.data.length - i) : "";
  }

}
