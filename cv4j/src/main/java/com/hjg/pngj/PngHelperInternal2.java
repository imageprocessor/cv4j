package com.hjg.pngj;

import java.io.File;
import java.io.OutputStream;

/**
 * For organization purposes, this class is the onlt that uses classes not in GAE (Google App Engine) white list
 * <p>
 * You should not use this class in GAE
 */
final class PngHelperInternal2 {

  /**
   * WARNING: this uses FileOutputStream which is not allowed in GoogleAppEngine
   * 
   * In GAE, dont use this
   * 
   * @param f
   * @param allowoverwrite
   * @return
   */
  static OutputStream ostreamFromFile(File f, boolean allowoverwrite) {
    java.io.FileOutputStream os = null; // this will fail in GAE!
    if (f.exists() && !allowoverwrite)
      throw new PngjOutputException("File already exists: " + f);
    try {
      os = new java.io.FileOutputStream(f);
    } catch (Exception e) {
      throw new PngjInputException("Could not open for write" + f, e);
    }
    return os;
  }
}
