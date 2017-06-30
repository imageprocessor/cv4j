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

import com.cv4j.core.datamodel.ByteProcessor;
import com.cv4j.core.datamodel.ColorProcessor;
import com.cv4j.core.datamodel.ImageData;
import com.cv4j.core.datamodel.ImageProcessor;
import com.cv4j.image.util.Tools;

/**
 * algorithm -http://en.wikipedia.org/wiki/Floyd%E2%80%93Steinberg_dithering
 * http://en.literateprograms.org/Floyd-Steinberg_dithering_(C)
 * ******Floyd Steinberg dithering*******
 * * 0       0,       0*
 * * 0       P     7/16*
 * * 3/16, 5/16,   1/16*
 * *************************
 * * 0        0           0*
 * * 0        *      0.4375*
 * * 0.1875, 0.3125, 0.0625*
 * *************************
 *
 *
 */
public class FloSteDitheringFilter implements CommonFilter {

	public final static float[] kernelData = new float[]{0.1875f, 0.3125f, 0.0625f, 0.4375f};
	public final static int[] COLOR_PALETTE = new int[] {0, 255};

	@Override
	public ImageProcessor filter(ImageProcessor src) {

        if(src instanceof ColorProcessor) {
            src.getImage().convert2Gray();
            src = src.getImage().getProcessor();
        }

		int width = src.getWidth();
        int height = src.getHeight();
        byte[] GRAY = ((ByteProcessor)src).getGray();
        byte[] output = new byte[GRAY.length];

        int gray = 0;
        int err = 0;
        for(int row=0; row<height; row++) {
            int offset = row*width;
        	for(int col=0; col<width; col++) {
                gray = GRAY[offset]&0xff;
                int cIndex = getCloseColor(gray);
                output[offset] = (byte)COLOR_PALETTE[cIndex];
                int er = gray - COLOR_PALETTE[cIndex];
                int k = 0;
                
                if(row + 1 < height && col - 1 > 0) {
                	k = (row + 1) * width + col - 1;
                    err = GRAY[k]&0xff;
                    err += (int)(er * kernelData[0]);
                    GRAY[k] = (byte)Tools.clamp(err);
                }
                
                if(col + 1 < width) {
                	k = row * width + col + 1;
                    err = GRAY[k]&0xff;
                    err += (int)(er * kernelData[3]);
                    GRAY[k] = (byte)Tools.clamp(err);
                }
                
                if(row + 1 < height) {
                	k = (row + 1) * width + col;
                    err = GRAY[k]&0xff;
                    err += (int)(er * kernelData[1]);
                    GRAY[k] = (byte)Tools.clamp(err);
                }
                
                if(row + 1 < height && col + 1 < width) {
                	k = (row + 1) * width + col + 1;
                    err = GRAY[k]&0xff;
                    err += (int)(er * kernelData[2]);
                    GRAY[k] = (byte)Tools.clamp(err);
                }
                offset++;
        	}
        }
        ((ByteProcessor)src).putGray(GRAY);
        GRAY = null;
        return src;
	}
	
	private int getCloseColor(int gray) {
		int minDistanceSquared = 255*255 + 1;
		int bestIndex = 0;
		for(int i=0; i<COLOR_PALETTE.length; i++) {
			int diff = Math.abs(gray - COLOR_PALETTE[i]);
			if(ImageData.SQRT_LUT[diff] < minDistanceSquared) {
				minDistanceSquared = ImageData.SQRT_LUT[diff];
				bestIndex = i;
			}
		}
		return bestIndex;
	}
}
