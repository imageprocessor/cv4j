package com.cv4j.core.filters;

import com.cv4j.core.datamodel.ColorProcessor;
import com.cv4j.core.datamodel.ImageProcessor;

public class GammaFilter implements CommonFilter {
	private int[] lut;
	private double gamma;

	public GammaFilter() {
		this.lut = new int[256];
		this.gamma = 0.5;
	}

	@Override
	public ImageProcessor filter(ImageProcessor src) {

		if (!(src instanceof ColorProcessor)) return src;

		int width = src.getWidth();
		int height = src.getHeight();
		byte[] R = ((ColorProcessor)src).getRed();
		byte[] G = ((ColorProcessor)src).getGreen();
		byte[] B = ((ColorProcessor)src).getBlue();
		// setup LUT
		setupGammaLut();
		int index = 0;
		for (int row = 0; row < height; row++) {
			int tr = 0, tg = 0, tb = 0;
			for (int col = 0; col < width; col++) {
				index = row * width + col;
				tr = R[index] & 0xff;
				tg = G[index] & 0xff;
				tb = B[index] & 0xff;

				// LUT search
				R[index] = (byte)lut[tr];
				G[index] = (byte)lut[tg];
				B[index] = (byte)lut[tb];
			}
		}
		return src;
	}

	private void setupGammaLut() {
		for (int i = 0; i < 256; i++) {
			lut[i] = (int) (Math.exp(Math.log(i / 255.0) * gamma) * 255.0);
		}

	}
}
