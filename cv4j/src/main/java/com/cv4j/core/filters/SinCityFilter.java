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

import android.graphics.Color;

import com.cv4j.core.datamodel.ImageData;
import com.cv4j.core.datamodel.ImageProcessor;

public class SinCityFilter extends BaseFilter {

	private double threshold = 200; // default value
	private int mainColor = Color.argb(255, 255, 0, 0);

	public void setMainColor(int argb) {
		this.mainColor = argb;
	}

	@Override
	public ImageProcessor doFilter(ImageProcessor src) {

        int total = width*height;
		int tr=0, tg=0, tb=0;
        for(int i=0; i<total; i++) {
			tr = R[i] & 0xff;
			tg = G[i] & 0xff;
			tb = B[i] & 0xff;
			int gray = (int)(0.299 * (double)tr + 0.587 * (double)tg + 0.114 * (double)tb);
			double distance = getDistance(tr, tg, tb);
			if(distance < threshold) {
				double k = distance / threshold;
				int[] rgb = getAdjustableRGB(tr, tg, tb, gray, (float)k);
				tr = rgb[0];
				tg = rgb[1];
				tb = rgb[2];
				R[i] = (byte)tr;
				G[i] = (byte)tg;
				B[i] = (byte)tb;
			} else {
				R[i] = (byte)gray;
				G[i] = (byte)gray;
				B[i] = (byte)gray;
			}
        }
        return src;
	}

	private int[] getAdjustableRGB(int tr, int tg, int tb, int gray, float rate) {
		int[] rgb = new int[3];
		rgb[0] = (int)(tr * rate + gray * (1.0f-rate));
		rgb[1] = (int)(tg * rate + gray * (1.0f-rate));
		rgb[2] = (int)(tb * rate + gray * (1.0f-rate));
		return rgb;
	}

	private double getDistance(int tr, int tg, int tb) {
		int dr = tr - Color.red(mainColor);
		int dg = tg - Color.green(mainColor);
		int db = tb - Color.blue(mainColor);
		int distance = ImageData.SQRT_LUT[Math.abs(dr)] +
				ImageData.SQRT_LUT[Math.abs(dg)] +
				ImageData.SQRT_LUT[Math.abs(db)];
		return Math.sqrt(distance);		
	}

}
