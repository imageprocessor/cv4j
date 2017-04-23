package com.cv4j.core.filters;

import com.cv4j.core.datamodel.ImageProcessor;
import com.cv4j.image.util.Tools;

public class BoxBlurFilter extends BaseFilter {

	private int hRadius=5;
	private int vRadius=5;
	private int iterations = 1;

    @Override
    public ImageProcessor doFilter(ImageProcessor src) {

        byte[][] output = new byte[3][width*height];
        byte[][] input = new byte[][]{R, G, B};
        for (int i = 0; i < iterations; i++ ) {
            blur( input, output, width, height, hRadius );
            blur( output, input, height, width, vRadius );
        }
        return src;
    }

    private void blur( byte[][] in, byte[][] out, int width, int height, int radius ) {
        int widthMinus1 = width-1;
        int tableSize = 2*radius+1;
        int divide[] = new int[256*tableSize];

        // the value scope will be 0 to 255, and number of 0 is table size
        // will get means from index not calculate result again since 
        // color value must be  between 0 and 255.
        for ( int i = 0; i < 256*tableSize; i++ )
            divide[i] = i/tableSize; 

        int inIndex = 0;

        // 每一行
        for ( int y = 0; y < height; y++ ) {
            int outIndex = y;
            int tr = 0, tg = 0, tb = 0;

            // 初始化盒子里面的像素和
            for ( int i = -radius; i <= radius; i++ ) {
                int offset = inIndex + Tools.clamp(i, 0, width-1);
                tr += in[0][offset] & 0xff;
                tg += in[1][offset] & 0xff;
                tb += in[2][offset] & 0xff;
            }

            // 每一列，每一个像素
            for ( int x = 0; x < width; x++ ) {
                // 赋值到输出像素
                out[0][outIndex] = (byte)divide[tr];
                out[1][outIndex] = (byte)divide[tg];
                out[2][outIndex] = (byte)divide[tb];

                // 移动盒子一个像素距离
                int i1 = x+radius+1;
                // 检测是否达到边缘
                if ( i1 > widthMinus1 )
                    i1 = widthMinus1;
                // 将要移出的一个像素
                int i2 = x-radius;
                if ( i2 < 0 )
                    i2 = 0;

                // 计算移除与移进像素之间的差值，更新像素和
                tr += (in[0][inIndex+i1]&0xff)-(in[0][inIndex+i2]&0xff);
                tg += (in[1][inIndex+i1]&0xff)-(in[1][inIndex+i2]&0xff);
                tb += (in[2][inIndex+i1]&0xff)-(in[2][inIndex+i2]&0xff);

                // 继续到下一行
                outIndex += height;
            }
            // 继续到下一行
            inIndex += width;
        }
    }
        
	public void setHRadius(int hRadius) {
		this.hRadius = hRadius;
	}
	
	public int getHRadius() {
		return hRadius;
	}
	
	public void setVRadius(int vRadius) {
		this.vRadius = vRadius;
	}
	
	public int getVRadius() {
		return vRadius;
	}
	
	public void setRadius(int radius) {
		this.hRadius = this.vRadius = radius;
	}
	
	public int getRadius() {
		return hRadius;
	}
	
	public void setIterations(int iterations) {
		this.iterations = iterations;
	}
	
	public int getIterations() {
		return iterations;
	}
}
