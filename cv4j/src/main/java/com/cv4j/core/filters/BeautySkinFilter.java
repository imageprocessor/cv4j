package com.cv4j.core.filters;

import com.cv4j.core.datamodel.ByteProcessor;
import com.cv4j.core.datamodel.ImageProcessor;

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
        int r = 0, g = 0, b = 0;
        for(int i=0; i<R.length; i++) {
            r = R[i]&0xff;
            g = G[i]&0xff;
            b = B[i]&0xff;
            if(!skinDetector.isSkin(r, g, b)) {
                src.toByte(0)[i] = (byte)r;
                src.toByte(1)[i] = (byte)g;
                src.toByte(2)[i] = (byte)b;
            }
        }

        byte[] gray = new byte[width*height];
        int c = 0;
        for(int i=0; i<R.length; i++) {
            r = R[i] & 0xff;
            g = G[i] & 0xff;
            b = B[i] & 0xff;
            c = (int)(0.299 *r + 0.587*g + 0.114*b);
            gray[i] = (byte)c;
        }

        GradientFilter gradientFilter = new GradientFilter();
        int[] gradient = gradientFilter.gradient(new ByteProcessor(gray, width, height));
        gray = null;
        for(int i=0; i<R.length; i++) {
            r = R[i]&0xff;
            g = G[i]&0xff;
            b = B[i]&0xff;
            if(gradient[i] > 50) {
                src.toByte(0)[i] = (byte)r;
                src.toByte(1)[i] = (byte)g;
                src.toByte(2)[i] = (byte)b;
            }
        }
        return src;
    }
}
