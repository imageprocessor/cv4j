package com.cv4j.core.filters;

import com.cv4j.core.datamodel.ColorProcessor;
import com.cv4j.core.datamodel.ImageProcessor;

public class SepiaToneFilter implements CommonFilter {

	@Override
	public ImageProcessor filter(ImageProcessor src) {
		int width = src.getWidth();
        int height = src.getHeight();

        int total = width*height;
		byte[] R = ((ColorProcessor)src).getRed();
		byte[] G = ((ColorProcessor)src).getGreen();
		byte[] B = ((ColorProcessor)src).getBlue();
		int r=0, g=0, b=0;
        for(int i=0; i<total; i++) {
			r = R[i] & 0xff;
			g = G[i] & 0xff;
			b = B[i] & 0xff;

			r = (int) colorBlend(noise(), (r * 0.393) + (g * 0.769) + (b * 0.189), r);
			g = (int) colorBlend(noise(), (r * 0.349) + (g * 0.686) + (b * 0.168), g);
			b = (int) colorBlend(noise(), (r * 0.272) + (g * 0.534) + (b * 0.131), b);

			R[i] = (byte)clamp(r);
			G[i] = (byte)clamp(g);
			B[i] = (byte)clamp(b);
		}
        return src;
	}
	
	private double noise() {
		return Math.random()*0.5 + 0.5;
	}
	
	private double colorBlend(double scale, double dest, double src) {
	    return (scale * dest + (1.0 - scale) * src);
	}

	private int clamp(int c)
	{
		return c > 255 ? 255 :( (c < 0) ? 0: c);
	}
}
