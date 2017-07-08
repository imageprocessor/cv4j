package com.hjg.pngj.chunks;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.hjg.pngj.ImageInfo;
import com.hjg.pngj.PngHelperInternal;
import com.hjg.pngj.PngjException;

/**
 * sPLT chunk.
 * <p>
 * see http://www.w3.org/TR/PNG/#11sPLT
 */
public class PngChunkSPLT extends PngChunkMultiple {
  public final static String ID = ChunkHelper.sPLT;

  // http://www.w3.org/TR/PNG/#11sPLT

  private String palName;
  private int sampledepth; // 8/16
  private int[] palette; // 5 elements per entry

  public PngChunkSPLT(ImageInfo info) {
    super(ID, info);
  }

  @Override
  public ChunkOrderingConstraint getOrderingConstraint() {
    return ChunkOrderingConstraint.BEFORE_IDAT;
  }

  @Override
  public ChunkRaw createRawChunk() {
    try {
      ByteArrayOutputStream ba = new ByteArrayOutputStream();
      ba.write(ChunkHelper.toBytes(palName));
      ba.write(0); // separator
      ba.write((byte) sampledepth);
      int nentries = getNentries();
      for (int n = 0; n < nentries; n++) {
        for (int i = 0; i < 4; i++) {
          if (sampledepth == 8)
            PngHelperInternal.writeByte(ba, (byte) palette[n * 5 + i]);
          else
            PngHelperInternal.writeInt2(ba, palette[n * 5 + i]);
        }
        PngHelperInternal.writeInt2(ba, palette[n * 5 + 4]);
      }
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
    int t = -1;
    for (int i = 0; i < c.data.length; i++) { // look for first zero
      if (c.data[i] == 0) {
        t = i;
        break;
      }
    }
    if (t <= 0 || t > c.data.length - 2)
      throw new PngjException("bad sPLT chunk: no separator found");
    palName = ChunkHelper.toString(c.data, 0, t);
    sampledepth = PngHelperInternal.readInt1fromByte(c.data, t + 1);
    t += 2;
    int nentries = (c.data.length - t) / (sampledepth == 8 ? 6 : 10);
    palette = new int[nentries * 5];
    int r, g, b, a, f, ne;
    ne = 0;
    for (int i = 0; i < nentries; i++) {
      if (sampledepth == 8) {
        r = PngHelperInternal.readInt1fromByte(c.data, t++);
        g = PngHelperInternal.readInt1fromByte(c.data, t++);
        b = PngHelperInternal.readInt1fromByte(c.data, t++);
        a = PngHelperInternal.readInt1fromByte(c.data, t++);
      } else {
        r = PngHelperInternal.readInt2fromBytes(c.data, t);
        t += 2;
        g = PngHelperInternal.readInt2fromBytes(c.data, t);
        t += 2;
        b = PngHelperInternal.readInt2fromBytes(c.data, t);
        t += 2;
        a = PngHelperInternal.readInt2fromBytes(c.data, t);
        t += 2;
      }
      f = PngHelperInternal.readInt2fromBytes(c.data, t);
      t += 2;
      palette[ne++] = r;
      palette[ne++] = g;
      palette[ne++] = b;
      palette[ne++] = a;
      palette[ne++] = f;
    }
  }

  public int getNentries() {
    return palette.length / 5;
  }

  public String getPalName() {
    return palName;
  }

  public void setPalName(String palName) {
    this.palName = palName;
  }

  public int getSampledepth() {
    return sampledepth;
  }

  public void setSampledepth(int sampledepth) {
    this.sampledepth = sampledepth;
  }

  public int[] getPalette() {
    return palette;
  }

  public void setPalette(int[] palette) {
    this.palette = palette;
  }

}
