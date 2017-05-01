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
		int rmax = (int) Math.sqrt(width * width + height * height);
		int[] houghspace = new int[rmax * 180];
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
					for(int hs=0; hs<180; hs++) {
						h1 = (int)(col*coslut[hs] + row*sinlut[hs]);
						if ((h1 > 0) && (h1 <= rmax)) {
							houghspace[h1*180+hs]++;
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
			for(int t=0; t<180; t++) {
				int acc = houghspace[a*180+t];
				// if its higher than lowest value add it and then sort
				if (acc > result[(numLines - 1) * 3]) {

					// add to bottom of array
					result[(numLines - 1) * 3] = acc; // 累积和
					result[(numLines - 1) * 3 + 1] = a;// 半径长度
					result[(numLines - 1) * 3 + 2] = t;// 角度

					// shift up until its in right place
					int i = (numLines - 2) * 3;
					while ((i >= 0) && (result[i + 3] > result[i])) {
						for (int j = 0; j < 3; j++) {
							int temp = result[i + j];
							result[i + j] = result[i + 3 + j];
							result[i + 3 + j] = temp;
						}
						i = i - 3;
						if (i < 0)
							break;
					}
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
						h1 = (int)(col*coslut[result[i*3+2]] + row*sinlut[result[i*3+2]]);
						if ((h1 - result[i*3+1]) == 0) {
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
