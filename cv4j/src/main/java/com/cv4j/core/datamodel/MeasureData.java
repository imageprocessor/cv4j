/**
 * Copyright (c) 2017-present, CV4J Contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cv4j.core.datamodel;

import java.text.DecimalFormat;
import java.text.NumberFormat;

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
