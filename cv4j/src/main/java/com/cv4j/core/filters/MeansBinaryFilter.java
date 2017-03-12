package com.cv4j.core.filters;

import com.cv4j.core.datamodel.ImageData;

public class MeansBinaryFilter implements CommonFilter {

	public ImageData filter(ImageData src) {
        if(src.getType() == ImageData.CV4J_IMAGE_TYPE_RGB) {
            src.convert2Gray();
        }
        // calculate means of pixel  
        int index = 0;  
        double graySum = 0;
        double total = src.getHeight() * src.getWidth();
        int gray =0;
        byte[] R = src.getChannel(0);
        byte[] output = new byte[R.length];
        for(int i=0; i<total; i++){
            gray = R[i]&0xff;
            graySum += gray;
        }
        int means = (int)(graySum / total);
        System.out.println(" threshold average value = " + means);
        
        // dithering
        for(int i=0; i<total; i++) {
            gray = R[i]&0xff;
            if (gray >= means) {
                gray = 255;
            } else {
                gray = 0;
            }
            output[i] = (byte)gray;
        }
        src.putPixels(new byte[][]{output, output, output});
        output = null;
        return src;
	}
}
