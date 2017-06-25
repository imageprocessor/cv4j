package com.cv4j.image.util;

import com.cv4j.core.binary.ConnectedAreaLabel;
import com.cv4j.core.binary.MorphOpen;
import com.cv4j.core.binary.Threshold;
import com.cv4j.core.datamodel.ByteProcessor;
import com.cv4j.core.datamodel.ImageProcessor;
import com.cv4j.core.datamodel.Rect;
import com.cv4j.core.datamodel.Size;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gloomy fish on 2017/6/25.
 */

public class QRCodeScanner {

    public Rect findQRCodeBounding(ImageProcessor image) {
        Rect rect = new Rect();
        image.getImage().convert2Gray();
        ByteProcessor src = ((ByteProcessor)image);
        int width = src.getWidth();
        int height = src.getHeight();
        Threshold t = new Threshold();
        t.process(src, Threshold.THRESH_VALUE, Threshold.METHOD_THRESH_BINARY_INV, 20);
        MorphOpen mOpen = new MorphOpen();
        byte[] data = new byte[width*height];
        System.arraycopy(src.getGray(), 0, data, 0, data.length);
        ByteProcessor copy = new ByteProcessor(data, width, height);
        mOpen.process(src, new Size(4, 24)); // Y
        mOpen.process(copy, new Size(24, 4)); // X
        for(int i=0; i<data.length; i++) {
            int pv = src.getGray()[i]&0xff;
            if(pv == 255) {
                copy.getGray()[i] = (byte)255;
            }
        }
        src.putGray(copy.getGray());
        ConnectedAreaLabel ccal = new ConnectedAreaLabel();
        List<Rect> rectList = new ArrayList<>();
        int[] labelMask = new int[width*height];
        ccal.process(src, labelMask, rectList, true);
        float w = 0;
        float h = 0;
        float rate = 0;
        List<Rect> qrRects = new ArrayList<>();
        for(Rect roi : rectList) {
            w = roi.width;
            h = roi.height;
            rate = (float)Math.abs(w / h  - 1.0);
            if(rate < 0.05 && isRect(roi, labelMask, width, height)) {
                qrRects.add(roi);
            }
        }

        // find RQ code bounding
        Rect[] blocks = qrRects.toArray(new Rect[0]);
        if (blocks.length == 1) {
            rect.x = blocks[0].x-5;
            rect.y = blocks[0].y- 5;
            rect.width= blocks[0].width + 10;
            rect.height = blocks[0].height + 10;
        }
        else if (blocks.length == 3) {
            for (int i = 0; i < 2; i++) {
                int idx1 = blocks[i].tl().y*width + blocks[i].tl().x;
                for (int j = i + 1; j < 3; j++) {
                    int idx2 = blocks[j].tl().y*width + blocks[j].tl().x;
                    if (idx2 < idx1){
                        Rect temp = blocks[i];
                        blocks[i] = blocks[j];
                        blocks[j] = temp;
                    }
                }
            }
            rect.x = blocks[0].x - 5;
            rect.y = blocks[0].y - 5;
            rect.width = blocks[1].width + (blocks[1].x - blocks[0].x) + 10;
            rect.height = (blocks[2].height + blocks[2].y - blocks[0].y) + 10;

        } else {
            rect.width = 0;
            rect.height = 0;
        }
        return rect;
    }

    private boolean isRect(Rect roi, int[] labelMask, int w, int h) {
        int ox = roi.x;
        int oy = roi.y;
        int width = roi.width;
        int height = roi.height;

        byte[] image = new byte[width*height];
        int label = roi.labelIdx;
        for(int row=oy; row<(oy + height); row++) {
            for(int col=ox; col<(ox + width); col++) {
                int v = labelMask[row*w + col];
                if(v == label) {
                    image[(row - oy) * width + col - ox] = (byte)255;
                }
            }
        }

        int cx = width / 2;
        int offset = 0;
        if (width % 2 > 0) {
            offset = 1;
        }

        int sum=0;
        int v1=0, v2=0;
        for(int row=0; row<height; row++) {
            for(int col=0; col<cx; col++) {
                v1 = image[row*width+ col]&0xff;
                v2 = image[row*width-1-col]&0xff;
                sum += Math.abs(v1-v2);
            }
        }
        return (sum / 255) <= 10;
    }
}
