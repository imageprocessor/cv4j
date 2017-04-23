package com.cv4j.core.spatial.conv;

import com.cv4j.core.datamodel.ColorProcessor;
import com.cv4j.core.datamodel.ImageProcessor;
import com.cv4j.core.filters.CommonFilter;
import com.cv4j.image.util.Tools;

public class VarianceFilter implements CommonFilter {
	private int radius;

	public VarianceFilter() {
		radius = 1;
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}

	public int getRadius() {
		return radius;
	}

	public ImageProcessor filter(ImageProcessor src) {

		if (!(src instanceof ColorProcessor)) return src;

		int width = src.getWidth();
		int height = src.getHeight();

		int numOfPixels = width * height;
		byte[] R = ((ColorProcessor)src).getRed();
		byte[] G = ((ColorProcessor)src).getGreen();
		byte[] B = ((ColorProcessor)src).getBlue();
		byte[][] output = new byte[3][numOfPixels];

		int size = radius * 2 + 1;
		int total = size * size;
		int r = 0, g = 0, b = 0;
		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {

				// 统计滤波器 -方差
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
				
				r = calculateVar(subpixels[0]); // red
				g = calculateVar(subpixels[1]); // green
				b = calculateVar(subpixels[2]); // blue

				output[0][row * width + col] = (byte)Tools.clamp(r);
				output[1][row * width + col] = (byte)Tools.clamp(r);
				output[2][row * width + col] = (byte)Tools.clamp(r);
			}
		}

		((ColorProcessor) src).putRGB(output[0], output[1], output[2]);
		output = null;
		return src;
	}

	private int calculateVar(int[] data) {
		int sum1=0, sum2=0;
		for(int i=0; i<data.length; i++) {
			sum1 += (data[i]*data[i]);
			sum2 += data[i];
		}
		
		int sum3 = (sum2*sum2) / data.length;
		return (sum1 - sum3) / data.length;
	}

}
