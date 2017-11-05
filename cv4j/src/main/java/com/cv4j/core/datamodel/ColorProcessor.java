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
package com.cv4j.core.datamodel;

import com.cv4j.exception.CV4JException;

public class ColorProcessor implements ImageProcessor {
    private byte[] R;
    private byte[] G;
    private byte[] B;
    private ImageData image;

    private int width;
    private int height;
    
    public ColorProcessor(int width, int height) {
        this.width = width;
        this.height = height;
        int size = width * height;
        R = new byte[size];
        G = new byte[size];
        B = new byte[size];
    }


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
        int length = input.length;
        for(int i=0; i<length; i++) {
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

    protected void setCallBack(ImageData data) {
        this.image = data;
    }

    public ImageData getImage() {
        return this.image;
    }

    @Override
    public float[] toFloat(int index) {
        if(index == 0) {
            float[] data = new float[R.length];
            int length = data.length;
            for(int i=0; i<length; i++)
                data[i] = R[i]&0xff;
            return data;
        }
        else if(index == 1) {
            float[] data = new float[G.length];
            int length = data.length;
            for(int i=0; i<length; i++)
                data[i] = G[i]&0xff;
            return data;
        }
        else if(index == 2) {
            float[] data = new float[B.length];
            int length = data.length;
            for(int i=0; i<length; i++)
                data[i] = B[i]&0xff;
            return data;
        } else {
            throw new CV4JException("invalid argument...");
        }

    }

    @Override
    public int[] toInt(int index) {
        if(index == 0) {
            int[] data = new int[R.length];
            int length = data.length;
            for(int i=0; i<length; i++)
                data[i] = R[i]&0xff;
            return data;
        }
        else if(index == 1) {
            int[] data = new int[G.length];
            int length = data.length;
            for(int i=0; i<length; i++)
                data[i] = G[i]&0xff;
            return data;
        }
        else if(index == 2) {
            int[] data = new int[B.length];
            int length = data.length;
            for(int i=0; i<length; i++)
                data[i] = B[i]&0xff;
            return data;
        } else {
            throw new CV4JException("invalid argument...");
        }
    }

    @Override
    public byte[] toByte(int index) {
        if(index == 0) {
            return R;
        }
        else if(index == 1) {
            return G;
        }
        else if(index == 2) {
            return B;
        } else {
            throw new CV4JException("invalid argument...");
        }
    }
}
