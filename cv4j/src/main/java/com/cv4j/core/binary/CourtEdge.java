package com.cv4j.core.binary;

import com.cv4j.core.datamodel.ByteProcessor;
import com.cv4j.core.datamodel.Size;

public class CourtEdge {

	public void process(ByteProcessor binary) {
		int width = binary.getWidth();
		int height = binary.getHeight();

		byte[] input1 = new byte[width*height];
		System.arraycopy(binary.getGray(), 0, input1, 0, input1.length);

		Erode erode = new Erode();
		erode.process(binary, new Size(3, 3));
		byte[] input2 = binary.getGray();
		byte[] output = new byte[input1.length];
		System.arraycopy(input1, 0, output, 0, input1.length);

		int p1=0, p2=0;
		int offset = 0;
		for(int row=1; row<height-1; row++) {
			offset = row*width;
			for(int col=1; col<width-1; col++) {
				p1 = input1[offset+col]&0xff;
				p2 = input2[offset+col]&0xff;
				if(p1 == p2) {
					output[offset+col] = (byte)0;
				}
				else {
					output[offset+col] = (byte)255;
				}
			}
		}
		binary.putGray(output);

		// release memory
		output = null;
		input1 = null;
		input2 = null;
	}

}
