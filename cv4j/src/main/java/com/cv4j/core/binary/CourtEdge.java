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
import com.cv4j.core.datamodel.Size;

public class CourtEdge {

	public void process(ByteProcessor binary) {
		int width = binary.getWidth();
		int height = binary.getHeight();

		byte[] input1 = new byte[width*height];
		System.arraycopy(binary.getGray(), 0, input1, 0, input1.length);

		Erode erode = new Erode();
		erode.process(binary, new Size(3, 3));
		byte[] input2 = binary.getGray();
		byte[] output = new byte[input1.length];
		System.arraycopy(input1, 0, output, 0, input1.length);

		int p1=0, p2=0;
		int offset = 0;
		for(int row=1; row<height-1; row++) {
			offset = row*width;
			for(int col=1; col<width-1; col++) {
				p1 = input1[offset+col]&0xff;
				p2 = input2[offset+col]&0xff;
				if(p1 == p2) {
					output[offset+col] = (byte)0;
				}
				else {
					output[offset+col] = (byte)255;
				}
			}
		}
		binary.putGray(output);

		// release memory
		output = null;
		input1 = null;
		input2 = null;
	}

}
