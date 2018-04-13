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
import com.cv4j.core.datamodel.IntIntegralImage;
import com.cv4j.core.datamodel.Size;

/**
 * erode, replace max with min value
 *
 */
public class Erode {

	/**
	 *
	 * @param binary - image data
	 * @param structureElement - structure element for morphology operator
	 */
	public void process(ByteProcessor binary, Size structureElement)
	{
		process(binary, structureElement, 1);
	}

	/**
	 *
	 * @param binary
	 * @param structureElement, 3, 5, 7, 9, 11, x y, must be odd
	 * @param iteration - 1 as default, better less than 10, for the sake of time consume
	 */
	public void process(ByteProcessor binary, Size s, int iteration){
		int width = binary.getWidth();
		int height = binary.getHeight();
		byte[] output = new byte[width*height];
		byte[] input = binary.getGray();
		IntIntegralImage ii = new IntIntegralImage();
		int x2 = 0, y2 = 0;
		int x1 = 0, y1 = 0;
		int cx = 0, cy = 0;
		for(int i=0; i<iteration; i++) {
			ii.setImage(input);
			ii.calculate(width, height);
			System.arraycopy(input, 0, output, 0, input.length);
			for(int row=0; row<height+(s.rows/2); row++) {
				y2 = (row + 1)>height ? height : (row + 1);
				y1 = (row - s.rows) < 0 ? 0 : (row - s.rows);
				for(int col=0; col<width+(s.cols/2); col++) {
					x2 = (col + 1)>width ? width : (col + 1);
	                x1 = (col - s.cols) < 0 ? 0 : (col - s.cols);
	                cx = (col - s.cols/2) < 0 ? 0 : col - s.cols/2;
	                cy = (row - s.rows/2) < 0 ? 0 : row - s.rows/2;
	                int num = (x2 - x1)*(y2 - y1);
	                int sum = ii.getBlockSum(x1, y1, x2, y2)/num;
					if(sum > 0 && sum < 255) {
						output[cy*width+cx] = (byte)0;
					}
				}
			}
			System.arraycopy(output, 0, input, 0, input.length);
		}

		// try to release memory
		output = null;
	}
}
