package com.cv4j.core.filters;

import com.cv4j.core.datamodel.ImageData;
import com.cv4j.image.util.Tools;

public class WaterFilter implements CommonFilter {
	private float wavelength = 16;

	public float getWavelength() {
		return wavelength;
	}

	private float amplitude = 10;
	private float phase = 0;
	private float centreX = 0.5f;
	private float centreY = 0.5f;
	private float radius = 50;

	private float radius2 = 0;
	private float icentreX;
	private float icentreY;
	
	public WaterFilter() {

	}
	
	public void setRadius(float radius) {
		this.radius = radius;
	}
	
	public float getRadius() {
		return radius;
	}
	
	public void setWavelength(float wavelength) {
		this.wavelength = wavelength;
	}

	@Override
	public ImageData filter(ImageData src){
		int width = src.getWidth();
        int height = src.getHeight();

		byte[] R = src.getChannel(0);
		byte[] G = src.getChannel(1);
		byte[] B = src.getChannel(2);
		int[] inPixels = convert2Pixels(src.getPixels());
		byte[][] output = new byte[3][R.length];

		icentreX = width * centreX;
		icentreY = height * centreY;
		if ( radius == 0 )
			radius = Math.min(icentreX, icentreY);
		radius2 = radius*radius;
        int index = 0;
        float[] out = new float[2];
        for(int row=0; row<height; row++) {
        	for(int col=0; col<width; col++) {
        		index = row * width + col;

				// 获取水波的扩散位置，最重要的一步
        		generateWaterRipples(col, row, out);
				int srcX = (int)Math.floor( out[0] );
				int srcY = (int)Math.floor( out[1] );
				float xWeight = out[0]-srcX;
				float yWeight = out[1]-srcY;
				int nw, ne, sw, se;

				// 获取周围四个像素，插值用，
				if ( srcX >= 0 && srcX < width-1 && srcY >= 0 && srcY < height-1) {
					// Easy case, all corners are in the image
					int i = width*srcY + srcX;
					nw = inPixels[i];
					ne = inPixels[i+1];
					sw = inPixels[i+width];
					se = inPixels[i+width+1];
				} else {
					// Some of the corners are off the image
					nw = getPixel( inPixels, srcX, srcY, width, height );
					ne = getPixel( inPixels, srcX+1, srcY, width, height );
					sw = getPixel( inPixels, srcX, srcY+1, width, height );
					se = getPixel( inPixels, srcX+1, srcY+1, width, height );
				}

				// 取得对应的振幅位置P(x, y)的像素，使用双线性插值
				int p = Tools.bilinearInterpolate(xWeight, yWeight, nw, ne, sw, se);
				int r = (p >> 16) & 0xff;
				int g = (p >> 8) & 0xff;
				int b = (p) & 0xff;
				output[0][index] = (byte)r;
				output[1][index] = (byte)g;
				output[2][index] = (byte)b;
        	}
        }

        src.putPixels(output);
		output = null;
        return src;
	}

	private int[] convert2Pixels(byte[][] rgb) {
		int[] pixels = new int[rgb[0].length];
		for (int i=0; i < pixels.length; i++)
			pixels[i] = 0xff000000 | ((rgb[0][i]&0xff)<<16) | ((rgb[1][i]&0xff)<<8) | rgb[2][i]&0xff;
		return pixels;
	}

	private int getPixel(int[] pixels, int x, int y, int width, int height) {
		if (x < 0 || x >= width || y < 0 || y >= height) {
			return 0; // 有点暴力啦，懒得管啦
		}
		return pixels[ y*width+x ];
	}

	protected void generateWaterRipples(int x, int y, float[] out) {
		float dx = x-icentreX;
		float dy = y-icentreY;
		float distance2 = dx*dx + dy*dy;
		// 确定 water ripple的半径，如果在半径之外，就直接获取原来位置，不用计算迁移量
		if (distance2 > radius2) { 
			out[0] = x;
			out[1] = y;
		} else {
			// 如果在radius半径之内，计算出来
			float distance = (float)Math.sqrt(distance2);
			// 计算改点振幅
			float amount = amplitude * (float)Math.sin(distance / wavelength * Tools.TWO_PI - phase);
			// 计算能量损失，
			amount *= (radius-distance)/radius; // 计算能量损失，
			if ( distance != 0 )
				amount *= wavelength/distance;
			// 得到water ripple 最终迁移位置
			out[0] = x + dx*amount;
			out[1] = y + dy*amount;
		}
	}
	
}
