package com.cv4j.core.datamodel;

public class IntIntegralImage {
	// sum index tables
	private int[] sum;
	// image
	private float[] squaresum;
	private byte[] image;
	private int width;
	private int height;

	public byte[] getImage() {
		return image;
	}

	public void setImage(byte[] image) {
		this.image = image;
	}
	
	public int getBlockSum(int x, int y, int m, int n) {
		int swx = x + n/2;
		int swy = y + m/2;
		int nex = x-n/2-1;
		int ney = y-m/2-1;
		int sum1, sum2, sum3, sum4;
		if(swx >= width) {
			swx = width - 1;
		}
		if(swy >= height) {
			swy = height - 1;
		}
		if(nex < 0) {
			nex = 0;
		}
		if(ney < 0) {
			ney = 0;
		}
		sum1 = sum[ney*width+nex];
		sum4 = sum[swy*width+swx];
		sum2 = sum[swy*width+nex];
		sum3 = sum[ney*width+swx];
		return ((sum1 + sum4) - sum2 - sum3);
	}

	public float getBlockSquareSum(int x, int y, int m, int n) {
		int swx = x + n/2;
		int swy = y + m/2;
		int nex = x-n/2-1;
		int ney = y-m/2-1;
		float sum1, sum2, sum3, sum4;
		if(swx >= width) {
			swx = width - 1;
		}
		if(swy >= height) {
			swy = height - 1;
		}
		if(nex < 0) {
			nex = 0;
		}
		if(ney < 0) {
			ney = 0;
		}
		sum1 = squaresum[ney*width+nex];
		sum4 = squaresum[swy*width+swx];
		sum2 = squaresum[swy*width+nex];
		sum3 = squaresum[ney*width+swx];
		return ((sum1 + sum4) - sum2 - sum3);
	}

	public void process(int width, int height) {
		this.width = width;
		this.height = height;
		sum = new int[width*height];
		// rows
		int p1=0, p2=0, p3=0, p4;
		int offset = 0, uprow=0, leftcol=0;
		float sp2=0, sp3=0, sp4=0;
		for(int row=0; row<height; row++ ) {
			offset = row*width;
			uprow = row-1;
			for(int col=0; col<width; col++) {
				leftcol=col-1;
				// 计算和查找表
				p1=image[offset]&0xff;// p(x, y)
				p2=(leftcol<0) ? 0:sum[offset-1]; // p(x-1, y)
				p3=(uprow<0) ? 0:sum[offset-width]; // p(x, y-1);
				p4=(uprow<0||leftcol<0) ? 0:sum[offset-width-1]; // p(x-1, y-1);
				sum[offset]= p1+p2+p3-p4;
				offset++;
			}
		}
	}

	public void process(int width, int height, boolean includeSqrt) {
		this.width = width;
		this.height = height;
		sum = new int[width*height];
		squaresum = new float[width*height];
		// rows
		int p1=0, p2=0, p3=0, p4;
		int offset = 0, uprow=0, leftcol=0;
		float sp2=0, sp3=0, sp4=0;
		for(int row=0; row<height; row++ ) {
			offset = row*width;
			uprow = row-1;
			for(int col=0; col<width; col++) {
				leftcol=col-1;
				// 计算和查找表
				p1=image[offset]&0xff;// p(x, y)
				p2=(leftcol<0) ? 0:sum[offset-1]; // p(x-1, y)
				p3=(uprow<0) ? 0:sum[offset-width]; // p(x, y-1);
				p4=(uprow<0||leftcol<0) ? 0:sum[offset-width-1]; // p(x-1, y-1);
				sum[offset]= p1+p2+p3-p4;

				// 计算平方查找表
				sp2=(leftcol<0) ? 0:squaresum[offset-1]; // p(x-1, y)
				sp3=(uprow<0) ? 0:squaresum[offset-width]; // p(x, y-1);
				sp4=(uprow<0||leftcol<0) ? 0:squaresum[offset-width-1]; // p(x-1, y-1);
				squaresum[offset]=p1*p1+sp2+sp3-sp4;
				offset++;
			}
			// System.out.println();
		}
	}
}
