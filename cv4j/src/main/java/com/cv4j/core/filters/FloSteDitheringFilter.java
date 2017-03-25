package com.cv4j.core.filters;

import com.cv4j.core.datamodel.ImageProcessor;
import com.cv4j.image.util.Tools;

/**
 * algorithm -http://en.wikipedia.org/wiki/Floyd%E2%80%93Steinberg_dithering
 * http://en.literateprograms.org/Floyd-Steinberg_dithering_(C)
 * ******Floyd Steinberg dithering*******
 * * 0       0,       0*
 * * 0       P     7/16*
 * * 3/16, 5/16,   1/16*
 * *************************
 * * 0        0           0*
 * * 0        *      0.4375*
 * * 0.1875, 0.3125, 0.0625*
 * *************************
 * 
 * @author gloomy fish
 *
 */
public class FloSteDitheringFilter implements CommonFilter {
	public final static float[] kernelData = new float[]{0.1875f, 0.3125f, 0.0625f, 0.4375f};
	public final static int[] COLOR_PALETTE = new int[] {0, 255};

	@Override
	public ImageProcessor filter(ImageProcessor src) {
        if(src.getType() == ImageProcessor.CV4J_IMAGE_TYPE_RGB) {
            src.convert2Gray();
        }
		int width = src.getWidth();
        int height = src.getHeight();
        byte[] R = src.getChannel(0);
        byte[] output = new byte[R.length];

        int gray = 0;
        int err = 0;
        for(int row=0; row<height; row++) {
            int offset = row*width;
        	for(int col=0; col<width; col++) {
                gray = R[offset]&0xff;
                int cIndex = getCloseColor(gray);
                output[offset] = (byte)COLOR_PALETTE[cIndex];
                int er = gray - COLOR_PALETTE[cIndex];
                int k = 0;
                
                if(row + 1 < height && col - 1 > 0) {
                	k = (row + 1) * width + col - 1;
                    err = R[k]&0xff;
                    err += (int)(er * kernelData[0]);
                    R[k] = (byte)Tools.clamp(err);
                }
                
                if(col + 1 < width) {
                	k = row * width + col + 1;
                    err = R[k]&0xff;
                    err += (int)(er * kernelData[3]);
                    R[k] = (byte)Tools.clamp(err);
                }
                
                if(row + 1 < height) {
                	k = (row + 1) * width + col;
                    err = R[k]&0xff;
                    err += (int)(er * kernelData[1]);
                    R[k] = (byte)Tools.clamp(err);
                }
                
                if(row + 1 < height && col + 1 < width) {
                	k = (row + 1) * width + col + 1;
                    err = R[k]&0xff;
                    err += (int)(er * kernelData[2]);
                    R[k] = (byte)Tools.clamp(err);
                }
                offset++;
        	}
        }
        src.putPixels(new byte[][]{output, output, output});
        output = null;
        return src;
	}
	
	private int getCloseColor(int gray) {
		int minDistanceSquared = 255*255 + 1;
		int bestIndex = 0;
		for(int i=0; i<COLOR_PALETTE.length; i++) {
			int diff = Math.abs(gray - COLOR_PALETTE[i]);
			if(ImageProcessor.SQRT_LUT[diff] < minDistanceSquared) {
				minDistanceSquared = ImageProcessor.SQRT_LUT[diff];
				bestIndex = i;
			}
		}
		return bestIndex;
	}
}
