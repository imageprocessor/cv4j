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
import com.cv4j.core.datamodel.IntIntegralImage;

public class OilPaintFilter extends BaseFilter {

    private int ksize = 15; // default value
    private float intensity = 30; // default value

    public OilPaintFilter() {
        this(15, 40);
    }

    public OilPaintFilter(int radius, int graylevel) {
        this.ksize = radius;
        this.intensity = graylevel;
    }

    public int getBlockSize() {
        return ksize;
    }

    public void setBlockSize(int ksize) {
        this.ksize = ksize;
    }

    public float getIntensity() {
        return intensity;
    }

    public void setIntensity(int intensity) {
        this.intensity = intensity;
    }

    @Override
    public ImageProcessor doFilter(ImageProcessor src) {
        // initialization parameters
        int w = src.getWidth();
        int h = src.getHeight();

        // start ep process
        IntIntegralImage redii = new IntIntegralImage();
        IntIntegralImage greenii = new IntIntegralImage();
        IntIntegralImage blueii = new IntIntegralImage();

        redii.setImage(src.toByte(0));
        greenii.setImage(src.toByte(1));
        blueii.setImage(src.toByte(2));

        redii.calculate(w, h);
        greenii.calculate(w, h);
        blueii.calculate(w, h);

        int x2 = 0, y2 = 0;
        int x1 = 0, y1 = 0;
        int cx = 0, cy = 0;
        int radius = ksize / 2;
        int sr=0, sg= 0, sb=0;
        int r = 0, g = 0, b = 0;
        float mr=0, mg=0, mb=0;
        byte[][] output = new byte[3][R.length];
        int pv = 0;
        int index = 0;
        for (int row = 0; row < h  + radius; row++) {
            y2 = (row + 1)>h ? h : (row + 1);
            y1 = (row - ksize) < 0 ? 0 : (row - ksize);
            for (int col = 0; col < w + radius; col++) {
                x2 = (col + 1)>w ? w : (col + 1);
                x1 = (col - ksize) < 0 ? 0 : (col - ksize);
                cx = (col - radius) < 0 ? 0 : col - radius;
                cy = (row - radius) < 0 ? 0 : row - radius;
                int num = (x2 - x1)*(y2 - y1);
                sr = redii.getBlockSum(x1, y1, x2, y2);
                sg = greenii.getBlockSum(x1, y1, x2, y2);
                sb = blueii.getBlockSum(x1, y1, x2, y2);
                mr = sr / num;
                mg = sg / num;
                mb = sb / num;
                index = cy*w+cx;
                r = src.toByte(0)[index]&0xff;
                g = src.toByte(1)[index]&0xff;
                b = src.toByte(2)[index]&0xff;
                float delta = (mr+mg+mb) - (r+g+b);
                if(delta > intensity) {
                    output[0][index] = (byte)r;
                    output[1][index] = (byte)g;
                    output[2][index] = (byte)b;
                } else {
                    output[0][index] = (byte)mr;
                    output[1][index] = (byte)mg;
                    output[2][index] = (byte)mb;
                }
            }
        }
        // release memory
        ((ColorProcessor) src).putRGB(output[0], output[1], output[2]);
        output = null;
        return src;
    }
}
