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
        makeGaussianKernel(sigma, 0.002, (int)Math.min(width, height));

        int[] inPixels = src.getPixels();
        int[] outPixels = new int[width*height];
        blur( inPixels, outPixels, width, height); // H Gaussian
        blur( outPixels, inPixels, height, width); // V Gaussain
        // save result
        byte[] R = ((ColorProcessor)src).getRed();
        byte[] G = ((ColorProcessor)src).getGreen();
        byte[] B = ((ColorProcessor)src).getBlue();
        getRGB(width, height, inPixels, R, G, B);
        return src;
    }

    public void getRGB(int width, int height, int[] pixels, byte[] R, byte[] G, byte[] B) {
        int c, r, g, b;
        for (int i=0; i < width*height; i++) {
            c = pixels[i];
            r = (c&0xff0000)>>16;
            g = (c&0xff00)>>8;
            b = c&0xff;
            R[i] = (byte)r;
            G[i] = (byte)g;
            B[i] = (byte)b;
        }
    }

    /**
     * <p> here is 1D Gaussian        , </p>
     *
     * @param inPixels
     * @param outPixels
     * @param width
     * @param height
     */
    private void blur(int[] inPixels, int[] outPixels, int width, int height)
    {
        int subCol = 0;
        int index = 0, index2 = 0;
        float redSum=0, greenSum=0, blueSum=0;
        for(int row=0; row<height; row++) {
            int ta = 0, tr = 0, tg = 0, tb = 0;
            index = row;
            for(int col=0; col<width; col++) {
                // index = row * width + col;
                redSum=0;
                greenSum=0;
                blueSum=0;
                for(int m = 0; m< kernel.length; m++) {
                    subCol = col + m;
                    if(subCol < 0 || subCol >= width) {
                        subCol = 0;
                    }
                    index2 = row * width + subCol;
                    ta = (inPixels[index2] >> 24) & 0xff;
                    tr = (inPixels[index2] >> 16) & 0xff;
                    tg = (inPixels[index2] >> 8) & 0xff;
                    tb = inPixels[index2] & 0xff;
                    redSum += (tr * kernel[m]);
                    greenSum += (tg * kernel[m]);
                    blueSum += (tb * kernel[m]);
                }
                outPixels[index] = (ta << 24) | (Tools.clamp(redSum) << 16) | (Tools.clamp(greenSum) << 8) | Tools.clamp(blueSum);
                index += height;// correct index at here!!!, out put pixels matrix,
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
