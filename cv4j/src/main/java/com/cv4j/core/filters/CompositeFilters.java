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

import java.util.ArrayList;
import java.util.List;

/**
 * 组合使用多个滤镜
 *
 */

public class CompositeFilters {

    List<CommonFilter> lists;

    public CompositeFilters() {

        lists = new ArrayList<>();
    }

    public CompositeFilters addFilter(CommonFilter filter) {

        lists.add(filter);
        return this;
    }

    public ImageProcessor filter(ImageProcessor imageData) {

        if (!(imageData instanceof ColorProcessor)) return imageData;

        if (lists!=null && lists.size()>0) {
            return filter(imageData,lists.size());
        }

        return imageData;
    }

    private ImageProcessor filter(ImageProcessor imageData, int size) {

        if (size==1) {
            CommonFilter filter = lists.get(0);
            return filter.filter(imageData);
        }
        
        CommonFilter filter = lists.get(size-1);
        imageData = filter.filter(imageData);

        return filter(imageData,size-1);
    }

}
