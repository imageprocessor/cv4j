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
package com.cv4j.core.hist;

import com.cv4j.core.datamodel.ColorProcessor;
import com.cv4j.core.datamodel.ImageProcessor;
import com.cv4j.image.util.Tools;

public class CalcHistogram {
    public final static int COLOR_RGB = 1;
    public final static int COLOR_HSV = 2;

    public void calcRGBHist(ImageProcessor src, int bins, int[][] hist, boolean norm) {

        if (src == null) return;

        int numChannels = src.getChannels();
        for(int i=0; i<numChannels; i++) {
            byte[] data = src.toByte(i);
            hist[i] = getHistogram(data, bins, new int[]{0, 256});
        }
        if(!norm) return;

        float min = 10000000, max = 0;
        float delta;
        for(int i=0; i<numChannels; i++) {
            for(int j=0; j<bins; j++) {
                min = Math.min(hist[i][j], min);
                max = Math.max(hist[i][j], max);
            }
            delta = max -min;
            for(int j=0; j<bins; j++) {
                hist[i][j] = (int)(((hist[i][j] - min)/delta)*255);
            }
        }
    }

    public void calcHSVHist(ImageProcessor src, int bins, int[][] hist, boolean norm) {
        calcHSVHist(src,bins,hist,norm,new int[][]{{0, 180},{0,256},{0,256}});
    }

    public void calcHSVHist(ImageProcessor src, int bins, int[][] hist, boolean norm, int[][] ranges) {

        if (src == null) return;

        if(src.getChannels() == 1) {
            calcRGBHist(src,bins,hist,norm);
            return;
        }

        int width = src.getWidth();
        int height = src.getHeight();
        byte[][] hsv = new byte[3][width*height];
        byte[] R = ((ColorProcessor)src).getRed();
        byte[] G = ((ColorProcessor)src).getGreen();
        byte[] B = ((ColorProcessor)src).getBlue();
        Tools.rgb2hsv(new byte[][]{R, G, B}, hsv);
        for(int i=0; i<3; i++) {
            byte[] data = hsv[i];
            hist[i] = getHistogram(data, bins, ranges[i]);
        }
        if(!norm) return;

        float min = 10000000, max = 0;
        float delta;
        for(int i=0; i<3; i++) {
            for(int j=0; j<bins; j++) {
                min = Math.min(hist[i][j], min);
                max = Math.max(hist[i][j], max);
            }
            delta = max -min;
            int dr = ranges[i][1] - ranges[i][0];
            for(int j=0; j<bins; j++) {
                hist[i][j] = (int)(((hist[i][j] - min)/delta)*dr);
            }
        }
    }

    private int[] getHistogram(byte[] data, int bins, int[] range) {
        int dr = range[1] - range[0];
        int[] hist = new int[dr];
        int length = data.length;
        for(int i=0; i<length; i++) {
            hist[data[i]&0xff]++;
        }

        double numOfGap = dr/bins;
        int[] wh = new int[bins];

        double prebin;
        double currbin;
        double w1;
        double w2;
        for(int k=0; k<bins; k++) {
            prebin = (k-1)*numOfGap;
            currbin = k*numOfGap;
            int obin = (int)Math.floor(prebin);
            if(obin < 0) {
                obin = 0;
                prebin = 0;
            }
            int nbin = (int)Math.floor(currbin);

            for(int j=obin; j<=nbin; j++) {
                wh[k] += hist[j];
            }

            w1 = prebin - obin;
            w2 = currbin - nbin;
            if(w1 > 0 && w1 < 1) {
                wh[k] = (int)(wh[k] - hist[obin]*w1);
            }
            if(w2 > 0 && w2 < 1) {
                wh[k] = (int)(wh[k] + hist[nbin+1]*w2);
            }
        }
        return wh;
    }

}
