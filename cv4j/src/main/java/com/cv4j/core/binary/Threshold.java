package com.cv4j.core.binary;

import com.cv4j.core.datamodel.ByteProcessor;

/**
 * Created by gloomy fish on 2017/4/14.
 */

public class Threshold {
    /** binary image */
    public static int THRESH_BINARY = 0;
    /** invert binary image */
    public static int THRESH_BINARY_INV = 1;

    /** it is not reasonable method to convert binary image */
    public static int THRESH_MEANS = 1;
    /** it is popular binary method in OPENCV and MATLAB */
    public static int THRESH_OTSU = 2;
    /** histogram statistic threshold method*/
    public static int THRESH_TRIANGLE = 3;
    /**based on 1D mean shift, CV4J custom binary method, sometimes it is very slow...*/
    public static int THRESH_MEANSHIFT = 4;

    /**
     *
     * @param gray - gray image data, ByteProcessor type
     * @param type - binary segmentation method, int
     */
    public void process(ByteProcessor gray, int type) {
        process(gray, type, THRESH_BINARY, 0);
    }

    /**
     *
     * @param gray - gray image data, ByteProcessor type
     * @param type - binary segmentation method, int
     * @param thresh - threshold value you are going to use it if type = 0;
     */
    public void process(ByteProcessor gray, int type, int method, int thresh) {
        int tvalue = thresh;
        if(type == THRESH_MEANS) {
            tvalue = getMeanThreshold(gray);
        }
        else if(type == THRESH_OTSU) {
            tvalue = getOTSUThreshold(gray);
        }
        else if(type == THRESH_TRIANGLE) {
            tvalue = getTriangleThreshold(gray);
        }
        else if(type == THRESH_MEANSHIFT) {
            tvalue = shift(gray);
        }

        byte[] data = gray.getGray();
        int c = 0;
        for(int i=0; i<data.length; i++) {
            c = data[i]&0xff;
            if(c <= tvalue) {
                data[i] = (method == THRESH_BINARY_INV)?(byte)255 : (byte)0;
            } else {
                data[i] = (method == THRESH_BINARY_INV)?(byte)0 : (byte)255;
            }
        }
    }

    private int getMeanThreshold(ByteProcessor gray) {
        byte[] data = gray.getGray();
        int sum = 0;
        for(int i=0; i<data.length; i++) {
            sum += data[i]&0xff;
        }
        return sum / data.length;
    }

    private int getOTSUThreshold(ByteProcessor gray) {
        // 获取直方图
        int[] histogram = new int[256];
        byte[] data = gray.getGray();
        int c = 0;
        for(int i=0; i<data.length; i++) {
            c = data[i]&0xff;
            histogram[c]++;
        }
        // 图像二值化 - OTSU 阈值化方法
        double total = data.length;
        double[] variances = new double[256];
        for(int i=0; i<variances.length; i++)
        {
            double bw = 0;
            double bmeans = 0;
            double bvariance = 0;
            double count = 0;
            for(int t=0; t<i; t++)
            {
                count += histogram[t];
                bmeans += histogram[t] * t;
            }
            bw = count / total;
            bmeans = (count == 0) ? 0 :(bmeans / count);
            for(int t=0; t<i; t++)
            {
                bvariance += (Math.pow((t-bmeans),2) * histogram[t]);
            }
            bvariance = (count == 0) ? 0 : (bvariance / count);
            double fw = 0;
            double fmeans = 0;
            double fvariance = 0;
            count = 0;
            for(int t=i; t<histogram.length; t++)
            {
                count += histogram[t];
                fmeans += histogram[t] * t;
            }
            fw = count / total;
            fmeans = (count == 0) ? 0 : (fmeans / count);
            for(int t=i; t<histogram.length; t++)
            {
                fvariance += (Math.pow((t-fmeans),2) * histogram[t]);
            }
            fvariance = (count == 0) ? 0 : (fvariance / count);
            variances[i] = bw * bvariance + fw * fvariance;
        }

        // find the minimum within class variance
        double min = variances[0];
        int threshold = 0;
        for(int m=1; m<variances.length; m++)
        {
            if(min > variances[m]){
                threshold = m;
                min = variances[m];
            }
        }
        // 二值化
        System.out.println("final threshold value : " + threshold);
        return threshold;
    }

    private int getTriangleThreshold(ByteProcessor gray) {
        // 获取直方图
        int[] histogram = new int[256];
        byte[] data = gray.getGray();
        int c = 0;
        for(int i=0; i<data.length; i++) {
            c = data[i]&0xff;
            histogram[c]++;
        }

        int left_bound = 0, right_bound = 0, max_ind = 0, max = 0;
        int temp;
        boolean isflipped = false;
        int i=0, j=0;
        int N = 256;

        // 找到最左边零的位置
        for( i = 0; i < N; i++ )
        {
            if( histogram[i] > 0 )
            {
                left_bound = i;
                break;
            }
        }
        // 位置再移动一个步长，即为最左侧零位置
        if( left_bound > 0 )
            left_bound--;

        // 找到最右边零点位置
        for( i = N-1; i > 0; i-- )
        {
            if( histogram[i] > 0 )
            {
                right_bound = i;
                break;
            }
        }
        // 位置再移动一个步长，即为最右侧零位置
        if( right_bound < N-1 )
            right_bound++;

        // 在直方图上寻找最亮的点Hmax
        for( i = 0; i < N; i++ )
        {
            if( histogram[i] > max)
            {
                max = histogram[i];
                max_ind = i;
            }
        }

        // 如果最大值落在靠左侧这样就无法满足三角法求阈值，所以要检测是否最大值是否靠近左侧
        // 如果靠近左侧则通过翻转到右侧位置。
        if( max_ind-left_bound < right_bound-max_ind)
        {
            isflipped = true;
            i = 0;
            j = N-1;
            while( i < j )
            {
                // 左右交换
                temp = histogram[i]; histogram[i] = histogram[j]; histogram[j] = temp;
                i++; j--;
            }
            left_bound = N-1-right_bound;
            max_ind = N-1-max_ind;
        }

        // 计算求得阈值
        double thresh = left_bound;
        double a, b, dist = 0, tempdist;
        a = max; b = left_bound-max_ind;
        for( i = left_bound+1; i <= max_ind; i++ )
        {
            // 计算距离 - 不需要真正计算
            tempdist = a*i + b*histogram[i];
            if( tempdist > dist)
            {
                dist = tempdist;
                thresh = i;
            }
        }
        thresh--;

        // 对已经得到的阈值T,如果前面已经翻转了，则阈值要用255-T
        if( isflipped )
            thresh = N-1-thresh;

        return (int)thresh;
    }

    private int shift(ByteProcessor gray) {
        // find threshold
        int t = 127, nt = 0;
        int m1=0, m2=0;
        int sum1=0, sum2=0;
        int count1=0, count2=0;
        int count = 0 ;
        int[] data = gray.toInt(0);
        while(true) {
            for(int i=0; i<data.length; i++) {
                if(data[i] > t) {
                    sum1 += data[i];
                    count1++;
                }
                else {
                    sum2 += data[i];
                    count2++;
                }
            }
            m1 = sum1 / count1;
            m2 = sum2 / count2;

            sum1 = 0;
            sum2 = 0;
            count1 = 0;
            count2 = 0;
            nt = (m1 + m2) / 2;
            if(t == nt) {
                break;
            }
            else {
                t = nt;
            }
        }
        return t;
    }
}
