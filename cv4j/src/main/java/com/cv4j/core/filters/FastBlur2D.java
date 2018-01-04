/*
 * Copyright (c) 2018 - present, CV4J Contributors.
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

import com.cv4j.core.datamodel.ImageProcessor;
import com.cv4j.core.datamodel.IntIntegralImage;
import com.cv4j.image.util.Tools;

public class FastBlur2D extends BaseFilter {

    private int ksize;
    public FastBlur2D() {
        ksize = 3;
    }

    public FastBlur2D(int ksize) {
        this.ksize = ksize;
    }

    @Override
    public ImageProcessor doFilter(ImageProcessor src) {
        byte[] output = new byte[width*height];
        IntIntegralImage ii = new IntIntegralImage();
        for(int i=0; i<src.getChannels(); i++) {
            System.arraycopy(src.toByte(i), 0, output, 0, output.length);
            ii.setImage(src.toByte(i));
            ii.calculate(width, height, true);
            processSingleChannel(width, height, ii, output);
            System.arraycopy(output, 0, src.toByte(i), 0, output.length);
        }
        return src;
    }

    public void processSingleChannel(int w, int h, IntIntegralImage ii, byte[] output) {
        int radius = ksize / 2;
        int x2 = 0, y2 = 0;
        int x1 = 0, y1 = 0;
        int cx = 0, cy = 0;
        for (int row = 0; row < h + radius; row++) {
            y2 = (row + 1)>h ? h : (row + 1);
            y1 = (row - ksize) < 0 ? 0 : (row - ksize);
            for (int col = 0; col < w + radius; col++) {
                x2 = (col + 1)>w ? w : (col + 1);
                x1 = (col - ksize) < 0 ? 0 : (col - ksize);
                cx = (col - radius) < 0 ? 0 : col - radius;
                cy = (row - radius) < 0 ? 0 : row - radius;
                int num = (x2 - x1)*(y2 - y1);
                int s = ii.getBlockSum(x1, y1, x2, y2);
                output[cy*w+cx] = (byte) Tools.clamp(s/num);
            }
        }
    }
}
