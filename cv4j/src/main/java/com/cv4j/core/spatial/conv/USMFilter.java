package com.cv4j.core.spatial.conv;
import com.cv4j.core.datamodel.ColorProcessor;
import com.cv4j.core.datamodel.ImageProcessor;
import com.cv4j.core.filters.GaussianBlurFilter;
import com.cv4j.image.util.Tools;

public class USMFilter extends GaussianBlurFilter {
	private double weight;
	
	public USMFilter() {
		this.weight = 0.6;
	}
	
	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}


	@Override
	public ImageProcessor filter(ImageProcessor src){

		if (!(src instanceof ColorProcessor)) return src;

		int width = src.getWidth();
		int height = src.getHeight();

		int total = width*height;
		byte[] R = new byte[total];((ColorProcessor)src).getRed();
		byte[] G = new byte[total]; ((ColorProcessor)src).getGreen();
		byte[] B = new byte[total]; ((ColorProcessor)src).getBlue();
		System.arraycopy(((ColorProcessor)src).getRed(), 0, R, 0, total);
		System.arraycopy(((ColorProcessor)src).getGreen(), 0, G, 0, total);
		System.arraycopy(((ColorProcessor)src).getBlue(), 0, B, 0, total);
		byte[][] output = new byte[3][total];
		
		// 高斯模糊
		ImageProcessor blurImage = super.filter(src);
		byte[] R2 = new byte[total];((ColorProcessor)blurImage).getRed();
		byte[] G2 = new byte[total]; ((ColorProcessor)blurImage).getGreen();
		byte[] B2 = new byte[total]; ((ColorProcessor)blurImage).getBlue();
		int r=0, g=0, b=0;
		int r1=0, g1=0, b1=0;
		int r2=0, g2=0, b2=0;
		for(int i=0; i<total; i++) {
			r1 = R[i]&0xff;
			g1 = G[i]&0xff;
			b1 = B[i]&0xff;
			
			r2 = R2[i]&0xff;
			g2 = G2[i]&0xff;
			b2 = B2[i]&0xff;
			
			r = (int)((r1-weight*r2)/(1-weight));
			g = (int)((g1-weight*g2)/(1-weight));
			b = (int)((b1-weight*b2)/(1-weight));
			
			output[0][i] = (byte)Tools.clamp(r);
			output[1][i] = (byte)Tools.clamp(g);
			output[2][i] = (byte)Tools.clamp(b);
		}

		((ColorProcessor) src).putRGB(output[0], output[1], output[2]);
		output = null;
		return src;
	}

}
