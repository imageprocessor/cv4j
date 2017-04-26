package com.cv4j.core.binary;

public class PixelNode implements Comparable<PixelNode> {

	public int index;
	public int row;
	public int col;
	
	@Override
	public int compareTo(PixelNode p) {
		if (index > p.index) {
			return 1;
		} else if (index < p.index) {
			return -1;
		} else {
			return 0;
		}
	}
}
