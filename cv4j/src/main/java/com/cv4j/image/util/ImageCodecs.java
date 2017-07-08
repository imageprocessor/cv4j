package com.cv4j.image.util;

import com.cv4j.core.datamodel.CV4JImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import edu.uthscsa.ric.volume.formats.jpeg.JPEGLosslessDecoderWrapper;

/**
 * Created by gloomy fish on 2017/7/8.
 */

public class ImageCodecs {

    public static CV4JImage imread(String filePath) {
        CV4JImage image = null;
        if(filePath.endsWith(".jpg")||filePath.endsWith(".JPG")
                || filePath.endsWith(".JPEG") || filePath.endsWith(".jpeg")) {
            try {
                image = JPEGLosslessDecoderWrapper.readImage(getBytesFromFile(new File(filePath)));
            } catch (IOException ioe) {

            }
        } else if(filePath.endsWith(".png")||filePath.endsWith(".PNG")) {
            // TODO: zhigang
        }
        return image;
    }

    public static byte[] getBytesFromFile(File file) {
        byte[] ret = null;
        try {
            if (file == null) {
                // log.error("helper:the file is null!");
                return null;
            }
            FileInputStream in = new FileInputStream(file);
            ByteArrayOutputStream out = new ByteArrayOutputStream(4096);
            byte[] b = new byte[4096];
            int n;
            while ((n = in.read(b)) != -1) {
                out.write(b, 0, n);
            }
            in.close();
            out.close();
            ret = out.toByteArray();
        } catch (IOException e) {
            // log.error("helper:get bytes from file process error!");
            e.printStackTrace();
        }
        return ret;
    }
}
