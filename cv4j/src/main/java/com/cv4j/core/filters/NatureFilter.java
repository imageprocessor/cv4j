package com.cv4j.core.filters;

import com.cv4j.core.datamodel.ImageData;

public class NatureFilter implements CommonFilter {
	public static final int ATMOSPHERE_STYLE = 1;
	public static final int BURN_STYLE = 2;
	public static final int FOG_STYLE = 3;
	public static final int FREEZE_STYLE = 4;
	public static final int LAVA_STYLE = 5;
	public static final int METAL_STYLE = 6;
	public static final int OCEAN_STYLE = 7;
	public static final int WATER_STYLE = 8;
	private int style;
	private int[] fogLookUp;

	public NatureFilter() {
		this.style = ATMOSPHERE_STYLE;
		buildFogLookupTable();
	}

	public NatureFilter(int style) {
		this.style = style;
		buildFogLookupTable();
	}
	
	private void buildFogLookupTable() {
		fogLookUp = new int[256];
		int fogLimit = 40;
		for(int i=0; i<fogLookUp.length; i++)
		{
			if(i > 127)
			{
				fogLookUp[i] = i - fogLimit;
				if(fogLookUp[i] < 127)
				{
					fogLookUp[i] = 127;
				}
			}
			else
			{
				fogLookUp[i] = i + fogLimit;
				if(fogLookUp[i] > 127)
				{
					fogLookUp[i] = 127;
				}
			}
		}

	}

	public int getStyle() {
		return style;
	}

	public void setStyle(int style) {
		this.style = style;
	}

	@Override
	public ImageData filter(ImageData src){
		int width = src.getWidth();
		int height = src.getHeight();

		int[] inPixels = src.getPixels();
		int[] outPixels = new int[width * height];
		int index = 0;
		for (int row = 0; row < height; row++) {
			int ta = 0, tr = 0, tg = 0, tb = 0;
			for (int col = 0; col < width; col++) {
				index = row * width + col;
				ta = (inPixels[index] >> 24) & 0xff;
				tr = (inPixels[index] >> 16) & 0xff;
				tg = (inPixels[index] >> 8) & 0xff;
				tb = inPixels[index] & 0xff;
				int[] onePixel = processOnePixel(ta, tr, tg, tb);
				outPixels[index] = (ta << 24) | (onePixel[1] << 16) | (onePixel[2] << 8) | onePixel[3];

			}
		}
		src.putPixels(outPixels);
		outPixels = null;
		return src;
	}

	private int[] processOnePixel(int ta, int tr, int tg, int tb) {
		int[] pixel = new int[4];
		pixel[0] = ta;
		if (style == ATMOSPHERE_STYLE) {
			pixel[1] = (tg + tb) / 2;
			pixel[2] = (tr + tb) / 2;
			pixel[3] = (tg + tr) / 2;
		} 
		else if (style == BURN_STYLE) {
			int gray = (tr + tg + tb) / 3;
			pixel[1] = clamp(gray * 3);
			pixel[2] = gray;
			pixel[3] = gray / 3;
		} 
		else if(style == FOG_STYLE) {
			pixel[1] = fogLookUp[tr];
			pixel[2] = fogLookUp[tg];
			pixel[3] = fogLookUp[tb];
		} 
		else if(style == FREEZE_STYLE) {
			pixel[1] = clamp((int)Math.abs((tr - tg - tb) * 1.5));
			pixel[2] = clamp((int)Math.abs((tg - tb - pixel[1]) * 1.5));
	        pixel[3] = clamp((int)Math.abs((tb - pixel[1] - pixel[2]) * 1.5));
	        
		}
		else if(style == LAVA_STYLE){
			int gray = (tr + tg + tb) / 3;
			pixel[1] = gray;
			pixel[2] = Math.abs(tb - 128);
	        pixel[3] = Math.abs(tb - 128);
		}
		else if(style == METAL_STYLE) {
            float r = Math.abs(tr - 64);
            float g = Math.abs(r - 64);
            float b = Math.abs(g - 64);
            float gray = ((222 * r + 707 * g + 71 * b) / 1000);
            r = gray + 70;
            r = r + (((r - 128) * 100) / 100f);
            g = gray + 65;
            g = g + (((g - 128) * 100) / 100f);
            b = gray + 75;
            b = b + (((b - 128) * 100) / 100f);
			pixel[1] = clamp((int)r);
			pixel[2] = clamp((int)g);
	        pixel[3] = clamp((int)b);
		}
		else if(style == OCEAN_STYLE)
		{
			int gray = (tr + tg + tb) / 3;
			pixel[1] = clamp(gray / 3);
			pixel[2] = gray;
	        pixel[3] = clamp(gray * 3);
		}
		else if(style == WATER_STYLE) {
			int gray = (tr + tg + tb) / 3;
			pixel[1] = clamp(gray - tg - tb);
			pixel[2] = clamp(gray - pixel[1] - tb);
			pixel[3] = clamp(gray - pixel[1] - pixel[2]);
		}
		return pixel;
	}

	public static int clamp(int value) {
		return (value > 255 ? 255 : (value < 0 ? 0 : value));
	}

}
