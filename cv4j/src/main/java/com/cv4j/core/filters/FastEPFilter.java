package com.cv4j.core.filters;

import com.cv4j.core.datamodel.ImageProcessor;
import com.cv4j.core.datamodel.IntIntegralImage;
import com.cv4j.image.util.Tools;

/**
 * good method try to smooth the minor noise
 * @author zhigang jia
 * @date 2017-04-23
 *
 */
public class FastEPFilter implements CommonFilter {

	private int xr;
	private int yr;
	private float sigma;
	public FastEPFilter() {
		sigma = 10.0f; // by default
	}
	
	public void setWinsize(int radius) {
		this.xr = radius;
		this.yr = radius;
	}

	public float getSigma() {
		return sigma;
	}

	public void setSigma(float sigma) {
		this.sigma = sigma;
	}

	@Override
	public ImageProcessor filter(ImageProcessor src) {
		// initialization parameters
		int width = src.getWidth();
		int height = src.getHeight();
		xr = yr = (int)(Math.max(width, height) * 0.02);
		sigma = 10 + sigma * sigma * 5;

		// start ep process
		byte[] output = new byte[width*height];
		IntIntegralImage ii = new IntIntegralImage();
		for(int i=0; i<src.getChannels(); i++) {
			System.arraycopy(src.toByte(i), 0, output, 0, output.length);
			ii.setImage(src.toByte(i));
			ii.process(width, height, true);
			processSingleChannel(width, height, ii, output);
			System.arraycopy(output, 0, src.toByte(i), 0, output.length);
		}

		// release memory
		output = null;
		return src;
	}

	public void processSingleChannel(int width, int height, IntIntegralImage input, byte[] output) {
		float sigma2 = sigma*sigma;
		int offset = 0;
		int wy = (yr * 2 + 1);
		int wx = (xr * 2 + 1);
		int r = 0;
		int size = 0;
		for (int row = 0; row < height; row++) {
			offset = row * width;
			for (int col = 0; col < width; col++) {
				int swx = col + xr;
				int swy = row + yr;
				int nex = col-xr-1;
				int ney = row-yr-1;
				if(swx >= width) {
					swx = width - 1;
				}
				if(swy >= height) {
					swy = height - 1;
				}
				if(nex < 0) {
					nex = 0;
				}
				if(ney < 0) {
					ney = 0;
				}
				size = (swx - nex)*(swy - ney);
				int sr = input.getBlockSum2(ney, nex, swy, swx);
				float a = input.getBlockSquareSum(col, row, wy, wx);
				// fix issue, size is not cover the whole block
				float b = sr / size;
				float c = (a - (sr*sr)/size)/size;
				float d = c / (c+sigma2);
				r = (int)((1-d)*b + d*r);
				output[offset + col] = (byte) Tools.clamp(r);
			}
		}
	}
}
