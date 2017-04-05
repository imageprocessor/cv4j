package com.cv4j.core.filters;

import com.cv4j.core.datamodel.ColorProcessor;
import com.cv4j.core.datamodel.ImageProcessor;

public class WhiteImageFilter implements CommonFilter {
	private double beta;

	public WhiteImageFilter() {
		this.beta = 1.1;
	}

	public double getBeta() {
		return beta;
	}

	public void setBeta(double beta) {
		this.beta = beta;
	}

	@Override
	public ImageProcessor filter(ImageProcessor src) {
		int width = src.getWidth();
		int height = src.getHeight();

		byte[] R = ((ColorProcessor)src).getRed();
		byte[] G = ((ColorProcessor)src).getGreen();
		byte[] B = ((ColorProcessor)src).getBlue();

		int index = 0;

		// make LUT
		int[] lut = new int[256];
		for(int i=0; i<256; i++) {
			lut[i] = imageMath(i);
		}
		
		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {
				index = row * width + col;
				R[index] = (byte)lut[R[index] & 0xff];
				G[index] = (byte)lut[G[index] & 0xff];
				B[index] = (byte)lut[B[index] & 0xff];
			}
		}
		return src;
	}

	private int imageMath(int gray) {
		double scale = 255 / (Math.log(255 * (this.beta -1) + 1) / Math.log(this.beta));
		double p1 = Math.log(gray * (this.beta -1) + 1);
		double np = p1 / Math.log(this.beta);
		return (int)(np * scale);
	}

}
