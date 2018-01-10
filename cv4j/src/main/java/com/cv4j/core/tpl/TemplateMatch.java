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
package com.cv4j.core.tpl;

import com.cv4j.core.datamodel.ByteProcessor;
import com.cv4j.core.datamodel.ColorProcessor;
import com.cv4j.core.datamodel.FloatProcessor;
import com.cv4j.core.datamodel.ImageProcessor;
import com.cv4j.core.datamodel.IntIntegralImage;
import com.cv4j.core.datamodel.Point;
import com.cv4j.image.util.Tools;

import java.util.Arrays;
import java.util.List;

public class TemplateMatch {

    public FloatProcessor match(ByteProcessor target, ByteProcessor tpl, int method) {
        int w = target.getWidth();
        int h = target.getHeight();
        int tw = tpl.getWidth();
        int th = tpl.getHeight();
        int offx = tpl.getWidth()/2+1;
        int offy = tpl.getHeight()/2+1;
        int raidus_x = tpl.getWidth() / 2;
        int raidus_y = tpl.getHeight()/2;
        int rw = w - offx*2;
        int rh = h - offy*2;
        float[] result = new float[w*h];
        IntIntegralImage ii = new IntIntegralImage();
        ii.setImage(target.getGray());
        float sum2 = calculateSum2(tpl);
        ii.calculate(w, h);
        if(target.getChannels() == 1 && tpl.getChannels() == 1) {
            int x2 = 0, y2 = 0;
            int x1 = 0, y1 = 0;
            int cx = 0, cy = 0;
            for (int row = 0; row < h + raidus_y; row++) {
                y2 = (row + 1)>h ? h : (row + 1);
                y1 = (row - th) < 0 ? 0 : (row - th);
                for (int col = 0; col < w + raidus_x; col++) {
                    x2 = (col + 1)>w ? w : (col + 1);
                    x1 = (col - tw) < 0 ? 0 : (col - tw);
                    cx = (col - raidus_x) < 0 ? 0 : col - raidus_x;
                    cy = (row - raidus_y) < 0 ? 0 : row - raidus_y;
                    int num = (x2 - x1)*(y2 - y1);
                    float sum = ii.getBlockSquareSum(x1, y1, x2, y2);
                    result[cy*w+cx] = (sum - sum2);
                }
            }
        } else {
            throw new IllegalStateException("\nERR:Image Type is not all Gray...\n");
        }
        return new FloatProcessor(result, rw, rh);
    }

    public float calculateSum2(ByteProcessor bp) {
        float sum2 = 0;
        byte[] data = bp.getGray();
        for(int i=0; i<data.length; i++) {
            int pv = data[i]&0xff;
            sum2 += (pv*pv);
        }
        return sum2;
    }

}
