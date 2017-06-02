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
import com.cv4j.image.util.Tools;

public class SepiaToneFilter extends BaseFilter {

	@Override
	public ImageProcessor doFilter(ImageProcessor src) {

        int total = width*height;
		int r=0, g=0, b=0;
        for(int i=0; i<total; i++) {
			r = R[i] & 0xff;
			g = G[i] & 0xff;
			b = B[i] & 0xff;

			r = (int) colorBlend(noise(), (r * 0.393) + (g * 0.769) + (b * 0.189), r);
			g = (int) colorBlend(noise(), (r * 0.349) + (g * 0.686) + (b * 0.168), g);
			b = (int) colorBlend(noise(), (r * 0.272) + (g * 0.534) + (b * 0.131), b);

			R[i] = (byte) Tools.clamp(r);
			G[i] = (byte) Tools.clamp(g);
			B[i] = (byte) Tools.clamp(b);
		}
        return src;
	}
	
	private double noise() {
		return Math.random()*0.5 + 0.5;
	}
	
	private double colorBlend(double scale, double dest, double src) {
	    return (scale * dest + (1.0 - scale) * src);
	}
}
