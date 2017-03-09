package com.cv4j.core.filters;

import com.cv4j.core.datamodel.ImageData;

/**
 * Created by Administrator on 2017/3/9.
 */

public class ExposureFilter implements CommonFilter  {

    @Override
    public ImageData filter(ImageData src) {
        int width = src.getWidth();
        int height = src.getHeight();
        int[] pixels = src.getPixels();
        int tr=0, tg=0, tb=0;
        for(int row=0; row<height; row++) {
            int offset = row*width;
            for(int col=0; col<width; col++) {
                tr = (pixels[offset] >> 16) & 0xff;
                tg = (pixels[offset] >> 8) & 0xff;
                tb = pixels[offset] & 0xff;
                tr = 255 - tr;
                tg = 255 - tg;
                tb = 255 - tb;
                pixels[offset] = (255 << 24) | (tr << 16) | (tg << 8) | tb;
                offset++;
            }
        }
        return src;
    }
}
