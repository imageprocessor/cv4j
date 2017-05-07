package com.cv4j.core.filters;

import com.cv4j.core.datamodel.ColorProcessor;
import com.cv4j.core.datamodel.ImageProcessor;

import static com.cv4j.image.util.Tools.clamp;

/**
 * Created by Tony Shen on 2017/5/7.
 */

public class StrokeAreaFilter extends BaseFilter {

    // default value, optional value 30, 15, 10, 5, 2
    private double size = 10;

    private static double d02 = 150*150;

    public StrokeAreaFilter() {
        this(15);
    }

    public StrokeAreaFilter(int strokeSize) {
        this.size = strokeSize;
    }

    public double getSize() {
        return size;
    }

    public void setSize(double size) {
        this.size = size;
    }

    @Override
    public ImageProcessor doFilter(ImageProcessor src) {

        byte[][] output = new byte[3][R.length];

        int index = 0, index2 = 0;
        int semiRow = (int)(size/2);
        int semiCol = (int)(size/2);
        int newX, newY;

        // initialize the color RGB array with zero...
        int[] rgb = new int[3];
        int[] rgb2 = new int[3];
        for(int i=0; i<rgb.length; i++) {
            rgb[i] = rgb2[i] = 0;
        }

        // start the algorithm process here!!
        for(int row=0; row<height; row++) {
            int ta = 0;
            for(int col=0; col<width; col++) {
                index = row * width + col;
                rgb[0] = R[index] & 0xff;
                rgb[1] = G[index] & 0xff;
                rgb[2] = B[index] & 0xff;

                /* adjust region to fit in source image */
                // color difference and moment Image
                double moment = 0.0d;
                for(int subRow = -semiRow; subRow <= semiRow; subRow++) {
                    for(int subCol = -semiCol; subCol <= semiCol; subCol++) {
                        newY = row + subRow;
                        newX = col + subCol;
                        if(newY < 0) {
                            newY = 0;
                        }
                        if(newX < 0) {
                            newX = 0;
                        }
                        if(newY >= height) {
                            newY = height-1;
                        }
                        if(newX >= width) {
                            newX = width - 1;
                        }
                        index2 = newY * width + newX;
                        rgb2[0] = R[index2] & 0xff; // red
                        rgb2[1] = G[index2] & 0xff; // green
                        rgb2[2] = B[index2] & 0xff; // blue
                        moment += colorDiff(rgb, rgb2);
                    }
                }
                // calculate the output pixel value.
                int outPixelValue = clamp((int) (255.0d * moment / (size*size)));
                output[0][index] = (byte)outPixelValue;
                output[1][index] = (byte)outPixelValue;
                output[2][index] = (byte)outPixelValue;
            }
        }

        ((ColorProcessor) src).putRGB(output[0], output[1], output[2]);
        output = null;
        return src;
    }

    public static double colorDiff(int[] rgb1, int[] rgb2) {
        // (1-(d/d0)^2)^2
        double d2, r2;
        d2 = colorDistance(rgb1, rgb2);

        if (d2 >= d02)
            return 0.0;

        r2 = d2 / d02;

        return ((1.0d - r2) * (1.0d - r2));
    }

    public static double colorDistance(int[] rgb1, int[] rgb2) {
        int dr, dg, db;
        dr = rgb1[0] - rgb2[0];
        dg = rgb1[1] - rgb2[1];
        db = rgb1[2] - rgb2[2];
        return dr * dr + dg * dg + db * db;
    }
}
