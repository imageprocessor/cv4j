package com.cv4j.core.spatial.conv;

import com.cv4j.core.datamodel.ColorProcessor;
import com.cv4j.core.datamodel.ImageProcessor;
import com.cv4j.core.filters.CommonFilter;
public class SAPNoiseFilter implements CommonFilter {
	private float precent;
	
	public SAPNoiseFilter() {
		precent = 0.01f;
	}

	public float getPrecent() {
		return precent;
	}

	public void setPrecent(float precent) {
		this.precent = precent;
	}

	@Override
	public ImageProcessor filter(ImageProcessor src) {

		if (!(src instanceof ColorProcessor)) return src;

		int width = src.getWidth();
		int height = src.getHeight();

		int total = width*height;
		byte[] R = ((ColorProcessor)src).getRed();
		byte[] G = ((ColorProcessor)src).getGreen();
		byte[] B = ((ColorProcessor)src).getBlue();

		java.util.Random random = new java.util.Random();
		int numOfSalt = (int)(width*height*precent);
		int row=0, col=0, index=0;
		for(int i=0; i<numOfSalt; i++) {
			row = getPosition(0, height, random);
			col = getPosition(0, width, random);
			index = row*width+col;
			R[index]= (byte)255;
			G[index]= (byte)255;
			B[index]= (byte)255;
			
			row = getPosition(0, height, random);
			col = getPosition(0, width, random);
			R[index]= (byte)0;
			G[index]= (byte)0;
			B[index]= (byte)0;
		}
		return src;
	}

	private int getPosition(int min, int max, java.util.Random random) {
		int result = random.nextInt(max-min);
		return result;
	}

}
