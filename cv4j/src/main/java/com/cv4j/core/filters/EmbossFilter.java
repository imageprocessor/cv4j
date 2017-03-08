package com.cv4j.core.filters;

import com.cv4j.core.datamodel.ImageData;
import com.cv4j.image.util.Tools;


public class EmbossFilter implements CommonFilter {
	private int COLORCONSTANTS;
	private boolean out;
	public EmbossFilter(boolean out) {
		this.out = out;
		this.COLORCONSTANTS = 100;
	}

	@Override
	public ImageData filter(ImageData src){
		int width = src.getWidth();
        int height = src.getHeight();

		int offset = 0;
		int r1=0, g1=0, b1=0;
		int r2=0, g2=0, b2=0;
		int r=0, g=0, b=0;
		int[] pixels = src.getPixels();
		int[] output = new int[pixels.length];
		for ( int y = 1; y < height-1; y++ ) {
			offset = y*width;
			for ( int x = 1; x < width-1; x++ ) {
				r1 = (pixels[offset] >> 16) & 0xff;
				g1 = (pixels[offset] >> 8) & 0xff;
				b1 = (pixels[offset]) & 0xff;

				r2 = (pixels[offset+width] >> 16) & 0xff;
				g2 = (pixels[offset+width] >> 8) & 0xff;
				b2 = (pixels[offset+width]) & 0xff;

				if(out) {
					r = r1 - r2;
					g = g1 - g2;
					b = b1 - b2;
				} else {
					r = r2 - r1;
					g = g2 - g1;
					b = b2 - b1;
				}
				r = Tools.clamp(r+COLORCONSTANTS);
				g = Tools.clamp(g+COLORCONSTANTS);
				b = Tools.clamp(b+COLORCONSTANTS);

				output[offset] = (255 << 24) | (r << 16) | (g << 8) | b;
				offset++;
			}
		}
		src.putPixels(output);
		output = null;
		return src;
	}

	/**
	 * 
	 * @param out
	 */
	public void setOUT(boolean out) {
		this.out = out;
	}
}
