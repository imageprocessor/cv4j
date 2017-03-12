package com.cv4j.core.filters;

import com.cv4j.core.datamodel.ImageData;

/**
 * Created by gloomy fish on 2017/3/9.
 */

public class ExposureFilter implements CommonFilter  {

    @Override
    public ImageData filter(ImageData src) {
        int width = src.getWidth();
        int height = src.getHeight();
        byte[] R = src.getChannel(0);
        byte[] G = src.getChannel(1);
        byte[] B = src.getChannel(2);
        byte[][] output = new byte[3][R.length];
        int tr=0, tg=0, tb=0;
        for(int row=0; row<height; row++) {
            int offset = row*width;
            for(int col=0; col<width; col++) {
                tr = R[offset] & 0xff;
                tg = G[offset] & 0xff;
                tb = B[offset] & 0xff;

                tr = 255 - tr;
                tg = 255 - tg;
                tb = 255 - tb;

                R[offset] = (byte)tr;
                G[offset] = (byte)tg;
                B[offset] = (byte)tb;
                offset++;
            }
        }
        return src;
    }
}
