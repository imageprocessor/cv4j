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

import com.cv4j.core.datamodel.ByteProcessor;
import com.cv4j.core.datamodel.Vec3i;

import java.util.Arrays;
import java.util.List;

/**
 * detect circle
 *
 */
public class HoughCircles {

    /***
     *
     * @param binary - image data
     * @param circles - number of circles we can detect or find
     * @param minRadius - min radius of circle can be found from hough space
     * @param maxRadius - max radius of circle can be found from hough space
     * @param maxonly - find the max accumulate hough space point only
     * @param accumulate - find the hough space acc value which more than input threshold t
     */
    public void process(ByteProcessor binary, List<Vec3i> circles, int minRadius, int maxRadius, boolean maxonly, int accumulate) {
        int width = binary.getWidth();
        int height = binary.getHeight();
        byte[] data = binary.getGray();

        // initialize the polar coordinates space/Hough Space
        int numOfR = (maxRadius - minRadius) + 1;
        int[][] acc = new int[numOfR][width * height];
        for(int i=0; i<numOfR; i++) {
            Arrays.fill(acc[i], 0);
        }

        int x0, y0;
        double t;
        double[] coslut = setupCosLUT();
        double[] sinlut = setupSinLUT();

        // convert to hough space and calculate accumulate
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if ((data[y * width + x] & 0xff) == 255) {
                    for (int theta = 0; theta < 360; theta++) {
                        for(int r=minRadius; r<=maxRadius; r++) {
                            x0 = (int) Math.round(x - r * coslut[theta]);
                            y0 = (int) Math.round(y - r * sinlut[theta]);
                            if (x0 < width && x0 > 0 && y0 < height && y0 > 0) {
                                acc[r-minRadius][x0 + (y0 * width)] += 1;
                            }
                        }
                    }
                }
            }
        }

        // find maximum for each space
        int[] tempCircle = new int[3];
//        int[] output = new int[width * height];

        // find the center and R for each circle
        for(int i=0; i<numOfR; i++) {
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    int value = (acc[i][x + (y * width)] & 0xff);

                    // if its higher than current value, swap it
                    if (maxonly) {
                        if(value > tempCircle[0]) {
                            tempCircle[0] = value; //radius?
                            tempCircle[1] = x; // center.x
                            tempCircle[2] = y; // center.y
                        }
                    }else {
                        if(value > accumulate) { // filter by threshold
                            Vec3i vec3i = new Vec3i();
                            vec3i.x = x;
                            vec3i.y = y;
                            vec3i.z = value;
                            circles.add(vec3i);
                        }
                    }
                }
            }

            if(maxonly) {
                Vec3i vec3i = new Vec3i();
                vec3i.x = tempCircle[1];
                vec3i.y = tempCircle[2];
                vec3i.z = tempCircle[0];
                circles.add(vec3i);
            }
        }

    }

    private double[] setupCosLUT() {
        double[] coslut = new double[360];
        for (int theta = 0; theta < 360; theta++) {
            coslut[theta] = Math.cos((theta * Math.PI) / 180.0);
        }
        return coslut;
    }

    private double[] setupSinLUT() {
        double[] sinlut = new double[360];
        for (int theta = 0; theta < 360; theta++) {
            sinlut[theta] = Math.sin((theta * Math.PI) / 180.0);
        }
        return sinlut;
    }

}

