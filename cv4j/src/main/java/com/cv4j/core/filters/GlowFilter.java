/*
** Copyright 2005 Huxtable.com. All rights reserved.
*/

package com.cv4j.core.filters;

import com.cv4j.core.datamodel.ColorProcessor;
import com.cv4j.core.datamodel.ImageProcessor;
import com.cv4j.image.util.Tools;

public class GlowFilter extends GaussianBlurFilter{

	static final long serialVersionUID = 5377089073023183684L;

	private float amount = 0.2f;
	private int radius;
	public GlowFilter() {
		super();
	}
	
	public void setAmount( float amount ) {
		this.amount = amount;
	}
	
	public float getAmount() {
		return amount;
	}

	@Override
	public ImageProcessor filter(ImageProcessor src){
		int width = src.getWidth();
		int height = src.getHeight();

		int total = width*height;
		byte[] R1 = new byte[total];((ColorProcessor)src).getRed();
		byte[] G1 = new byte[total]; ((ColorProcessor)src).getGreen();
		byte[] B1 = new byte[total]; ((ColorProcessor)src).getBlue();
		System.arraycopy(((ColorProcessor)src).getRed(), 0, R1, 0, total);
		System.arraycopy(((ColorProcessor)src).getGreen(), 0, G1, 0, total);
		System.arraycopy(((ColorProcessor)src).getBlue(), 0, B1, 0, total);

		// 高斯模糊
		ImageProcessor blurImage = super.filter(src);
		byte[] R2 = new byte[total];((ColorProcessor)blurImage).getRed();
		byte[] G2 = new byte[total]; ((ColorProcessor)blurImage).getGreen();
		byte[] B2 = new byte[total]; ((ColorProcessor)blurImage).getBlue();

		float a = 4*amount;

		int index = 0;
		for ( int y = 0; y < height; y++ ) {
			for ( int x = 0; x < width; x++ ) {
				int r1 = R2[index] & 0xff;
				int g1 = G2[index] & 0xff;
				int b1 = B2[index] & 0xff;

				int r2 = R1[index] & 0xff;
				int g2 = G1[index] & 0xff;
				int b2 = B1[index] & 0xff;

				R2[index] = (byte)Tools.clamp( (int)(r1 + a * r2) );
				G2[index] = (byte)Tools.clamp( (int)(g1 + a * g2) );
				B2[index] = (byte)Tools.clamp( (int)(b1 + a * b2) );
				index++;
			}
		}
		return src;
    }
}
