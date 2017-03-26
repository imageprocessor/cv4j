package com.cv4j.core.datamodel;

public interface ImageProcessor {

	/** Returns the width of this image in pixels. */
	int getWidth();

	/** Returns the height of this image in pixels. */
	int getHeight();

	/** Returns the channels of this image. */
	int getChannels();

	void getPixel(int row, int col, byte[] rgb);

	/** get all pixels */
	int[] getPixels();

	ImageData getImage();
}
