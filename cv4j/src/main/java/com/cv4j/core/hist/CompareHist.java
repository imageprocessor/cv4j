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
package com.cv4j.core.hist;

import com.cv4j.exception.CV4JException;

public class CompareHist {

    /**
     * 0 到 1 之间取值
     * @param source
     * @param target
     * @return
     */
    public double bhattacharyya(int[] source, int[] target) {
        if(source.length != target.length) {
            throw new CV4JException("number of histogram bins is not same...");
        }
        int len = source.length;
        double[] mixedData = new double[len];
        // start to normalize the histogram data
        double[] hist1 = new double[len];
        double[] hist2 = new double[len];
        double sum1=0, sum2=0;
        for (int i = 0; i < len; i++)
        {
            sum1 += source[i];
            sum2 += target[i];
        }
        for (int i = 0; i < len; i++)
        {
            hist1[i] = source[i] / sum1;
            hist2[i] = target[i] / sum2;
        }

        for(int i=0; i<len; i++ ) {
            mixedData[i] = Math.sqrt(hist1[i] * hist2[i]);
        }

        // The values of Bhattacharyya Coefficient ranges from 0 to 1,
        double similarity = 0;
        for(int i=0; i<mixedData.length; i++ ) {
            similarity += mixedData[i];
        }

        // The degree of similarity
        return similarity;
    }

    public double covariance(int[] source, int[] target) {
        if(source.length != target.length) {
            throw new CV4JException("number of histogram bins is not same...");
        }
        int len = source.length;
        double[] mixedData = new double[len];
        // start to normalize the histogram data
        double sum1=0, sum2=0;
        for (int i = 0; i < len; i++)
        {
            sum1 += source[i];
            sum2 += target[i];
        }

        double m1 = sum1 / len;
        double m2 = sum2 / len;

        for(int i=0; i<len; i++ ) {
            mixedData[i] = (source[i] - m1)*(target[i]-m2);
        }

        // The values of Bhattacharyya Coefficient ranges from 0 to 1,
        double similarity = 0;
        for(int i=0; i<mixedData.length; i++ ) {
            similarity += mixedData[i];
        }

        // The degree of similarity
        return similarity / len;
    }

    /**
     * -1 到 1 之间取值
     * @param source
     * @param target
     * @return
     */
    public double ncc(int[] source, int[] target) {
        if(source.length != target.length) {
            throw new CV4JException("number of histogram bins is not same...");
        }
        int len = source.length;
        double[] mixedData = new double[len];
        // start to normalize the histogram data
        double sum1=0, sum2=0;
        for (int i = 0; i < len; i++)
        {
            sum1 += source[i];
            sum2 += target[i];
        }

        double m1 = sum1 / len;
        double m2 = sum2 / len;
        sum1 = 0;
        sum2 = 0;
        double sum3 = 0;
        for(int i=0; i<len; i++ ) {
            sum3 += ((source[i] - m1)*(target[i]-m2));
            sum1 += ((source[i] - m1)*(source[i]-m1));
            sum2 += ((target[i] - m2)*(target[i]-m2));
        }
        double ncc = 0.0;
        ncc = sum3 / Math.sqrt(sum1*sum2);
        return ncc;
    }
}
