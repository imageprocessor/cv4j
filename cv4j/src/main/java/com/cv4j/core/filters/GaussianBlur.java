package com.cv4j.core.filters;

import com.cv4j.core.datamodel.ImageData;

/**
 * Created by Administrator on 2017/3/21.
 */

public class GaussianBlur implements CommonFilter {
    private int radius=5;

    public GaussianBlur(int radius) {
        this.radius = 5;
    }

    public GaussianBlur() {
        this(5);
    }

    @Override
    public ImageData filter(ImageData src) {
        int width = src.getWidth();
        int height = src.getHeight();
        byte[] R = src.getChannel(0);
        byte[] G = src.getChannel(1);
        byte[] B = src.getChannel(2);

        GaussianByteProcessor byteProcessor = new GaussianByteProcessor();
        byteProcessor.process(R, width, height);
        byteProcessor.process(G, width, height);
        byteProcessor.process(B, width, height);
        return src;
    }
}
