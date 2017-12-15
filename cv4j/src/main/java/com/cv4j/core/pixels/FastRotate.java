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
import com.cv4j.core.datamodel.Scalar;
import com.cv4j.image.util.Tools;

public class FastRotate {
	
	private int outw;
	private int outh;
	private Scalar color;
	public ImageProcessor rotate(ImageProcessor processor, double degree) 
	{
		color = new Scalar();
		return rotate(processor, degree, color);
	}
	
	public ImageProcessor rotate(ImageProcessor processor, double degree, Scalar bgColor) {
		this.color = bgColor;
        if(Math.floor(degree/90) == 1){
        	processor = rotate90(processor, 90);
        	degree = degree - 90;
        } else if(Math.floor(degree/90) == 2) {
        	processor = rotate90(processor, 180);
        	degree = degree - 180;
        }
        else if(Math.floor(degree/90) == 3) {
        	processor = rotate90(processor, 270);
        	degree = degree - 270;
        }
		int width = processor.getWidth();
		int height = processor.getHeight();
		int srcHeight = processor.getHeight();
		this.color = bgColor;
		
		// X shear
        double angleValue = ((degree/2.0d)/180.0d) * Math.PI;
        outh = height;
        outw = (int)(width + height * Math.tan(angleValue));   
        int channels = processor.getChannels();
		ImageProcessor temp = (channels == 3) ? new ColorProcessor(outw, outh) : new ByteProcessor(outw, outh);
		int index = 0;
		for(int row=0; row<outh; row++) {
        	for(int col=0; col<outw; col++) {
        		double prow = row;
        		double pcol = col + Math.tan(angleValue) * (row - height);
        		byte[] rgb = getNearestPixels(processor, width, height, prow, pcol, false);
        		index = row * outw + col;
        		for(int i=0; i<channels; i++) {
        			temp.toByte(i)[index] = (byte)rgb[i];
				}
        	}
        }
        
		// Y shear
        int srcWidth = width;
        width = outw;
        height = outh;
        angleValue = ((degree)/180.0d) * Math.PI;
        outw = width;        // big trick!!!!
        outh = (int)(srcWidth * Math.sin(angleValue) 
        		+ height * Math.cos(angleValue));
        int outhh = (int)(srcWidth * Math.sin(angleValue) + height);
        int offsetY = outhh - outh;
        index = 0;
        ImageProcessor dst = (channels == 3) ? new ColorProcessor(outw, outh) : new ByteProcessor(outw, outh);
        for(int row=0; row<outhh; row++) {
        	for(int col=0; col<width; col++) {
        		double pcol = col;
        		double prow = row - ((col) * Math.sin(angleValue));
        		byte[] rgb = getNearestPixels(temp, width, height, prow, pcol, true);
        		if((row - offsetY) < 0) continue;
        		index = (row - offsetY) * outw + col;
        		for(int i=0; i<channels; i++) {
        			dst.toByte(i)[index] = (byte)rgb[i];
				}
        	}
        }
        
		// 输出结果
        width = outw;
        height = outh;
        angleValue = ((degree/2.0d)/180.0d) * Math.PI;
        double fullAngleValue = ((degree)/180.0d) * Math.PI;
        outh = height; // big trick 
        outw = (int)(srcWidth * Math.cos(fullAngleValue) 
        		+ srcHeight * Math.sin(fullAngleValue));
        int outww = (int)(width + height * Math.tan(angleValue)); 
        double offsetX = Math.floor((outww - outw)/2.0d + 0.5d);
        temp = (channels == 3) ? new ColorProcessor(outw, outh) : new ByteProcessor(outw, outh);
        index = 0;
        for(int row=0; row<outh; row++) {
        	for(int col=0; col<outww; col++) {
        		double prow = row;
        		double pcol = col + Math.tan(angleValue) * (row - height);
        		byte[] rgb = getNearestPixels(dst, width, height, prow, pcol, false);
        		if(col - offsetX < 0 || col - outw >= offsetX) continue;
        		index = row * outw + (int)(col - offsetX);
        		for(int i=0; i<channels; i++) {
        			temp.toByte(i)[index] = (byte)rgb[i];
				}
        	}
        }
        return temp;
	}
	
	private ImageProcessor rotate90(ImageProcessor processor, float degree) {
		int width = processor.getWidth();
		int height = processor.getHeight();
		int outw = -1;
		int outh = -1;
		int ch = processor.getChannels();
        int index = 0;
        int index2 = 0;
		if(degree == 90)
		{
			outw = height;
			outh = width;
		}
		else if(degree == 180)
		{
			outw = width;
			outh = height;
		}
		else if(degree == 270)
		{
			outw = height;
			outh = width;
		}
		ImageProcessor dst = (ch == 3) ? new ColorProcessor(outw, outh) : new ByteProcessor(outw, outh);

        for(int row=0; row<height; row++) {
        	for(int col=0; col<width; col++) {
        		index = row * width + col;
        		if(degree == 90)
        		{
        			index2 = outw * col + (height - 1- row);
        		}
        		else if(degree == 180)
        		{
        			index2 = outw * (height - 1 - row) + 
        					(width - 1 - col);
        		}
        		else if(degree == 270)
        		{
        			index2 = outw * (width - 1 - col) + row;
        		}
        		for(int i=0; i<ch; i++) {
        			dst.toByte(i)[index2] = processor.toByte(i)[index];
        		}
        	}
        }
        return dst;
	}

	
	private byte[] getNearestPixels(ImageProcessor processor, int width, int height, 
			double prow, double pcol, boolean yshear) {
		double row = Math.floor(prow);
		double col = Math.floor(pcol);
		int ch = processor.getChannels();
		if(row < 0 || row >= height) {
			return (ch == 1) ? new byte[]{(byte)0}: new byte[]{(byte)color.red, (byte)color.green, (byte)color.blue};
		}
		if(col < 0 || col >= width) {
			return (ch == 1) ? new byte[]{(byte)0}: new byte[]{(byte)color.red, (byte)color.green, (byte)color.blue};
		}
		double u = yshear ? prow - row : pcol - col;
		int nextRow = (int)(row + 1);
		int nextCol = (int)(col + 1);
		if((col + 1) >= width) {
			nextCol = (int)col;
		}
		if((row + 1) >= height) {
			nextRow = (int)row;
		}
		int index1 = yshear?(int)(row * width + col) :
			(int)(row * width + col);
		int index2 = yshear?(int)(nextRow * width + col):
			(int)(row * width + nextCol);
		int c1, c2;

		byte[] rgb = new byte[ch];
        for(int i=0; i<ch; i++) {
        	c1 = processor.toByte(i)[index1]&0xff;
        	c2 = processor.toByte(i)[index2]&0xff;
        	rgb[i] = (byte)Tools.clamp(c1 * (1-u) + c2 * u);
        }
        return rgb;
	}
}
