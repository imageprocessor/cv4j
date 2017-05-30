package com.cv4j.core.filters.face;

import com.cv4j.image.util.Tools;

/**
 * RGB to YCrCb Color space transform
 * 
 * Y = 0299R + 0.587G + 0.114B - 220 level, scope 16 ~ 235
 * Cr = R - Y [ 225 level 16 ~ 240 ]
 * Cb = B - Y [ 225 level 16 ~ 240 ]
 * 
 * skin cluster is given as following for Europe white race:
 * Y > 80
 * 85 < Cb < 135
 * 135 < Cr < 180
 * where Y, Cb, Cr = [0,255]
 * 
 * find skin
 * 77 < Cb < 127
 * 133 < Cr < 173
 * 
 * @author gloomy fish
 *
 */
public class FastSkinDetection implements ISkinDetection {

	@Override
	public boolean findSkin(int tr, int tg, int tb) {
		int y = (int)(tr * 0.299 + tg * 0.587 + tb * 0.114);
		int Cr = tr - y;
		int Cb = tb - y;
		if(y> 80 && y < 255 && Cr > 133 && Cr < 173 && 77 < Cb && Cb < 127) {
			return true;
		}
		return false;
	}

	@Override
	public boolean isSkin(int tr, int tg, int tb) {
		int[] ycrcb = Tools.rgbToYcrCb(tr, tg, tb);
		if(ycrcb[1] > 133 && ycrcb[1] < 173 && 77 < ycrcb[2] && ycrcb[2] < 127) {
			return true;
		}
		return false;
	}

}
