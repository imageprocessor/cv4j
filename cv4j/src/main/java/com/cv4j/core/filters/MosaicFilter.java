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
import com.cv4j.core.datamodel.IntIntegralImage;

public class MosaicFilter extends BaseFilter {
	// 窗口半径大小
	private int r=1;

	public MosaicFilter() {
		r = 1;
	}

	public int getRadius() {
		return r;
	}

	public void setRadius(int r) {
		this.r = r;
	}

	@Override
	public ImageProcessor doFilter(ImageProcessor src) {

		int size = (r * 2 + 1) * (r * 2 + 1);
		int tr = 0, tg = 0, tb = 0;
		byte[][] output = new byte[3][R.length];

		IntIntegralImage rii = new IntIntegralImage();
		rii.setImage(R);
		rii.calculate(width, height);
		IntIntegralImage gii = new IntIntegralImage();
		gii.setImage(G);
		gii.calculate(width, height);
		IntIntegralImage bii = new IntIntegralImage();
		bii.setImage(B);
		bii.calculate(width, height);

		int x2 = 0, y2 = 0;
		int x1 = 0, y1 = 0;
		int index = 0;
		for (int row = 0; row < height; row++) {
			int dy = (row / size);
			y1 = dy*size;
			y2 = (y1+size) > height ? (height -1): (y1 + size);
			index = row*width;
			for (int col = 0; col < width; col++) {
				int dx = (col / size);
				x1 = dx*size;
				x2 = (x1+size) > width ? (width -1): (x1 + size);
				int sr = rii.getBlockSum(x1, y1, x2, y2);
				int sg = gii.getBlockSum(x1, y1, x2, y2);
				int sb = bii.getBlockSum(x1, y1, x2, y2);
				int num = (x2 - x1)*(y2 - y1);
				tr = sr / num;
				tg = sg / num;
				tb = sb / num;
				output[0][index+col] = (byte)tr;
				output[1][index+col] = (byte)tg;
				output[2][index+col] = (byte)tb;
			}
		}
		((ColorProcessor) src).putRGB(output[0], output[1], output[2]);
		output = null;
		return src;
	}
}
