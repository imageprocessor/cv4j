package com.cv4j.core.filters;

import com.cv4j.core.datamodel.ColorProcessor;
import com.cv4j.core.datamodel.ImageProcessor;

/**
 * Created by gloomy fish on 2017/3/5.
 */

public interface CommonFilter {

    ImageProcessor filter(ColorProcessor imagedata);
}
