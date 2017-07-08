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

    public static CV4JImage read(String filePath) {
        CV4JImage image = null;
        if (filePath.endsWith(".jpg") || filePath.endsWith(".JPG")
                || filePath.endsWith(".JPEG") || filePath.endsWith(".jpeg")) {
            try {
                image = JPEGLosslessDecoderWrapper.readImage(getBytesFromFile(new File(filePath)));
            } catch (IOException ioe) {

            }
        } else if (filePath.endsWith(".png") || filePath.endsWith(".PNG")) {
            // TODO: zhigang
        }
        return image;
    }

    private static byte[] getBytesFromFile(File file) {

        if (file == null) return null;

        byte[] ret = null;

        FileInputStream in = null;
        ByteArrayOutputStream out = null;
        try {
            in = new FileInputStream(file);
            out = new ByteArrayOutputStream(4096);
            byte[] b = new byte[4096];
            int n;
            while ((n = in.read(b)) != -1) {
                out.write(b, 0, n);
            }

            ret = out.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(out);
        }

        return ret;
    }
}
