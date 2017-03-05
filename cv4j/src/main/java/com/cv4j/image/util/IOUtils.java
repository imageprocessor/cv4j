package com.cv4j.image.util;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created by Tony Shen on 2017/3/5.
 */

public class IOUtils {

    /**
     * 安全关闭io流
     * @param closeable
     */
    public static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
