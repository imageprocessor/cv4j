package com.cv4j.core.filters;

import com.cv4j.core.datamodel.ImageProcessor;

/**
 * Created by gloomy fish on 2017/3/9.
 */

public class ExposureFilter extends BaseFilter  {

    @Override
    public ImageProcessor doFilter(ImageProcessor src) {

        int tr=0, tg=0, tb=0;
        int size = R.length;
        for(int i=0; i<size; i++) {
            R[i] = (byte)~R[i];
            G[i] = (byte)~G[i];
            B[i] = (byte)~B[i];
        }
        return src;
    }
}
