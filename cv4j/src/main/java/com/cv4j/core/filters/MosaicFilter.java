package com.cv4j.core.filters;
import com.cv4j.core.datamodel.ColorProcessor;
import com.cv4j.core.datamodel.ImageProcessor;
import com.cv4j.core.datamodel.IntIntegralImage;


public class MosaicFilter implements CommonFilter {
	// 窗口半径大小
	private int r=1;

	public MosaicFilter() {
		r = 1;
	}

	public int getRadius() {
		return r;
	}

	public void setRadius(int r) {
		this.r = r;
	}

	@Override
	public ImageProcessor filter(ImageProcessor src) {
		int width = src.getWidth();
		int height = src.getHeight();
		byte[] R = ((ColorProcessor)src).getRed();
		byte[] G = ((ColorProcessor)src).getGreen();
		byte[] B = ((ColorProcessor)src).getBlue();
		int size = (r * 2 + 1) * (r * 2 + 1);
		int tr = 0, tg = 0, tb = 0;
		byte[][] output = new byte[3][R.length];

		IntIntegralImage rii = new IntIntegralImage();
		rii.setImage(R);
		rii.process(width, height);
		IntIntegralImage gii = new IntIntegralImage();
		gii.setImage(G);
		gii.process(width, height);
		IntIntegralImage bii = new IntIntegralImage();
		bii.setImage(B);
		bii.process(width, height);

		int offset = 0;
		for (int row = 0; row < height; row++) {
			offset = row*width;
			for (int col = 0; col < width; col++) {
				int dy = (row / size);
				int dx = (col / size);
				int ny = dy*size+r;
				int nx = dx*size+r;
				int sr = rii.getBlockSum(nx, ny, (r * 2 + 1), (r * 2 + 1));
				int sg = gii.getBlockSum(nx, ny, (r * 2 + 1), (r * 2 + 1));
				int sb = bii.getBlockSum(nx, ny, (r * 2 + 1), (r * 2 + 1));
				tr = sr / size;
				tg = sg / size;
				tb = sb / size;
				output[0][offset] = (byte)tr;
				output[1][offset] = (byte)tg;
				output[2][offset] = (byte)tb;
				offset++;
			}
		}
		((ColorProcessor) src).putRGB(output[0], output[1], output[2]);
		output = null;
		return src;
	}
}
