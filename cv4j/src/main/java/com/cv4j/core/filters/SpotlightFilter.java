package com.cv4j.core.filters;

import com.cv4j.core.datamodel.ImageData;

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
	public ImageData filter(ImageData src){
		int width = src.getWidth();
        int height = src.getHeight();

        int[] inPixels = src.getPixels();
        int[] outPixels = new int[width*height];

        int index = 0;
        int centerX = width/2;
        int centerY = height/2;
        double maxDistance = Math.sqrt(centerX * centerX + centerY * centerY);
        for(int row=0; row<height; row++) {
        	int ta = 0, tr = 0, tg = 0, tb = 0;
        	for(int col=0; col<width; col++) {
        		index = row * width + col;
        		ta = (inPixels[index] >> 24) & 0xff;
                tr = (inPixels[index] >> 16) & 0xff;
                tg = (inPixels[index] >> 8) & 0xff;
                tb = inPixels[index] & 0xff;
                double scale = 1.0 - getDistance(centerX, centerY, col, row)/maxDistance;
                for(int i=0; i<factor; i++) {
                	scale = scale * scale;
                }

            	tr = (int)(scale * tr);
            	tg = (int)(scale * tg);
            	tb = (int)(scale * tb);
                
                outPixels[index] = (ta << 24) | (tr << 16) | (tg << 8) | tb;
                
        	}
        }
        src.putPixels(outPixels);
		outPixels = null;
        return src;
	}
	
	private double getDistance(int centerX, int centerY, int px, int py) {
		double xx = (centerX - px)*(centerX - px);
		double yy = (centerY - py)*(centerY - py);
		return (int)Math.sqrt(xx + yy);
	}

}
