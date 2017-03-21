package com.cv4j.core.filters;

import com.cv4j.core.datamodel.ImageData;

/**
 * Created by gloomy fish on 2017/3/21.
 */

public class GaussianBlurFilter implements CommonFilter {
    private int radius=5;

    public GaussianBlurFilter(int radius) {
        this.radius = radius;
    }

    public GaussianBlurFilter() {
        this(5);
    }

    @Override
    public ImageData filter(ImageData src) {
        int width = src.getWidth();
        int height = src.getHeight();
        byte[] R = src.getChannel(0);
        byte[] G = src.getChannel(1);
        byte[] B = src.getChannel(2);

        GaussianByteProcessor byteProcessor = new GaussianByteProcessor(radius);
        byteProcessor.process(R, width, height);
        byteProcessor.process(G, width, height);
        byteProcessor.process(B, width, height);
        return src;
    }
}
