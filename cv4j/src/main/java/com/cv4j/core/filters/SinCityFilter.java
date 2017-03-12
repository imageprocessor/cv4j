package com.cv4j.core.filters;

import android.graphics.Color;

import com.cv4j.core.datamodel.ImageData;

public class SinCityFilter implements CommonFilter {

	private double threshold = 200; // default value
	private int mainColor = Color.argb(255, 255, 0, 0);

	public void setMainColor(int argb) {
		this.mainColor = argb;
	}

	@Override
	public ImageData filter(ImageData src) {
		int width = src.getWidth();
        int height = src.getHeight();

        int offset = 0;
		byte[] R = src.getChannel(0);
		byte[] G = src.getChannel(1);
		byte[] B = src.getChannel(2);
		byte[][] output = new byte[3][R.length];

        for(int row=0; row<height; row++) {
			offset = row*width;
        	int ta = 0, tr = 0, tg = 0, tb = 0;
        	for(int col=0; col<width; col++) {
                tr = R[offset] & 0xff;
                tg = G[offset] & 0xff;
                tb = B[offset] & 0xff;
                int gray = (int)(0.299 * (double)tr + 0.587 * (double)tg + 0.114 * (double)tb);
                double distance = getDistance(tr, tg, tb);
                if(distance < threshold) {
                	double k = distance / threshold;
                	int[] rgb = getAdjustableRGB(tr, tg, tb, gray, (float)k);
                	tr = rgb[0];
                	tg = rgb[1];
                	tb = rgb[2];
					output[0][offset] = (byte)tr;
					output[1][offset] = (byte)tg;
					output[2][offset] = (byte)tb;
                } else {
					output[0][offset] = (byte)gray;
					output[1][offset] = (byte)gray;
					output[2][offset] = (byte)gray;
                }
				offset++;
        	}
        }
        src.putPixels(output);
        return src;
	}

	private int[] getAdjustableRGB(int tr, int tg, int tb, int gray, float rate) {
		int[] rgb = new int[3];
		rgb[0] = (int)(tr * rate + gray * (1.0f-rate));
		rgb[1] = (int)(tg * rate + gray * (1.0f-rate));
		rgb[2] = (int)(tb * rate + gray * (1.0f-rate));
		return rgb;
	}

	private double getDistance(int tr, int tg, int tb) {
		int dr = tr - Color.red(mainColor);
		int dg = tg - Color.green(mainColor);
		int db = tb - Color.blue(mainColor);
		int distance = dr * dr + dg * dg + db * db;
		return Math.sqrt(distance);		
	}

}
