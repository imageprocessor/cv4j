package com.cv4j.core.binary;

import com.cv4j.core.datamodel.ByteProcessor;
import com.cv4j.core.datamodel.Size;

/**
 * Created by gloomy fish on 2017/4/16.
 */

public class TopHat {

    /***
     * top hat is difference between original binary and morphology open on it
     * @param binary - input binary image
     * @param structureElement  - size of morph
     */
    public void process(ByteProcessor binary, Size structureElement) {
        int width = binary.getWidth();
        int height = binary.getHeight();
        byte[] data = new byte[width*height];
        System.arraycopy(binary.getGray(), 0, data, 0, data.length);
        MorphOpen open = new MorphOpen();
        open.process(binary, structureElement);
        byte[] output = binary.getGray();
        int c = 0;
        for(int i=0; i<data.length; i++) {
            c = (data[i]&0xff - output[i]&0xff);
            output[i] = (byte) ((c > 0) ? 255 : 0);
        }
    }
}
