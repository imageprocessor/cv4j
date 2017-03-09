package com.cv4j.core.datamodel;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.cv4j.exception.CV4JException;
import com.cv4j.image.util.IOUtils;

import java.io.InputStream;

public class ColorImage implements ImageData {

	private int[] pdata;
	private int width;
	private int height;
	private int type;

	public ColorImage(Bitmap bitmap) {
		if (bitmap == null) {
			throw new CV4JException("bitmap is null");
		}

		width = bitmap.getWidth();
		height = bitmap.getHeight();
		pdata = new int[width*height];
		bitmap.getPixels(pdata, 0, width, 0, 0, width, height);
	}

	public ColorImage(InputStream inputStream) {
		if (inputStream == null) {
			throw new CV4JException("inputStream is null");
		}

		Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
		width = bitmap.getWidth();
		height = bitmap.getHeight();
		pdata = new int[width*height];
		bitmap.getPixels(pdata, 0, width, 0, 0, width, height);

		IOUtils.closeQuietly(inputStream);
	}

	@Override
	public int[] getPixels() {
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

	@Override
	public byte[] getChannel(int index) {
		byte[] data = new byte[width*height];
		int len = width*height;
		if(type == CV4J_IMAGE_TYPE_RGB) {
			for(int i=0; i<len; i++) {
				int c = pdata[i];
				int b = 0;
				if(index == 0) {
					b = (c&0xff0000)>>16;
				} else if(index == 1) {
					b = (c&0xff00)>>8;
				} else if(index == 2) {
					b = c&0xff;
				}
				data[i] = (byte)b;
			}

		} else if(CV4J_IMAGE_TYPE_GRAY == type ||
				CV4J_IMAGE_TYPE_BINARY == type) {
			for(int i=0; i<len; i++) {
				int c = pdata[i];
				int b = 0;
				b = (c&0xff0000)>>16;
				data[i] = (byte)b;
			}
		}
		return data;
	}

	@Override
	public void putPixels(int[] pixels) {
		System.arraycopy(pixels, 0, pdata, 0, width*height);
	}

	@Override
	public int getPixel(int row, int col) {
		int index = row*width + col;
		// check OutOfBoundary
		return pdata[index];
	}

	@Override
	public void setPixel(int row, int col, int rgb) {
		int index = row*width + col;
		pdata[index] = rgb;
	}

	@Override
	public int[] getPixelByRowNumber(int rowIndex) {
		int[] pixels = new int[width];
		System.arraycopy(pdata, rowIndex*width, pixels, 0, width);
		return pixels;
	}

	public void convert2Gray() {
		int[] gray = new int[pdata.length];
		int offset = 0;
		int g=0;
		for(int row=0; row < height; row++) {
			offset = row*width;
			int ta=0, tr=0, tg=0, tb=0;
			for(int col=0; col<width; col++) {
				ta = (pdata[offset] >> 24) & 0xff;
				tr = (pdata[offset] >> 16) & 0xff;
				tg = (pdata[offset] >> 8) & 0xff;
				tb = pdata[offset] & 0xff;
				g= (int)(0.299 *tr + 0.587*tg + 0.114*tb);
				gray[offset]  = g;
				offset++;
			}
		}
		type = CV4J_IMAGE_TYPE_GRAY;
		System.arraycopy(gray, 0, pdata, 0, pdata.length);
		gray = null;
	}

	public Bitmap toBitmap() {

		return toBitmap(Bitmap.Config.RGB_565);
	}

	public Bitmap toBitmap(Bitmap.Config bitmapConfig) {
		Bitmap bitmap = Bitmap.createBitmap(width, height, bitmapConfig);
		if(type == CV4J_IMAGE_TYPE_RGB) {
			bitmap.setPixels(pdata, 0, width, 0, 0, width, height);
		} else if(CV4J_IMAGE_TYPE_GRAY == type ||
				CV4J_IMAGE_TYPE_BINARY == type) {
			int[] rgb = new int[pdata.length];
			int offset = 0;
			for(int row=0; row < height; row++) {
				offset = row*width;
				int ta=255, tr=0, tg=0, tb=0;
				for(int col=0; col<width; col++) {
					rgb[offset] = (ta << 24) | (pdata[offset] << 16) | (pdata[offset] << 8) | pdata[offset];
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
