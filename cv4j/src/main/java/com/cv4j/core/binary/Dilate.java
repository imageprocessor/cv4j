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
 * dilate, replace min with max value
 *
 */
public class Dilate {

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
	public void process(ByteProcessor binary, Size structureElement, int iteration){
		int width = binary.getWidth();
		int height = binary.getHeight();
		byte[] output = new byte[width*height];
		byte[] input = binary.getGray();
		IntIntegralImage ii = new IntIntegralImage();
		int blocksum = structureElement.cols*structureElement.rows*255;
		for(int i=0; i<iteration; i++) {
			ii.setImage(input);
			ii.process(width, height);
			System.arraycopy(input, 0, output, 0, input.length);
			for(int row=0; row<height; row++) {
				for(int col=0; col<width; col++) {
					int xr = structureElement.cols/2;
					int yr = structureElement.rows/2;
					int ny = row+yr;
					int nx = col+xr;
					int sum = ii.getBlockSum(nx, ny, (yr * 2 + 1), (xr * 2 + 1));
					if(sum > 0 && sum < blocksum) {
						output[row*width+col] = (byte)255;
					}
				}
			}
			System.arraycopy(output, 0, input, 0, input.length);
		}

		// try to release memory
		output = null;
	}
}
