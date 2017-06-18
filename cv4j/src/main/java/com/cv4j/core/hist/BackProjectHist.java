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
import com.cv4j.core.datamodel.ColorProcessor;
import com.cv4j.core.datamodel.ImageProcessor;

public class BackProjectHist {

    public void backProjection(ImageProcessor src, ByteProcessor backProjection, int[] mHist, int bins) {

        int width = src.getWidth();
        int height = src.getHeight();
        byte[] R = ((ColorProcessor)src).getRed();
        byte[] G = ((ColorProcessor)src).getGreen();
        byte[] B = ((ColorProcessor)src).getBlue();

        // 计算直方图
        int[] iHist = CalcHistogram.calculateNormHist(src, bins);

        // 计算比率脂肪图 R
        float[] rHist = new float[iHist.length];
        for(int i=0; i<iHist.length; i++) {
            float a = mHist[i];
            float b = iHist[i];
            rHist[i] = a / b;
        }

        // 根据像素值查找R，得到分布概率权重
        int index = 0;
        int bidx = 0;
        int tr=0, tg=0, tb=0;
        int level = 256 / bins;
        float[] rimage = new float[width*height];
        for(int row=0; row<height; row++) {
            for(int col=0; col<width; col++) {
                index = row * width + col;
                tr = R[index]&0xff;
                tg = G[index]&0xff;
                tb = B[index]&0xff;
                bidx = (tr / level) + (tg / level)*bins + (tb / level)*bins*bins;
                rimage[index] = Math.min(1, rHist[bidx]);
            }
        }

        // 计算卷积
        int offset = 0;
        float sum = 0;
        float[] output = new float[width*height];
        System.arraycopy(rimage, 0, output, 0, output.length);
        for(int row=1; row<height-1; row++) {
            offset = width * row;
            for(int col=1; col<width-1; col++) {
                sum += rimage[offset+col];
                sum += rimage[offset+col-1];
                sum += rimage[offset+col+1];
                sum += rimage[offset+width+col];
                sum += rimage[offset+width+col-1];

                sum += rimage[offset+width+col+1];
                sum += rimage[offset-width+col];
                sum += rimage[offset-width+col-1];
                sum += rimage[offset-width+col+1];
                output[offset+col] = sum / 9.0f;
                sum = 0f; // for next
            }
        }

        // 归一化
        float min = 1000;
        float max = 0;
        for(int i=0; i<output.length; i++) {
            min = Math.min(min, output[i]);
            max = Math.max(max, output[i]);
        }
        float delta = max - min;
        for(int i=0; i<output.length; i++) {
            output[i] =  ((output[i] - min)/delta)*255;
        }

        // 阈值二值化显示
        for(int i=0; i<output.length; i++) {
            int pv = (int)output[i];
            if(pv < 50) pv = 0;
            backProjection.getGray()[i] = (byte)pv;
        }

    }
}
