package com.cv4j.core.spatial.conv;

import com.cv4j.core.datamodel.ColorProcessor;
import com.cv4j.core.datamodel.ImageProcessor;
import com.cv4j.core.filters.CommonFilter;
import com.cv4j.image.util.Tools;

/**
 * Created by Administrator on 2017/3/26.
 */

public class GaussianBlurFilter implements CommonFilter {
    private float[] kernel;
    private double sigma = 2;
    public GaussianBlurFilter() {
        kernel = new float[0];
    }

    public void setSigma(double a) {
        this.sigma = a;
    }

    @Override
    public ImageProcessor filter(ImageProcessor src){
        int width = src.getWidth();
        int height = src.getHeight();
        int size = width*height;
        int dims = src.getChannels();
        makeGaussianKernel(sigma, 0.002, (int)Math.min(width, height));

        // save result
        for(int i=0; i<dims; i++) {
            byte[] inPixels = src.toByte(i);
            byte[] temp = new byte[size];
            blur(inPixels, temp, width, height); // H Gaussian
            blur(temp, inPixels, height, width); // V Gaussain
        }
        return src;
    }

    /**
     * <p> here is 1D Gaussian        , </p>
     *
     * @param inPixels
     * @param outPixels
     * @param width
     * @param height
     */
    private void blur(byte[] inPixels, byte[] outPixels, int width, int height)
    {
        int subCol = 0;
        int index = 0, index2 = 0;
        float sum = 0;
        int k = kernel.length-1;
        for(int row=0; row<height; row++) {
            int c = 0;
            index = row;
            for(int col=0; col<width; col++) {
                sum = 0;
                for(int m = -k; m< kernel.length; m++) {
                    subCol = col + m;
                    if(subCol < 0 || subCol >= width) {
                        subCol = 0;
                    }
                    index2 = row * width + subCol;
                    c = inPixels[index2] & 0xff;
                    sum += c * kernel[Math.abs(m)];
                }
                outPixels[index] = (byte)Tools.clamp(sum);
                index += height;
            }
        }
    }

    public void makeGaussianKernel(final double sigma, final double accuracy, int maxRadius) {
        int kRadius = (int)Math.ceil(sigma*Math.sqrt(-2*Math.log(accuracy)))+1;
        if (maxRadius < 50) maxRadius = 50;         // too small maxRadius would result in inaccurate sum.
        if (kRadius > maxRadius) kRadius = maxRadius;
        kernel = new float[kRadius];
        for (int i=0; i<kRadius; i++)               // Gaussian function
            kernel[i] = (float)(Math.exp(-0.5*i*i/sigma/sigma));
        double sum;                                 // sum over all kernel elements for normalization
        if (kRadius < maxRadius) {
            sum = kernel[0];
            for (int i=1; i<kRadius; i++)
                sum += 2*kernel[i];
        } else
            sum = sigma * Math.sqrt(2*Math.PI);

        for (int i=0; i<kRadius; i++) {
            double v = (kernel[i]/sum);
            kernel[i] = (float)v;
        }
        return;
    }
}
