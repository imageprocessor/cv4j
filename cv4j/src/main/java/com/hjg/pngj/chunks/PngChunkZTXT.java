package com.hjg.pngj.chunks;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.hjg.pngj.ImageInfo;
import com.hjg.pngj.PngjException;

/**
 * zTXt chunk.
 * <p>
 * see http://www.w3.org/TR/PNG/#11zTXt
 */
public class PngChunkZTXT extends PngChunkTextVar {
  public final static String ID = ChunkHelper.zTXt;

  // http://www.w3.org/TR/PNG/#11zTXt
  public PngChunkZTXT(ImageInfo info) {
    super(ID, info);
  }

  @Override
  public ChunkRaw createRawChunk() {
    if (key == null || key.trim().length() == 0)
      throw new PngjException("Text chunk key must be non empty");
    try {
      ByteArrayOutputStream ba = new ByteArrayOutputStream();
      ba.write(ChunkHelper.toBytes(key));
      ba.write(0); // separator
      ba.write(0); // compression method: 0
      byte[] textbytes = ChunkHelper.compressBytes(ChunkHelper.toBytes(val), true);
      ba.write(textbytes);
      byte[] b = ba.toByteArray();
      ChunkRaw chunk = createEmptyChunk(b.length, false);
      chunk.data = b;
      return chunk;
    } catch (IOException e) {
      throw new PngjException(e);
    }
  }

  @Override
  public void parseFromRaw(ChunkRaw c) {
    int nullsep = -1;
    for (int i = 0; i < c.data.length; i++) { // look for first zero
      if (c.data[i] != 0)
        continue;
      nullsep = i;
      break;
    }
    if (nullsep < 0 || nullsep > c.data.length - 2)
      throw new PngjException("bad zTXt chunk: no separator found");
    key = ChunkHelper.toString(c.data, 0, nullsep);
    int compmet = (int) c.data[nullsep + 1];
    if (compmet != 0)
      throw new PngjException("bad zTXt chunk: unknown compression method");
    byte[] uncomp =
        ChunkHelper.compressBytes(c.data, nullsep + 2, c.data.length - nullsep - 2, false); // uncompress
    val = ChunkHelper.toString(uncomp);
  }

}
