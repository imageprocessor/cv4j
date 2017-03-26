package com.cv4j.core.filters;

import com.cv4j.core.datamodel.ByteProcessor;
import com.cv4j.core.datamodel.ColorProcessor;
import com.cv4j.core.datamodel.ImageProcessor;

public class MeansBinaryFilter implements CommonFilter {

	public ImageProcessor filter(ImageProcessor src) {
        if(src instanceof ColorProcessor) {
            src.getImage().convert2Gray();
            src = src.getImage().getProcessor();
        }
        int width = src.getWidth();
        int height = src.getHeight();
        byte[] GRAY = ((ByteProcessor)src).getGray();

        float graySum = 0;
        int total = width * height;
        for(int i=0; i<total; i++){
            graySum += GRAY[i]&0xff;
        }
        int means = (int)(graySum / total);
        
        // dithering
        int c = 0;
        for(int i=0; i<total; i++) {
            c = ((GRAY[i]&0xff) >= means) ? 255 : 0;
            GRAY[i] = (byte)c;
        }
        return src;
	}
}
