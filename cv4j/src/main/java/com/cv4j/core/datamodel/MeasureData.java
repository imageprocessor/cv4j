package com.cv4j.core.datamodel;

import java.text.DecimalFormat;
import java.text.NumberFormat;

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

    @Override
    public String toString() {

        NumberFormat format = new DecimalFormat("#.00");
        StringBuilder sb = new StringBuilder();
        sb.append("Point:").append(cp.x).append(",").append(cp.y).append("\n")
                .append("angle:").append((Math.abs(angle) == 0 ? 0.0 :format.format(angle))).append("\n")
                .append("area:").append((int)area).append("\n")
                .append("roundness:").append(format.format(roundness));
        return sb.toString();
    }
}
