package com.cv4j.core.datamodel;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.cv4j.exception.CV4JException;
import com.cv4j.image.util.IOUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

/**
 * Created by gloomyfish on 2017/3/25.
 */

public class CV4JImage implements ImageData, Serializable{

    private int width;
    private int height;
    private ImageProcessor processor;

    public CV4JImage(Bitmap bitmap) {
        if (bitmap == null) {
            throw new CV4JException("bitmap is null");
        }
        width = bitmap.getWidth();
        height = bitmap.getHeight();
        int[] input = new int[width * height];
        bitmap.getPixels(input, 0, width, 0, 0, width, height);
        processor = new ColorProcessor(input,width, height);
        ((ColorProcessor)processor).setCallBack(this);
        input = null;
    }

    public CV4JImage(InputStream inputStream) {
        if (inputStream == null) {
            throw new CV4JException("inputStream is null");
        }

        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(inputStream);
        } catch (OutOfMemoryError e) {
            return;
        }

        width = bitmap.getWidth();
        height = bitmap.getHeight();
        int[] input = new int[width * height];
        bitmap.getPixels(input, 0, width, 0, 0, width, height);
        processor = new ColorProcessor(input,width, height);
        ((ColorProcessor)processor).setCallBack(this);
        input = null;
        IOUtils.closeQuietly(inputStream);
    }

    public CV4JImage(byte[] bytes) {
        if (bytes == null) {
            throw new CV4JException("byte is null");
        }

        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        } catch (OutOfMemoryError e) {
            return;
        }

        width = bitmap.getWidth();
        height = bitmap.getHeight();
        int[] input = new int[width * height];
        bitmap.getPixels(input, 0, width, 0, 0, width, height);
        processor = new ColorProcessor(input,width, height);
        ((ColorProcessor)processor).setCallBack(this);
        input = null;
    }


    public ImageProcessor getProcessor() {
        return this.processor;
    }

    @Override
    public void convert2Gray() {
        if(processor instanceof ColorProcessor) {

            byte[] gray = new byte[width * height];
            int tr=0, tg=0, tb=0, c=0;
            byte[] R = ((ColorProcessor) processor).getRed();
            byte[] G = ((ColorProcessor) processor).getGreen();
            byte[] B = ((ColorProcessor) processor).getBlue();
            for (int i=0; i<gray.length; i++) {
                tr = R[i] & 0xff;
                tg = G[i] & 0xff;
                tb = B[i] & 0xff;
                c = (int) (0.299 * tr + 0.587 * tg + 0.114 * tb);
                gray[i] = (byte) c;
            }
            processor = new ByteProcessor(gray, width, height);
            ((ByteProcessor)processor).setCallBack(this);
        }
    }

    @Override
    public Bitmap toBitmap() {
        return toBitmap(Bitmap.Config.RGB_565);
    }

    @Override
    public Bitmap toBitmap(Bitmap.Config bitmapConfig) {

        Bitmap bitmap = Bitmap.createBitmap(width, height, bitmapConfig);
        if(processor instanceof ColorProcessor || processor instanceof ByteProcessor) {
            bitmap.setPixels(processor.getPixels(), 0, width, 0, 0, width, height);
        } else {
            // Exception
            Log.e("ColorImage","can not convert to bitmap!");
        }
        return bitmap;
    }


    /**
     * 保存图片到指定路径
     * @param bitmap
     * @param format 支持jpg、png、webp
     * @param path
     */
    public void savePic(Bitmap bitmap, Bitmap.CompressFormat format, String path) {
        File file = new File(path);
        try {
            FileOutputStream out = new FileOutputStream(file);
            if (bitmap.compress(format, 100, out)) {
                out.flush();
                out.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
