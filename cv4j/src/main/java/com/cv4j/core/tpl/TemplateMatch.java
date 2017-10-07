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
package com.cv4j.core.tpl;

import com.cv4j.core.datamodel.ByteProcessor;
import com.cv4j.core.datamodel.ColorProcessor;
import com.cv4j.core.datamodel.FloatProcessor;
import com.cv4j.core.datamodel.ImageProcessor;
import com.cv4j.core.datamodel.Point;
import com.cv4j.image.util.Tools;

import java.util.Arrays;
import java.util.List;

public class TemplateMatch {

    public static final int TM_SQDIFF_NORMED = 2;
    public static final int TM_CCORR_NORMED = 4;
    public static final int TM_CCOEFF_NORMED = 6;

    /***
     *
     * @param target - source image contain template or not
     * @param tpl - template
     * @param method
     * @return FloatProcessor -
     */
    public FloatProcessor match(ImageProcessor target, ImageProcessor tpl, int method) {
        int width = target.getWidth();
        int height = target.getHeight();
        int tw = tpl.getWidth();
        int th = tpl.getHeight();
        int offx = tpl.getWidth()/2+1;
        int offy = tpl.getHeight()/2+1;
        int raidus_width = tpl.getWidth() / 2;
        int raidus_height = tpl.getHeight()/2;
        int[] tplmask = new int[tpl.getWidth() * tpl.getHeight()];
        Arrays.fill(tplmask, 0);
        int rw = width - offx*2;
        int rh = height - offy*2;
        float[] result = new float[rw*rh];
        if(target.getChannels() == 3 && tpl.getChannels() == 3) {
            byte[] R = ((ColorProcessor)target).getRed();
            byte[] G = ((ColorProcessor)target).getGreen();
            byte[] B = ((ColorProcessor)target).getBlue();
            for(int row=offy; row<height-offy; row++) {
                for(int col=offx; col<width-offx; col++) {

                }
            }

        } else if(target.getChannels() == 1 && tpl.getChannels() == 1) {
            if(method == TM_CCORR_NORMED) {
                generateNCCResult(target, tpl, result, tplmask);
            } else if (method == TM_SQDIFF_NORMED) {
                // TODO:zhigang
            }


        } else {
            throw new IllegalStateException("\nERR:Image Type is not same...\n");
        }
        return new FloatProcessor(result, rw, rh);
    }
    /**
     *
     * @param target - source image contain template or not
     * @param tpl - template
     * @param locations, left-upper corner with match template location
     * @param method, support TM_SQDIFF\TM_SQDIFF_NORMED\TM_CCORR\TM_CCORR_NORMED
     *                TM_CCOEFF\TM_CCOEFF_NORMED
     * @param threhold
     */
    public void match(ImageProcessor target, ImageProcessor tpl, List<Point> locations, int method,double threhold) {
        int width = target.getWidth();
        int height = target.getHeight();
        int tw = tpl.getWidth();
        int th = tpl.getHeight();
        int offx = tpl.getWidth()/2+1;
        int offy = tpl.getHeight()/2+1;
        int raidus_width = tpl.getWidth() / 2;
        int raidus_height = tpl.getHeight()/2;
        int[] tplmask = new int[tpl.getWidth() * tpl.getHeight()];
        Arrays.fill(tplmask, 0);
        if(target.getChannels() == 3 && tpl.getChannels() == 3) {
            byte[] R = ((ColorProcessor)target).getRed();
            byte[] G = ((ColorProcessor)target).getGreen();
            byte[] B = ((ColorProcessor)target).getBlue();
            for(int row=offy; row<height-offy; row++) {
                for(int col=offx; col<width-offx; col++) {

                }
            }

        } else if(target.getChannels() == 1 && tpl.getChannels() == 1) {
            byte[] data = ((ByteProcessor)target).getGray();
            byte[] tdata = ((ByteProcessor)tpl).getGray();
            float[] meansdev = Tools.calcMeansAndDev(((ByteProcessor)tpl).toFloat(0));
            double[] tDiff = calculateDiff(tdata, meansdev[0]);
            for(int row=offy; row<height-offy; row+=2) {
                for(int col=offx; col<width-offx; col+=2) {
                    int wrow = 0;
                    Arrays.fill(tplmask, 0);
                    for(int subrow = -raidus_height; subrow <= raidus_height; subrow++ )
                    {
                        int wcol = 0;
                        for(int subcol = -raidus_width; subcol <= raidus_width; subcol++ )
                        {
                            if(wrow >= th || wcol >= tw)
                            {
                                continue;
                            }
                            tplmask[wrow * tw + wcol] = data[(row+subrow)*width + (col+subcol)]&0xff;
                            wcol++;
                        }
                        wrow++;
                    }
                    // calculate the ncc
                    float[] _meansDev = Tools.calcMeansAndDev(tplmask);
                    double[] diff = calculateDiff(tplmask, _meansDev[0]);
                    double ncc = calculateNcc(tDiff, diff, _meansDev[1], meansdev[1]);
                    if(ncc > threhold) {
                        Point mpoint = new Point();
                        mpoint.x = col-raidus_width;
                        mpoint.y  = row-raidus_height;
                        locations.add(mpoint);
                    }
                }
            }
        } else {
            // do nothing and throw exception later on...
            System.err.println("\nERR:could not match input image type...\n");
        }
    }

    private void generateNCCResult(ImageProcessor target, ImageProcessor tpl, float[] result, int[] tplmask) {
        int width = target.getWidth();
        int height = target.getHeight();
        int tw = tpl.getWidth();
        int th = tpl.getHeight();
        int offx = tpl.getWidth()/2+1;
        int offy = tpl.getHeight()/2+1;
        int raidus_width = tpl.getWidth() / 2;
        int raidus_height = tpl.getHeight()/2;
        byte[] data = ((ByteProcessor)target).getGray();
        byte[] tdata = ((ByteProcessor)tpl).getGray();
        float[] meansdev = Tools.calcMeansAndDev(((ByteProcessor)tpl).toFloat(0));
        double[] tDiff = calculateDiff(tdata, meansdev[0]);

        int rw = width - offx*2;
        int rh = height - offy*2;

        for(int row=offy; row<height-offy; row+=2) {
            for(int col=offx; col<width-offx; col+=2) {
                int wrow = 0;
                Arrays.fill(tplmask, 0);
                for(int subrow = -raidus_height; subrow <= raidus_height; subrow++ )
                {
                    int wcol = 0;
                    for(int subcol = -raidus_width; subcol <= raidus_width; subcol++ )
                    {
                        if(wrow >= th || wcol >= tw)
                        {
                            continue;
                        }
                        tplmask[wrow * tw + wcol] = data[(row+subrow)*width + (col+subcol)]&0xff;
                        wcol++;
                    }
                    wrow++;
                }
                // calculate the ncc
                float[] _meansDev = Tools.calcMeansAndDev(tplmask);
                double[] diff = calculateDiff(tplmask, _meansDev[0]);
                double ncc = calculateNcc(tDiff, diff, _meansDev[1], meansdev[1]);
                result[(row-offy)*rw + (col-offx)] = (float) ncc;
            }
        }
    }

    private double[] calculateDiff(byte[] pixels, float mean) {
        double[] diffs = new double[pixels.length];
        int length = diffs.length;
        for (int i = 0; i < length; i++) {
            diffs[i] = (int) (pixels[i] & 0xff) - mean;
        }
        return diffs;
    }

    private double[] calculateDiff(int[] pixels, float mean) {
        double[] diffs = new double[pixels.length];
        int length = diffs.length;
        for (int i = 0; i < length; i++) {
            diffs[i] = pixels[i] - mean;
        }
        return diffs;
    }

    private double calculateNcc(double[] tDiff, double[] diff, double dev1, double dev2) {
        double sum = 0.0d;
        double count = diff.length;
        for (int i = 0; i < diff.length; i++) {
            sum += ((tDiff[i] * diff[i]) / (dev1 * dev2));
        }
        return (sum / count);
    }

}
