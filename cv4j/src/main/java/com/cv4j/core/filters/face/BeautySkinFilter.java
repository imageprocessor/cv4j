package com.cv4j.core.filters.face;

import com.cv4j.core.binary.Erode;
import com.cv4j.core.datamodel.ByteProcessor;
import com.cv4j.core.datamodel.ImageProcessor;
import com.cv4j.core.datamodel.IntIntegralImage;
import com.cv4j.core.datamodel.Size;
import com.cv4j.core.filters.CommonFilter;
import com.cv4j.core.filters.FastEPFilter;
import com.cv4j.core.filters.GradientFilter;

import java.util.Arrays;

/**
 * Created by gloomy fish on 2017/4/23.
 */

public class BeautySkinFilter implements CommonFilter {
    @Override
    public ImageProcessor filter(ImageProcessor src) {
        int width = src.getWidth();
        int height = src.getHeight();
        byte[] R = new byte[width*height];
        byte[] G = new byte[width*height];
        byte[] B = new byte[width*height];
        System.arraycopy(src.toByte(0), 0, R, 0, R.length);
        System.arraycopy(src.toByte(1), 0, G, 0, G.length);
        System.arraycopy(src.toByte(2), 0, B, 0, B.length);

        FastEPFilter epFilter = new FastEPFilter();
        epFilter.filter(src);

        ISkinDetection skinDetector = new DefaultSkinDetection();
        byte[] mask = new byte[width*height];
        Arrays.fill(mask, (byte)0);
        int r = 0, g = 0, b = 0;
        for(int i=0; i<R.length; i++) {
            r = R[i]&0xff;
            g = G[i]&0xff;
            b = B[i]&0xff;
            if(!skinDetector.isSkin(r, g, b)) {
                mask[i] = (byte)255;
            }
        }
        Erode erode = new Erode();
        erode.process(new ByteProcessor(mask, width, height), new Size(5));
        for(int i=0; i<mask.length; i++) {
            int c = mask[i]&0xff;
            if(c > 0) {
                src.toByte(0)[i] = R[i];
                src.toByte(1)[i] = G[i];
                src.toByte(2)[i] = B[i];
            }
        }

        int c = 0;
        for(int i=0; i<R.length; i++) {
            r = R[i] & 0xff;
            g = G[i] & 0xff;
            b = B[i] & 0xff;
            c = (int)(0.299 *r + 0.587*g + 0.114*b);
            mask[i] = (byte)c;
        }

        GradientFilter gradientFilter = new GradientFilter();
        int[] gradient = gradientFilter.gradient(new ByteProcessor(mask, width, height));
        Arrays.fill(mask, (byte)0);
        for(int i=0; i<R.length; i++) {
            if(gradient[i] > 35) {
                mask[i] = (byte)255;
            }
        }
        gradient = null;

        // 遮罩层模糊
        IntIntegralImage ii = new IntIntegralImage();
        ii.setImage(mask);
        ii.process(width, height);
        byte[] blurmask = new byte[mask.length];
        int offset = 0;
        for (int row = 1; row < height-1; row++) {
            offset = row * width;
            for (int col = 1; col < width-1; col++) {
                int sr = ii.getBlockSum(col, row, 5, 5);
                blurmask[offset + col] = (byte)(sr/25);
            }
        }

        // alpha blend
        mask = null;
        ii = null;
        float w = 0.0f;
        int wc = 0;
        for(int i=0; i<blurmask.length; i++) {
            wc = blurmask[i]&0xff;
            w = wc/255.0f;

            r = (int)((R[i]&0xff)*w+(src.toByte(0)[i]&0xff)*(1.0f-w));
            g = (int)((G[i]&0xff)*w+(src.toByte(1)[i]&0xff)*(1.0f-w));
            b = (int)((B[i]&0xff)*w+(src.toByte(2)[i]&0xff)*(1.0f-w));

            src.toByte(0)[i] = (byte)r;
            src.toByte(1)[i] = (byte)g;
            src.toByte(2)[i] = (byte)b;
        }
        R = null;
        G = null;
        B = null;
        blurmask = null;
        return src;
    }
}
