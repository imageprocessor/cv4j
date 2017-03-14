package com.cv4j.rxjava;


import android.graphics.Bitmap;

import com.cv4j.core.datamodel.ColorImage;
import com.cv4j.core.datamodel.ImageData;
import com.cv4j.core.filters.CommonFilter;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Tony Shen on 2017/3/14.
 */

public class RxImageData {

    private Observable observable;

    public RxImageData(final Bitmap bitmap) {

        observable = Observable.defer(new Func0() {
            @Override
            public Observable call() {
                return Observable.just(new ColorImage(bitmap));
            }
        });
    }

    public RxImageData(final ColorImage colorImage) {

        observable = Observable.defer(new Func0() {
            @Override
            public Observable call() {
                return Observable.just(colorImage);
            }
        });
    }

    public Observable addFilter(final CommonFilter filter) {

        if (observable==null) {
            return null;
        }

        return observable.map(new Func1<ColorImage,ImageData>() {
            @Override
            public ImageData call(ColorImage colorImage) {
                return filter.filter(colorImage);
            }
        });
    }

    /**
     * 跟compose()配合使用
     * @param <T>
     * @return
     */
    public static <T> Observable.Transformer<T, T> toMain() {
        return new Observable.Transformer<T, T>() {
            @Override
            public Observable<T> call(Observable<T> tObservable) {
                return tObservable
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }
}
