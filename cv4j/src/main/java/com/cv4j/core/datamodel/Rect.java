package com.cv4j.core.datamodel;


/**
 * Created by gloomy fish on 2017/4/14.
 */

public class Rect {
    public int x;
    public int y;
    public int width;
    public int height;
    public Point tl() {
        return new Point(x, y);
    }
    public Point br() {
        return new Point(x+width, y+height);
    }
}
