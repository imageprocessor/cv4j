package com.cv4j.core.filters;

import com.cv4j.core.datamodel.ColorProcessor;
import com.cv4j.core.datamodel.ImageProcessor;

/**
 * Created by gloomy fish on 2017/3/9.
 */

public class ExposureFilter implements CommonFilter  {

    @Override
    public ImageProcessor filter(ImageProcessor src) {
        int width = src.getWidth();
        int height = src.getHeight();
        byte[] R = ((ColorProcessor)src).getRed();
        byte[] G = ((ColorProcessor)src).getGreen();
        byte[] B = ((ColorProcessor)src).getBlue();
        int tr=0, tg=0, tb=0;
        int size = R.length;
        for(int i=0; i<height; i++) {
            R[i] = (byte)~R[i];
            G[i] = (byte)~G[i];
            B[i] = (byte)~B[i];
        }
        return src;
    }
}
