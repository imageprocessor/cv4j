package com.cv4j.rxjava;

import android.graphics.Bitmap;

import com.cv4j.core.datamodel.ColorImage;
import com.cv4j.core.datamodel.ImageData;
import com.cv4j.core.filters.CommonFilter;

import io.reactivex.Flowable;
import io.reactivex.functions.Function;

/**
 * Created by Tony Shen on 2017/3/14.
 */

public class RxImageData {

    ColorImage colorImage;
    Flowable flowable;

    private RxImageData(Bitmap bitmap) {

        this.colorImage = new ColorImage(bitmap);
        flowable = Flowable.just(colorImage);
    }

    private RxImageData(ColorImage colorImage) {

        this.colorImage = colorImage;
        flowable = Flowable.just(colorImage);
    }

    public static RxImageData imageData(Bitmap bitmap) {

        return new RxImageData(bitmap);
    }

    public static RxImageData imageData(ColorImage colorImage) {

        return new RxImageData(colorImage);
    }

    public RxImageData addFilter(final CommonFilter filter) {

        flowable = flowable.map(new Function<ImageData,ImageData>() {
            @Override
            public ImageData apply(ImageData imageData) throws Exception {
                return filter.filter(imageData);
            }
        });

        return this;
    }

    public Flowable toFlowable() {

        return flowable;
    }

}
