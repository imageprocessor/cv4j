
package edu.uthscsa.ric.volume.formats.jpeg;

import com.cv4j.core.datamodel.CV4JImage;

import java.io.IOException;

/**
 * This class provides the conversion of a byte buffer 
 * containing a JPEGLossless to an BufferedImage. 
 * Therefore it uses the rii-mango JPEGLosslessDecoder
 * Library ( https://github.com/rii-mango/JPEGLosslessDecoder )
 *
 * Currently the following conversions are supported
 * 	- 24Bit, RGB       -> BufferedImage.TYPE_INT_RGB
 *  -  8Bit, Grayscale -> BufferedImage.TYPE_BYTE_GRAY
 *  - 16Bit, Grayscale -> BufferedImage.TYPE_USHORT_GRAY
 * 
 * @author gloomy fish
 *
 */
public class JPEGLosslessDecoderWrapper {

	/**
	 * Converts a byte buffer (containing a jpeg lossless) 
	 * to an Java BufferedImage
	 * Currently the following conversions are supported
	 * 	- 24Bit, RGB       -> BufferedImage.TYPE_INT_RGB
	 *  -  8Bit, Grayscale -> BufferedImage.TYPE_BYTE_GRAY
	 *  - 16Bit, Grayscale -> BufferedImage.TYPE_USHORT_GRAY
	 * 
	 * @param data byte buffer which contains a jpeg lossless
	 * @return if successfully a BufferedImage is returned
	 * @throws IOException is thrown if the decoder failed or a conversion is not supported
	 */
	public static CV4JImage readImage(byte[] data) throws IOException{
		JPEGLosslessDecoder decoder = new JPEGLosslessDecoder(data);
		int width = decoder.getDimX();
		int height = decoder.getDimY();
		int[][] decoded = decoder.decode();

		int[] pixels = new int[width*height];
		if(decoder.getNumComponents() == 3) {
			for(int i=0; i<pixels.length; i++) {
				pixels[i] = (decoded[0][i] << 16) | (decoded[1][i] << 8) | (decoded[2][i]);
			}
		} else if(decoder.getNumComponents() == 1) {
			for(int i=0; i<pixels.length; i++) {
				pixels[i] = (decoded[0][i] << 16) | (decoded[0][i] << 8) | (decoded[0][i]);
			}
		}
		decoded = null;
		decoder = null;
		return new CV4JImage(width, height, pixels);
	}
}
