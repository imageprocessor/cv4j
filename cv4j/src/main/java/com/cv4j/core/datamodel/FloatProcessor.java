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

public class FloatProcessor implements ImageProcessor {

    private int width;
    private int height;
    private float[] GRAY;
    private ImageData image;

    public FloatProcessor(float[] data, int width, int height) {
        this.width = width;
        this.height = height;
        this.GRAY = data;
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

    @Override
    public void getPixel(int row, int col, byte[] rgb) {

    }

    @Override
    public int[] getPixels() {
        return new int[0];
    }

    public void getPixel(int row, int col, float[] rgb) {
        int index = row*width + col;
        if(rgb != null && rgb.length == 1) {
            rgb[0] = GRAY[index];
        }
    }

    public float[] getGray() {
        return GRAY;
    }

    public void putGray(float[] gray) {
        System.arraycopy(gray, 0, GRAY, 0, gray.length);
    }

    public ImageData getImage() {

        return this.image;
    }

    @Override
    public float[] toFloat(int index) {
        return GRAY;
    }

    @Override
    public int[] toInt(int index) {
        int[] data = new int[GRAY.length];
        for(int i=0; i<data.length; i++)
            data[i] = (int)GRAY[i];
        return data;
    }

    @Override
    public byte[] toByte(int index) {
        throw new IllegalStateException("Invalid data type, not support this type!!!");
    }

}
