/**
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

import com.cv4j.core.datamodel.ColorProcessor;
import com.cv4j.core.datamodel.ImageProcessor;
import com.cv4j.core.filters.BaseFilter;
import com.cv4j.image.util.Tools;

public class VarianceFilter extends BaseFilter {
	private int radius;

	public VarianceFilter() {
		radius = 1;
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}

	public int getRadius() {
		return radius;
	}

	public ImageProcessor doFilter(ImageProcessor src) {

		int numOfPixels = width * height;
		byte[][] output = new byte[3][numOfPixels];

		int size = radius * 2 + 1;
		int total = size * size;
		int r = 0, g = 0, b = 0;
		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {

				// 统计滤波器 -方差
				int[][] subpixels = new int[3][total];
				int index = 0;
				for (int i = -radius; i <= radius; i++) {
					int roffset = row + i;
					roffset = (roffset < 0) ? 0 : (roffset >= height ? height - 1 : roffset);
					for (int j = -radius; j <= radius; j++) {
						int coffset = col + j;
						coffset = (coffset < 0) ? 0 : (coffset >= width ? width - 1 : coffset);
						subpixels[0][index] = R[roffset * width + coffset] & 0xff;
						subpixels[1][index] = G[roffset * width + coffset] & 0xff;
						subpixels[2][index] = B[roffset * width + coffset] & 0xff;
						index++;
					}
				}
				
				r = calculateVar(subpixels[0]); // red
				g = calculateVar(subpixels[1]); // green
				b = calculateVar(subpixels[2]); // blue

				output[0][row * width + col] = (byte)Tools.clamp(r);
				output[1][row * width + col] = (byte)Tools.clamp(g);
				output[2][row * width + col] = (byte)Tools.clamp(b);
			}
		}

		((ColorProcessor) src).putRGB(output[0], output[1], output[2]);
		output = null;
		return src;
	}

	private int calculateVar(int[] data) {
		int sum1=0, sum2=0;
		for(int i=0; i<data.length; i++) {
			sum1 += (data[i]*data[i]);
			sum2 += data[i];
		}
		
		int sum3 = (sum2*sum2) / data.length;
		return (sum1 - sum3) / data.length;
	}

}
