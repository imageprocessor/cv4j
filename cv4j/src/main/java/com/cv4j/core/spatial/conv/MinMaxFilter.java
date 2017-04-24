package com.cv4j.core.spatial.conv;

import com.cv4j.core.datamodel.ColorProcessor;
import com.cv4j.core.datamodel.ImageProcessor;
import com.cv4j.core.filters.BaseFilter;

import java.util.Arrays;

public class MinMaxFilter extends BaseFilter {

	private boolean minFilter;
	private int radius;

	public MinMaxFilter() {
		minFilter = true;
		radius = 1;
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}

	public boolean isMinFilter() {
		return minFilter;
	}

	public void setMinFilter(boolean minFilter) {
		this.minFilter = minFilter;
	}

	public int getRadius() {
		return radius;
	}

	@Override
	public ImageProcessor doFilter(ImageProcessor src){

		int numOfPixels = width*height;
		byte[][] output = new byte[3][numOfPixels];

		int size = radius * 2 + 1;
		int total = size * size;
		int r = 0, g = 0, b = 0;
		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {

				// 统计滤波器
				int[][] subpixels = new int[3][total];
				int index = 0;
				for (int i = -radius; i <= radius; i++) {
					int roffset = row + i;
					roffset = (roffset < 0) ? 0 : (roffset >= height ? height - 1 : roffset);
					for (int j = -radius; j <= radius; j++) {
						int coffset = col + j;
						coffset = (coffset < 0) ? 0 : (coffset >= width ? width - 1 : coffset);
						subpixels[0][index] = R[roffset * width + coffset] & 0xff;
						subpixels[1][index] = G[roffset * width + coffset] & 0xff;
						subpixels[2][index] = B[roffset * width + coffset] & 0xff;
						index++;
					}
				}
				
				Arrays.sort(subpixels[0]);
				Arrays.sort(subpixels[1]);
				Arrays.sort(subpixels[2]);
				
				if (minFilter) {
					r = subpixels[0][0];
					g = subpixels[1][0];
					b = subpixels[2][0];
				} else {
					r = subpixels[0][total-1];
					g = subpixels[1][total-1];
					b = subpixels[2][total-1];
				}

				output[0][row * width + col] = (byte)r;
				output[1][row * width + col] = (byte)g;
				output[2][row * width + col] = (byte)b;
			}
		}

		((ColorProcessor) src).putRGB(output[0], output[1], output[2]);
		output = null;
		return src;
	}

}
