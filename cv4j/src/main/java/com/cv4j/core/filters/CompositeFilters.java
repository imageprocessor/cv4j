package com.cv4j.core.filters;

import com.cv4j.core.datamodel.ColorProcessor;
import com.cv4j.core.datamodel.ImageProcessor;

import java.util.ArrayList;
import java.util.List;

/**
 * 组合使用多个滤镜
 * Created by Tony Shen on 2017/3/11.
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
