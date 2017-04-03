package com.cv4j.core.spatial.conv;

import com.cv4j.core.datamodel.ByteProcessor;
import com.cv4j.core.datamodel.ImageProcessor;
import com.cv4j.core.filters.CommonFilter;

/**
 * Created by Administrator on 2017/3/26.
 */

public class GaussianBlurFilter implements CommonFilter {
    private double sigma;
    private double accuracy;
    public GaussianBlurFilter(){
        sigma = 2.0;
        accuracy = 0.002;
    }

    public void setSigma(double sigma) {
        this.sigma = sigma;
    }

    @Override
    public ImageProcessor filter(ImageProcessor src) {
        int width = src.getWidth();
        int height = src.getHeight();
        int channels = src.getChannels();
        for(int i=0; i<channels; i++) {
            byte[] data = src.toByte(i);
            ByteProcessor bp = new ByteProcessor(data, width, height);
            blurFloat(bp, sigma, sigma);
        }
        return src;
    }

    public void blurFloat(ByteProcessor bp, double sigmaX, double sigmaY) {
        if (sigmaX > 0)
            blur1Direction(bp, sigmaX, accuracy, true, (int)Math.ceil(5*sigmaY));
        if (sigmaY > 0)
            blur1Direction(bp, sigmaY, accuracy, false, 0);
    }

    private void blur1Direction(ByteProcessor bp, double sigmaX, double accuracy, boolean b, int extraLines) {
        final Thread[] lineThreads = new Thread[4];

    }
}
