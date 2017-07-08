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

import com.cv4j.core.datamodel.ImageProcessor;
import com.cv4j.core.datamodel.lut.LUT;

public class ColorFilter extends BaseFilter {

    public static final int AUTUMN_STYLE = 0;
    public static final int BONE_STYLE = 1;
    public static final int COOL_STYLE = 2;
    public static final int HOT_STYLE = 3;
    public static final int HSV_STYLE = 4;
    public static final int JET_STYLE = 5;
    public static final int OCEAN_STYLE = 6;
    public static final int PINK_STYLE = 7;
    public static final int RAINBOW_STYLE = 8;
    public static final int SPRING_STYLE = 9;
    public static final int SUMMER_STYLE = 10;
    public static final int WINTER_STYLE = 11;

    private int style;

    public ColorFilter() {
        style = AUTUMN_STYLE;
    }

    public void setStyle(int style) {
        this.style = style;
    }

    @Override
    public ImageProcessor doFilter(ImageProcessor src) {

        int tr=0;
        int tg=0;
        int tb=0;
        int[][] lut = getStyleLUT(style);
        int size = R.length;
        for(int i=0; i<size; i++) {
            tr = R[i] & 0xff;
            tg = G[i] & 0xff;
            tb = B[i] & 0xff;

            R[i] = (byte)lut[tr][0];
            G[i] = (byte)lut[tg][1];
            B[i] = (byte)lut[tb][2];
        }
        return src;
    }

    private int[][] getStyleLUT(int style) {
        return LUT.getColorFilterLUT(style);
    }
}
