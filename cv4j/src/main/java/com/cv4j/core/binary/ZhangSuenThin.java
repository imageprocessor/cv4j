package com.cv4j.core.binary;

import com.cv4j.core.datamodel.ByteProcessor;
import java.util.Arrays;

public class ZhangSuenThin {

	public void process(ByteProcessor binary) {
		int width = binary.getWidth();
		int height = binary.getHeight();
		byte[] pixels = binary.getGray();
		int[] flagmap = new int[width*height];
		Arrays.fill(flagmap, 0);

		// thinning process
		boolean stop = false;
		while(!stop) {
			// step one
			boolean s1 = step1Scan(pixels, flagmap, width, height);
			deletewithFlag(pixels, flagmap);
			Arrays.fill(flagmap, 0);
			// step two
			boolean s2 = step2Scan(pixels, flagmap, width, height);
			deletewithFlag(pixels, flagmap);
			Arrays.fill(flagmap, 0);
			if(s1 && s2) {
				stop = true;
			}
		}

	}
	
	private void deletewithFlag(byte[] pixels, int[] flagmap) {
		for(int i=0; i<pixels.length; i++) {
			if(flagmap[i] == 1) {
				pixels[i] = (byte)0;
			}
		}
		
	}

	private boolean step1Scan(byte[] input, int[] flagmap, int width, int height) {
		boolean stop = true;
		int p1=0, p2=0, p3=0;
		int p4=0, p5=0, p6=0;
		int p7=0, p8=0, p9=0;
		int offset = 0;
		for(int row=1; row<height-1; row++) {
			offset = row*width;
			for(int col=1; col<width-1; col++) {
				p1 = input[offset+col]&0xff;
				if(p1 == 0) continue;
				p2 = input[offset-width+col]&0xff;
				p3 = input[offset-width+col+1]&0xff;
				p4 = input[offset+col+1]&0xff;
				p5 = input[offset+width+col+1]&0xff;
				p6 = input[offset+width+col]&0xff;
				p7 = input[offset+width+col-1]&0xff;
				p8 = input[offset+col-1]&0xff;
				p9 = input[offset-width+col-1]&0xff;
				// match 1 - foreground, 0 - background
				p1 = (p1 == 255) ? 1 : 0;
				p2 = (p2 == 255) ? 1 : 0;
				p3 = (p3 == 255) ? 1 : 0;
				p4 = (p4 == 255) ? 1 : 0;
				p5 = (p5 == 255) ? 1 : 0;
				p6 = (p6 == 255) ? 1 : 0;
				p7 = (p7 == 255) ? 1 : 0;
				p8 = (p8 == 255) ? 1 : 0;
				p9 = (p9 == 255) ? 1 : 0;
				
				int con1 = p2+p3+p4+p5+p6+p7+p8+p9;
				String sequence = "" + String.valueOf(p2) + String.valueOf(p3) + String.valueOf(p4) + String.valueOf(p5) +
						String.valueOf(p6) + String.valueOf(p7) + String.valueOf(p8) + String.valueOf(p9) + String.valueOf(p2);
				int index1 = sequence.indexOf("01");
				int index2 = sequence.lastIndexOf("01");
				
				int con3 = p2*p4*p6;
				int con4 = p4*p6*p8;
				
				if((con1 >= 2 && con1 <= 6) && (index1 == index2) && con3 == 0 && con4 == 0) {
					flagmap[offset+col] = 1;
					stop = false;
				}

			}
		}
		return stop;
	}
	
	private boolean step2Scan(byte[] input, int[] flagmap, int width, int height) {
		boolean stop = true;
		int p1=0, p2=0, p3=0;
		int p4=0, p5=0, p6=0;
		int p7=0, p8=0, p9=0;
		int offset = 0;
		for(int row=1; row<height-1; row++) {
			offset = row*width;
			for(int col=1; col<width-1; col++) {
				p1 = input[offset+col]&0xff;
				if(p1 == 0) continue;
				p2 = input[offset-width+col]&0xff;
				p3 = input[offset-width+col+1]&0xff;
				p4 = input[offset+col+1]&0xff;
				p5 = input[offset+width+col+1]&0xff;
				p6 = input[offset+width+col]&0xff;
				p7 = input[offset+width+col-1]&0xff;
				p8 = input[offset+col-1]&0xff;
				p9 = input[offset-width+col-1]&0xff;
				// match 1 - foreground, 0 - background
				p1 = (p1 == 255) ? 1 : 0;
				p2 = (p2 == 255) ? 1 : 0;
				p3 = (p3 == 255) ? 1 : 0;
				p4 = (p4 == 255) ? 1 : 0;
				p5 = (p5 == 255) ? 1 : 0;
				p6 = (p6 == 255) ? 1 : 0;
				p7 = (p7 == 255) ? 1 : 0;
				p8 = (p8 == 255) ? 1 : 0;
				p9 = (p9 == 255) ? 1 : 0;
				
				int con1 = p2+p3+p4+p5+p6+p7+p8+p9;
				String sequence = "" + String.valueOf(p2) + String.valueOf(p3) + String.valueOf(p4) + String.valueOf(p5) +
						String.valueOf(p6) + String.valueOf(p7) + String.valueOf(p8) + String.valueOf(p9) + String.valueOf(p2);
				int index1 = sequence.indexOf("01");
				int index2 = sequence.lastIndexOf("01");
				
				int con3 = p2*p4*p8;
				int con4 = p2*p6*p8;
				
				if((con1 >= 2 && con1 <= 6) && (index1 == index2) && con3 == 0 && con4 == 0) {
					flagmap[offset+col] = 1;
					stop = false;
				}

			}
		}
		return stop;
	}

}
