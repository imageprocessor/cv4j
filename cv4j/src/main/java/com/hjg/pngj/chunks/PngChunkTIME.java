package com.hjg.pngj.chunks;

import java.util.Calendar;

import com.hjg.pngj.ImageInfo;
import com.hjg.pngj.PngHelperInternal;
import com.hjg.pngj.PngjException;

/**
 * tIME chunk.
 * <p>
 * see http://www.w3.org/TR/PNG/#11tIME
 */
public class PngChunkTIME extends PngChunkSingle {
  public final static String ID = ChunkHelper.tIME;

  // http://www.w3.org/TR/PNG/#11tIME
  private int year, mon, day, hour, min, sec;

  public PngChunkTIME(ImageInfo info) {
    super(ID, info);
  }

  @Override
  public ChunkOrderingConstraint getOrderingConstraint() {
    return ChunkOrderingConstraint.NONE;
  }

  @Override
  public ChunkRaw createRawChunk() {
    ChunkRaw c = createEmptyChunk(7, true);
    PngHelperInternal.writeInt2tobytes(year, c.data, 0);
    c.data[2] = (byte) mon;
    c.data[3] = (byte) day;
    c.data[4] = (byte) hour;
    c.data[5] = (byte) min;
    c.data[6] = (byte) sec;
    return c;
  }

  @Override
  public void parseFromRaw(ChunkRaw chunk) {
    if (chunk.len != 7)
      throw new PngjException("bad chunk " + chunk);
    year = PngHelperInternal.readInt2fromBytes(chunk.data, 0);
    mon = PngHelperInternal.readInt1fromByte(chunk.data, 2);
    day = PngHelperInternal.readInt1fromByte(chunk.data, 3);
    hour = PngHelperInternal.readInt1fromByte(chunk.data, 4);
    min = PngHelperInternal.readInt1fromByte(chunk.data, 5);
    sec = PngHelperInternal.readInt1fromByte(chunk.data, 6);
  }

  public void setNow(int secsAgo) {
    Calendar d = Calendar.getInstance();
    d.setTimeInMillis(System.currentTimeMillis() - 1000 * (long) secsAgo);
    year = d.get(Calendar.YEAR);
    mon = d.get(Calendar.MONTH) + 1;
    day = d.get(Calendar.DAY_OF_MONTH);
    hour = d.get(Calendar.HOUR_OF_DAY);
    min = d.get(Calendar.MINUTE);
    sec = d.get(Calendar.SECOND);
  }

  public void setYMDHMS(int yearx, int monx, int dayx, int hourx, int minx, int secx) {
    year = yearx;
    mon = monx;
    day = dayx;
    hour = hourx;
    min = minx;
    sec = secx;
  }

  public int[] getYMDHMS() {
    return new int[] {year, mon, day, hour, min, sec};
  }

  /** format YYYY/MM/DD HH:mm:SS */
  public String getAsString() {
    return String.format("%04d/%02d/%02d %02d:%02d:%02d", year, mon, day, hour, min, sec);
  }

}
