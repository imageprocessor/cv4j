package com.cv4j.core.datamodel;

/**
 * Created by gloomyfish on 2017/3/25.
 */

public class ByteProcessor implements ImageProcessor {

    private int width;
    private int height;
    private byte[] GRAY;
    private int[] hist;
    private ImageData image;

    public ByteProcessor(byte[] data, int width, int height) {
        this.width = width;
        this.height = height;
        this.GRAY = data;
        // setup hist
        hist = new int[256];
        for(int i=0; i<data.length; i++) {
            hist[data[i]&0xff]++;
        }
    }

    protected void setCallBack(ImageData data) {
        this.image = data;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public int getChannels() {
        return 1;
    }

    public void getPixel(int row, int col, byte[] rgb) {
        int index = row*width + col;
        if(rgb != null && rgb.length == 1) {
            rgb[0] = GRAY[index];
        }
    }

    public byte[] getGray() {
        return GRAY;
    }

    public void putGray(byte[] gray) {
        System.arraycopy(gray, 0, GRAY, 0, gray.length);
    }

    public int[] histogram() {
        return new int[0];
    }

    public int[] getPixels() {
        int size = width * height;
        int[] pixels = new int[size];
        for (int i=0; i < size; i++)
            pixels[i] = 0xff000000 | ((GRAY[i]&0xff)<<16) | ((GRAY[i]&0xff)<<8) | GRAY[i]&0xff;
        return pixels;
    }
    public ImageData getImage() {
        return this.image;
    }

    @Override
    public float[] toFloat(int index) {
        float[] data = new float[GRAY.length];
        for(int i=0; i<data.length; i++)
            data[i] = GRAY[i]&0xff;
        return data;
    }

    @Override
    public int[] toInt(int index) {
        int[] data = new int[GRAY.length];
        for(int i=0; i<data.length; i++)
            data[i] = GRAY[i]&0xff;
        return data;
    }

    @Override
    public byte[] toByte(int index) {
        return GRAY;
    }

}
