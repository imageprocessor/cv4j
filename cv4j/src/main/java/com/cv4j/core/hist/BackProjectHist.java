package com.cv4j.core.hist;

import com.cv4j.core.datamodel.ByteProcessor;

import java.util.Arrays;

/**
 * Created by gloomy fish on 2017/5/14.
 */

public class BackProjectHist {

    public void backProjection(ByteProcessor src, ByteProcessor backProjection, int bins, int[] histData, int[] range) {
        CalcHistogram calcHist = new CalcHistogram();
        int[][] hist = new int[1][bins];
        calcHist.calcHist(src, bins, hist, true);
        byte[] data = src.getGray();
        byte[] bp = backProjection.getGray();
        int width = src.getWidth();
        int height = src.getHeight();
        int offset = 0;

        // setup look up table
        float delta = 256.0f / bins;
        int[] lutHist = new int[256];
        for (int i = 0; i < 256; i++) {
            int hidx = (int) (i / delta);
            if (hidx < bins)
                lutHist[i] = hist[0][hidx];
        }

        // back project stage
        Arrays.fill(bp, (byte) 0);
        for (int row = 0; row < height; row++) {
            int t0 = 0, t1 = 0;
            offset = row * width;
            for (int x = 0; x < width - 4; x += 4) {
                t0 = lutHist[data[offset + x] & 0xff];
                t1 = lutHist[data[offset + x + 1] & 0xff];
                bp[offset + x] = (byte) t0;
                bp[offset + x + 1] = (byte) t1;

                t0 = lutHist[data[offset + x + 2] & 0xff];
                t1 = lutHist[data[offset + x + 3] & 0xff];
                bp[offset + x + 2] = (byte) t0;
                bp[offset + x + 3] = (byte) t1;
            }
        }
        backProjection.putGray(bp);

    }
}
