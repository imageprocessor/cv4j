package com.cv4j.core.filters;

import com.cv4j.core.datamodel.ImageData;
import com.cv4j.image.util.Tools;

/**
 * this filter illustrate the brightness and contrast of the image
 * and demo how to change the both attributes of the image.
 * 
 * @author gloomy fish
 *
 */
public class ConBriFilter implements CommonFilter  {

	private float contrast = 1.2f; // default value;
	private float brightness = 0.7f; // default value;

	@Override
	public ImageData filter(ImageData src) {
		int width = src.getWidth();
		int height = src.getHeight();
		byte[] R = src.getChannel(0);
		byte[] G = src.getChannel(1);
		byte[] B = src.getChannel(2);
		byte[][] output = new byte[3][R.length];
        
        // calculate RED, GREEN, BLUE means of pixel
		int index = 0;
		int[] rgbmeans = new int[3];
		double redSum = 0, greenSum = 0, blueSum = 0;
		double total = height * width;
        for(int row=0; row<height; row++) {
        	int tr = 0, tg = 0, tb = 0;
        	for(int col=0; col<width; col++) {
        		index = row * width + col;
                tr = R[index] & 0xff;
                tg = G[index] & 0xff;
                tb = B[index] & 0xff;
                redSum += tr;
                greenSum += tg;
                blueSum +=tb;
        	}
        }
        
        rgbmeans[0] = (int)(redSum / total);
        rgbmeans[1] = (int)(greenSum / total);
        rgbmeans[2] = (int)(blueSum / total);
        
        // adjust contrast and brightness algorithm, here
        for(int row=0; row<height; row++) {
        	int ta = 0, tr = 0, tg = 0, tb = 0;
        	for(int col=0; col<width; col++) {
        		index = row * width + col;
				tr = R[index] & 0xff;
				tg = G[index] & 0xff;
				tb = B[index] & 0xff;
                
                // remove means
                tr -=rgbmeans[0];
                tg -=rgbmeans[1];
                tb -=rgbmeans[2];
                
                // adjust contrast now !!!
                tr = (int)(tr * getContrast());
                tg = (int)(tg * getContrast());
                tb = (int)(tb * getContrast());
                
                // adjust brightness
                tr += (int)(rgbmeans[0] * getBrightness());
                tg += (int)(rgbmeans[1] * getBrightness());
                tb += (int)(rgbmeans[2] * getBrightness());

                output[0][index] = (byte)Tools.clamp(tr);
				output[1][index] = (byte)Tools.clamp(tr);
				output[2][index] = (byte)Tools.clamp(tr);
        	}
        }
		src.putPixels(output);
		output[0] = null;
		output[1] = null;
		output[2] = null;
		output = null;
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
