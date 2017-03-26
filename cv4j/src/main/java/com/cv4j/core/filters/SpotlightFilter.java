package com.cv4j.core.filters;

import com.cv4j.core.datamodel.ColorProcessor;
import com.cv4j.core.datamodel.ImageProcessor;

public class SpotlightFilter implements CommonFilter {
	// attenuation coefficient, default is 1 means line decrease...
	private int factor;
	public SpotlightFilter() {
		factor = 1;
	}
	
	public void setFactor(int coefficient) {
		this.factor = coefficient;
	}

	@Override
	public ImageProcessor filter(ImageProcessor src){
		int width = src.getWidth();
        int height = src.getHeight();

		int total = width*height;
		byte[] R = ((ColorProcessor)src).getRed();
		byte[] G = ((ColorProcessor)src).getGreen();
		byte[] B = ((ColorProcessor)src).getBlue();

        int offset = 0;
        int centerX = width/2;
        int centerY = height/2;
        double maxDistance = Math.sqrt(centerX * centerX + centerY * centerY);
		int tr = 0, tg = 0, tb = 0;
        for(int row=0; row<height; row++) {
        	offset = row * width;
        	for(int col=0; col<width; col++) {
                tr = R[offset] & 0xff;
                tg = G[offset] & 0xff;
                tb = B[offset] & 0xff;
                double scale = 1.0 - getDistance(centerX, centerY, col, row)/maxDistance;
                for(int i=0; i<factor; i++) {
                	scale = scale * scale;
                }

            	tr = (int)(scale * tr);
            	tg = (int)(scale * tg);
            	tb = (int)(scale * tb);

				R[offset] = (byte)tr;
				G[offset] = (byte)tg;
				B[offset] = (byte)tb;
				offset++;
        	}
        }
        return src;
	}
	
	private double getDistance(int centerX, int centerY, int px, int py) {
		double xx = (centerX - px)*(centerX - px);
		double yy = (centerY - py)*(centerY - py);
		return (int)Math.sqrt(xx + yy);
	}

}
