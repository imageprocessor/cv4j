package com.cv4j.core.pixels;

import com.cv4j.core.datamodel.ByteProcessor;
import com.cv4j.core.datamodel.ColorProcessor;
import com.cv4j.core.datamodel.ImageProcessor;
import com.cv4j.core.datamodel.Scalar;
/**
 * 
 * @author gloomy fish
 *
 */
public class NormRotate {
	private Scalar background;
	public NormRotate() {

	}
	
	public ImageProcessor rotate(ImageProcessor processor, float degree) {
		background = new Scalar();
		return rotate(processor, degree, background);
	}

	public ImageProcessor rotate(ImageProcessor processor, float degree, Scalar bgColor) {
		this.background = bgColor;
        if(Math.floor(degree/90) == 1){
        	processor = rotate90(processor, 90);
        	degree = degree - 90;
        } else if(Math.floor(degree/90) == 2) {
        	processor = rotate90(processor, 180);
        	degree = degree - 180;
        }
        else if(Math.floor(degree/90) == 3) {
        	processor = rotate90(processor, 270);
        	degree = degree - 270;
        }
        int width = processor.getWidth();
        int height = processor.getHeight();
        int ch = processor.getChannels();
        double angle = (degree/180.0d) * Math.PI;
        int outw = (int)(width*Math.cos(angle)+height*Math.sin(angle)); 
        int outh = (int)(height*Math.cos(angle)+width*Math.sin(angle));
        ImageProcessor dst = (ch == 3) ? new ColorProcessor(outw, outh) : new ByteProcessor(outw, outh);
        int index = 0;
        // calculate new center coordinate
        float centerX = outw / 2.0f + 0.5f;
        float centerY = outh /2.0f + 0.5f;
        
        // calculate the original center coordinate
        float ocenterX = width / 2.0f + 0.5f;
        float ocenterY = height /2.0f + 0.5f;
        
        float rx =0, ry = 0; //after rotated coordinate
        float px = 0, py = 0; // original coordinate
        float prow = 0, pcol = 0;
        for(int row=0; row<outh; row++) {
        	for(int col=0; col<outw; col++) {
        		rx = col - centerX;
        		ry = centerY - row;
        		float fDistance = (float)Math.sqrt(rx * rx + ry * ry);
        		float fPolarAngle = 0; //;
        		if(rx != 0) {
        			fPolarAngle = (float)Math.atan2((double)ry, (double)rx);
        		} else {
        			if(rx == 0) {
        				if(ry == 0) {
        					for(int i=0; i<ch; i++) {
        						dst.toByte(i)[index] = processor.toByte(i)[height/2 * width + width/2];
        					}
        					continue; 
        				} 
        				else if(ry < 0) {
            				fPolarAngle = 1.5f * (float)Math.PI;
            			} else {
            				fPolarAngle = 0.5f * (float)Math.PI;
            			}
        			}
        		}
        		
        		// "reverse" rotate, so minus instead of plus
                fPolarAngle -= angle;
                px = fDistance * (float)Math.cos(fPolarAngle);
                py = fDistance * (float)Math.sin(fPolarAngle);

                // get original pixel float point
                prow = ((float)ocenterY) - py;
                pcol = ((float)ocenterX) + px;

                // now start the biline-interpolation algorithm here!!!
                byte[] rgb = bilineInterpolation(processor, prow, pcol);
                
                index = row * outw + col;
                for(int i=0; i<ch; i++) {
                	dst.toByte(i)[index] = rgb[i];
                }
        	}
        }
        return dst;
	}

	private ImageProcessor rotate90(ImageProcessor processor, float degree) {
		int width = processor.getWidth();
		int height = processor.getHeight();
		int outw = -1;
		int outh = -1;
		int ch = processor.getChannels();
        int index = 0;
        int index2 = 0;
		if(degree == 90)
		{
			outw = height;
			outh = width;
		}
		else if(degree == 180)
		{
			outw = width;
			outh = height;
		}
		else if(degree == 270)
		{
			outw = height;
			outh = width;
		}
		ImageProcessor dst = (ch == 3) ? new ColorProcessor(outw, outh) : new ByteProcessor(outw, outh);

        for(int row=0; row<height; row++) {
        	for(int col=0; col<width; col++) {
        		index = row * width + col;
        		if(degree == 90)
        		{
        			index2 = outw * (width - 1 - col) + row;
        		}
        		else if(degree == 180)
        		{
        			index2 = outw * (height - 1 - row) + 
        					(width - 1 - col);
        		}
        		else if(degree == 270)
        		{
        			index2 = outw * col + (height - 1- row);
        		}
        		for(int i=0; i<ch; i++) {
        			dst.toByte(i)[index2] = processor.toByte(i)[index];
        		}
        	}
        }
        return dst;
	}

	private byte[] bilineInterpolation(ImageProcessor processor, float prow, float pcol) {
		int width = processor.getWidth();
		int height = processor.getHeight();
		int ch = processor.getChannels();
		double row = Math.floor(prow);
		double col = Math.floor(pcol);
		if(row < 0 || row >= height) {
			return (ch == 1) ? new byte[]{(byte)0}: new byte[]{(byte)background.red, (byte)background.green, (byte)background.blue};
		}
		if(col < 0 || col >= width) {
			return (ch == 1) ? new byte[]{(byte)0}: new byte[]{(byte)background.red, (byte)background.green, (byte)background.blue};
		}
		
		int rowNext = (int)row + 1, colNext = (int)col + 1;
		if((row + 1) >= height) {
			rowNext = (int)row;
		}
		
		if((col + 1) >= width) {
			colNext = (int)col;
		}
		double t = prow - row;
		double u = pcol - col;
		double coffiecent1 = (1.0d-t)*(1.0d-u);
		double coffiecent2 = (t)*(1.0d-u);
		double coffiecent3 = t*u;
		double coffiecent4 = (1.0d-t)*u;
		
		int index1 = (int)(row * width + col);
		int index2 = (int)(row * width + colNext);
		
		int index3 = (int)(rowNext * width + col);
		int index4 = (int)(rowNext * width + colNext);
		int c = 0;
        
        byte[] rgb = new byte[ch];
        for(int i=0; i<ch; i++) {
        	int c1 = processor.toByte(i)[index1] &0xff;
        	int c2 = processor.toByte(i)[index2] &0xff;
        	int c3 = processor.toByte(i)[index3] &0xff;
        	int c4 = processor.toByte(i)[index4] &0xff;
        	c = (int)(c1 * coffiecent1 + c2 * coffiecent4 + c3  * coffiecent2 + c4  * coffiecent3);  
        	rgb[i] = (byte)c;
        }

		return rgb;
	}

}
