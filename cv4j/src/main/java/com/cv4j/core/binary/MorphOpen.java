package com.cv4j.core.binary;

import com.cv4j.core.datamodel.ByteProcessor;
import com.cv4j.core.datamodel.Size;

/**
 * Created by Administrator on 2017/4/14.
 */

public class MorphOpen {
    /**
     * in order to remove litter noise block, erode + dilate operator
     *
     * @param binary
     * @param structureElement
     */
    public void process(ByteProcessor binary, Size structureElement) {
        Erode erode = new Erode();
        Dilate dilate = new Dilate();
        erode.process(binary, structureElement);
        dilate.process(binary, structureElement);
    }
}
