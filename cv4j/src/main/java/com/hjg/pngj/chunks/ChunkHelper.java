package com.hjg.pngj.chunks;

// see http://www.libpng.org/pub/png/spec/1.2/PNG-Chunks.html
// http://www.w3.org/TR/PNG/#5Chunk-naming-conventions
// http://www.w3.org/TR/PNG/#table53
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

import com.hjg.pngj.PngHelperInternal;
import com.hjg.pngj.PngjException;

/**
 * Helper methods and constants related to Chunk processing.
 * <p>
 * This should only be of interest to developers doing special chunk processing or extending the ChunkFactory
 */
public class ChunkHelper {
  ChunkHelper() {}

  public static final String IHDR = "IHDR";
  public static final String PLTE = "PLTE";
  public static final String IDAT = "IDAT";
  public static final String IEND = "IEND";
  public static final String cHRM = "cHRM";
  public static final String gAMA = "gAMA";
  public static final String iCCP = "iCCP";
  public static final String sBIT = "sBIT";
  public static final String sRGB = "sRGB";
  public static final String bKGD = "bKGD";
  public static final String hIST = "hIST";
  public static final String tRNS = "tRNS";
  public static final String pHYs = "pHYs";
  public static final String sPLT = "sPLT";
  public static final String tIME = "tIME";
  public static final String iTXt = "iTXt";
  public static final String tEXt = "tEXt";
  public static final String zTXt = "zTXt";

  public static final byte[] b_IHDR = toBytes(IHDR);
  public static final byte[] b_PLTE = toBytes(PLTE);
  public static final byte[] b_IDAT = toBytes(IDAT);
  public static final byte[] b_IEND = toBytes(IEND);

  /*
   * static auxiliary buffer. any method that uses this should synchronize against this
   */
  private static byte[] tmpbuffer = new byte[4096];

  /**
   * Converts to bytes using Latin1 (ISO-8859-1)
   */
  public static byte[] toBytes(String x) {
    try {
      return x.getBytes(PngHelperInternal.charsetLatin1name);
    } catch (UnsupportedEncodingException e) {
      throw new PngBadCharsetException(e);
    }
  }

  /**
   * Converts to String using Latin1 (ISO-8859-1)
   */
  public static String toString(byte[] x) {
    try {
      return new String(x, PngHelperInternal.charsetLatin1name);
    } catch (UnsupportedEncodingException e) {
      throw new PngBadCharsetException(e);
    }
  }

  /**
   * Converts to String using Latin1 (ISO-8859-1)
   */
  public static String toString(byte[] x, int offset, int len) {
    try {
      return new String(x, offset, len, PngHelperInternal.charsetLatin1name);
    } catch (UnsupportedEncodingException e) {
      throw new PngBadCharsetException(e);
    }
  }

  /**
   * Converts to bytes using UTF-8
   */
  public static byte[] toBytesUTF8(String x) {
    try {
      return x.getBytes(PngHelperInternal.charsetUTF8name);
    } catch (UnsupportedEncodingException e) {
      throw new PngBadCharsetException(e);
    }
  }

  /**
   * Converts to string using UTF-8
   */
  public static String toStringUTF8(byte[] x) {
    try {
      return new String(x, PngHelperInternal.charsetUTF8name);
    } catch (UnsupportedEncodingException e) {
      throw new PngBadCharsetException(e);
    }
  }

  /**
   * Converts to string using UTF-8
   */
  public static String toStringUTF8(byte[] x, int offset, int len) {
    try {
      return new String(x, offset, len, PngHelperInternal.charsetUTF8name);
    } catch (UnsupportedEncodingException e) {
      throw new PngBadCharsetException(e);
    }
  }

  /**
   * critical chunk : first letter is uppercase
   */
  public static boolean isCritical(String id) {
    return (Character.isUpperCase(id.charAt(0)));
  }

  /**
   * public chunk: second letter is uppercase
   */
  public static boolean isPublic(String id) { //
    return (Character.isUpperCase(id.charAt(1)));
  }

  /**
   * Safe to copy chunk: fourth letter is lower case
   */
  public static boolean isSafeToCopy(String id) {
    return (!Character.isUpperCase(id.charAt(3)));
  }

  /**
   * "Unknown" just means that our chunk factory (even when it has been augmented by client code) did not recognize its
   * id
   */
  public static boolean isUnknown(PngChunk c) {
    return c instanceof PngChunkUNKNOWN;
  }

  /**
   * Finds position of null byte in array
   * 
   * @param b
   * @return -1 if not found
   */
  public static int posNullByte(byte[] b) {
    for (int i = 0; i < b.length; i++)
      if (b[i] == 0)
        return i;
    return -1;
  }

  /**
   * Decides if a chunk should be loaded, according to a ChunkLoadBehaviour
   * 
   * @param id
   * @param behav
   * @return true/false
   */
  public static boolean shouldLoad(String id, ChunkLoadBehaviour behav) {
    if (isCritical(id))
      return true;
    switch (behav) {
      case LOAD_CHUNK_ALWAYS:
        return true;
      case LOAD_CHUNK_IF_SAFE:
        return isSafeToCopy(id);
      case LOAD_CHUNK_NEVER:
        return false;
      case LOAD_CHUNK_MOST_IMPORTANT:
        return id.equals(PngChunkTRNS.ID);
    }
    return false; // should not reach here
  }

  public final static byte[] compressBytes(byte[] ori, boolean compress) {
    return compressBytes(ori, 0, ori.length, compress);
  }

  public static byte[] compressBytes(byte[] ori, int offset, int len, boolean compress) {
    try {
      ByteArrayInputStream inb = new ByteArrayInputStream(ori, offset, len);
      InputStream in = compress ? inb : new InflaterInputStream(inb);
      ByteArrayOutputStream outb = new ByteArrayOutputStream();
      OutputStream out = compress ? new DeflaterOutputStream(outb) : outb;
      shovelInToOut(in, out);
      in.close();
      out.close();
      return outb.toByteArray();
    } catch (Exception e) {
      throw new PngjException(e);
    }
  }

  /**
   * Shovels all data from an input stream to an output stream.
   */
  private static void shovelInToOut(InputStream in, OutputStream out) throws IOException {
    synchronized (tmpbuffer) {
      int len;
      while ((len = in.read(tmpbuffer)) > 0) {
        out.write(tmpbuffer, 0, len);
      }
    }
  }

  /**
   * Returns only the chunks that "match" the predicate
   * 
   * See also trimList()
   */
  public static List<PngChunk> filterList(List<PngChunk> target, ChunkPredicate predicateKeep) {
    List<PngChunk> result = new ArrayList<PngChunk>();
    for (PngChunk element : target) {
      if (predicateKeep.match(element)) {
        result.add(element);
      }
    }
    return result;
  }

  /**
   * Remove (in place) the chunks that "match" the predicate
   * 
   * See also filterList
   */
  public static int trimList(List<PngChunk> target, ChunkPredicate predicateRemove) {
    Iterator<PngChunk> it = target.iterator();
    int cont = 0;
    while (it.hasNext()) {
      PngChunk c = it.next();
      if (predicateRemove.match(c)) {
        it.remove();
        cont++;
      }
    }
    return cont;
  }

  /**
   * Adhoc criteria: two ancillary chunks are "equivalent" ("practically same type") if they have same id and (perhaps,
   * if multiple are allowed) if the match also in some "internal key" (eg: key for string values, palette for sPLT,
   * etc)
   * 
   * When we use this method, we implicitly assume that we don't allow/expect two "equivalent" chunks in a single PNG
   * 
   * Notice that the use of this is optional, and that the PNG standard actually allows text chunks that have same key
   * 
   * @return true if "equivalent"
   */
  public static final boolean equivalent(PngChunk c1, PngChunk c2) {
    if (c1 == c2)
      return true;
    if (c1 == null || c2 == null || !c1.id.equals(c2.id))
      return false;
    if (c1.crit)
      return false;
    // same id
    if (c1.getClass() != c2.getClass())
      return false; // should not happen
    if (!c2.allowsMultiple())
      return true;
    if (c1 instanceof PngChunkTextVar) {
      return ((PngChunkTextVar) c1).getKey().equals(((PngChunkTextVar) c2).getKey());
    }
    if (c1 instanceof PngChunkSPLT) {
      return ((PngChunkSPLT) c1).getPalName().equals(((PngChunkSPLT) c2).getPalName());
    }
    // unknown chunks that allow multiple? consider they don't match
    return false;
  }

  public static boolean isText(PngChunk c) {
    return c instanceof PngChunkTextVar;
  }

}
