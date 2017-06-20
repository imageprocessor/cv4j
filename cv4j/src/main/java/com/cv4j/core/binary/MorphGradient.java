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
package com.cv4j.core.binary;

import com.cv4j.core.datamodel.ByteProcessor;
import com.cv4j.core.datamodel.Size;
import com.cv4j.exception.CV4JException;

public class MorphGradient {

    public static final int INTERNAL_GRADIENT = 1;
    public static final int EXTERNAL_GRADIENT = 2;
    public static final int BASIC_GRADIENT = 3;

    /***
     *
     * @param gray
     * @param structureElement - 3, 5, 7 must be odd
     * @param gradientType
     */
    public void process(ByteProcessor gray, Size structureElement, int gradientType) {
        int width = gray.getWidth();
        int height = gray.getHeight();
        byte[] ero = new byte[width*height];
        byte[] dil = new byte[width*height];
        byte[] data = new byte[width*height];
        System.arraycopy(gray.getGray(), 0, data, 0, data.length);

        // X Direction
        int xr = structureElement.cols/2;
        int min = 0, max = 0;
        System.arraycopy(data, 0, ero, 0, data.length);
        System.arraycopy(data, 0, dil, 0, data.length);
        int offset = 0;
        for(int row=0; row<height; row++) {
            // find min and max for input array
            offset = row*width;
            for(int col=0; col<width; col++) {
                min = 256;
                max = 0;
                for(int i=-xr; i<=xr; i++) {
                    if(i == 0) continue;
                    if((offset+col+i) < 0 || (offset+col+i) >= width) {
                        continue;
                    }
                    min = Math.min(min, data[offset+col+i]&0xff);
                    max = Math.max(max, data[offset+col+i]&0xff);
                }
                ero[offset+col] = (byte)min;
                dil[offset+col] = (byte)max;
            }
        }

        // Y Direction
        System.arraycopy(ero, 0, data, 0, data.length);
        for(int col=0; col<width; col++) {
            for(int row=0; row<height; row++) {
                // find min for input array
                min = 256;
                for(int i=-xr; i<=xr; i++) {
                    if(i == 0) continue;
                    if((row+i) < 0 || (row+i) >= height) {
                        continue;
                    }
                    offset = (row+i)*width;
                    min = Math.min(min, data[offset+col]&0xff);
                }
                ero[row*width+col] = (byte)min;
            }
        }

        System.arraycopy(dil, 0, data, 0, data.length);
        for(int col=0; col<width; col++) {
            for(int row=0; row<height; row++) {
                // find max for input array
                max = 0;
                for(int i=-xr; i<=xr; i++) {
                    if(i == 0) continue;
                    if((row+i) < 0 || (row+i) >= height) {
                        continue;
                    }
                    offset = (row+i)*width;
                    max = Math.max(max, data[offset+col]&0xff);
                }
                dil[row*width+col] = (byte)max;
            }
        }

        // calculate gradient
        int c = 0;
        if(gradientType == BASIC_GRADIENT) {
            for(int i=0; i<data.length; i++) {
                c = (dil[i]&0xff - ero[i]&0xff);
                data[i] = (byte) ((c > 0) ? 255 : 0);
            }
            gray.putGray(data);
        }
        else if(gradientType == EXTERNAL_GRADIENT) {
            data = gray.getGray();
            for(int i=0; i<data.length; i++) {
                data[i] = (byte)(dil[i]&0xff - data[i]&0xff);
            }
        }
        else if(gradientType == INTERNAL_GRADIENT) {
            data = gray.getGray();
            for(int i=0; i<data.length; i++) {
                data[i] = (byte)(data[i]&0xff - ero[i]&0xff);
            }
        } else {
            throw new CV4JException("Unknown Gradient type, not supported...");
        }
    }
}
