package com.hjg.pngj.pixels;

import java.nio.ByteOrder;

/**
 * This estimator actually uses the LZ4 compression algorithm, and hopes that it's well correlated with Deflater. It's
 * about 3 to 4 times faster than Deflater.
 * 
 * This is a modified heavily trimmed version of the net.jpountz.lz4.LZ4JavaSafeCompressor class plus some methods from
 * other classes from LZ4 Java library: https://github.com/jpountz/lz4-java , originally licensed under the Apache
 * License 2.0
 */
final public class DeflaterEstimatorLz4 {

  /**
   * This object is stateless, it's thread safe and can be reused
   */
  public DeflaterEstimatorLz4() {}

  /**
   * Estimates the length of the compressed bytes, as compressed by Lz4 WARNING: if larger than LZ4_64K_LIMIT it cuts it
   * in fragments
   * 
   * WARNING: if some part of the input is discarded, this should return the proportional (so that
   * returnValue/srcLen=compressionRatio)
   * 
   * @param src
   * @param srcOff
   * @param srcLen
   * @return length of the compressed bytes
   */
  public int compressEstim(byte[] src, int srcOff, final int srcLen) {
    if (srcLen < 10)
      return srcLen; // too small
    int stride = LZ4_64K_LIMIT - 1;
    int segments = (srcLen + stride - 1) / stride;
    stride = srcLen / segments;
    if (stride >= LZ4_64K_LIMIT - 1 || stride * segments > srcLen || segments < 1 || stride < 1)
      throw new RuntimeException("?? " + srcLen);
    int bytesIn = 0;
    int bytesOut = 0;
    int len = srcLen;
    while (len > 0) {
      if (len > stride)
        len = stride;
      bytesOut += compress64k(src, srcOff, len);
      srcOff += len;
      bytesIn += len;
      len = srcLen - bytesIn;
    }
    double ratio = bytesOut / (double) bytesIn;
    return bytesIn == srcLen ? bytesOut : (int) (ratio * srcLen + 0.5);
  }

  public int compressEstim(byte[] src) {
    return compressEstim(src, 0, src.length);
  }

  static final ByteOrder NATIVE_BYTE_ORDER = ByteOrder.nativeOrder();

  static final int MEMORY_USAGE = 14;
  static final int NOT_COMPRESSIBLE_DETECTION_LEVEL = 6;

  static final int MIN_MATCH = 4;

  static final int HASH_LOG = MEMORY_USAGE - 2;
  static final int HASH_TABLE_SIZE = 1 << HASH_LOG;

  static final int SKIP_STRENGTH = Math.max(NOT_COMPRESSIBLE_DETECTION_LEVEL, 2);
  static final int COPY_LENGTH = 8;
  static final int LAST_LITERALS = 5;
  static final int MF_LIMIT = COPY_LENGTH + MIN_MATCH;
  static final int MIN_LENGTH = MF_LIMIT + 1;

  static final int MAX_DISTANCE = 1 << 16;

  static final int ML_BITS = 4;
  static final int ML_MASK = (1 << ML_BITS) - 1;
  static final int RUN_BITS = 8 - ML_BITS;
  static final int RUN_MASK = (1 << RUN_BITS) - 1;

  static final int LZ4_64K_LIMIT = (1 << 16) + (MF_LIMIT - 1);
  static final int HASH_LOG_64K = HASH_LOG + 1;
  static final int HASH_TABLE_SIZE_64K = 1 << HASH_LOG_64K;

  static final int HASH_LOG_HC = 15;
  static final int HASH_TABLE_SIZE_HC = 1 << HASH_LOG_HC;
  static final int OPTIMAL_ML = ML_MASK - 1 + MIN_MATCH;

  static int compress64k(byte[] src, int srcOff, int srcLen) {
    final int srcEnd = srcOff + srcLen;
    final int srcLimit = srcEnd - LAST_LITERALS;
    final int mflimit = srcEnd - MF_LIMIT;

    int sOff = srcOff, dOff = 0;

    int anchor = sOff;

    if (srcLen >= MIN_LENGTH) {

      final short[] hashTable = new short[HASH_TABLE_SIZE_64K];

      ++sOff;

      main: while (true) {

        // find a match
        int forwardOff = sOff;

        int ref;
        int findMatchAttempts = (1 << SKIP_STRENGTH) + 3;
        do {
          sOff = forwardOff;
          forwardOff += findMatchAttempts++ >>> SKIP_STRENGTH;

          if (forwardOff > mflimit) {
            break main;
          }

          final int h = hash64k(readInt(src, sOff));
          ref = srcOff + readShort(hashTable, h);
          writeShort(hashTable, h, sOff - srcOff);
        } while (!readIntEquals(src, ref, sOff));

        // catch up
        final int excess = commonBytesBackward(src, ref, sOff, srcOff, anchor);
        sOff -= excess;
        ref -= excess;
        // sequence == refsequence
        final int runLen = sOff - anchor;
        dOff++;

        if (runLen >= RUN_MASK) {
          if (runLen > RUN_MASK)
            dOff += (runLen - RUN_MASK) / 0xFF;
          dOff++;
        }
        dOff += runLen;
        while (true) {
          // encode offset
          dOff += 2;
          // count nb matches
          sOff += MIN_MATCH;
          ref += MIN_MATCH;
          final int matchLen = commonBytes(src, ref, sOff, srcLimit);
          sOff += matchLen;
          // encode match len
          if (matchLen >= ML_MASK) {
            if (matchLen >= ML_MASK + 0xFF)
              dOff += (matchLen - ML_MASK) / 0xFF;
            dOff++;
          }
          // test end of chunk
          if (sOff > mflimit) {
            anchor = sOff;
            break main;
          }
          // fill table
          writeShort(hashTable, hash64k(readInt(src, sOff - 2)), sOff - 2 - srcOff);
          // test next position
          final int h = hash64k(readInt(src, sOff));
          ref = srcOff + readShort(hashTable, h);
          writeShort(hashTable, h, sOff - srcOff);
          if (!readIntEquals(src, sOff, ref)) {
            break;
          }
          dOff++;
        }
        // prepare next loop
        anchor = sOff++;
      }
    }
    int runLen = srcEnd - anchor;
    if (runLen >= RUN_MASK + 0xFF) {
      dOff += (runLen - RUN_MASK) / 0xFF;
    }
    dOff++;
    dOff += runLen;
    return dOff;
  }

  static final int maxCompressedLength(int length) {
    if (length < 0) {
      throw new IllegalArgumentException("length must be >= 0, got " + length);
    }
    return length + length / 255 + 16;
  }

  static int hash(int i) {
    return (i * -1640531535) >>> ((MIN_MATCH * 8) - HASH_LOG);
  }

  static int hash64k(int i) {
    return (i * -1640531535) >>> ((MIN_MATCH * 8) - HASH_LOG_64K);
  }

  static int readShortLittleEndian(byte[] buf, int i) {
    return (buf[i] & 0xFF) | ((buf[i + 1] & 0xFF) << 8);
  }

  static boolean readIntEquals(byte[] buf, int i, int j) {
    return buf[i] == buf[j] && buf[i + 1] == buf[j + 1] && buf[i + 2] == buf[j + 2]
        && buf[i + 3] == buf[j + 3];
  }

  static int commonBytes(byte[] b, int o1, int o2, int limit) {
    int count = 0;
    while (o2 < limit && b[o1++] == b[o2++]) {
      ++count;
    }
    return count;
  }

  static int commonBytesBackward(byte[] b, int o1, int o2, int l1, int l2) {
    int count = 0;
    while (o1 > l1 && o2 > l2 && b[--o1] == b[--o2]) {
      ++count;
    }
    return count;
  }

  static int readShort(short[] buf, int off) {
    return buf[off] & 0xFFFF;
  }

  static byte readByte(byte[] buf, int i) {
    return buf[i];
  }

  static void checkRange(byte[] buf, int off) {
    if (off < 0 || off >= buf.length) {
      throw new ArrayIndexOutOfBoundsException(off);
    }
  }

  static void checkRange(byte[] buf, int off, int len) {
    checkLength(len);
    if (len > 0) {
      checkRange(buf, off);
      checkRange(buf, off + len - 1);
    }
  }

  static void checkLength(int len) {
    if (len < 0) {
      throw new IllegalArgumentException("lengths must be >= 0");
    }
  }

  static int readIntBE(byte[] buf, int i) {
    return ((buf[i] & 0xFF) << 24) | ((buf[i + 1] & 0xFF) << 16) | ((buf[i + 2] & 0xFF) << 8)
        | (buf[i + 3] & 0xFF);
  }

  static int readIntLE(byte[] buf, int i) {
    return (buf[i] & 0xFF) | ((buf[i + 1] & 0xFF) << 8) | ((buf[i + 2] & 0xFF) << 16)
        | ((buf[i + 3] & 0xFF) << 24);
  }

  static int readInt(byte[] buf, int i) {
    if (NATIVE_BYTE_ORDER == ByteOrder.BIG_ENDIAN) {
      return readIntBE(buf, i);
    } else {
      return readIntLE(buf, i);
    }
  }

  static void writeShort(short[] buf, int off, int v) {
    buf[off] = (short) v;
  }

}
