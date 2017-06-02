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

public class SpotlightFilter extends BaseFilter {
	// attenuation coefficient, default is 1 means line decrease...
	private int factor;
	public SpotlightFilter() {
		factor = 1;
	}
	
	public void setFactor(int coefficient) {
		this.factor = coefficient;
	}

	@Override
	public ImageProcessor doFilter(ImageProcessor src){

        int offset = 0;
        int centerX = width/2;
        int centerY = height/2;
        double maxDistance = Math.sqrt(centerX * centerX + centerY * centerY);
		int tr = 0, tg = 0, tb = 0;
        for(int row=0; row<height; row++) {
        	offset = row * width;
        	for(int col=0; col<width; col++) {
                tr = R[offset] & 0xff;
                tg = G[offset] & 0xff;
                tb = B[offset] & 0xff;
                double scale = 1.0 - getDistance(centerX, centerY, col, row)/maxDistance;
                for(int i=0; i<factor; i++) {
                	scale = scale * scale;
                }

            	tr = (int)(scale * tr);
            	tg = (int)(scale * tg);
            	tb = (int)(scale * tb);

				R[offset] = (byte)tr;
				G[offset] = (byte)tg;
				B[offset] = (byte)tb;
				offset++;
        	}
        }
        return src;
	}
	
	private double getDistance(int centerX, int centerY, int px, int py) {
		double xx = (centerX - px)*(centerX - px);
		double yy = (centerY - py)*(centerY - py);
		return (int)Math.sqrt(xx + yy);
	}

}
