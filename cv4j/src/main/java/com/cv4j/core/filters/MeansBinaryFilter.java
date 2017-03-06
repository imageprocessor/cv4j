package com.cv4j.core.filters;

import com.cv4j.core.datamodel.ImageData;

public class MeansBinaryFilter implements CommonFilter {

	public ImageData filter(ImageData src) {
        // calculate means of pixel  
        int index = 0;  
        double graySum = 0;
        double total = src.getHeight() * src.getWidth();
        int gray =0;
        for(int i=0; i<total; i++){
            gray = src.getPixels()[i];
            graySum += gray;
        }
        int means = (int)(graySum / total);
        System.out.println(" threshold average value = " + means);
        
        // dithering
        int[] output = new int[src.getPixels().length];
        for(int i=0; i<total; i++) {
            gray = src.getPixels()[i];
            if (gray >= means) {
                gray = 255;
            } else {
                gray = 0;
            }
            output[i] = gray;
        }
        src.putPixels(output);
        return src;
	}
}
