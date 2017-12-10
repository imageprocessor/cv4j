/*
 * Copyright (c) 2017 - present, CV4J Contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cv4j.core.pixels;

import com.cv4j.core.datamodel.ByteProcessor;
import com.cv4j.core.datamodel.ColorProcessor;
import com.cv4j.core.datamodel.ImageProcessor;
import com.cv4j.image.util.Tools;

public class Resize {

	public final static int NEAREST_INTEPOLATE = 1;
	public final static int BILINE_INTEPOLATE = 2;
	private float xrate;
	private float yrate;
	
	public Resize(float rate)
	{
		xrate = rate;
		yrate = rate;
	}
	
	public Resize(float xrate, float yrate)
	{
		this.xrate = xrate;
		this.yrate = yrate;
	}
	
	public ImageProcessor resize(ImageProcessor processor, int type) {
		if(type == NEAREST_INTEPOLATE) {
			return nearest(processor);
		} else if(type == BILINE_INTEPOLATE) {
			return biline(processor);
		} else {
			throw new RuntimeException("Unsupported resize type...");
		}
	}

	private ImageProcessor nearest(ImageProcessor processor) {
		int width = processor.getWidth();
		int height = processor.getHeight();
		int w = (int)(width * xrate);
		int h = (int)(height * yrate);
		int channels = processor.getChannels();
		ImageProcessor dst = (channels == 3) ? new ColorProcessor(w, h) : new ByteProcessor(w, h);
		int index = 0;
		for (int row = 0; row < h; row++) {
			int srcRow = Math.round(((float)row)*yrate);
			if(srcRow >=height) {
				srcRow = height - 1;
			}
			for (int col = 0; col < w; col++) {
				int srcCol = Math.round(((float)col)*xrate);
				if(srcCol >= width) {
					srcCol = width - 1;
				}
				int index2 = row * w + col;
				index = srcRow * width + srcCol;
				for(int i=0; i<channels; i++) {
					dst.toByte(i)[index2] = processor.toByte(i)[index];
				}			
			}
		}
		return dst;
	}
	
	public ImageProcessor biline(ImageProcessor processor) {
		int width = processor.getWidth();
		int height = processor.getHeight();
		int w = (int)(width * xrate);
		int h = (int)(height * yrate);
		int channels = processor.getChannels();
		ImageProcessor dst = (channels == 3) ? new ColorProcessor(w, h) : new ByteProcessor(w, h);
		int index = 0;
		for(int row=0; row<h; row++) {
			double srcRow = ((float)row)*yrate;
			// 获取整数部分坐标 row Index
			double j = Math.floor(srcRow);
			// 获取行的小数部分坐标
			double t = srcRow - j; 
			for(int col=0; col<w; col++) {
				double srcCol = ((float)col)*xrate;
				// 获取整数部分坐标 column Index
				double k = Math.floor(srcCol);
				// 获取列的小数部分坐标
				double u = srcCol - k;
				int[] p1 = getPixel(j, k, width, height, processor);
				int[] p2 = getPixel(j, k+1, width, height, processor);
				int[] p3 = getPixel(j+1, k, width, height, processor);
				int[] p4 = getPixel(j+1, k+1, width, height, processor);
				double a = (1.0d-t)*(1.0d-u);
				double b = (1.0d-t)*u;
				double c = (t)*(1.0d-u);
				double d = t*u;
				index = row * w + col;
				for(int i=0; i<channels; i++) {
					int pv = (int)(p1[i] * a + p2[i] * b + p3[i] * c + p4[i] * d);
					dst.toByte(i)[index] = (byte)Tools.clamp(pv);
				}	
			}
		}
		return dst;
	}
	
    private int[] getPixel(double j, double k, int width, int height, ImageProcessor processor) {

    	int row = (int)j;
    	int col = (int)k;

    	if(row >= height)
    	{
    		row = height - 1;
    	}

    	if(row < 0)
    	{
    		row = 0;
    	}

    	if(col < 0)
    	{
    		col = 0;
    	}

    	if(col >= width)
    	{
    		col = width - 1;
    	}

    	int index = row * width + col;
    	int channels = processor.getChannels();
    	int[] rgb = new int[channels];
    	for(int i=0; i<channels; i++) {
    		rgb[i] = processor.toByte(i)[index]&0xff; 		
    	}
		return rgb;	
	}
    

}
