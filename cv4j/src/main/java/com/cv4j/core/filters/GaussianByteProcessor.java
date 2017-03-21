package com.cv4j.core.filters;

import com.cv4j.image.util.Tools;
// never try to use it directly, internal class for this package
class GaussianByteProcessor implements ByteProcessor {
	private float[] kernal;
	private int radius=10;
	protected GaussianByteProcessor() {
		this(10);
	}

	protected GaussianByteProcessor(int radius) {
		radius = 10;
		setupKernal(radius, 15.0f);
	}

	public byte[] process(byte[] data, int width, int height) {
		byte[] out = new byte[data.length];
        blur( data, out, width, height); // H Gaussian
        blur( out, data, height, width); // V Gaussian
		out = null;
		return data;
	}

	/**
	 * <p> here is 1D Gaussian        , </p>
	 * 
	 * @param inPixels
	 * @param outPixels
	 * @param width
	 * @param height
	 */
	private void blur(byte[] inPixels, byte[] outPixels, int width, int height)
	{
		int subCol = 0;
		int index = 0, index2 = 0;
		float sum=0;
        for(int row=0; row<height; row++) {
        	int tr = 0;
        	index = row;
        	for(int col=0; col<width; col++) {
        		// index = row * width + col;
				sum=0;
        		for(int m=-radius; m<=radius; m++) {
        			subCol = col + m;
        			if(subCol < 0 || subCol >= width) {
        				subCol = 0;
        			}
        			index2 = row * width + subCol;
                    tr = inPixels[index2] & 0xff;
					sum += (tr * kernal[m + radius]);
        		}
        		outPixels[index] = (byte) Tools.clamp(sum);
        		index += height;
        	}
        }
	}

	private float[] setupKernal(int n, float sigma) {
		float sigma22 = 2*sigma*sigma;
		float Pi2 = 2*(float)Math.PI;
		float sqrtSigmaPi2 = (float)Math.sqrt(Pi2) * sigma ;
		int size = 2*n + 1;
		int index = 0;
		kernal = new float[size];
		float sum = 0.0f;
		for(int i=-n; i<=n; i++) {
			float distance = i*i;
			kernal[index] = (float)Math.exp((-distance)/sigma22)/sqrtSigmaPi2;
			sum += kernal[index];
			index++;
		}

		// nomalization to 1
		for(int i=0; i<kernal.length; i++) {
			kernal[i] = kernal[i]/sum;
		}
		return kernal;
	}
}
