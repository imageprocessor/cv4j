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
package com.cv4j.core.filters;

import com.cv4j.core.datamodel.ImageProcessor;

public class GammaFilter extends BaseFilter {

	private int[] lut;
	private double gamma;

	public GammaFilter() {
		this.lut = new int[256];
		this.gamma = 0.5;
	}

	@Override
	public ImageProcessor doFilter(ImageProcessor src) {

		// setup LUT
		setupGammaLut();
		int index = 0;
		for (int row = 0; row < height; row++) {
			int tr = 0, tg = 0, tb = 0;
			for (int col = 0; col < width; col++) {
				index = row * width + col;
				tr = R[index] & 0xff;
				tg = G[index] & 0xff;
				tb = B[index] & 0xff;

				// LUT search
				R[index] = (byte)lut[tr];
				G[index] = (byte)lut[tg];
				B[index] = (byte)lut[tb];
			}
		}
		return src;
	}

	private void setupGammaLut() {

		for (int i = 0; i < 256; i++) {
			lut[i] = (int) (Math.exp(Math.log(i / 255.0) * gamma) * 255.0);
		}
	}
}
