package com.cv4j.core.datamodel;

public class Scalar {
	public int red;
	public int green;
	public int blue;
	public int alpha;
	public Scalar(int red, int green, int blue) {
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.alpha = 255;
	}
	
	public static Scalar argb(int alpha, int red, int green, int blue){
		return new Scalar(red, green, blue);
	}
	
	public static Scalar rgb(int red, int green, int blue){
		return new Scalar(red, green, blue);
	}
	
	public Scalar() {
		red = 0;
		green = 0;
		blue = 0;
		alpha = 255;
	}
	
}
