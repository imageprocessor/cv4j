package com.cv4j.core.hist;

import com.cv4j.core.datamodel.ByteProcessor;

/**
 * Created by gloomy fish on 2017/5/14.
 */

public class ProjectionHist {
    public final static int X_DIRECTION = 1;
    public final static int Y_DIRECTION = 2;

    /***
     *
     * @param src - binary image
     * @param direction - X or Y direction
     * @param bins - number of bins
     * @param output
     */
    public void projection(ByteProcessor src, int direction, int bins, double[] output) {
        // calculate Y Projection
        int width = src.getWidth();
        int height = src.getHeight();
        byte[] data = src.getGray();
        float xstep = width / 4.0f;
        float ystep = height / 5.0f;
        int index = 0;
        if(direction == X_DIRECTION) {
            xstep = width / bins;
            for (float x = 0; x < width; x += xstep) {
                if ((xstep + x) - width > 1)
                    continue;
                output[index] = getWeightBlackNumber(data, width, height, x, 0, xstep, height);
                index++;
            }
        } else {
            // calculate X Projection
            ystep = height / bins;
            for (float y = 0; y < height; y += ystep) {
                if ((y + ystep) - height > 1) continue;
                output[index] = getWeightBlackNumber(data, width, height, 0, y, width, ystep);
                index++;
            }
        }
    }

    private float getWeightBlackNumber(byte[] data, float width, float height, float x, float y, float xstep, float ystep) {
        float weightNum = 0;

        // 整数部分
        int nx = (int)Math.floor(x);
        int ny = (int)Math.floor(y);

        // 小数部分
        float fx = x - nx;
        float fy = y - ny;

        // 宽度与高度
        float w = x+xstep;
        float h = y+ystep;
        if(w > width) {
            w = width - 1;
        }
        if(h > height) {
            h = height - 1;
        }

        // 宽高整数部分
        int nw = (int)Math.floor(w);
        int nh = (int)Math.floor(h);

        // 小数部分
        float fw = w - nw;
        float fh = h - nh;

        // 统计黑色像素个数
        int c = 0;
        int ww = (int)width;
        float weight = 0;
        int row=0;
        int col=0;
        for(row=ny; row<nh; row++) {
            for(col=nx; col<nw; col++) {
                c = data[row*ww+col]&0xff;
                if(c == 0) {
                    weight++;
                }
            }
        }

        // 计算小数部分黑色像素权重加和
        float w1=0, w2=0, w3=0, w4=0;
        // calculate w1
        if(fx > 0) {
            col = nx+1;
            if(col > width - 1) {
                col = col - 1;
            }
            float count = 0;
            for(row=ny; row<nh; row++) {
                c = data[row*ww+col]&0xff;
                if(c == 0){
                    count++;
                }
            }
            w1 = count*fx;
        }

        // calculate w2
        if(fy > 0) {
            row = ny+1;
            if(row > height - 1) {
                row = row - 1;
            }
            float count = 0;
            for(col=nx; col<nw; col++) {
                c = data[row*ww+col]&0xff;
                if(c == 0){
                    count++;
                }
            }
            w2 = count*fy;
        }

        if(fw > 0) {
            col = nw + 1;
            if(col > width - 1) {
                col = col - 1;
            }
            float count = 0;
            for(row=ny; row<nh; row++) {
                c = data[row*ww+col]&0xff;
                if(c == 0) {
                    count++;
                }
            }
            w3 = count*fw;
        }

        if(fh > 0) {
            row = nh + 1;
            if(row > height - 1) {
                row = row - 1;
            }
            float count = 0;
            for(col=nx; col<nw; col++) {
                c = data[row*ww+col]&0xff;
                if(c == 0) {
                    count++;
                }
            }
            w4 = count*fh;
        }

        weightNum = (weight - w1 - w2 + w3 + w4);
        if(weightNum < 0) {
            weightNum = 0;
        }
        return weightNum;
    }
}
