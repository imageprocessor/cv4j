package com.cv4j.core.filters.face;

/**
 * based on RGB Color Model, statistic skin detection algorithm
 * 
 * @author gloomy fish
 *
 */
public class DefaultSkinDetection implements ISkinDetection{
// RGB Color model pixel skin detection method
// (R, G, B) is classified as skin if:
// R > 95 and G > 40 and B > 20 and
// max(R, G, B) - min(R, G, B) > 15 and
// |R-G| > 15 and R > G and R > B
//===============================================
	
	@Override
	public boolean findSkin(int tr, int tg, int tb) {
		return isSkin(tr, tg, tb);
	}

	@Override
	public boolean isSkin(int tr, int tg, int tb) {
		int max = Math.max(tr, Math.max(tg, tb));
		int min = Math.min(tr, Math.min(tg, tb));
		int rg = Math.abs(tr - tg);
		if(tr > 95 && tg > 40 && tb > 20 && rg > 15 && 
				(max - min) > 15 && tr > tg && tr > tb) {
			return true;
		} else {
			return false;
		}
	}

}
