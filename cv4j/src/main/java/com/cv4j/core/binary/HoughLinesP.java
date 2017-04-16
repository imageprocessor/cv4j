package com.cv4j.core.binary;

import com.cv4j.core.datamodel.ByteProcessor;
import com.cv4j.core.datamodel.Line;

public class HoughLinesP {


	public void process(ByteProcessor binary, Line[] lines, int numLines) {
		int width = binary.getWidth();
		int height = binary.getHeight();
		byte[] data = binary.getGray();
		// 初始化霍夫空间,0, 179, 90
		int p=0;
		int offset = 0;
		int[] hspace = new int[]{0, 1, 2, 89, 90, 91, 178, 179};
		int rmax = (int) Math.sqrt(width * width + height * height);
		int[] houghspace = new int[rmax * hspace.length];
		// 建立查找表
		double[] sinlut = setupSinLUT(); // new double[180];
		double[] coslut = setupCosLUT(); // new double[180];
		// 霍夫变换
		int h1=0;
		for (int row = 0; row < height; row++) {
			offset = row * width;
			for (int col = 0; col < width; col++) {
				p=data[offset]&0xff;
				if(p==255) {
					for(int hs=0; hs<hspace.length; hs++) {
						h1 = (int)(col*coslut[hspace[hs]] + row*sinlut[hspace[hs]]);
						if ((h1 > 0) && (h1 <= rmax)) {
							houghspace[h1*hspace.length+hs]++;
						}
					}
				}
				offset++;
			}
		}
		
		// find first K lines
		int[] result = new int[numLines*3];
		int max = 0;
		for(int a=0; a<rmax; a++) {
			for(int t=0; t<8; t++) {
				int h = houghspace[a*hspace.length+t];
				if(h > max) {
					max = h;
					result[3] = result[0];
					result[4] = result[1];
					result[5] = result[2];
					
					result[0] = max;
					result[1] = hspace[t]; // 角度
					result[2] = a; // 半径
				}
			}
		}
		
		// draw detected lines
		for (int row = 0; row < height; row++) {
			offset = row * width;
			for (int col = 0; col < width; col++) {
				p=data[offset]&0xff;
				if(p==255) {
					for(int i=0; i<numLines; i++) {
						h1 = (int)(col*coslut[result[i*3+1]] + row*sinlut[result[i*3+1]]);
						if ((h1 - result[i*3+2]) == 0) {
							data[offset] = (byte)255;
							if(lines[i] == null) {
								lines[i] = new Line();
								lines[i].x1 = col;
								lines[i].y1 = row;
								lines[i].x2 = col;
								lines[i].y2 = row;
							}
							if(lines[i].x1 > col && lines[i].y1>row) {
								lines[i].x1 = col;
								lines[i].y1 = row;
							}
							if(lines[i].x2 < col && lines[i].y2 < row ) {
								lines[i].x2 = col;
								lines[i].y2 = row;
							}
						}
					}
				}
				offset++;
			}
		}
	}

	private double[] setupCosLUT() {
		double[] coslut = new double[180];
		for (int theta = 0; theta < 180; theta++) {
			coslut[theta] = Math.cos((theta * Math.PI) / 180.0); 
		}
		return coslut;
	}

	private double[] setupSinLUT() {
		double[] sinlut = new double[180];
		for (int theta = 0; theta < 180; theta++) {
			sinlut[theta] = Math.sin((theta * Math.PI) / 180.0); 
		}
		return sinlut;
	}

}
