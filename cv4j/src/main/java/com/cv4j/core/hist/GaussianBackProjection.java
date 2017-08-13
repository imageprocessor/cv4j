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

import com.cv4j.core.datamodel.ByteProcessor;
import com.cv4j.core.datamodel.ImageProcessor;
import com.cv4j.exception.CV4JException;
import com.cv4j.image.util.Tools;

public class GaussianBackProjection {

    public void backProjection(ImageProcessor src, ImageProcessor model, ByteProcessor dst) {
        if(src.getChannels() == 1 || model.getChannels() == 1) {
            throw new CV4JException("did not support image type : single-channel...");
        }
        float[] R = model.toFloat(0);
        float[] G = model.toFloat(1);
        int r = 0, g = 0, b = 0;
        float sum = 0;
        int mw = model.getWidth();
        int mh = model.getHeight();
        int index = 0;
        for (int row = 0; row < mh; row++) {
            for (int col = 0; col < mw; col++) {
                index = row*mw + col;
                b = model.toByte(2)[index]&0xff;
                g = model.toByte(1)[index]&0xff;
                r = model.toByte(0)[index]&0xff;
                sum = b + g + r;
                R[index] = r / sum;
                G[index] = g / sum;
            }
        }

        // 计算均值与标准方差
        float[] rmdev = Tools.calcMeansAndDev(R);
        float[] gmdev = Tools.calcMeansAndDev(G);

        int width = src.getWidth();
        int height = src.getHeight();

        // 反向投影
        float pr = 0, pg = 0;
        float[] result = new float[width*height];
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                index = row*width + col;
                b = src.toByte(2)[index]&0xff;
                g = src.toByte(1)[index]&0xff;
                r = src.toByte(0)[index]&0xff;
                sum = b + g + r;
                float red = r / sum;
                float green = g / sum;
                pr = (float)((1.0 / (rmdev[1]*Math.sqrt(2 * Math.PI)))*Math.exp(-(Math.pow((red - rmdev[0]), 2)) / (2 * Math.pow(rmdev[1], 2))));
                pg = (float)((1.0 / (gmdev[1]*Math.sqrt(2 * Math.PI)))*Math.exp(-(Math.pow((green - gmdev[0]),2)) / (2 * Math.pow(gmdev[1], 2))));
                sum = pr*pg;

                if(Float.isNaN(sum)){
                    result[index] = 0;
                    continue;
                }

                result[index] = sum;

            }
        }

        // 归一化显示高斯反向投影
        float min = 1000;
        float max = 0;
        for(int i=0; i<result.length; i++) {
            min = Math.min(min, result[i]);
            max = Math.max(max, result[i]);
        }

        float delta = max - min;
        for(int i=0; i<result.length; i++) {
            dst.getGray()[i] =  (byte)(((result[i] - min)/delta)*255);
        }
    }
}
