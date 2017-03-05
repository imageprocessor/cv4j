package com.cv4j.core.datamodel;

import android.graphics.Bitmap;

public interface ImageData {
	public final static int CV4J_IMAGE_TYPE_RGB = 1;
	public final static int CV4J_IMAGE_TYPE_GRAY = 2;
	public final static int CV4J_IMAGE_TYPE_HSV = 4;
	public final static int CV4J_IMAGE_TYPE_BINARY = 8;

	public int[] getPixels();

	public int getWidth();

	public int getHeight();

	public int getType();

	public byte[] getChannel(int index);

	public void putPixels(int[] pixels);

	public int getPixel(int row, int col);

	public void setPixel(int row, int col, int rgb);

	public int[] getPixelByRowNumber(int rowIndex);

	public void convert2Gray();

	public Bitmap toBitmap();
}
