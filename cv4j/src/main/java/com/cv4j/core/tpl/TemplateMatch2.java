package com.cv4j.core.tpl;

import java.util.Arrays;

import com.cv4j.core.datamodel.FloatProcessor;
import com.cv4j.core.datamodel.ImageProcessor;
import com.cv4j.core.datamodel.IntIntegralImage;

public class TemplateMatch2 {
    public static final int TM_SQDIFF_NORMED = 2;
    public static final int TM_CCORR_NORMED = 4;
    public static final int TM_CCOEFF_NORMED = 6;
    
    private float[] sqrt_tpl;
    private void initParams(ImageProcessor tpl) {
    	int chs = tpl.getChannels();
        sqrt_tpl = new float[chs];
        Arrays.fill(sqrt_tpl, 0);
        int r=0;
        for(int c=0; c<chs; c++) {
        	byte[] R = tpl.toByte(c);
        	for(int i=0; i<R.length; i++) {
        		r = R[i]&0xff;
        		sqrt_tpl[c] += r*r;
        	}        	
        }
    }
    
    public FloatProcessor match(ImageProcessor target, ImageProcessor tpl, int method) {
        int width = target.getWidth();
        int height = target.getHeight();
        int tw = tpl.getWidth();
        int th = tpl.getHeight();
        int[] tplmask = new int[tpl.getWidth() * tpl.getHeight()];
        Arrays.fill(tplmask, 0);
        initParams(tpl);
        int offx = tw/2+1;
        int offy = th/2+1;
        int rw = width - offx*2;
        int rh = height - offy*2;
        float[] result = new float[rw*rh];
        Arrays.fill(result, 0);
        FloatProcessor tpl_data = new FloatProcessor(result, rw, rh);
        if(target.getChannels() == 3 && tpl.getChannels() == 3) {
            for(int ch=0; ch<3; ch++) {
            	FloatProcessor tmp = processSingleChannels(width, height, target.toByte(ch), tw, th, tpl.toByte(ch), ch);
            	tpl_data.addArray(tmp.toFloat(0));
            }
        } else if(target.getChannels() == 1 && tpl.getChannels() == 1) {
        	tpl_data = processSingleChannels(width, height, target.toByte(0), tw, th, tpl.toByte(0), 0);
        } else {
            throw new IllegalStateException("\nERR:Image Type is not same...\n");
        }
        return tpl_data;
    }
    
    public FloatProcessor processSingleChannels(int width, int height, byte[] pixels, int tw, int th, byte[] tpl, int ch_index){
        int offx = tw/2+1;
        int offy = th/2+1;
        int[] tplmask = new int[tw * th];
        Arrays.fill(tplmask, 0);
        int rw = width - offx*2;
        int rh = height - offy*2;
        float[] result = new float[rw*rh];
        IntIntegralImage ii = new IntIntegralImage();
        ii.setImage(pixels);
        ii.process(width, height, true);
        for(int row=offy; row<height-offy; row++) {
            for(int col=offx; col<width-offx; col++) {
            	int[] roi = getROI(width, height, tw, th, row, col, pixels);
            	float sr = ii.getBlockSquareSum(col, row, (offx * 2 + 1), (offy * 2 + 1));
            	float sum = sqrt_tpl[ch_index] + sr - multplyArras(roi, tpl);
            	result[(row-offy)*rw + (col-offx)] = sum;
            }
        }

        return new FloatProcessor(result, rw, rh);
    }
    
    public float multplyArras(int[] roi, byte[] tpl) {
    	int sum = 0;
    	for(int i=0; i<roi.length; i++) {
    		sum += (tpl[i]&0xff)*roi[i];
    	}
    	return 2*sum;
    }
    
    
	private int[] getROI(int w, int h, int tw, int th, int y, int x, byte[] pixels) {
		int[] roidata = new int[tw*th];
		int xoffset = x;
		int yoffset = y;
		for(int row=yoffset; row<th+yoffset; row++) {
			int index1 = row*w;
			int index2 = (row-yoffset)*tw;
			for(int col=xoffset; col<tw+xoffset; col++) {
				roidata[index2+col-xoffset] = pixels[index1+col]&0xff;
			}
		}
		return roidata;
	}
}
