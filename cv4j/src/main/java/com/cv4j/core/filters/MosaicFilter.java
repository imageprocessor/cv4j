package com.cv4j.core.filters;

import com.cv4j.core.datamodel.ImageData;
import com.cv4j.core.datamodel.IntIntegralImage;

public class MosaicFilter implements CommonFilter {
	private int size;
	
	public MosaicFilter() {
		size = 10; // default block size=10x10
	}
	
	public MosaicFilter(int size) {
		this.size = size;
	}

	@Override
	public ImageData filter(ImageData src){
		int width = src.getWidth();
        int height = src.getHeight();

		byte[] R = src.getChannel(0);
		byte[] G = src.getChannel(1);
		byte[] B = src.getChannel(2);
		byte[][] output = new byte[3][R.length];
        int index = 0;

		IntIntegralImage iir = new IntIntegralImage();
		iir.setImage(R);
		iir.process(width, height);

		IntIntegralImage iig = new IntIntegralImage();
		iig.setImage(G);
		iig.process(width, height);

		IntIntegralImage iib = new IntIntegralImage();
		iib.setImage(B);
		iib.process(width, height);

        
        int offsetX = 0, offsetY = 0;
        int newX = 0, newY = 0;
        double total = size*size;
        double sumred = 0, sumgreen = 0, sumblue = 0;
        for(int row=0; row<height; row++) {
        	int ta = 0, tr = 0, tg = 0, tb = 0;
        	for(int col=0; col<width; col++) {
        		newY = (row/size) * size;
        		newX = (col/size) * size;
        		offsetX = newX + size;
        		offsetY = newY + size;

				// 积分图像查找
        		index = row * width + col;
				sumred = iir.getBlockSum(newX-offsetX/2, newY-offsetY/2, offsetY, offsetX);
				sumgreen = iig.getBlockSum(newX-offsetX/2, newY-offsetY/2, offsetY, offsetX);
				sumblue = iib.getBlockSum(newX-offsetX/2, newY-offsetY/2, offsetY, offsetX);
        		tr = (int)(sumred/total);
        		tg = (int)(sumgreen/total);
        		tb = (int)(sumblue/total);

				// 赋值
        		output[0][index] = (byte)tr;
				output[1][index] = (byte)tg;
				output[2][index] = (byte)tb;
        	}
        }
		iir = null;
		iig = null;
		iib = null;
		src.putPixels(output);
		output = null;
		return src;
	}

}
