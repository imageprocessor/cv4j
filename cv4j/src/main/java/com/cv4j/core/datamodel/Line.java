package com.cv4j.core.datamodel;

/**
 * Created by Administrator on 2017/4/16.
 */

public class Line
{
    public int x1;
    public int y1;
    public int x2;
    public int y2;

    public Line() {
        x1 = 0;
        x2 = 0;
        y1 = 0;
        y2 = 0;
    }

    public Point getPoint1() {
        return new Point(x1, y1);
    }

    public Point getPoint2() {
        return new Point(x2, y2);
    }

}
