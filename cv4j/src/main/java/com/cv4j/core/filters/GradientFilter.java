package com.cv4j.core.filters;

import com.cv4j.core.datamodel.ByteProcessor;
import com.cv4j.image.util.Tools;

/**
 * 
 * @author gloomy-fish
 * @date 2012-06-11
 */
public class GradientFilter {
	// sobel operator
	public final static int[][] SOBEL_X = new int[][]{{-1, 0, 1}, {-2, 0, 2}, {-1, 0, 1}};
	public final static int[][] SOBEL_Y = new int[][]{{-1, -2, -1}, {0,  0,  0}, {1,  2,  1}};
	
	// direction parameter
	public final static int X_DIRECTION = 0;
	public final static int Y_DIRECTION = 2;
	public final static int XY_DIRECTION = 4;
	private int direction;
	private boolean isSobel;
	
	public GradientFilter() {
		direction = XY_DIRECTION;
		isSobel = true;
	}
	
	public void setSoble(boolean sobel) {
		this.isSobel = sobel;
	}

	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	public int[] gradient(ByteProcessor src){
		int width = src.getWidth();
        int height = src.getHeight();

		int[] outPixels = new int[width*height];
        int index = 0, index2 = 0;
        double xred = 0, xgreen = 0, xblue = 0;
        double yred = 0, ygreen = 0, yblue = 0;
        int newRow, newCol;
        float min = 255, max = 0;
		byte[] intput = src.getGray();
        for(int row=0; row<height; row++) {
        	int pv = 0;
        	for(int col=0; col<width; col++) {
        		index = row * width + col;
        		for(int subrow = -1; subrow <= 1; subrow++) {
        			for(int subcol = -1; subcol <= 1; subcol++) {
        				newRow = row + subrow;
        				newCol = col + subcol;
        				if(newRow < 0 || newRow >= height) {
        					newRow = row;
        				}
        				if(newCol < 0 || newCol >= width) {
        					newCol = col;
        				}
        				index2 = newRow * width + newCol;
						pv = intput[index2] & 0xff;

						xred += (SOBEL_X[subrow + 1][subcol + 1] * pv);
						yred += (SOBEL_Y[subrow + 1][subcol + 1] * pv);
        			}
        		}
        		
                double mred = Math.sqrt(xred * xred + yred * yred);
                max = Math.max(Tools.clamp((int)mred) , max);
                min = Math.min(Tools.clamp((int)mred) , min);
                if(XY_DIRECTION == direction) 
                {
                	outPixels[index] = Tools.clamp((int)mred);
                } 
                else if(X_DIRECTION == direction)
                {
                	outPixels[index] = Tools.clamp((int)yred);
                } 
                else if(Y_DIRECTION == direction) 
                {
					outPixels[index] = Tools.clamp((int)xred);
                } 
                else 
                {
                	// as default, always XY gradient
					outPixels[index] = Tools.clamp((int)mred);
                }
                
                // cleanup for next loop
                newRow = newCol = 0;
                xred = 0;
                yred = 0;
                
        	}
        }
		return outPixels;
	}

}
