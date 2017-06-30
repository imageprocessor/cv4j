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
package com.cv4j.core.binary;

import com.cv4j.core.datamodel.ByteProcessor;
import java.util.Arrays;

public class DistanceTransform {

	public void process(ByteProcessor binary) {
		int width = binary.getWidth();
		int height = binary.getHeight();
		byte[] pixels = binary.getGray();

		byte[] output = new byte[width*height];
		int[] distmap = new int[width*height];
		System.arraycopy(pixels, 0, output, 0, output.length);
		Arrays.fill(distmap, 0);
		
		// initialize distance value
		int offset =0;
		int pv = 0;
		for(int row=0; row<height; row++) {
			offset = row*width;
			for(int col=0; col<width; col++) {
				pv = pixels[offset+col];
				if(pv == 255) {
					distmap[offset+col] = 1;
				}
			}
		}
		
		// distance transform stage
		boolean stop = false;
		int level = 0;
		while(!stop) {
			stop = dt(pixels, output, distmap, level, width, height);
			System.arraycopy(output, 0, pixels, 0, output.length);
			level++;
		}

		// assign different gray value by distance value
		int step = 255 / level;
		int dis = 0;
		Arrays.fill(output, (byte)0);
		for(int row=0; row<height; row++) {
			offset = row*width;
			for(int col=0; col<width; col++) {
				dis = distmap[offset+col];
				if(dis > 0) {
					int gray = dis*step;
					output[offset+col] = (byte)gray;
				}
			}
		}
		// update pixels and release memory
		binary.putGray(output);
		output = null;
		distmap = null;
	}
	
	private boolean dt(byte[] input, byte[] output, int[] distmap, int level, int width, int height) {

		boolean stop = true;
		int p1=0;
		int p2=0;
		int p3=0;
		int p4=0;
		int p5=0;
		int p6=0;
		int p7=0;
		int p8=0;
		int p9=0;
		int offset = 0;

		for(int row=1; row<height-1; row++) {
			offset = row*width;
			for(int col=1; col<width-1; col++) {
				p1 = input[offset-width+col-1]&0xff;
				p2 = input[offset-width+col]&0xff;
				p3 = input[offset-width+col+1]&0xff;
				p4 = input[offset+col-1]&0xff;
				p5 = input[offset+col]&0xff;
				p6 = input[offset+col-1]&0xff;
				p7 = input[offset+width+col-1]&0xff;
				p8 = input[offset+width+col]&0xff;
				p9 = input[offset+width+col+1]&0xff;
				int sum = p1+p2+p3+p4+p6+p7+p8+p9;
				int total = 255*8;
				if(p5 == 255 &&  sum != total) {
					output[offset+col] = (byte)0;
					distmap[offset+col] = distmap[offset+col] + level;
					stop = false;
				}
			}
		}
		return stop;
	}

}
