package com.cv4j.core.binary;

import com.cv4j.core.datamodel.MeasureData;
import com.cv4j.core.datamodel.Point;

import java.util.List;

/**
 * Created by gloomy fish on 2017/5/1.
 */

public class GeoMoments {

    /***
     *
     * @param pixelList - the tagged pixel of this contours
     * @return some benchmark data for the contour
     */
    public MeasureData calculate(List<PixelNode> pixelList) {
        MeasureData measures = new MeasureData();
        measures.setArea(pixelList.size());
        measures.setCp(getCenterPoint(pixelList));

        double m11 = centralMoments(pixelList, 1, 1);
        double m02 = centralMoments(pixelList, 0, 2);
        double m20 = centralMoments(pixelList, 2, 0);
        double m112 = m11 * m11;
        double dd = Math.pow((m20-m02), 2);
        double sum1 = Math.sqrt(dd + 4*m112);
        double sum2 = m02 + m20;
        double a1 = sum2 + sum1;
        double a2 = sum2 - sum1;

        double ra = Math.sqrt((2*a1)/Math.abs(pixelList.size()));
        double rb = Math.sqrt((2*a2)/Math.abs(pixelList.size()));
        double angle = Math.atan((2*m11)/(m20 - m02))/2.0;
        measures.setAngle(angle);
        measures.setRoundness(ra/rb);
        return measures;
    }

    private Point getCenterPoint(List<PixelNode> pixelList)
    {
        double m00 = moments(pixelList, 0, 0);
        double xCr = moments(pixelList, 1, 0) / m00; // row
        double yCr = moments(pixelList, 0, 1) / m00; // column
        return new Point((int)xCr, (int)yCr);
    }

    private double moments(List<PixelNode> pixelList, int p, int q)
    {
        double mpq = 0.0;
        int index = 0;
        for(PixelNode pixel : pixelList) {
            int row = pixel.row;
            int col = pixel.col;
            mpq += Math.pow(row, p) * Math.pow(col, q);
        }
        return mpq;
    }

    private double centralMoments(List<PixelNode> pixelList, int p, int q)
    {
        double m00 = moments(pixelList, 0, 0);
        double xCr = moments(pixelList, 1, 0) / m00;
        double yCr = moments(pixelList, 0, 1) / m00;
        double cMpq = 0.0;
        int index = 0;
        for(PixelNode pixel : pixelList) {
            int row = pixel.row;
            int col = pixel.col;
            cMpq += Math.pow(row - xCr, p) * Math.pow(col - yCr, q);
        }
        return cMpq;
    }
}
