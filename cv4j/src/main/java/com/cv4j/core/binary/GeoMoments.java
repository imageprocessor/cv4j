/*
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
package com.cv4j.core.binary;

import com.cv4j.core.datamodel.MeasureData;
import com.cv4j.core.datamodel.Point;

import java.util.List;

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
        double angle = ((m20 - m02) == 0) ? Math.PI/4.0 : Math.atan((2*m11)/(m20 - m02))/2.0;
        measures.setAngle(angle);
        measures.setRoundness(rb == 0? Double.MAX_VALUE : ra/rb);
        return measures;
    }

    private Point getCenterPoint(List<PixelNode> pixelList)
    {
        double m00 = moments(pixelList, 0, 0);
        double yCr = moments(pixelList, 1, 0) / m00; // row
        double xCr = moments(pixelList, 0, 1) / m00; // column
        return new Point((int)xCr, (int)yCr);
    }

    private double moments(List<PixelNode> pixelList, int p, int q)
    {
        double mpq = 0.0;

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
        double yCr = moments(pixelList, 1, 0) / m00;
        double xCr = moments(pixelList, 0, 1) / m00;
        double cMpq = 0.0;

        for(PixelNode pixel : pixelList) {
            int row = pixel.row;
            int col = pixel.col;
            cMpq += Math.pow(row - yCr, p) * Math.pow(col - xCr, q);
        }
        return cMpq;
    }
}
