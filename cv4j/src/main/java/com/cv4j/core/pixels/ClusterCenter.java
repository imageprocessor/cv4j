package com.cv4j.core.pixels;

import com.cv4j.core.datamodel.Scalar;

public class ClusterCenter {
	protected double x;
	protected double y;
	protected Scalar color;
	protected int cIndex;
	protected int numOfPoints;

	public ClusterCenter(int x, int y, int red, int green, int blue)
	{
		this.x = x;
		this.y = y;
		this.color = new Scalar(red, green, blue);
	}
	
	public double getX() {
		return x;
	}
	public void setX(double x) {
		this.x = x;
	}
	public double getY() {
		return y;
	}
	public void setY(double y) {
		this.y = y;
	}
	public Scalar getPixelColor() {
		return color;
	}
	public void setPixelColor(Scalar pixelColor) {
		this.color = pixelColor;
	}
	public int getcIndex() {
		return cIndex;
	}
	public void setcIndex(int cIndex) {
		this.cIndex = cIndex;
	}
	
	public int getNumOfPoints() {
		return numOfPoints;
	}
	
	public void addPoints()
	{
		numOfPoints++;
	}

	public void setNumOfPoints(int numOfPoints) {
		this.numOfPoints = numOfPoints;
	}


}
