package com.cv4j.core.filters;

import com.cv4j.core.datamodel.ImageData;

/**
 * Created by Administrator on 2017/3/15.
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
        style = 0;
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

                R[offset] = (byte)lut[0][tr];
                G[offset] = (byte)lut[1][tg];
                B[offset] = (byte)lut[2][tb];
                offset++;
            }
        }
        return src;
    }

    private int[][] getStyleLUT(int style) {
        if(style == 0) {
            return ImageData.AUTUMN_LUT;
        } else if(style == 1) {
            return ImageData.BONE_LUT;
        } else if(style == 2) {
            return ImageData.COOL_LUT;
        }else if(style == 3) {
            return ImageData.HOT_LUT;
        }else if(style == 4) {
            return ImageData.HSV_LUT;
        }else if(style == 5) {
            return ImageData.JET_LUT;
        }else if(style == 6) {
            return ImageData.OCEAN_LUT;
        }else if(style == 7) {
            return ImageData.PINK_LUT;
        }else if(style == 8) {
            return ImageData.RAINBOW_LUT;
        }else if(style == 9) {
            return ImageData.SPRING_LUT;
        }else if(style == 10) {
            return ImageData.SUMMER_LUT;
        }else if(style == 11) {
            return ImageData.WINTER_LUT;
        }else {
            // Excepiton
            return null;
        }
    }
}
