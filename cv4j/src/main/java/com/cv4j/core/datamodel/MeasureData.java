package com.cv4j.core.datamodel;

/**
 * Created by gloomy fish on 2017/5/1.
 */

public class MeasureData {

    private Point cp; // center x of the contour and center y of the contour
    private double angle; // angle of the contour rotated
    private double area; // measure the area of contour
    private double roundness; // measure the possible circle of the contour
    public MeasureData() {
        super();
    }

    public Point getCp() {
        return cp;
    }

    public void setCp(Point cp) {
        this.cp = cp;
    }

    public double getAngle() {
        return angle;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }

    public double getArea() {
        return area;
    }

    public void setArea(double area) {
        this.area = area;
    }

    public double getRoundness() {
        return roundness;
    }

    public void setRoundness(double roundness) {
        this.roundness = roundness;
    }
}
