package com.cv4j.core.binary;

public class PixelNode implements Comparable<PixelNode> {
	protected int index;
	protected int row;
	protected int col;
	
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
