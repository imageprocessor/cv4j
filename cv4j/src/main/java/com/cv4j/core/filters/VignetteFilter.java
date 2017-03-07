package com.cv4j.core.filters;

import android.graphics.Color;

import com.cv4j.core.datamodel.ImageData;

/**
 * @author gloomy fish
 * Vignette - a photograph whose edges shade off gradually
 * 
 */
public class VignetteFilter implements CommonFilter {
		
	private int vignetteWidth;
	private int fade;
	private int vignetteColor;
	
	public VignetteFilter() {
		vignetteWidth = 50;
		fade = 35;
		vignetteColor = Color.BLACK;
	}
	
	@Override
	public ImageData filter(ImageData src){
		int width = src.getWidth();
        int height = src.getHeight();

		int[] inPixels = src.getPixels();
		int[] outPixels = new int[width*height];

		int index = 0;
		for(int row=0; row<height; row++) {
			int ta = 0, tr = 0, tg = 0, tb = 0;
			for(int col=0; col<width; col++) {

				int dX = Math.min(col, width - col);
				int dY = Math.min(row, height - row);
				index = row * width + col;
				ta = (inPixels[index] >> 24) & 0xff;
				tr = (inPixels[index] >> 16) & 0xff;
				tg = (inPixels[index] >> 8) & 0xff;
				tb = inPixels[index] & 0xff;
				if ((dY <= vignetteWidth) & (dX <= vignetteWidth))
				{
					double k = 1 - (double)(Math.min(dY, dX) - vignetteWidth + fade) / (double)fade;
					outPixels[index] = superpositionColor(ta, tr, tg, tb, k);
					continue;
				}

				if ((dX < (vignetteWidth - fade)) | (dY < (vignetteWidth - fade)))
				{
					outPixels[index] = (ta << 24) | (Color.red(vignetteColor) << 16) | (Color.green(vignetteColor) << 8) | Color.blue(vignetteColor);
				}
				else
				{
					if ((dX < vignetteWidth)&(dY>vignetteWidth))
					{
						double k = 1 - (double)(dX - vignetteWidth + fade) / (double)fade;
						outPixels[index] = superpositionColor(ta, tr, tg, tb, k);
					}
					else
					{
						if ((dY < vignetteWidth)&(dX > vignetteWidth))
						{
							double k = 1 - (double)(dY - vignetteWidth + fade) / (double)fade;
							outPixels[index] = superpositionColor(ta, tr, tg, tb, k);
						}
						else
						{
							outPixels[index] = (ta << 24) | (tr << 16) | (tg << 8) | tb;
						}
					}
				}
			}
		}
        
        src.putPixels(outPixels);
		outPixels = null;
        return src;
	}
	
	public int superpositionColor(int ta, int red, int green, int blue, double k) {
		red = (int)(Color.red(vignetteColor) * k + red *(1.0-k));
		green = (int)(Color.green(vignetteColor) * k + green *(1.0-k));
		blue = (int)(Color.blue(vignetteColor) * k + blue *(1.0-k));
		int color = (ta << 24) | (clamp(red) << 16) | (clamp(green) << 8) | clamp(blue);
		return color;
	}
	
	public int clamp(int value) {
		return value > 255 ? 255 :((value < 0) ? 0 : value);
	}
	
	public int getVignetteWidth() {
		return vignetteWidth;
	}

	public void setVignetteWidth(int vignetteWidth) {
		this.vignetteWidth = vignetteWidth;
	}

	public int getFade() {
		return fade;
	}

	public void setFade(int fade) {
		this.fade = fade;
	}
	
	public int getVignetteColor() {
		return vignetteColor;
	}

	public void setVignetteColor(int vignetteColor) {
		this.vignetteColor = vignetteColor;
	}
	
}
