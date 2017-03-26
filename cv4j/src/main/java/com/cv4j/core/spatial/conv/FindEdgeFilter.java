package com.cv4j.core.spatial.conv;

import com.cv4j.core.datamodel.ColorProcessor;
import com.cv4j.core.datamodel.ImageProcessor;
import com.cv4j.core.filters.CommonFilter;

import static com.cv4j.image.util.Tools.clamp;


public class FindEdgeFilter implements CommonFilter {
	public static int[] sobel_y = new int[] { -1, -2, -1, 0, 0, 0, 1, 2, 1 };
	public static int[] sobel_x = new int[] { -1, 0, 1, -2, 0, 2, -1, 0, 1 };

	public FindEdgeFilter() {
	}

	@Override
	public ImageProcessor filter(ImageProcessor src) {
		int width = src.getWidth();
		int height = src.getHeight();
		int total = width*height;
		byte[] R = ((ColorProcessor)src).getRed();
		byte[] G = ((ColorProcessor)src).getGreen();
		byte[] B = ((ColorProcessor)src).getBlue();
		byte[][] output = new byte[3][total];

		int offset = 0;
		int x0 = sobel_x[0];
		int x1 = sobel_x[1];
		int x2 = sobel_x[2];
		int x3 = sobel_x[3];
		int x4 = sobel_x[4];
		int x5 = sobel_x[5];
		int x6 = sobel_x[6];
		int x7 = sobel_x[7];
		int x8 = sobel_x[8];
		
		int k0 = sobel_y[0];
		int k1 = sobel_y[1];
		int k2 = sobel_y[2];
		int k3 = sobel_y[3];
		int k4 = sobel_y[4];
		int k5 = sobel_y[5];
		int k6 = sobel_y[6];
		int k7 = sobel_y[7];
		int k8 = sobel_y[8];

		int yr = 0, yg = 0, yb = 0;
		int xr = 0, xg = 0, xb = 0;
		int r = 0, g = 0, b = 0;
		for (int row = 1; row < height - 1; row++) {
			offset = row * width;
			for (int col = 1; col < width - 1; col++) {
				// red
				yr = k0 * (R[offset - width + col - 1]& 0xff)
						+ k1 * (R[offset - width + col] & 0xff)
						+ k2 * (R[offset - width + col + 1] & 0xff)
						+ k3 * (R[offset + col - 1] & 0xff)
						+ k4 * (R[offset + col] & 0xff)
						+ k5 * (R[offset + col + 1] & 0xff)
						+ k6 * (R[offset + width + col - 1] & 0xff)
						+ k7 * (R[offset + width + col] & 0xff)
						+ k8 * (R[offset + width + col + 1] & 0xff);
				
				xr = x0 * (R[offset - width + col - 1] & 0xff)
						+ x1 * (R[offset - width + col] & 0xff)
						+ x2 * (R[offset - width + col + 1] & 0xff)
						+ x3 * (R[offset + col - 1] & 0xff)
						+ x4 * (R[offset + col] & 0xff)
						+ x5 * (R[offset + col + 1] & 0xff)
						+ x6 * (R[offset + width + col - 1] & 0xff)
						+ x7 * (R[offset + width + col] & 0xff)
						+ x8 * (R[offset + width + col + 1] & 0xff);
				
				// green
				yg = k0 * (G[offset - width + col - 1] & 0xff)
						+ k1 * (G[offset - width + col] & 0xff)
						+ k2 * (G[offset - width + col + 1] & 0xff)
						+ k3 * (G[offset + col - 1] & 0xff)
						+ k4 * (G[offset + col] & 0xff)
						+ k5 * (G[offset + col + 1] & 0xff)
						+ k6 * (G[offset + width + col - 1] & 0xff)
						+ k7 * (G[offset + width + col] & 0xff)
						+ k8 * (G[offset + width + col + 1] & 0xff);
				
				xg = x0 * (G[offset - width + col - 1] & 0xff)
						+ x1 * (G[offset - width + col] & 0xff)
						+ x2 * (G[offset - width + col + 1] & 0xff)
						+ x3 * (G[offset + col - 1] & 0xff)
						+ x4 * (G[offset + col] & 0xff)
						+ x5 * (G[offset + col + 1] & 0xff)
						+ x6 * (G[offset + width + col - 1] & 0xff)
						+ x7 * (G[offset + width + col] & 0xff)
						+ x8 * (G[offset + width + col + 1] & 0xff);
				// blue
				yb = k0 * (B[offset - width + col - 1] & 0xff)
						+ k1 * (B[offset - width + col] & 0xff)
						+ k2 * (B[offset - width + col + 1] & 0xff)
						+ k3 * (B[offset + col - 1] & 0xff)
						+ k4 * (B[offset + col] & 0xff)
						+ k5 * (B[offset + col + 1] & 0xff)
						+ k6 * (B[offset + width + col - 1] & 0xff)
						+ k7 * (B[offset + width + col] & 0xff)
						+ k8 * (B[offset + width + col + 1] & 0xff);
				
				xb = x0 * (B[offset - width + col - 1] & 0xff)
						+ x1 * (B[offset - width + col] & 0xff)
						+ x2 * (B[offset - width + col + 1] & 0xff)
						+ x3 * (B[offset + col - 1] & 0xff)
						+ x4 * (B[offset + col] & 0xff)
						+ x5 * (B[offset + col + 1] & 0xff)
						+ x6 * (B[offset + width + col - 1] & 0xff)
						+ x7 * (B[offset + width + col] & 0xff)
						+ x8 * (B[offset + width + col + 1] & 0xff);
				
				// magnitude 
				r = (int)Math.sqrt(yr*yr + xr*xr);
				g = (int)Math.sqrt(yg*yg + xg*xg);
				b = (int)Math.sqrt(yb*yb + xb*xb);
				
				// find edges
				output[0][offset + col] = (byte)clamp(r);
				output[1][offset + col] = (byte)clamp(g);
				output[2][offset + col] = (byte)clamp(b);

				double dy = (yr+yg+yb);
				double dx = (xr+xg+xb);
				double theta = Math.atan(dy/dx);
				
				// for next pixel
				yr = 0;
				yg = 0;
				yb = 0;
				xr=0;
				xg=0;
				xb=0;
			}
		}
		((ColorProcessor) src).putRGB(output[0], output[1], output[2]);
		output[0] = null;
		output[1] = null;
		output[2] = null;
		output = null;
		return src;
	}

}
