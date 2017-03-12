package com.cv4j.core.datamodel;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.cv4j.exception.CV4JException;
import com.cv4j.image.util.IOUtils;

import java.io.InputStream;
import java.io.Serializable;

public class ColorImage implements ImageData,Serializable {

	private static final long serialVersionUID = -6258992466268616924L;

	private byte[][] pdata; // R, B, G Channels
	private int width;
	private int height;
	private int type;

	public ColorImage(Bitmap bitmap) {
		if (bitmap == null) {
			throw new CV4JException("bitmap is null");
		}

		width = bitmap.getWidth();
		height = bitmap.getHeight();

		pdata = new byte[3][width * height];
		int[] input = new int[width * height];
		bitmap.getPixels(input, 0, width, 0, 0, width, height);
		backFillData(input);
		input = null;
	}

	private void backFillData(int[] input) {
		for(int i=0; i<input.length; i++) {
			int r = (input[i] >> 16) & 0xff;
			int g = (input[i] >> 8) & 0xff;
			int b = (input[i]) & 0xff;
			pdata[0][i] = (byte)r;
			pdata[1][i] = (byte)g;
			pdata[2][i] = (byte)b;
		}
	}

	private int[] getOutputPixels() {
		int[] pixels = new int[width*height];
		for (int i=0; i < width*height; i++)
			pixels[i] = 0xff000000 | ((pdata[0][i]&0xff)<<16) | ((pdata[1][i]&0xff)<<8) | pdata[2][i]&0xff;
		return pixels;
	}

	public ColorImage(InputStream inputStream) {
		if (inputStream == null) {
			throw new CV4JException("inputStream is null");
		}

		Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
		width = bitmap.getWidth();
		height = bitmap.getHeight();

		pdata = new byte[3][width * height];
		int[] input = new int[width * height];
		bitmap.getPixels(input, 0, width, 0, 0, width, height);
		backFillData(input);
		input = null;

		IOUtils.closeQuietly(inputStream);
	}

	public ColorImage(byte[] bytes) {
		if (bytes == null) {
			throw new CV4JException("byte is null");
		}

		Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
		width = bitmap.getWidth();
		height = bitmap.getHeight();
		pdata = new byte[3][width * height];
		int[] input = new int[width * height];
		bitmap.getPixels(input, 0, width, 0, 0, width, height);
		backFillData(input);
		input = null;
	}

	@Override
	public byte[][] getPixels() {
		return pdata;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public int getType() {
		return this.type;
	}

	/**
	 *
	 * @param index, 0 -red channel, 1 - green channel, 2 - blue channel,
	 *
	 * @return 0, 1, 2 return byte array, otherwise null
     */
	public byte[] getChannel(int index) {
		// TODO:zhigang check invalid paramter - index
		byte[] data = new byte[width*height];
		int len = width*height;
		if(type == CV4J_IMAGE_TYPE_RGB) {
			return pdata[index];
		} else if(CV4J_IMAGE_TYPE_GRAY == type ||
				CV4J_IMAGE_TYPE_BINARY == type) {
			return pdata[0];
		}
		return data;
	}

	public void putPixels(byte[][] pixels) {
		System.arraycopy(pixels[0], 0, pdata[0], 0, width*height);
		System.arraycopy(pixels[1], 0, pdata[1], 0, width*height);
		System.arraycopy(pixels[2], 0, pdata[2], 0, width*height);
	}

	@Override
	public int getPixel(int row, int col) {
		int index = row*width + col;
		// check OutOfBoundary
		int p = 0xff000000 | ((pdata[0][index]&0xff)<<16) |
				((pdata[1][index]&0xff)<<8) |
				pdata[2][index]&0xff;
		return p;
	}

	@Override
	public void setPixel(int row, int col, int rgb) {
		int index = row*width + col;
		int r = (rgb&0xff0000)>>16;
		int g = (rgb&0xff00)>>8;
		int b = rgb&0xff;
		pdata[0][index] = (byte)r;
		pdata[1][index] = (byte)g;
		pdata[2][index] = (byte)b;
	}

	@Override
	public int[] getPixelByRowNumber(int rowIndex) {
		int[] pixels = new int[width];
		System.arraycopy(pdata, rowIndex*width, pixels, 0, width);
		return pixels;
	}

	public void convert2Gray() {
		byte[] gray = new byte[width*height];
		int offset = 0;
		int g=0;
		for(int row=0; row < height; row++) {
			offset = row*width;
			int tr=0, tg=0, tb=0;
			for(int col=0; col<width; col++) {
				tr = pdata[0][offset] & 0xff;
				tg = pdata[1][offset] & 0xff;
				tb = pdata[2][offset] & 0xff;
				g= (int)(0.299 *tr + 0.587*tg + 0.114*tb);
				gray[offset]  = (byte)g;
				offset++;
			}
		}
		type = CV4J_IMAGE_TYPE_GRAY;
		System.arraycopy(gray, 0, pdata[0], 0, pdata.length);
		gray = null;
	}

	public Bitmap toBitmap() {

		return toBitmap(Bitmap.Config.RGB_565);
	}

	public Bitmap toBitmap(Bitmap.Config bitmapConfig) {
		Bitmap bitmap = Bitmap.createBitmap(width, height, bitmapConfig);
		if(type == CV4J_IMAGE_TYPE_RGB) {
			bitmap.setPixels(getOutputPixels(), 0, width, 0, 0, width, height);
		} else if(CV4J_IMAGE_TYPE_GRAY == type ||
				CV4J_IMAGE_TYPE_BINARY == type) {
			int[] rgb = new int[pdata.length];
			int offset = 0;
			for(int row=0; row < height; row++) {
				offset = row*width;
				int ta=255, tr=0, tg=0, tb=0;
				for(int col=0; col<width; col++) {
					rgb[offset] = (ta << 24) | (pdata[0][offset] << 16) | (pdata[0][offset] << 8) | pdata[0][offset];
					offset++;
				}
			}
			bitmap.setPixels(rgb, 0, width, 0, 0, width, height);
		} else {
			// Exception
			Log.e("ColorImage","can not convert to bitmap!");
		}
		return bitmap;
	}
}
