/**
 * Copyright (c) 2017-present, CV4J Contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cv4j.core.filters.face;

/**
 * Based on RGB Color Model, statistic skin detection algorithm
 *
 */
public class DefaultSkinDetection implements ISkinDetection{

	/**
	 * RGB Color model pixel skin detection method. <p>
	 *
	 * @param tr the value R of RGB color space
	 * @param tg the value G of RGB color space
	 * @param tb the value B of RGB color space
	 * @return whether the color is skin's color
	 *
	 * @see #isSkin(int, int, int)
	 */
	@Override
	public boolean findSkin(int tr, int tg, int tb) {
		return isSkin(tr, tg, tb);
	}

	/**
	 * RGB Color model pixel skin detection method. <p>
	 * (R, G, B) is classified as skin if: <br>
	 * R > 95 and G > 40 and B > 20 <br>
	 * and max(R, G, B) - min(R, G, B) > 15 <br>
	 * and |R-G| > 15 and R > G and R > B <br>
	 *
	 * @param tr the value R of RGB color space
	 * @param tg the value G of RGB color space
	 * @param tb the value B of RGB color space
	 * @return whether the color is skin's color
	 */
	@Override
	public boolean isSkin(int tr, int tg, int tb) {
		int max = Math.max(tr, Math.max(tg, tb));
		int min = Math.min(tr, Math.min(tg, tb));
		int rg = Math.abs(tr - tg);

		return tr > 95 && tg > 40 && tb > 20 && rg > 15 &&
				(max - min) > 15 && tr > tg && tr > tb;
	}

}
