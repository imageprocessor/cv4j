package com.cv4j.core.filters;

import com.cv4j.core.datamodel.ImageData;

public class SepiaToneFilter implements CommonFilter {

	@Override
	public ImageData filter(ImageData src) {
		int width = src.getWidth();
        int height = src.getHeight();

        int offset = 0;
        for(int row=0; row<height; row++) {
			offset = row * width;
			int tr = 0, tg = 0, tb = 0;
			for (int col = 0; col < width; col++) {
				tr = (src.getPixels()[offset] >> 16) & 0xff;
				tg = (src.getPixels()[offset] >> 8) & 0xff;
				tb = src.getPixels()[offset] & 0xff;
				int fr = (int) colorBlend(noise(), (tr * 0.393) + (tg * 0.769) + (tb * 0.189), tr);
				int fg = (int) colorBlend(noise(), (tr * 0.349) + (tg * 0.686) + (tb * 0.168), tg);
				int fb = (int) colorBlend(noise(), (tr * 0.272) + (tg * 0.534) + (tb * 0.131), tb);

				src.getPixels()[offset] = (255 << 24) | (clamp(fr) << 16) | (clamp(fg) << 8) | clamp(fb);
				offset++;
			}
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
