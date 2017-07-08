package com.hjg.pngj.chunks;

import com.hjg.pngj.ImageInfo;

/**
 * Superclass (abstract) for three textual chunks (TEXT, ITXT, ZTXT)
 */
public abstract class PngChunkTextVar extends PngChunkMultiple {
  protected String key; // key/val: only for tEXt. lazy computed
  protected String val;

  // http://www.w3.org/TR/PNG/#11keywords
  public final static String KEY_Title = "Title"; // Short (one line) title or caption for image
  public final static String KEY_Author = "Author"; // Name of image's creator
  public final static String KEY_Description = "Description"; // Description of image (possibly
                                                              // long)
  public final static String KEY_Copyright = "Copyright"; // Copyright notice
  public final static String KEY_Creation_Time = "Creation Time"; // Time of original image creation
  public final static String KEY_Software = "Software"; // Software used to create the image
  public final static String KEY_Disclaimer = "Disclaimer"; // Legal disclaimer
  public final static String KEY_Warning = "Warning"; // Warning of nature of content
  public final static String KEY_Source = "Source"; // Device used to create the image
  public final static String KEY_Comment = "Comment"; // Miscellaneous comment

  protected PngChunkTextVar(String id, ImageInfo info) {
    super(id, info);
  }

  @Override
  public ChunkOrderingConstraint getOrderingConstraint() {
    return ChunkOrderingConstraint.NONE;
  }

  public static class PngTxtInfo {
    public String title;
    public String author;
    public String description;
    public String creation_time;// = (new Date()).toString();
    public String software;
    public String disclaimer;
    public String warning;
    public String source;
    public String comment;

  }

  public String getKey() {
    return key;
  }

  public String getVal() {
    return val;
  }

  public void setKeyVal(String key, String val) {
    this.key = key;
    this.val = val;
  }

}
