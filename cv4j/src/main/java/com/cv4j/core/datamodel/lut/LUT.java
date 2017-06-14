/*
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
package com.cv4j.core.datamodel.lut;

import static com.cv4j.core.filters.ColorFilter.AUTUMN_STYLE;
import static com.cv4j.core.filters.ColorFilter.BONE_STYLE;
import static com.cv4j.core.filters.ColorFilter.COOL_STYLE;
import static com.cv4j.core.filters.ColorFilter.HOT_STYLE;
import static com.cv4j.core.filters.ColorFilter.HSV_STYLE;
import static com.cv4j.core.filters.ColorFilter.JET_STYLE;
import static com.cv4j.core.filters.ColorFilter.OCEAN_STYLE;
import static com.cv4j.core.filters.ColorFilter.PINK_STYLE;
import static com.cv4j.core.filters.ColorFilter.RAINBOW_STYLE;
import static com.cv4j.core.filters.ColorFilter.SPRING_STYLE;
import static com.cv4j.core.filters.ColorFilter.SUMMER_STYLE;
import static com.cv4j.core.filters.ColorFilter.WINTER_STYLE;

public class LUT {

    public static int[][] getColorFilterLUT(int style) {

        switch(style) {
            case AUTUMN_STYLE:
                return AutumnLUT.AUTUMN_LUT;

            case BONE_STYLE:
                return BoneLUT.BONE_LUT;

            case COOL_STYLE:
                return CoolLUT.COOL_LUT;

            case HOT_STYLE:
                return HotLUT.HOT_LUT;

            case HSV_STYLE:
                return HsvLUT.HSV_LUT;

            case JET_STYLE:
                return JetLUT.JET_LUT;

            case OCEAN_STYLE:
                return OceanLUT.OCEAN_LUT;

            case PINK_STYLE:
                return PinkLUT.PINK_LUT;

            case RAINBOW_STYLE:
                return RainbowLUT.RAINBOW_LUT;

            case SPRING_STYLE:
                return SpringLUT.SPRING_LUT;

            case SUMMER_STYLE:
                return SummerLUT.SUMMER_LUT;

            case WINTER_STYLE:
                return WinterLUT.WINTER_LUT;

            default:
                return AutumnLUT.AUTUMN_LUT;
        }
    }
}
