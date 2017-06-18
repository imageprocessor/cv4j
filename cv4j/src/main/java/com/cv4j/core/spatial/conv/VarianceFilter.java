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
package com.cv4j.core.spatial.conv;

import com.cv4j.core.datamodel.ColorProcessor;
import com.cv4j.core.datamodel.ImageProcessor;
import com.cv4j.core.filters.BaseFilter;
import com.cv4j.image.util.TaskUtils;
import com.cv4j.image.util.Tools;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;

public class VarianceFilter extends BaseFilter {

	private int radius;
	ExecutorService mExecutor;
	CompletionService<Void> service;

	public VarianceFilter() {
		radius = 1;
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}

	public int getRadius() {
		return radius;
	}

	public ImageProcessor doFilter(ImageProcessor src) {

		int numOfPixels = width * height;
		byte[][] output = new byte[3][numOfPixels];

		int dims = src.getChannels();
		mExecutor = TaskUtils.newFixedThreadPool("cv4j",dims);
		service = new ExecutorCompletionService<>(mExecutor);

		for (int i = 0;i<dims;i++) {
			final byte[] realOutput = output[i];
			final byte[] input = src.toByte(i);
			service.submit(new Callable<Void>() {
				public Void call() throws Exception {
					getNewPixels(realOutput,input);
					return null;
				}
			});
		}

		for (int i = 0; i < dims; i++) {
			try {
				service.take();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		mExecutor.shutdown();

		((ColorProcessor) src).putRGB(output[0], output[1], output[2]);
		output = null;
		return src;
	}

	private void getNewPixels(byte[] output, byte[] input) {
		int size = radius * 2 + 1;
		int total = size * size;
		int r = 0, g = 0, b = 0;
		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {

				// 统计滤波器 -方差
				int[] subpixels = new int[total];
				int index = 0;
				for (int i = -radius; i <= radius; i++) {
					int roffset = row + i;
					roffset = (roffset < 0) ? 0 : (roffset >= height ? height - 1 : roffset);
					for (int j = -radius; j <= radius; j++) {
						int coffset = col + j;
						coffset = (coffset < 0) ? 0 : (coffset >= width ? width - 1 : coffset);
						subpixels[index] = input[roffset * width + coffset] & 0xff;
						index++;
					}
				}
				r = calculateVar(subpixels); // red
				output[row * width + col] = (byte)Tools.clamp(r);
			}
		}
	}

	private int calculateVar(int[] data) {
		int sum1=0, sum2=0;
		for(int i=0; i<data.length; i++) {
			sum1 += (data[i]*data[i]);
			sum2 += data[i];
		}
		
		int sum3 = (sum2*sum2) / data.length;
		return (sum1 - sum3) / data.length;
	}

}
