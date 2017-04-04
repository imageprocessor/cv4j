package com.cv4j.rxjava;

import com.cv4j.core.datamodel.CV4JImage;
import com.cv4j.core.filters.CommonFilter;

import java.util.List;

/**
 * Created by Tony Shen on 2017/3/31.
 */

public class WrappedCV4JImage {

    public CV4JImage image;
    public List<CommonFilter> filters;

    public WrappedCV4JImage(CV4JImage image,List<CommonFilter> filters) {

        this.image = image;
        this.filters = filters;
    }
}
