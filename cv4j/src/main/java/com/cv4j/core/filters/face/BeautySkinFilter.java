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
package com.cv4j.core.filters.face;

import com.cv4j.core.binary.Erode;
import com.cv4j.core.datamodel.ByteProcessor;
import com.cv4j.core.datamodel.ImageProcessor;
import com.cv4j.core.datamodel.IntIntegralImage;
import com.cv4j.core.datamodel.Size;
import com.cv4j.core.filters.CommonFilter;
import com.cv4j.core.filters.FastEPFilter;
import com.cv4j.core.filters.GradientFilter;

import java.util.Arrays;

public class BeautySkinFilter implements CommonFilter {

    @Override
    public ImageProcessor filter(ImageProcessor src) {
        int width = src.getWidth();
        int height = src.getHeight();
        int length = width * height;
        byte[] R = new byte[length];
        byte[] G = new byte[length];
        byte[] B = new byte[length];
        System.arraycopy(src.toByte(0), 0, R, 0, R.length);
        System.arraycopy(src.toByte(1), 0, G, 0, G.length);
        System.arraycopy(src.toByte(2), 0, B, 0, B.length);

        FastEPFilter epFilter = new FastEPFilter();
        epFilter.filter(src);

        ISkinDetection skinDetector = new DefaultSkinDetection();
        byte[] mask = new byte[length];
        Arrays.fill(mask, (byte) 0);
        int r = 0, g = 0, b = 0;
        for (int i = 0; i < R.length; i++) {
            r = R[i] & 0xff;
            g = G[i] & 0xff;
            b = B[i] & 0xff;
            if (!skinDetector.isSkin(r, g, b)) {
                mask[i] = (byte) 255;
            }
        }
        Erode erode = new Erode();
        erode.process(new ByteProcessor(mask, width, height), new Size(5));
        for (int i = 0; i < mask.length; i++) {
            int c = mask[i] & 0xff;
            if (c > 0) {
                src.toByte(0)[i] = R[i];
                src.toByte(1)[i] = G[i];
                src.toByte(2)[i] = B[i];
            }
        }

        int c = 0;
        for (int i = 0; i < R.length; i++) {
            r = R[i] & 0xff;
            g = G[i] & 0xff;
            b = B[i] & 0xff;
            c = (int) (0.299 * r + 0.587 * g + 0.114 * b);
            mask[i] = (byte) c;
        }

        GradientFilter gradientFilter = new GradientFilter();
        int[] gradient = gradientFilter.gradient(new ByteProcessor(mask, width, height));
        Arrays.fill(mask, (byte) 0);
        for (int i = 0; i < R.length; i++) {
            if (gradient[i] > 35) {
                mask[i] = (byte) 255;
            }
        }
        gradient = null;

        // 遮罩层模糊
        IntIntegralImage ii = new IntIntegralImage();
        ii.setImage(mask);
        ii.calculate(width, height);
        byte[] blurmask = new byte[mask.length];
        int x2 = 0, y2 = 0;
        int x1 = 0, y1 = 0;
        int cx = 0, cy = 0;
        for (int row = 0; row < height - 1; row++) {
            y2 = (row + 1)>height ? height : (row + 1);
            y1 = (row - 3) < 0 ? 0 : (row - 3);
            for (int col = 0; col < width - 1; col++) {
                x2 = (col + 1)>width ? width : (col + 1);
                x1 = (col - 3) < 0 ? 0 : (col - 3);
                cx = (col - 1) < 0 ? 0 : col - 1;
                cy = (row - 1) < 0 ? 0 : row - 1;
                int sr = ii.getBlockSum(x1, y1, x2, y2);
                blurmask[cx*width + cy] = (byte) (sr / 25);
            }
        }

        // alpha blend
        mask = null;
        ii = null;
        float w = 0.0f;
        int wc = 0;
        for (int i = 0; i < blurmask.length; i++) {
            wc = blurmask[i] & 0xff;
            w = wc / 255.0f;

            r = (int) ((R[i] & 0xff) * w + (src.toByte(0)[i] & 0xff) * (1.0f - w));
            g = (int) ((G[i] & 0xff) * w + (src.toByte(1)[i] & 0xff) * (1.0f - w));
            b = (int) ((B[i] & 0xff) * w + (src.toByte(2)[i] & 0xff) * (1.0f - w));

            src.toByte(0)[i] = (byte) r;
            src.toByte(1)[i] = (byte) g;
            src.toByte(2)[i] = (byte) b;
        }
        R = null;
        G = null;
        B = null;
        blurmask = null;
        return src;
    }
}
