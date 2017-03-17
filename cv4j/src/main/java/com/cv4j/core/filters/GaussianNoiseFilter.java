package com.cv4j.core.filters;
/** 随机噪声滤镜 **/
import com.cv4j.core.datamodel.ImageData;
import com.cv4j.image.util.Tools;

public class GaussianNoiseFilter implements CommonFilter  {
	private int sigma;
	
	public GaussianNoiseFilter() {
		sigma = 25;
	}

	public int getSigma() {
		return sigma;
	}

	public void setSigma(int sigma) {
		this.sigma = sigma;
	}

	public ImageData filter(ImageData src) {
		int width = src.getWidth();
		int height = src.getHeight();
		byte[] R = src.getChannel(0);
		byte[] G = src.getChannel(1);
		byte[] B = src.getChannel(2);

		int r=0, g=0, b=0;
		int offset = 0;
		java.util.Random random = new java.util.Random();
		for(int row=0; row<height; row++) {
			offset = row*width;
			for(int col=0; col<width; col++) {
				r= R[offset]&0xff;
				g= G[offset]&0xff;
				b= B[offset]&0xff;
				
				// add Gaussian noise
				r = (int)(r + sigma*random.nextGaussian());
				g = (int)(g + sigma*random.nextGaussian());
				b = (int)(b + sigma*random.nextGaussian());
				
				R[offset] = (byte) Tools.clamp(r);
				G[offset] = (byte) Tools.clamp(g);
				B[offset] = (byte) Tools.clamp(b);
				offset++;
			}
		}
		return src;
	}

}
