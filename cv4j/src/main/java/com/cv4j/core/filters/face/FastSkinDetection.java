package com.cv4j.core.filters.face;

import com.cv4j.image.util.Tools;

/**
 * This class provides an interface that quickly identifies whether a color is close to the skin color.
 * You can call {@link #findSkin(int, int, int)} or {@link #isSkin(int, int, int)} to identify colors
 *
 * @author gloomy fish, Jianguo Yang
 * @version v1.0
 */
public class FastSkinDetection implements ISkinDetection {

	/**
	 * RGB to YCrCb Color space transform
	 * <p>
	 * <b>Y = 0299R + 0.587G + 0.114B </b>[ 220 level, scope 16 ~ 235 ] <br>
	 * <b>Cr = R - Y </b> [ 225 level 16 ~ 240 ] <br>
	 * <b>Cb = B - Y </b> [ 225 level 16 ~ 240 ] <br>
	 * </p>
	 * skin cluster is given as following for Europe white race:<br>
	 * Y > 80 <br>
	 * 85 < Cb < 135 <br>
	 * 135 < Cr < 180 <br>
	 * where Y, Cb, Cr = [0,255] <br>
	 *
	 * @param tr the value R of RGB color space
	 * @param tg the value G of RGB color space
	 * @param tb the value B of RGB color space
	 * @return  whether the color is skin's color
	 */
	@Override
	public boolean findSkin(int tr, int tg, int tb) {
		int y = (int)(tr * 0.299 + tg * 0.587 + tb * 0.114);
		int cr = tr - y;
		int cb = tb - y;
		return (y > 80 && y < 255) && (cr > 133 && cr < 173) && (77 < cb && cb < 127);
	}

	/**
	 * whether the input color is skin color : <br>
	 * if the color transformed to YCrCb color space fit below: <br>
	 * 77 < Cb < 127  <br>
	 * 133 < Cr < 173 <br>
	 * will return true
	 *
	 * @param tr the value R of RGB color space
	 * @param tg the value G of RGB color space
	 * @param tb the value B of RGB color space
	 * @return whether the color is skin's color
	 */
	@Override
	public boolean isSkin(int tr, int tg, int tb) {
		int[] ycrcb = Tools.rgbToYcrCb(tr, tg, tb);
		int cr = ycrcb[1];
		int cb = ycrcb[2];
		return (cr > 133 && cr < 173) && (77 < cb && cb < 127);
	}

}
