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
package com.cv4j.core.filters;

import com.cv4j.core.datamodel.ColorProcessor;
import com.cv4j.core.datamodel.ImageProcessor;
import com.cv4j.image.util.Tools;

public class EmbossFilter extends BaseFilter {
	private int COLORCONSTANTS;
	private boolean out;

	public EmbossFilter() {
		this.COLORCONSTANTS = 100;
	}

	public EmbossFilter(boolean out) {
		this.out = out;
		this.COLORCONSTANTS = 100;
	}

	@Override
	public ImageProcessor doFilter(ImageProcessor src){

		int offset = 0;
		int r1=0, g1=0, b1=0;
		int r2=0, g2=0, b2=0;
		int r=0, g=0, b=0;

		byte[][] output = new byte[3][R.length];
		for ( int y = 1; y < height-1; y++ ) {
			offset = y*width;
			for ( int x = 1; x < width-1; x++ ) {
				r1 = R[offset] & 0xff;
				g1 = G[offset] & 0xff;
				b1 = B[offset] & 0xff;

				r2 = R[offset+width] & 0xff;
				g2 = G[offset+width] & 0xff;
				b2 = B[offset+width] & 0xff;

				if(out) {
					r = r1 - r2;
					g = g1 - g2;
					b = b1 - b2;
				} else {
					r = r2 - r1;
					g = g2 - g1;
					b = b2 - b1;
				}
				r = Tools.clamp(r+COLORCONSTANTS);
				g = Tools.clamp(g+COLORCONSTANTS);
				b = Tools.clamp(b+COLORCONSTANTS);

				output[0][offset] = (byte)r;
				output[1][offset] = (byte)g;
				output[2][offset] = (byte)b;
				offset++;
			}
		}
		((ColorProcessor)src).putRGB(output[0], output[1], output[2]);
		output = null;
		return src;
	}

	/**
	 * 
	 * @param out
	 */
	public void setOUT(boolean out) {
		this.out = out;
	}
}
