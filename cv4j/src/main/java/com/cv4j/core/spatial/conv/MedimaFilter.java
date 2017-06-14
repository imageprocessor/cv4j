/*
 * Copyright (c) 2017-present, CV4J Contributors.
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
package com.cv4j.core.spatial.conv;

import com.cv4j.core.datamodel.ByteProcessor;
import com.cv4j.core.datamodel.ColorProcessor;
import com.cv4j.core.datamodel.ImageProcessor;
import com.cv4j.core.filters.CommonFilter;

import java.util.Arrays;

public class MedimaFilter implements CommonFilter {
	private boolean meanfilter;
	private int radius;
	public MedimaFilter() {
		meanfilter = true;
		radius = 1;
	}
	
	public void setRadius(int radius) {
		this.radius = radius;
	}

	public boolean isMeanfilter() {
		return meanfilter;
	}

	public void setMeanfilter(boolean meanfilter) {
		this.meanfilter = meanfilter;
	}

	@Override
	public ImageProcessor filter(ImageProcessor src){
		if(src instanceof ColorProcessor) {
			src.getImage().convert2Gray();
			src = src.getImage().getProcessor();
		}
		int width = src.getWidth();
		int height = src.getHeight();
		byte[] GRAY = ((ByteProcessor)src).getGray();
		byte[] output = new byte[GRAY.length];

		int size = radius*2+1;
		int total = size*size;
		int c=0;
		for(int row=0; row<height; row++) {
			for(int col=0; col<width; col++) {
				
				// 统计滤波器
				int[] subpixels = new int[total];
				int index = 0;
				int sum=0;
				for(int i=-radius; i<=radius; i++) {
					int roffset = row + i;
					roffset = (roffset < 0) ? 0 :(roffset>=height ? height-1 : roffset);
					for(int j=-radius; j<=radius; j++) {
						int coffset = col+j;
						coffset = (coffset < 0) ? 0 :(coffset>=width ? width-1 : coffset);
						c = GRAY[roffset*width+coffset]&0xff;
						sum += c;
						subpixels[index++] = c;
					}
				}
				
				Arrays.sort(subpixels);
				c = subpixels[total/2];
				output[row*width+col]=(byte)c;
			}
		}

		((ByteProcessor)src).putGray(output);
		output = null;
		GRAY = null;
		return src;
	}

}
