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
package com.cv4j.core.binary;

import com.cv4j.core.datamodel.ByteProcessor;
import com.cv4j.core.datamodel.Size;

public class FastDilate {

    /**
     *
     * @param binary
     * @param structureElement
     * @param iteration  number of times
     */
    public void process(ByteProcessor binary, Size structureElement, int iteration) {
        int width = binary.getWidth();
        int height = binary.getHeight();
        int size = width*height;
        byte[] data = binary.getGray();
        byte[] output = new byte[size];
        System.arraycopy(data, 0, output, 0, size);

        //TODO: This place can use multi-thread.

        // X Direction
        int xr = structureElement.cols/2;
        byte c = (byte)0;
        int offset = 0;
        for(int row=0; row<height; row++) {
            for(int col=0; col<width; col++) {
                c = data[row*width+col];
                if((c&0xff) == 255)continue;
                for(int x=-xr; x<=xr; x++) {
                    if(x==0)continue;
                    offset = x + col;
                    if(offset < 0) {
                        offset = 0;
                    }
                    if(offset >= width) {
                        offset = width - 1;
                    }
                    c |=data[row*width+offset];
                }

                // TODO: this seems never happen
                if(c == 255){
                    output[row*width+col] = (byte)255;
                }
            }
        }
        System.arraycopy(output, 0, data, 0, size);

        // Y Direction
        int yr = structureElement.rows/2;
        c = 0;
        offset = 0;
        for(int col=0; col<width; col++) {
            for(int row=0; row<height; row++) {
                c = data[row*width+col];
                if((c&0xff) == 255)continue;
                for(int y=-yr; y<=yr; y++) {
                    if(y == 0)continue;
                    offset = y + row;
                    if(offset < 0) {
                        offset = 0;
                    }
                    if(offset >= height) {
                        offset = height - 1;
                    }
                    c |=data[offset*width+col];
                }
                if(c == 255){
                    output[row*width+col] = (byte)255;
                }
            }
        }
        System.arraycopy(output, 0, data, 0, size);
    }
}
