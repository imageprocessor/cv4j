package com.cv4j.core.filters;

import com.cv4j.core.datamodel.ImageProcessor;
import com.cv4j.image.util.Tools;

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
	public ImageProcessor filter(ImageProcessor src){
		int width = src.getWidth();
		int height = src.getHeight();

		byte[] R = src.getChannel(0);
		byte[] G = src.getChannel(1);
		byte[] B = src.getChannel(2);
		byte[][] output = new byte[3][R.length];

		int index = 0;
		for (int row = 0; row < height; row++) {
			int ta = 0, tr = 0, tg = 0, tb = 0;
			for (int col = 0; col < width; col++) {
				index = row * width + col;
				tr = R[index] & 0xff;
				tg = G[index] & 0xff;
				tb = B[index] & 0xff;
				int[] onePixel = processOnePixel(ta, tr, tg, tb);

				output[0][index] = (byte)onePixel[0];
				output[1][index] = (byte)onePixel[1];
				output[2][index] = (byte)onePixel[2];
			}
		}
		src.putPixels(output);
		output = null;
		return src;
	}

	private int[] processOnePixel(int ta, int tr, int tg, int tb) {
		int[] pixel = new int[4];
		pixel[0] = ta;
		int gray = (tr + tg + tb) / 3;

		switch (style) {
			case ATMOSPHERE_STYLE:
				pixel[1] = (tg + tb) / 2;
				pixel[2] = (tr + tb) / 2;
				pixel[3] = (tg + tr) / 2;

				break;

			case BURN_STYLE:
				pixel[1] = Tools.clamp(gray * 3);
				pixel[2] = gray;
				pixel[3] = gray / 3;

				break;

			case FOG_STYLE:
				pixel[1] = fogLookUp[tr];
				pixel[2] = fogLookUp[tg];
				pixel[3] = fogLookUp[tb];

				break;

			case FREEZE_STYLE:
				pixel[1] = Tools.clamp((int)Math.abs((tr - tg - tb) * 1.5));
				pixel[2] = Tools.clamp((int)Math.abs((tg - tb - pixel[1]) * 1.5));
				pixel[3] = Tools.clamp((int)Math.abs((tb - pixel[1] - pixel[2]) * 1.5));

				break;

			case LAVA_STYLE:
				pixel[1] = gray;
				pixel[2] = Math.abs(tb - 128);
				pixel[3] = Math.abs(tb - 128);

				break;

			case METAL_STYLE:
				float r = Math.abs(tr - 64);
				float g = Math.abs(r - 64);
				float b = Math.abs(g - 64);
				float grayFloat = ((222 * r + 707 * g + 71 * b) / 1000);
				r = grayFloat + 70;
				r = r + (((r - 128) * 100) / 100f);
				g = grayFloat + 65;
				g = g + (((g - 128) * 100) / 100f);
				b = grayFloat + 75;
				b = b + (((b - 128) * 100) / 100f);
				pixel[1] = Tools.clamp((int)r);
				pixel[2] = Tools.clamp((int)g);
				pixel[3] = Tools.clamp((int)b);

				break;

			case OCEAN_STYLE:
				pixel[1] = Tools.clamp(gray / 3);
				pixel[2] = gray;
				pixel[3] = Tools.clamp(gray * 3);

				break;

			case WATER_STYLE:
				pixel[1] = Tools.clamp(gray - tg - tb);
				pixel[2] = Tools.clamp(gray - pixel[1] - tb);
				pixel[3] = Tools.clamp(gray - pixel[1] - pixel[2]);

				break;

			default:
				pixel[1] = (tg + tb) / 2;
				pixel[2] = (tr + tb) / 2;
				pixel[3] = (tg + tr) / 2;
				break;
		}

		return pixel;
	}
}
