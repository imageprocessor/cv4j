package com.cv4j.core.pixels;

import com.cv4j.core.datamodel.ByteProcessor;
import com.cv4j.core.datamodel.ColorProcessor;
import com.cv4j.core.datamodel.ImageProcessor;

public class Flip {
	
	public static void flip(ImageProcessor processor, int option) {
		int width = processor.getWidth();
        int height = processor.getHeight();
        int ch = processor.getChannels();
        int index1 = 0;
        int index2 = 0;
        int total = width*height;
        byte[][] output = new byte[ch][total];
        for(int row=0; row<height; row++) {
        	for(int col=0; col<width; col++) {
        		index1 = row*width+col;
        		if(option == 1) {
        			index2 = row*width + width-col-1;
        		} else if(option == -1){
        			index2 = (height-row-1)*width + col;
        		} else {
        			throw new IllegalArgumentException("invalid option : " + option);
        		}
        		for(int i=0; i<ch; i++) {
        			output[i][index2] = processor.toByte(i)[index1];
        		}
        	}
        }
        if(ch == 3) {
        	((ColorProcessor) processor).putRGB(output[0], output[1], output[2]);
        } else {
        	((ByteProcessor) processor).putGray(output[0]);
        }
	}

}
