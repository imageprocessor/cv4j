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

public class SobelFilter extends BaseFilter {
	public static int[] sobel_y = new int[] { -1, -2, -1, 0, 0, 0, 1, 2, 1 };
	public static int[] sobel_x = new int[] { -1, 0, 1, -2, 0, 2, -1, 0, 1 };
	private boolean xdirect;

	public SobelFilter() {
		xdirect = true;
	}

	public boolean isXdirect() {
		return xdirect;
	}

	public void setXdirect(boolean xdirect) {
		this.xdirect = xdirect;
	}

	@Override
	public ImageProcessor doFilter(ImageProcessor src){

		int total = width*height;
		byte[][] output = new byte[3][total];

		int offset = 0;
		int k0 = 0, k1 = 0, k2 = 0;
		int k3 = 0, k4 = 0, k5 = 0;
		int k6 = 0, k7 = 0, k8 = 0;
		if(xdirect) {
			k0 = sobel_x[0];
			k1 = sobel_x[1];
			k2 = sobel_x[2];
			k3 = sobel_x[3];
			k4 = sobel_x[4];
			k5 = sobel_x[5];
			k6 = sobel_x[6];
			k7 = sobel_x[7];
			k8 = sobel_x[8];
		}
		else {
			k0 = sobel_y[0];
			k1 = sobel_y[1];
			k2 = sobel_y[2];
			k3 = sobel_y[3];
			k4 = sobel_y[4];
			k5 = sobel_y[5];
			k6 = sobel_y[6];
			k7 = sobel_y[7];
			k8 = sobel_y[8];
		}

		int sr = 0, sg = 0, sb = 0;
		int r = 0, g = 0, b = 0;
		for (int row = 1; row < height - 1; row++) {
			offset = row * width;
			for (int col = 1; col < width - 1; col++) {
				// red
				sr = k0 * (R[offset - width + col - 1] & 0xff)
						+ k1 * (R[offset - width + col] & 0xff)
						+ k2 * (R[offset - width + col + 1] & 0xff)
						+ k3 * (R[offset + col - 1] & 0xff)
						+ k4 * (R[offset + col] & 0xff)
						+ k5 * (R[offset + col + 1] & 0xff)
						+ k6 * (R[offset + width + col - 1] & 0xff)
						+ k7 * (R[offset + width + col] & 0xff)
						+ k8 * (R[offset + width + col + 1] & 0xff);
				// green
				sg = k0 * (G[offset - width + col - 1]  & 0xff)
						+ k1 * (G[offset - width + col] & 0xff)
						+ k2 * (G[offset - width + col + 1] & 0xff)
						+ k3 * (G[offset + col - 1] & 0xff)
						+ k4 * (G[offset + col] & 0xff)
						+ k5 * (G[offset + col + 1] & 0xff)
						+ k6 * (G[offset + width + col - 1] & 0xff)
						+ k7 * (G[offset + width + col] & 0xff)
						+ k8 * (G[offset + width + col + 1] & 0xff);
				// blue
				sb = k0 * (B[offset - width + col - 1] & 0xff)
						+ k1 * (B[offset - width + col] & 0xff)
						+ k2 * (B[offset - width + col + 1] & 0xff)
						+ k3 * (B[offset + col - 1] & 0xff)
						+ k4 * (B[offset + col] & 0xff)
						+ k5 * (B[offset + col + 1] & 0xff)
						+ k6 * (B[offset + width + col - 1] & 0xff)
						+ k7 * (B[offset + width + col] & 0xff)
						+ k8 * (B[offset + width + col + 1] & 0xff);
				r = sr;
				g = sg;
				b = sb;
				output[0][offset + col] = (byte)Tools.clamp(r);
				output[1][offset + col] = (byte)Tools.clamp(g);
				output[2][offset + col] = (byte)Tools.clamp(b);

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
