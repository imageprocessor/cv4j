package com.cv4j.core.filters;

import com.cv4j.core.datamodel.ImageData;
import com.cv4j.image.util.Tools;

import static com.cv4j.image.util.Tools.clamp;

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
	public ImageData filter(ImageData src) {
        if(src.getType() == ImageData.CV4J_IMAGE_TYPE_RGB) {
            src.convert2Gray();
        }
		int width = src.getWidth();
        int height = src.getHeight();
        int[] output = new int[width*height];

        int gray = 0;
        int err = 0;
        for(int row=0; row<height; row++) {
            int offset = row*width;
        	for(int col=0; col<width; col++) {
                gray = src.getPixels()[offset];
                int cIndex = getCloseColor(gray);
                output[offset] = COLOR_PALETTE[cIndex];
                int er = gray - COLOR_PALETTE[cIndex];
                int k = 0;
                
                if(row + 1 < height && col - 1 > 0) {
                	k = (row + 1) * width + col - 1;
                    err = src.getPixels()[k];
                    err += (int)(er * kernelData[0]);
                    src.getPixels()[k] = Tools.clamp(err);
                }
                
                if(col + 1 < width) {
                	k = row * width + col + 1;
                    err = src.getPixels()[k];
                    err += (int)(er * kernelData[3]);
                    src.getPixels()[k] = Tools.clamp(err);
                }
                
                if(row + 1 < height) {
                	k = (row + 1) * width + col;
                    err = src.getPixels()[k];
                    err += (int)(er * kernelData[1]);
                    src.getPixels()[k] = Tools.clamp(err);
                }
                
                if(row + 1 < height && col + 1 < width) {
                	k = (row + 1) * width + col + 1;
                    err = (src.getPixels()[k] >> 16) & 0xff;
                    err += (int)(er * kernelData[2]);
                    src.getPixels()[k] = Tools.clamp(err);
                }
                offset++;
        	}
        }
        src.putPixels(output);
        return src;
	}
	
	private int getCloseColor(int gray) {
		int minDistanceSquared = 255*255 + 1;
		int bestIndex = 0;
		for(int i=0; i<COLOR_PALETTE.length; i++) {
			int diff = gray - COLOR_PALETTE[i];
			int distanceSquared = diff*diff;
			if(distanceSquared < minDistanceSquared) {
				minDistanceSquared = distanceSquared;
				bestIndex = i;
			}
		}
		return bestIndex;
	}
}
