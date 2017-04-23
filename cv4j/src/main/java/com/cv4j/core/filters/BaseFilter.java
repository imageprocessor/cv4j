package com.cv4j.core.filters;

import com.cv4j.core.datamodel.ColorProcessor;
import com.cv4j.core.datamodel.ImageProcessor;

/**
 * Created by Tony Shen on 2017/4/23.
 */

public abstract class BaseFilter implements CommonFilter {

    protected int width;
    protected int height;
    protected byte[] R;
    protected byte[] G;
    protected byte[] B;

    @Override
    public ImageProcessor filter(ImageProcessor src) {

        if (src == null) return null;

        if (!(src instanceof ColorProcessor)) return src;

        width = src.getWidth();
        height = src.getHeight();
        R = ((ColorProcessor)src).getRed();
        G = ((ColorProcessor)src).getGreen();
        B = ((ColorProcessor)src).getBlue();

        return doFilter(src);
    }

    public abstract ImageProcessor doFilter(ImageProcessor src);
}
