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
            PngReader pngr = new PngReader(new File(filePath));
            System.out.println(pngr.toString());
            int channels = pngr.imgInfo.channels;
            if (channels < 3 || pngr.imgInfo.bitDepth != 8)
                throw new RuntimeException("This method is for RGB8/RGBA8 images");

            ImageInfo info = pngr.getImgInfo();
            int width = info.cols;
            int height = info.rows;
            int dims = info.channels;
            int r = 0, g = 0, b = 0;
            int index = 0;
            int[] pixels = new int[width * height];
            for (int row = 0; row < height; row++) {
                ImageLineInt line = (ImageLineInt) pngr.readRow(row);
                int[] data = line.getScanline();
                for (int col = 0; col < width; col++) {
                    if (dims == 3) {
                        r = data[col*dims];
                        g = data[col*dims + 1];
                        b = data[col*dims + 2];
                        pixels[row * width + col] =
                                ((255 & 0xff) << 24) | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
                    } else if (dims == 4) {
                        r = data[col*dims + 1];
                        g = data[col*dims + 2];
                        b = data[col*dims + 3];
                        pixels[row * width + col] =
                                ((255 & 0xff) << 24) | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
                    }
                }
            }
            image = new CV4JImage(width, height, pixels);
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
