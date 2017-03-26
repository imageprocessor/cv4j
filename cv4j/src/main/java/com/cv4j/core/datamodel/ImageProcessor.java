package com.cv4j.core.datamodel;

import android.graphics.Bitmap;

public interface ImageProcessor {

	/** Returns the width of this image in pixels. */
	public int getWidth();

	/** Returns the height of this image in pixels. */
	public int getHeight();

	/** Returns the channels of this image. */
	public int getChannels();

	public void getPixel(int row, int col, byte[] rgb);

	/** get all pixels */
	public int[] getPixels();

	public ImageData getImage();
}
