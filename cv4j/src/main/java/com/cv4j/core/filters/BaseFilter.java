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
package com.cv4j.core.filters;

import com.cv4j.core.datamodel.ColorProcessor;
import com.cv4j.core.datamodel.ImageProcessor;

public abstract class BaseFilter implements CommonFilter {

    protected int width;
    protected int height;
    protected byte[] R;
    protected byte[] G;
    protected byte[] B;

    @Override
    public ImageProcessor filter(ImageProcessor src) {

        if (src == null) return null;

        if (!(src instanceof ColorProcessor)) return src;

        width = src.getWidth();
        height = src.getHeight();
        R = ((ColorProcessor)src).getRed();
        G = ((ColorProcessor)src).getGreen();
        B = ((ColorProcessor)src).getBlue();

        return doFilter(src);
    }

    public abstract ImageProcessor doFilter(ImageProcessor src);
}
