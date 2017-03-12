package com.cv4j.core.filters;

import com.cv4j.core.datamodel.ImageData;
import com.cv4j.image.util.Tools;

public class MotionFilter implements CommonFilter  {

	private float distance = 10;// default;
	private float onePI = (float)Math.PI;
	private float angle = 0.0f;
	private float zoom = 0.4f;

	public float getDistance() {
		return distance;
	}

	public void setDistance(float distance) {
		this.distance = distance;
	}

	public float getAngle() {
		return angle;
	}

	public void setAngle(float angle) {
		this.angle = angle;
	}

	
	@Override
	public ImageData filter(ImageData src){
		int width = src.getWidth();
        int height = src.getHeight();

		byte[] R = src.getChannel(0);
		byte[] G = src.getChannel(1);
		byte[] B = src.getChannel(2);
		byte[][] output = new byte[3][R.length];
        int index = 0;
        int cx = width/2;
        int cy = height/2;
        
        // calculate the triangle geometry value
        float sinAngle = (float)Math.sin(angle/180.0f * onePI);
        float coseAngle = (float)Math.cos(angle/180.0f * onePI);
        
        // calculate the distance, same as box blur
        float imageRadius = (float)Math.sqrt(cx*cx + cy*cy);
        float maxDistance = distance + imageRadius * zoom;
        
        int iteration = (int)maxDistance;
        for(int row=0; row<height; row++) {
        	int ta = 0, tr = 0, tg = 0, tb = 0;
        	for(int col=0; col<width; col++) {
        		int newX= col, count = 0;
        		int newY = row;
        		
        		// iterate the source pixels according to distance
        		float m11 = 0.0f, m22 = 0.0f;
        		for(int i=0; i<iteration; i++) {
        			newX = col;
        			newY = row;
        			
        			// calculate the operator source pixel
        			if(distance > 0) {
	        			newY = (int)Math.floor((newY + i*sinAngle));
	        			newX = (int)Math.floor((newX + i*coseAngle));
        			}
        			float f = (float)i/iteration;
        			if (newX < 0 || newX >= width) {
        				break;
					}
					if (newY < 0 || newY >= height) {
						break;
					}
					
					// scale the pixels
					float scale = 1-zoom*f;
					m11 = cx - cx*scale;
					m22 = cy - cy*scale;
					newY = (int)(newY * scale + m22);
					newX = (int)(newX * scale + m11);
					
					// blur the pixels, here
					count++;
					int idx = newY*width+newX;
					tr += R[idx] & 0xff;
					tg += G[idx] & 0xff;
					tb += B[idx] & 0xff;
        		}
        		
        		// fill the destination pixel with final RGB value
        		if (count == 0) {
					output[0][index] = R[index];
					output[1][index] = G[index];
					output[2][index] = B[index];
				} else {
					tr = Tools.clamp((int)(tr/count));
					tg = Tools.clamp((int)(tg/count));
					tb = Tools.clamp((int)(tb/count));
					output[0][index] = (byte)tr;
					output[1][index] = (byte)tg;
					output[2][index] = (byte)tb;
				}
				index++;
        	}
        }
		src.putPixels(output);
		output = null;
		return src;
	}

}