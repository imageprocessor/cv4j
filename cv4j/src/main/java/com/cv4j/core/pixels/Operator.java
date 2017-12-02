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
package com.cv4j.core.pixels;

import com.cv4j.core.datamodel.ByteProcessor;
import com.cv4j.core.datamodel.ColorProcessor;
import com.cv4j.core.datamodel.ImageProcessor;
import com.cv4j.core.datamodel.Rect;
import com.cv4j.exception.CV4JException;
import com.cv4j.image.util.Preconditions;
import com.cv4j.image.util.Tools;

public final class Operator {
	
	public static ImageProcessor add(ImageProcessor image1, ImageProcessor image2) {
		if(!checkParams(image1, image2)) {
			return null;
		}
		int channels = image1.getChannels();
		int w = image1.getWidth();
		int h = image1.getHeight();
		ImageProcessor dst = (channels == 3) ? new ColorProcessor(w, h) : new ByteProcessor(w, h);
		int size = w*h;
		int a=0, b=0;
		int c=0;
		for(int i=0; i<size; i++) {
			for(int n=0; n<channels; n++) {
				a = image1.toByte(n)[i]&0xff;
				b = image2.toByte(n)[i]&0xff;
				c = Tools.clamp(a + b);
				dst.toByte(n)[i] = (byte)c;
			}
		}
		return dst;
	}
	
	public static ImageProcessor substract(ImageProcessor image1, ImageProcessor image2) {
		if(!checkParams(image1, image2)) {
			return null;
		}
		int channels = image1.getChannels();
		int w = image1.getWidth();
		int h = image1.getHeight();
		ImageProcessor dst = (channels == 3) ? new ColorProcessor(w, h) : new ByteProcessor(w, h);
		int size = w*h;
		int a=0, b=0;
		int c=0;
		for(int i=0; i<size; i++) {
			for(int n=0; n<channels; n++) {
				a = image1.toByte(n)[i]&0xff;
				b = image2.toByte(n)[i]&0xff;
				c = Tools.clamp(a - b);
				dst.toByte(n)[i] = (byte)c;
			}
		}
		return dst;
	}
	
	public static ImageProcessor multiple(ImageProcessor image1, ImageProcessor image2) {
		if(!checkParams(image1, image2)) {
			return null;
		}
		int channels = image1.getChannels();
		int w = image1.getWidth();
		int h = image1.getHeight();
		ImageProcessor dst = (channels == 3) ? new ColorProcessor(w, h) : new ByteProcessor(w, h);
		int size = w*h;
		int a=0, b=0;
		int c=0;
		for(int i=0; i<size; i++) {
			for(int n=0; n<channels; n++) {
				a = image1.toByte(n)[i]&0xff;
				b = image2.toByte(n)[i]&0xff;
				c = Tools.clamp(a * b);
				dst.toByte(n)[i] = (byte)c;
			}
		}
		return dst;
	}
	
	public static ImageProcessor division(ImageProcessor image1, ImageProcessor image2) {
		if(!checkParams(image1, image2)) {
			return null;
		}
		int channels = image1.getChannels();
		int w = image1.getWidth();
		int h = image1.getHeight();
		ImageProcessor dst = (channels == 3) ? new ColorProcessor(w, h) : new ByteProcessor(w, h);
		int size = w*h;
		int a=0, b=0;
		int c=0;
		for(int i=0; i<size; i++) {
			for(int n=0; n<channels; n++) {
				a = image1.toByte(n)[i]&0xff;
				b = image2.toByte(n)[i]&0xff;
				c = b == 0 ? 0 : Tools.clamp(a / b);
				dst.toByte(n)[i] = (byte)c;
			}
		}
		return dst;
	}
	
	public static ImageProcessor bitwise_and(ImageProcessor image1, ImageProcessor image2) {
		if(!checkParams(image1, image2)) {
			return null;
		}
		int channels = image1.getChannels();
		int w = image1.getWidth();
		int h = image1.getHeight();
		ImageProcessor dst = (channels == 3) ? new ColorProcessor(w, h) : new ByteProcessor(w, h);
		int size = w*h;
		int a=0, b=0;
		int c=0;
		for(int i=0; i<size; i++) {
			for(int n=0; n<channels; n++) {
				a = image1.toByte(n)[i]&0xff;
				b = image2.toByte(n)[i]&0xff;
				c = a&b;
				dst.toByte(n)[i] = (byte)Tools.clamp(c);
			}
		}
		return dst;
	}
	
	public static ImageProcessor bitwise_or(ImageProcessor image1, ImageProcessor image2) {
		if(!checkParams(image1, image2)) {
			return null;
		}
		int channels = image1.getChannels();
		int w = image1.getWidth();
		int h = image1.getHeight();
		ImageProcessor dst = (channels == 3) ? new ColorProcessor(w, h) : new ByteProcessor(w, h);
		int size = w*h;
		int a=0, b=0;
		int c=0;
		for(int i=0; i<size; i++) {
			for(int n=0; n<channels; n++) {
				a = image1.toByte(n)[i]&0xff;
				b = image2.toByte(n)[i]&0xff;
				c = a|b;
				dst.toByte(n)[i] = (byte)Tools.clamp(c);
			}
		}
		return dst;
	}
	
	public static ImageProcessor bitwise_not(ImageProcessor image) {
		int channels = image.getChannels();
		int w = image.getWidth();
		int h = image.getHeight();
		ImageProcessor dst = (channels == 3) ? new ColorProcessor(w, h) : new ByteProcessor(w, h);
		int size = w*h;
		int c=0;
		for(int i=0; i<size; i++) {
			for(int n=0; n<channels; n++) {
				c = ~image.toByte(n)[i];
				dst.toByte(n)[i] = (byte)c;
			}
		}
		return dst;
	}
	
	public static ImageProcessor bitwise_xor(ImageProcessor image1, ImageProcessor image2) {
		if(!checkParams(image1, image2)) {
			return null;
		}
		int channels = image1.getChannels();
		int w = image1.getWidth();
		int h = image1.getHeight();
		ImageProcessor dst = (channels == 3) ? new ColorProcessor(w, h) : new ByteProcessor(w, h);
		int size = w*h;
		int a=0, b=0;
		int c=0;
		for(int i=0; i<size; i++) {
			for(int n=0; n<channels; n++) {
				a = image1.toByte(n)[i]&0xff;
				b = image2.toByte(n)[i]&0xff;
				c = a^b;
				dst.toByte(n)[i] = (byte)Tools.clamp(c);
			}
		}
		return dst;
	}
	
	public static ImageProcessor addWeight(ImageProcessor image1, float w1, ImageProcessor image2, float w2, int gamma) {
		if(!checkParams(image1, image2)) {
			return null;
		}
		int channels = image1.getChannels();
		int w = image1.getWidth();
		int h = image1.getHeight();
		ImageProcessor dst = (channels == 3) ? new ColorProcessor(w, h) : new ByteProcessor(w, h);
		int size = w*h;
		int a=0, b=0;
		int c=0;
		for(int i=0; i<size; i++) {
			for(int n=0; n<channels; n++) {
				a = image1.toByte(n)[i]&0xff;
				b = image2.toByte(n)[i]&0xff;
				c = (int)(a*w1 + b*w2 + gamma);
				dst.toByte(n)[i] = (byte)Tools.clamp(c);
			}
		}
		return dst;
	}

	/**
	 * ROI sub image by rect.x, rect.y, rect.width, rect.height
	 * @param image
	 * @param rect
	 * @return
	 * @throws CV4JException
	 */
	public static ImageProcessor subImage(ImageProcessor image, Rect rect) throws CV4JException{
		int channels = image.getChannels();
		int w = rect.width;
		int h = rect.height;
		ImageProcessor dst = (channels == 3) ? new ColorProcessor(w, h) : new ByteProcessor(w, h);
		int a=0;
		int index = 0;

		try {
			for(int n=0; n<channels; n++) {
				for(int row=rect.y; row < (rect.y+rect.height); row++) {
					for(int col=rect.x; col < (rect.x+rect.width); col++) {
						index = row*image.getWidth() + col;
						a = image.toByte(n)[index]&0xff;
						index = (row - rect.y)*w + (col - rect.x);
						dst.toByte(n)[index] = (byte)a;
					}
				}
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new CV4JException("数组越界了");
		}

		return dst;
	}

	private static boolean checkParams(ImageProcessor src1, ImageProcessor src2) {

		return Preconditions.isNotBlank(src1)
				&& Preconditions.isNotBlank(src2)
				&& src1.getChannels() == src2.getChannels()
				&& src1.getWidth() == src2.getWidth()
				&& src1.getHeight() == src2.getHeight();
	}
}
