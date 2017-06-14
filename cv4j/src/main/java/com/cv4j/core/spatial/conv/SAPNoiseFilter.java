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

import com.cv4j.core.datamodel.ImageProcessor;
import com.cv4j.core.filters.BaseFilter;

public class SAPNoiseFilter extends BaseFilter {
	private float precent;
	
	public SAPNoiseFilter() {
		precent = 0.01f;
	}

	public float getPrecent() {
		return precent;
	}

	public void setPrecent(float precent) {
		this.precent = precent;
	}

	@Override
	public ImageProcessor doFilter(ImageProcessor src) {


		int total = width*height;

		java.util.Random random = new java.util.Random();
		int numOfSalt = (int)(width*height*precent);
		int row=0, col=0, index=0;
		for(int i=0; i<numOfSalt; i++) {
			row = getPosition(0, height, random);
			col = getPosition(0, width, random);
			index = row*width+col;
			R[index]= (byte)255;
			G[index]= (byte)255;
			B[index]= (byte)255;
			
			row = getPosition(0, height, random);
			col = getPosition(0, width, random);
			R[index]= (byte)0;
			G[index]= (byte)0;
			B[index]= (byte)0;
		}
		return src;
	}

	private int getPosition(int min, int max, java.util.Random random) {
		return random.nextInt(max-min);
	}

}
