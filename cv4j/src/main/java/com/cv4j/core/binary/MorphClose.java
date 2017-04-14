package com.cv4j.core.binary;

import com.cv4j.core.datamodel.ByteProcessor;
import com.cv4j.core.datamodel.Size;

/**
 * Created by Administrator on 2017/4/14.
 */

public class MorphClose {
    /***
     * erode operator after success doing dilate operator
     * can fill litter hole.
     *
     * @param binary
     * @param structureElement
     */
    public void process(ByteProcessor binary, Size structureElement) {
        Erode erode = new Erode();
        Dilate dilate = new Dilate();
        dilate.process(binary, structureElement);
        erode.process(binary, structureElement);
    }
}
