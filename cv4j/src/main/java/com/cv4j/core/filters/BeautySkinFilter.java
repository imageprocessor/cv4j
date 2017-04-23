package com.cv4j.core.filters;

import com.cv4j.core.datamodel.ColorProcessor;
import com.cv4j.core.datamodel.ImageProcessor;
import com.cv4j.core.datamodel.IntIntegralImage;
import com.cv4j.image.util.Tools;

/**
 * good method try to smooth the minor noise
 * @author zhigang jia
 * @date 2017-04-23
 *
 */
public class BeautySkinFilter implements CommonFilter {

	private int xr;
	private int yr;
	private float sigma;
	public BeautySkinFilter() {
		sigma = 30.0f; // by default
		xr = 5;
		yr = 5;
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
		// get image data
		int width = src.getWidth();
		int height = src.getHeight();

		// start ep process
		byte[] output = new byte[width*height];
		IntIntegralImage ii = new IntIntegralImage();
		for(int i=0; i<src.getChannels(); i++) {
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
		int size = wx * wy;
		int r = 0;
		for (int row = yr; row < height-yr; row++) {
			offset = row * width;
			for (int col = xr; col < width-xr; col++) {
				int sr = input.getBlockSum(col, row, wy, wx);
				float a = input.getBlockSquareSum(col, row, wy, wx);
				float b = sr / size;
				float c = (a - (sr*sr)/size)/size;
				float d = c / (c+sigma2);
				r = (int)((1-d)*b + d*r);
				output[offset + col] = (byte)Tools.clamp(r);
			}
		}
	}
}
