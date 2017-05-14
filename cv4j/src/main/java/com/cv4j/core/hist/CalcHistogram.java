package com.cv4j.core.hist;

import com.cv4j.core.datamodel.ImageProcessor;

/**
 * Created by gloomy fish on 2017/5/14.
 */

public class CalcHistogram {

    public void calcHist(ImageProcessor src, int bins, int[][] hist, boolean norm) {
        int numChannels = src.getChannels();
        for(int i=0; i<numChannels; i++) {
            byte[] data = src.toByte(i);
            hist[i] = getHistogram(data, bins);
        }

        if(!norm) return;
        float min = 10000000, max = 0;
        for(int i=0; i<numChannels; i++) {
            for(int j=0; j<bins; j++) {
                min = Math.min(hist[i][j], min);
                max = Math.max(hist[i][j], max);
            }
            float delta = max -min;
            for(int j=0; j<bins; j++) {
                hist[i][j] = (int)(((hist[i][j] - min)/delta)*255);
            }
        }
    }

    private int[] getHistogram(byte[] data, int bins) {
        int[] hist = new int[256];
        for(int i=0; i<data.length; i++) {
            hist[data[i]&0xff]++;
        }

        double numOfGap = 256.0/bins;
        int[] wh = new int[bins];
        for(int k=0; k<bins; k++) {
            double prebin = (k-1)*numOfGap;
            double currbin = k*numOfGap;
            int obin = (int)Math.floor(prebin);
            if(obin < 0) {
                obin = 0;
                prebin = 0;
            }
            int nbin = (int)Math.floor(currbin);
            for(int j=obin; j<=nbin; j++) {
                wh[k] += hist[j];
            }
            double w1 = prebin - obin;
            double w2 = currbin - nbin;
            if(w1 > 0 && w1 < 1) {
                wh[k] = (int)(wh[k] - hist[obin]*w1);
            }
            if(w2 > 0 && w2 < 1) {
                wh[k] = (int)(wh[k] + hist[nbin+1]*w2);
            }
        }
        return wh;
    }

}
