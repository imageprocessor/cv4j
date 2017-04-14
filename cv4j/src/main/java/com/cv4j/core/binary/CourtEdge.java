package com.cv4j.core.binary;

import com.cv4j.core.datamodel.ByteProcessor;

public class CourtEdge {

	public void process(ByteProcessor binary) {
		int width = binary.getWidth();
		int height = binary.getHeight();
		byte[] input = binary.getGray();
		byte[] output = new byte[input.length];
		System.arraycopy(input, 0, output, 0, input.length);

		int p1=0, p2=0;
		int offset = 0;
		for(int row=1; row<height-1; row++) {
			offset = row*width;
			for(int col=1; col<width-1; col++) {
				p1 = input[offset+col]&0xff;
				p2 = input[offset+col]&0xff;
				if(p1 == p2) {
					output[offset+col] = (byte)0;
				}
				else {
					output[offset+col] = (byte)255;
				}
			}
		}
		System.arraycopy(output, 0, input, 0, input.length);
	}

}
