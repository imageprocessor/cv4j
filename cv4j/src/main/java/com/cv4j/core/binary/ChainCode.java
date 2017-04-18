package com.cv4j.core.binary;

import com.cv4j.core.datamodel.ByteProcessor;
import com.cv4j.exception.CV4JException;

import java.util.Arrays;

public class ChainCode extends CourtEdge {

	public void process(ByteProcessor binary, int[] codeMap) {
		super.process(binary);
		int width = binary.getWidth();
		int height = binary.getHeight();
		if(codeMap.length != (width*height))
		{
			throw new CV4JException("chain code map length assert failure");
		}
		byte[] input = binary.getGray();
		byte[] output = new byte[width*height];
		System.arraycopy(input, 0, output, 0, input.length);
		
		// initialization code map
		Arrays.fill(codeMap, -1);
		int offset = 0;
		for(int row=0; row<height; row++) {
			offset = row*width;
			for(int col=0; col<width; col++) {
				int pv = input[offset+col]&0xff;
				if(pv == 255) {
					// do something here!!!
					int code = getRelationship(input, codeMap, row, col, width, height);
					if(code >= 0) {
						codeMap[offset+col] = code;
					}
				}
			}
		}

	}

	private int getRelationship(byte[] pixels1, int[] codemap, int row, int col, int width, int height) {
		int offset = row*width;
		if((col+1) < width) {
			int pv = pixels1[offset+col+1]&0xff;
			int c = codemap[offset+col+1];
			if(pv == 255 && c < 0 ) {
				return 0;
			}
		}
		if((col+1) < width && (row+1) < height) {
			int pv = pixels1[offset+width+col+1]&0xff;
			int c = codemap[offset+width+col+1];
			if(pv == 255 && c < 0 ) {
				return 1;
			}
		}
		if((row+1) < height) {
			int pv = pixels1[offset+width+col]&0xff;
			int c = codemap[offset+width+col];
			if(pv == 255 && c < 0) {
				return 2;
			}
		}
		if((col-1) >= 0 && (row+1) < height) {
			int pv = pixels1[offset+width+col-1]&0xff;
			int c = codemap[offset+width+col-1];
			if(pv == 255 && c < 0 ) {
				return 3;
			}
		}
		if((col-1) >= 0) {
			int pv = pixels1[offset+col-1]&0xff;
			int c = codemap[offset+col-1];
			if(pv == 255 && c < 0 ) {
				return 4;
			}
		}
		if((col-1) >= 0 && (row-1) >= 0) {
			int pv = pixels1[offset-width+col-1]&0xff;
			int c = codemap[offset-width+col-1];
			if(pv == 255 && c < 0 ) {
				return 5;
			}
		}
		if((row-1) >= 0) {
			int pv = pixels1[offset-width+col]&0xff;
			int c = codemap[offset-width+col];
			if(pv == 255 && c < 0 ) {
				return 6;
			}
		}
		if((row-1) >= 0 && (col+1) < width) {
			int pv = pixels1[offset-width+col+1]&0xff;
			int c = codemap[offset-width+col+1];
			if(pv == 255 && c < 0) {
				return 7;
			}
		}
		return -2; // invalid, stop condition
	}
}
