package com.cv4j.core.filters;

import com.cv4j.core.datamodel.ImageProcessor;
import com.cv4j.image.util.Tools;

/**
 * this filter illustrate the brightness and contrast of the image
 * and demo how to change the both attributes of the image.
 * 
 * @author gloomy fish
 *
 */
public class ConBriFilter extends BaseFilter  {

	private float contrast = 1.2f; // default value;
	private float brightness = 0.7f; // default value;

	@Override
	public ImageProcessor doFilter(ImageProcessor src) {
        
        // calculate RED, GREEN, BLUE means of pixel
		int index = 0;
		int[] rgbmeans = new int[3];
		double redSum = 0, greenSum = 0, blueSum = 0;
		int total = height * width;
		int r=0, g=0, b=0;
        for(int i=0; i<total; i++) {
			r = R[i] & 0xff;
			g = G[i] & 0xff;
			b = B[i] & 0xff;
			redSum += r;
			greenSum += g;
			blueSum +=b;
        }
        rgbmeans[0] = (int)(redSum / total);
        rgbmeans[1] = (int)(greenSum / total);
        rgbmeans[2] = (int)(blueSum / total);
        
        // adjust contrast and brightness algorithm, here
        for(int i=0; i<total; i++) {
			r = R[i] & 0xff;
			g = G[i] & 0xff;
			b = B[i] & 0xff;

			// remove means
			r -=rgbmeans[0];
			g -=rgbmeans[1];
			b -=rgbmeans[2];

			// adjust contrast now !!!
			r = (int)(r * getContrast());
			g = (int)(g * getContrast());
			b = (int)(b * getContrast());

			// adjust brightness
			r += (int)(rgbmeans[0] * getBrightness());
			g += (int)(rgbmeans[1] * getBrightness());
			b += (int)(rgbmeans[2] * getBrightness());

			R[i] = (byte)Tools.clamp(r);
			G[i] = (byte)Tools.clamp(g);
			B[i] = (byte)Tools.clamp(b);
        }
        return src;
	}

	public float getContrast() {
		return contrast;
	}

	public void setContrast(float contrast) {
		this.contrast = contrast;
	}

	public float getBrightness() {
		return brightness;
	}

	public void setBrightness(float brightness) {
		this.brightness = brightness;
	}

}
