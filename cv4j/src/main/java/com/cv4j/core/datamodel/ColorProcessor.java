package com.cv4j.core.datamodel;
import java.io.Serializable;

public class ColorProcessor implements ImageProcessor,Serializable {
    private byte[] R;
    private byte[] G;
    private byte[] B;

    private int width;
    private int height;

    public ColorProcessor(int[] pixels, int width, int height) {
        this.width = width;
        this.height = height;
        int size = width * height;
        R = new byte[size];
        G = new byte[size];
        B = new byte[size];
        backFillData(pixels);
    }

    private void backFillData(int[] input) {
        int c=0, r=0, g=0, b=0;
        for(int i=0; i<input.length; i++) {
            c = input[i];
            r = (c&0xff0000)>>16;
            g = (c&0xff00)>>8;
            b = c&0xff;
            R[i] = (byte)r;
            G[i] = (byte)g;
            B[i] = (byte)b;
        }
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
        return 3;
    }

    public byte[] getRed() {
        return R;
    }

    public byte[] getGreen() {
        return G;
    }

    public byte[] getBlue() {
        return B;
    }

    @Override
    public void getPixel(int row, int col, byte[] rgb) {
        int index = row*width + col;
        if(rgb != null && rgb.length == 3) {
            rgb[0] = R[index];
            rgb[1] = G[index];
            rgb[2] = B[index];
        }
    }

    public void putRGB(byte[] red, byte[] green, byte[] blue) {
        System.arraycopy(red, 0, R, 0, red.length);
        System.arraycopy(green, 0, G, 0, green.length);
        System.arraycopy(blue, 0, B, 0, blue.length);
    }

    public int[] getPixels() {
        int size = width * height;
        int[] pixels = new int[size];
        for (int i=0; i < size; i++)
            pixels[i] = 0xff000000 | ((R[i]&0xff)<<16) | ((G[i]&0xff)<<8) | B[i]&0xff;
        return pixels;
    }
}
