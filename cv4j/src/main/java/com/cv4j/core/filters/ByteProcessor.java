package com.cv4j.core.filters;

/**
 * Created by Administrator on 2017/3/17.
 */

public interface ByteProcessor {

    byte[] process(byte[] data, int width, int height);
}
