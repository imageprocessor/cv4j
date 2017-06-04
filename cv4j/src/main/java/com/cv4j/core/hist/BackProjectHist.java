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
package com.cv4j.core.hist;

import com.cv4j.core.datamodel.ByteProcessor;
import com.cv4j.core.datamodel.ColorProcessor;
import com.cv4j.core.datamodel.ImageProcessor;
import com.cv4j.image.util.Tools;

import java.util.Arrays;

public class BackProjectHist {

    public void backProjection(ColorProcessor src, ByteProcessor backProjection, int bins, int[] histData) {
        CalcHistogram calcHist = new CalcHistogram();
        int[][] hist = new int[3][bins];
        calcHist.calcHSVHist(src, bins, hist, true, new int[][]{{0, 180},{0,256},{0,256}});
        byte[] R = src.getRed();
        byte[] G = src.getGreen();
        byte[] B = src.getBlue();
        byte[][] hsv = new byte[3][R.length];
        Tools.rgb2hsv(new byte[][]{R, G, B}, hsv);
        byte[] data = hsv[0]; // H channel...
        byte[] bp = backProjection.getGray();
        int width = src.getWidth();
        int height = src.getHeight();
        int offset = 0;

        // setup look up table
        float delta = (180.0f) / bins;
        int[] lutHist = new int[180];
        for (int i = 0; i < 180; i++) {
            int hidx = (int) (i / delta);
            if (hidx < bins)
                lutHist[i] = hist[0][hidx];
        }

        // back project stage
        Arrays.fill(bp, (byte) 0);
        for (int row = 0; row < height; row++) {
            int t0 = 0, t1 = 0;
            offset = row * width;
            for (int x = 0; x < width - 4; x += 4) {
                t0 = lutHist[data[offset + x] & 0xff];
                t1 = lutHist[data[offset + x + 1] & 0xff];
                bp[offset + x] = (byte) t0;
                bp[offset + x + 1] = (byte) t1;

                t0 = lutHist[data[offset + x + 2] & 0xff];
                t1 = lutHist[data[offset + x + 3] & 0xff];
                bp[offset + x + 2] = (byte) t0;
                bp[offset + x + 3] = (byte) t1;
            }
        }
        backProjection.putGray(bp);

    }
}
