package com.cv4j.core.datamodel;

/**
 * Created by Administrator on 2017/4/16.
 */

public class Line
{
    public int x1 = 0;
    public int y1 = 0;
    public int x2 = 0;
    public int y2 = 0;

    public Line(int x1, int y1, int x2, int y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    public Line() {

    }
    public Point getPoint1() {
        return new Point(x1, y1);
    }

    public Point getPoint2() {
        return new Point(x2, y2);
    }

}
