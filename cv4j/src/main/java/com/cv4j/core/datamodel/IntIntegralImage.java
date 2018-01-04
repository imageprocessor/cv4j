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
package com.cv4j.core.datamodel;

import java.util.Arrays;

public class IntIntegralImage {
	// sum index tables
	private int[] sum;
	private float[] squaresum;
	private byte[] image;
	private int width;
	private int height;

	public byte[] getImage() {
		return image;
	}

	public void setImage(byte[] image) {
		this.image = image;
	}

	public int getBlockSum(int x1, int y1, int x2, int y2) {
		int tl = sum[y1*width+x1];
		int tr = sum[y2*width+x1];
		int bl = sum[y1*width+x2];
		int br = sum[y2*width+x2];
		int s = (br - bl - tr + tl);
		return s;
	}

	public float getBlockSquareSum(int x1, int y1, int x2, int y2) {
		float tl = squaresum[y1*width+x1];
		float tr = squaresum[y2*width+x1];
		float bl = squaresum[y1*width+x2];
		float br = squaresum[y2*width+x2];
		float var = (br - bl - tr + tl);
		return var;
	}

	public void calculate(int w, int h) {
		// 初始化积分图
		this.width = w+1;
		this.height = h+1;
		sum = new int[width*height];
		Arrays.fill(sum, 0);
		// 计算积分图
		int p1=0, p2=0, p3=0, p4;
		for(int row=1; row<height; row++ ) {
			for(int col=1; col<width; col++) {
				// 计算和查找表
				p1=image[(row-1)*w+col-1]&0xff;// p(x, y)
				p2=sum[row*width+col-1]; // p(x-1, y)
				p3=sum[(row-1)*width+col]; // p(x, y-1);
				p4=sum[(row-1)*width+col-1]; // p(x-1, y-1);
				sum[row*width+col]= p1+p2+p3-p4;
			}
		}
	}

	public void calculate(int w, int h, boolean sqrtsum) {
		this.width = w+1;
		this.height = h+1;
		sum = new int[width*height];
		squaresum = new float[width*height];
		Arrays.fill(sum, 0);
		Arrays.fill(squaresum, 0);
		// rows
		int p1=0, p2=0, p3=0, p4;
		float sp2=0, sp3=0, sp4=0;
		for(int row=1; row<height; row++ ) {
			for(int col=1; col<width; col++) {
				// 计算和查找表
				p1=image[(row-1)*w+col-1]&0xff;// p(x, y)
				p2=sum[row*width+col-1]; // p(x-1, y)
				p3=sum[(row-1)*width+col]; // p(x, y-1);
				p4=sum[(row-1)*width+col-1]; // p(x-1, y-1);
				sum[row*width+col]= p1+p2+p3-p4;

				// 计算平方查找表
				sp2=squaresum[row*width+col-1]; // p(x-1, y)
				sp3=squaresum[(row-1)*width+col]; // p(x, y-1);
				sp4=squaresum[(row-1)*width+col-1]; // p(x-1, y-1);
				squaresum[row*width+col]=p1*p1+sp2+sp3-sp4;
			}
		}
	}
}
