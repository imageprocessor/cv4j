package com.cv4j.core.spatial.conv;

import com.cv4j.core.datamodel.ImageProcessor;
import com.cv4j.core.filters.BaseFilter;

public class SAPNoiseFilter extends BaseFilter {
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
	public ImageProcessor doFilter(ImageProcessor src) {


		int total = width*height;

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
		return random.nextInt(max-min);
	}

}
