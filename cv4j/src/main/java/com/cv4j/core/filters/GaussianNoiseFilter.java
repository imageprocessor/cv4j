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

import com.cv4j.core.datamodel.ImageProcessor;
import com.cv4j.image.util.Tools;

/**
 * 随机噪声滤镜
 */
public class GaussianNoiseFilter extends BaseFilter {
	private int sigma;
	
	public GaussianNoiseFilter() {
		sigma = 25;
	}

	public int getSigma() {
		return sigma;
	}

	public void setSigma(int sigma) {
		this.sigma = sigma;
	}

	public ImageProcessor doFilter(ImageProcessor src) {

		int r=0, g=0, b=0;
		int offset = 0;
		int total = width * height;
		java.util.Random random = new java.util.Random();
		for(int i=0; i<total; i++) {
			r= R[i]&0xff;
			g= G[i]&0xff;
			b= B[i]&0xff;

			// add Gaussian noise
			r = (int)(r + sigma*random.nextGaussian());
			g = (int)(g + sigma*random.nextGaussian());
			b = (int)(b + sigma*random.nextGaussian());

			R[i] = (byte) Tools.clamp(r);
			G[i] = (byte) Tools.clamp(g);
			B[i] = (byte) Tools.clamp(b);
		}
		return src;
	}

}
