package com.cv4j.core.spatial.conv;

import com.cv4j.core.datamodel.ColorProcessor;
import com.cv4j.core.datamodel.ImageProcessor;
import com.cv4j.core.filters.GaussianBlurFilter;
import com.cv4j.image.util.Tools;

public class MaerOperatorFilter extends GaussianBlurFilter {

	public static int[] FOUR = new int[] { 0, -1, 0, -1, 4, -1, 0, -1, 0 };
	public static int[] EIGHT = new int[] { -1, -1, -1, -1, 8, -1, -1, -1, -1};
	private boolean _4direction;

	public MaerOperatorFilter() {
		_4direction = true;
	}

	public boolean is4direct() {
		return _4direction;
	}

	public void set4direct(boolean xdirect) {
		this._4direction = xdirect;
	}

	@Override
	public ImageProcessor filter(ImageProcessor src){

		// 高斯模糊
		src = super.filter(src);
		
		// 拉普拉斯算子 ，发现边缘

		int total = width*height;
		byte[][] output = new byte[3][total];

		int offset = 0;
		int k0 = 0, k1 = 0, k2 = 0;
		int k3 = 0, k4 = 0, k5 = 0;
		int k6 = 0, k7 = 0, k8 = 0;
		if(_4direction) {
			k0 = FOUR[0];
			k1 = FOUR[1];
			k2 = FOUR[2];
			k3 = FOUR[3];
			k4 = FOUR[4];
			k5 = FOUR[5];
			k6 = FOUR[6];
			k7 = FOUR[7];
			k8 = FOUR[8];
		} else {
			k0 = EIGHT[0];
			k1 = EIGHT[1];
			k2 = EIGHT[2];
			k3 = EIGHT[3];
			k4 = EIGHT[4];
			k5 = EIGHT[5];
			k6 = EIGHT[6];
			k7 = EIGHT[7];
			k8 = EIGHT[8];
		}

		int sr = 0, sg = 0, sb = 0;
		int r = 0, g = 0, b = 0;
		for (int row = 1; row < height - 1; row++) {
			offset = row * width;
			for (int col = 1; col < width - 1; col++) {
				// red
				sr = k0 * (R[offset - width + col - 1] & 0xff)
						+ k1 * (R[offset - width + col] & 0xff)
						+ k2 * (R[offset - width + col + 1] & 0xff)
						+ k3 * (R[offset + col - 1] & 0xff)
						+ k4 * (R[offset + col] & 0xff)
						+ k5 * (R[offset + col + 1] & 0xff)
						+ k6 * (R[offset + width + col - 1] & 0xff)
						+ k7 * (R[offset + width + col] >> 16 & 0xff)
						+ k8 * (R[offset + width + col + 1] >> 16 & 0xff);
				// green
				sg = k0 * (G[offset - width + col - 1] & 0xff)
						+ k1 * (G[offset - width + col] & 0xff)
						+ k2 * (G[offset - width + col + 1] & 0xff)
						+ k3 * (G[offset + col - 1] & 0xff)
						+ k4 * (G[offset + col] & 0xff)
						+ k5 * (G[offset + col + 1] & 0xff)
						+ k6 * (G[offset + width + col - 1] & 0xff)
						+ k7 * (G[offset + width + col] & 0xff)
						+ k8 * (G[offset + width + col + 1] & 0xff);
				// blue
				sb = k0 * (B[offset - width + col - 1] & 0xff)
						+ k1 * (B[offset - width + col] & 0xff)
						+ k2 * (B[offset - width + col + 1] & 0xff)
						+ k3 * (B[offset + col - 1] & 0xff)
						+ k4 * (B[offset + col] & 0xff)
						+ k5 * (B[offset + col + 1] & 0xff)
						+ k6 * (B[offset + width + col - 1] & 0xff)
						+ k7 * (B[offset + width + col] & 0xff)
						+ k8 * (B[offset + width + col + 1] & 0xff);
				r = sr;
				g = sg;
				b = sb;

				output[0][offset + col] = (byte) Tools.clamp(r);
				output[1][offset + col] = (byte)Tools.clamp(g);
				output[2][offset + col] = (byte)Tools.clamp(b);

				// for next pixel
				sr = 0;
				sg = 0;
				sb = 0;
			}
		}
		((ColorProcessor) src).putRGB(output[0], output[1], output[2]);
		output = null;
		return src;
	}

}
