package com.cv4j.core.hist;

import com.cv4j.core.datamodel.ByteProcessor;
import com.cv4j.core.datamodel.ImageProcessor;

import java.util.Arrays;

/**
 * Created by gloomy fish on 2017/5/14.
 */

public class EqualHist {

    public void Equalization(ByteProcessor src) {

        int width = src.getWidth();
        int height = src.getHeight();

        int[] inputBins = new int[256]; // RGB
        int[] outputBins = new int[256]; // after HE
        Arrays.fill(inputBins, 0);
        Arrays.fill(outputBins, 0);

        int index = 0;
        byte[] data = src.getGray();
        for(int i=0; i<data.length; i++) {
            inputBins[data[i]&0xff]++;
        }

        // generate original source image RGB histogram
        generateHEData(inputBins, outputBins, data.length, 256);
        for(int row=0; row<height; row++) {
            int pv = 0;
            for(int col=0; col<width; col++) {
                index = row * width + col;
                pv = data[index]&0xff;
                data[index] = (byte)outputBins[pv];
            }
        }
    }

    /**
     *
     * @param input
     * @param output
     * @param numOfPixels
     * @param grayLevel
     */
    private void generateHEData(int[] input, int[] output, int numOfPixels, int grayLevel) {
        for(int i=0; i<grayLevel; i++) {
            output[i] = getNewintensityRate(input, numOfPixels, i);
        }
    }

    private int getNewintensityRate(int[] grayHis, double total, int index) {
        double sum = 0;
        for(int i=0; i<=index; i++) {
            sum += ((double)grayHis[i])/total;
        }
        return (int)(sum * 255.0);
    }
}
