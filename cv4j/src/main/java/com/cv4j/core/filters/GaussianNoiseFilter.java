package com.cv4j.core.filters;
/** 随机噪声滤镜 **/
import com.cv4j.core.datamodel.ImageProcessor;
import com.cv4j.image.util.Tools;

public class GaussianNoiseFilter extends BaseFilter {
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

	public ImageProcessor doFilter(ImageProcessor src) {

		int r=0, g=0, b=0;
		int offset = 0;
		int total = width * height;
		java.util.Random random = new java.util.Random();
		for(int i=0; i<total; i++) {
			r= R[i]&0xff;
			g= G[i]&0xff;
			b= B[i]&0xff;

			// add Gaussian noise
			r = (int)(r + sigma*random.nextGaussian());
			g = (int)(g + sigma*random.nextGaussian());
			b = (int)(b + sigma*random.nextGaussian());

			R[i] = (byte) Tools.clamp(r);
			G[i] = (byte) Tools.clamp(g);
			B[i] = (byte) Tools.clamp(b);
		}
		return src;
	}

}
