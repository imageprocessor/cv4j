package com.cv4j.core.filters;

import com.cv4j.core.datamodel.ImageData;
import com.cv4j.core.datamodel.lut.LUT;

/**
 * Created by gloomy fish on 2017/3/15.
 */

public class ColorFilter implements CommonFilter {
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
    public ImageData filter(ImageData src) {
        int width = src.getWidth();
        int height = src.getHeight();
        byte[] R = src.getChannel(0);
        byte[] G = src.getChannel(1);
        byte[] B = src.getChannel(2);
        byte[][] output = new byte[3][R.length];
        int tr=0, tg=0, tb=0;
        int[][] lut = getStyleLUT(style);
        for(int row=0; row<height; row++) {
            int offset = row*width;
            for(int col=0; col<width; col++) {
                tr = R[offset] & 0xff;
                tg = G[offset] & 0xff;
                tb = B[offset] & 0xff;

                R[offset] = (byte)lut[tr][0];
                G[offset] = (byte)lut[tg][1];
                B[offset] = (byte)lut[tb][2];
                offset++;
            }
        }
        return src;
    }

    private int[][] getStyleLUT(int style) {

        return LUT.getColorFilterLUT(style);
    }
}
