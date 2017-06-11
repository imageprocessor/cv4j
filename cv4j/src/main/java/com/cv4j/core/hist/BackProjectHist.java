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
import com.cv4j.image.util.Tools;

import java.util.Arrays;

public class BackProjectHist {

    public void backProjection(ByteProcessor src, ByteProcessor backProjection, int[] hist, int[] ranges) {
        int bins = hist.length;
        int dr = ranges[1] - ranges[0] + 1;
        double gap = dr / bins;
        byte[] input = src.getGray();
        byte[] output = backProjection.getGray();
        int w = src.getWidth();
        int h = src.getHeight();

        int[] lutHist = new int[dr];
        for (int i = 0; i < dr; i++) {
            int hidx = (int) (i / gap);
            if (hidx < bins)
                lutHist[i] = hist[hidx];
        }

        int index = 0;
        int pv = 0;
        for(int row=0; row<h; row++) {
            for(int col=0; col<w; col++) {
                index = row*w + col;
                pv = input[index]&0xff;
                output[index] = (byte)lutHist[pv];
            }
        }
    }
}
