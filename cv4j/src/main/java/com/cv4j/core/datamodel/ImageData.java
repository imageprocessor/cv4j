package com.cv4j.core.datamodel;

import android.graphics.Bitmap;

public interface ImageData {

	int CV4J_IMAGE_TYPE_RGB = 0;
	int CV4J_IMAGE_TYPE_GRAY = 2;
	int CV4J_IMAGE_TYPE_HSV = 4;
	int CV4J_IMAGE_TYPE_BINARY = 8;

	int[] getPixels();

	int getWidth();

	int getHeight();

	int getType();

	byte[] getChannel(int index);

	void putPixels(int[] pixels);

	int getPixel(int row, int col);

	void setPixel(int row, int col, int rgb);

	int[] getPixelByRowNumber(int rowIndex);

	void convert2Gray();

	Bitmap toBitmap();
}
