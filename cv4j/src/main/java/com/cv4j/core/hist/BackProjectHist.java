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

import java.util.Arrays;

public class BackProjectHist {

    public void backProjection(ByteProcessor src, ByteProcessor backProjection, int bins, int[] histData, int[] range) {
        CalcHistogram calcHist = new CalcHistogram();
        int[][] hist = new int[1][bins];
        calcHist.calcHSVHist(src, bins, hist, true);
        byte[] data = src.getGray();
        byte[] bp = backProjection.getGray();
        int width = src.getWidth();
        int height = src.getHeight();
        int offset = 0;

        // setup look up table
        float delta = 256.0f / bins;
        int[] lutHist = new int[256];
        for (int i = 0; i < 256; i++) {
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
