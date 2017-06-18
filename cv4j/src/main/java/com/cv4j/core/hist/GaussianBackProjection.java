package com.cv4j.core.hist;

import com.cv4j.core.datamodel.ByteProcessor;
import com.cv4j.core.datamodel.ImageProcessor;

/**
 * Created by gloomyfish on 2017/6/15.
 */

public class GaussianBackProjection {

    public void backProjection(ImageProcessor src, ByteProcessor dst, int bins, int[] ranges, int[] modelHist) {
        Mat src = imread("D:/gloomyfish/gc_test.png");
        Mat model = imread("D:/gloomyfish/gm.png");
        if (src.empty() || model.empty()) {
            printf("could not load image...\n");
            return -1;
        }
        imshow("input image", src);

        // 对每个通道 计算高斯PDF的参数
        // 有一个通道不计算，是因为它可以通过1-r-g得到
        // 无需再计算
        Mat R = Mat::zeros(model.size(), CV_32FC1);
        Mat G = Mat::zeros(model.size(), CV_32FC1);
        int r = 0, g = 0, b = 0;
        float sum = 0;
        for (int row = 0; row < model.rows; row++) {
            uchar* current = model.ptr<uchar>(row);
            for (int col = 0; col < model.cols; col++) {
                b = *current++;
                g = *current++;
                r = *current++;
                sum = b + g + r;
                R.at<float>(row, col) = r / sum;
                G.at<float>(row, col) = g / sum;
            }
        }

        // 计算均值与标准方差
        Mat mean, stddev;
        double mr, devr;
        double mg, devg;
        meanStdDev(R, mean, stddev);
        mr = mean.at<double>(0, 0);
        devr = mean.at<double>(0, 0);

        meanStdDev(G, mean, stddev);
        mg = mean.at<double>(0, 0);
        devg = mean.at<double>(0, 0);

        int width = src.cols;
        int height = src.rows;

        // 反向投影
        float pr = 0, pg = 0;
        Mat result = Mat::zeros(src.size(), CV_32FC1);
        for (int row = 0; row < height; row++) {
            uchar* currentRow = src.ptr<uchar>(row);
            for (int col = 0; col < width; col++) {
                b = *currentRow++;
                g = *currentRow++;
                r = *currentRow++;
                sum = b + g + r;
                float red = r / sum;
                float green = g / sum;
                pr = (1 / (devr*sqrt(2 * CV_PI)))*exp(-(pow((red - mr), 2)) / (2 * pow(devr, 2)));
                pg = (1 / (devg*sqrt(2 * CV_PI)))*exp(-(pow((green - mg),2)) / (2 * pow(devg, 2)));
                sum = pr*pg;
                result.at<float>(row, col) = sum;
            }
        }

        // 归一化显示高斯反向投影
        Mat img(src.size(), CV_8UC1);
        normalize(result, result, 0, 255, NORM_MINMAX);
        result.convertTo(img, CV_8U);
        Mat segmentation;
        src.copyTo(segmentation, img);

        // 显示
        imshow("backprojection demo", img);
        imshow("segmentation demo", segmentation);

        waitKey(0);
        return 0;
    }
}
