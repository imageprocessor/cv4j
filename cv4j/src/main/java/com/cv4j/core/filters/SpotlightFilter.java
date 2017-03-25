package com.cv4j.core.filters;

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

		byte[] R = src.getChannel(0);
		byte[] G = src.getChannel(1);
		byte[] B = src.getChannel(2);
		byte[][] output = new byte[3][R.length];

        int index = 0;
        int centerX = width/2;
        int centerY = height/2;
        double maxDistance = Math.sqrt(centerX * centerX + centerY * centerY);
        for(int row=0; row<height; row++) {
        	int ta = 0, tr = 0, tg = 0, tb = 0;
        	for(int col=0; col<width; col++) {
        		index = row * width + col;
                tr = R[index] & 0xff;
                tg = G[index] & 0xff;
                tb = B[index] & 0xff;
                double scale = 1.0 - getDistance(centerX, centerY, col, row)/maxDistance;
                for(int i=0; i<factor; i++) {
                	scale = scale * scale;
                }

            	tr = (int)(scale * tr);
            	tg = (int)(scale * tg);
            	tb = (int)(scale * tb);
                
                output[0][index] = (byte)tr;
				output[1][index] = (byte)tg;
				output[2][index] = (byte)tb;
                
        	}
        }
        src.putPixels(output);
		output = null;
        return src;
	}
	
	private double getDistance(int centerX, int centerY, int px, int py) {
		double xx = (centerX - px)*(centerX - px);
		double yy = (centerY - py)*(centerY - py);
		return (int)Math.sqrt(xx + yy);
	}

}
