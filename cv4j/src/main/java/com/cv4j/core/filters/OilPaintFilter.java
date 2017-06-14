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

import com.cv4j.core.datamodel.ColorProcessor;
import com.cv4j.core.datamodel.ImageProcessor;

public class OilPaintFilter extends BaseFilter {

    private int radius = 15; // default value
    private int intensity = 40; // default value

    public OilPaintFilter() {
        this(15, 40);
    }

    public OilPaintFilter(int radius, int graylevel) {
        this.radius = radius;
        this.intensity = graylevel;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public int getIntensity() {
        return intensity;
    }

    public void setIntensity(int intensity) {
        this.intensity = intensity;
    }

    @Override
    public ImageProcessor doFilter(ImageProcessor src) {

        byte[][] output = new byte[3][R.length];

        int index = 0;
        int subradius = this.radius / 2;
        int[] intensityCount = new int[intensity+1];
        int[] ravg = new int[intensity+1];
        int[] gavg = new int[intensity+1];
        int[] bavg = new int[intensity+1];

        for(int i=0; i<=intensity; i++) {
            intensityCount[i] = 0;
            ravg[i] = 0;
            gavg[i] = 0;
            bavg[i] = 0;
        }

        for(int row=0; row<height; row++) {
            int ta = 0, tr = 0, tg = 0, tb = 0;
            for(int col=0; col<width; col++) {

                for(int subRow = -subradius; subRow <= subradius; subRow++)
                {
                    for(int subCol = -subradius; subCol <= subradius; subCol++)
                    {
                        int nrow = row + subRow;
                        int ncol = col + subCol;
                        if(nrow >=height || nrow < 0)
                        {
                            nrow = 0;
                        }
                        if(ncol >= width || ncol < 0)
                        {
                            ncol = 0;
                        }
                        index = nrow * width + ncol;
                        tr = R[index] & 0xff;
                        tg = G[index] & 0xff;
                        tb = B[index] & 0xff;
                        int curIntensity = (int)(((double)((tr+tg+tb)/3)*intensity)/255.0f);
                        intensityCount[curIntensity]++;
                        ravg[curIntensity] += tr;
                        gavg[curIntensity] += tg;
                        bavg[curIntensity] += tb;
                    }
                }

                // find the max number of same gray level pixel
                int maxCount = 0, maxIndex = 0;
                for(int m=0; m<intensityCount.length; m++)
                {
                    if(intensityCount[m] > maxCount)
                    {
                        maxCount = intensityCount[m];
                        maxIndex = m;
                    }
                }

                // get average value of the pixel
                int nr = ravg[maxIndex] / maxCount;
                int ng = gavg[maxIndex] / maxCount;
                int nb = bavg[maxIndex] / maxCount;
                index = row * width + col;
                output[0][index] = (byte) nr;
                output[1][index] = (byte) ng;
                output[2][index] = (byte) nb;

                // post clear values for next pixel
                for(int i=0; i<=intensity; i++)
                {
                    intensityCount[i] = 0;
                    ravg[i] = 0;
                    gavg[i] = 0;
                    bavg[i] = 0;
                }

            }
        }

        ((ColorProcessor) src).putRGB(output[0], output[1], output[2]);
        output = null;

        return src;
    }
}
