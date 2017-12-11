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

public class Flip {

	public final static int FLIP_VERTICAL = -1;
	public final static int FLIP_HORIZONTAL = 1;
	
	public static void flip(ImageProcessor processor, int option) {
		int width = processor.getWidth();
        int height = processor.getHeight();
        int ch = processor.getChannels();
        int index1 = 0;
        int index2 = 0;
        int total = width*height;
        byte[][] output = new byte[ch][total];
        for(int row=0; row<height; row++) {
        	for(int col=0; col<width; col++) {
        		index1 = row*width+col;
        		if(option == FLIP_HORIZONTAL) {
        			index2 = row*width + width-col-1;
        		} else if(option == FLIP_VERTICAL){
        			index2 = (height-row-1)*width + col;
        		} else {
        			throw new IllegalArgumentException("invalid option : " + option);
        		}
        		for(int i=0; i<ch; i++) {
        			output[i][index2] = processor.toByte(i)[index1];
        		}
        	}
        }
        if(ch == 3) {
        	((ColorProcessor) processor).putRGB(output[0], output[1], output[2]);
        } else {
        	((ByteProcessor) processor).putGray(output[0]);
        }
	}

}
