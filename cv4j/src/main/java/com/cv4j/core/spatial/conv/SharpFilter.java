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

public class SharpFilter extends BaseFilter {

	public static int[] kernel=new int[]{-1,-1,-1, -1, 12, -1, -1,-1,-1};

	@Override
	public ImageProcessor doFilter(ImageProcessor src){

		int total = width*height;
		byte[][] output = new byte[3][total];

		int offset=0;
		int k0=kernel[0], k1=kernel[1], k2=kernel[2];
		int k3=kernel[3], k4=kernel[4], k5=kernel[5];
		int k6=kernel[6], k7=kernel[7], k8=kernel[8];
		
		int scale = k0+k1+k2+k3+k4+k5+k6+k7+k8;
		int sr=0, sg=0, sb=0;
		int r=0, g=0, b=0;
		for(int row=1; row<height-1; row++) {
			offset = row*width;
			for(int col=1; col<width-1; col++) {
				// red
				sr = k0*(R[offset-width+col-1]&0xff)
					+ k1* (R[offset-width+col]&0xff)
					+ k2* (R[offset-width+col+1]&0xff)
					+ k3* (R[offset+col-1]&0xff)
					+ k4* (R[offset+col]&0xff)
					+ k5* (R[offset+col+1]&0xff)
					+ k6* (R[offset+width+col-1]&0xff)
					+ k7* (R[offset+width+col]&0xff)
					+ k8* (R[offset+width+col+1]&0xff);
				// green
				sg = k0*(G[offset-width+col-1]&0xff)
						+ k1* (G[offset-width+col]&0xff)
						+ k2* (G[offset-width+col+1]&0xff)
						+ k3* (G[offset+col-1]&0xff)
						+ k4* (G[offset+col]&0xff)
						+ k5* (G[offset+col+1]&0xff)
						+ k6* (G[offset+width+col-1]&0xff)
						+ k7* (G[offset+width+col]&0xff)
						+ k8* (G[offset+width+col+1]&0xff);
				// blue
				sb = k0*(B[offset-width+col-1]&0xff)
						+ k1* (B[offset-width+col]&0xff)
						+ k2* (B[offset-width+col+1]&0xff)
						+ k3* (B[offset+col-1]&0xff)
						+ k4* (B[offset+col]&0xff)
						+ k5* (B[offset+col+1]&0xff)
						+ k6* (B[offset+width+col-1]&0xff)
						+ k7* (B[offset+width+col]&0xff)
						+ k8* (B[offset+width+col+1]&0xff);
				r = sr / scale;
				g = sg / scale;
				b = sb / scale;
				output[0][offset+col]=(byte)Tools.clamp(r);
				output[1][offset+col]=(byte)Tools.clamp(g);
				output[2][offset+col]=(byte)Tools.clamp(b);

				// for next pixel
				sr = 0;
				sg = 0;
				sb = 0;
			}
		}
		((ColorProcessor) src).putRGB(output[0], output[1], output[2]);
		output = null;
		return src;
	}

}
