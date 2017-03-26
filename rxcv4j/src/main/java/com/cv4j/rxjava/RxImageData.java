package com.cv4j.rxjava;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.cv4j.core.datamodel.CV4JImage;
import com.cv4j.core.datamodel.ImageData;
import com.cv4j.core.datamodel.ImageProcessor;
import com.cv4j.core.filters.CommonFilter;

import org.reactivestreams.Publisher;

import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Tony Shen on 2017/3/14.
 */

public class RxImageData {

    CV4JImage image;
    Flowable flowable;

    private RxImageData(Bitmap bitmap) {

        this.image = new CV4JImage(bitmap);
        flowable = Flowable.just(image);
    }

    private RxImageData(CV4JImage image) {

        this.image = image;
        flowable = Flowable.just(image);
    }

    public static RxImageData bitmap(Bitmap bitmap) {

        return new RxImageData(bitmap);
    }

    public static RxImageData image(CV4JImage image) {

        return new RxImageData(image);
    }

    public RxImageData addFilter(final CommonFilter filter) {

        flowable = flowable.map(new Function<CV4JImage,ImageProcessor>() {
            @Override
            public ImageProcessor apply(CV4JImage imageData) throws Exception {
                return filter.filter(imageData.getProcessor());
            }
        });

        return this;
    }

    public Flowable toFlowable() {

        return flowable;
    }

    /**
     * 占位符，必须在addFilter()之前，因为滤镜操作会花费时间
     * @param imageview
     * @param resId
     * @return
     */
    public RxImageData placeHolder(final ImageView imageview,final int resId) {

        flowable = this.toFlowable().doOnNext(new Consumer<ImageData>() {
            @Override
            public void accept(@NonNull ImageData imageData) throws Exception {
                imageview.setImageResource(resId);
            }
        });

        return this;
    }

    /**
     * RxImageData.bitmap(bitmap).addFilter(new ColorFilter()).into(view);
     * @param imageview
     */
    public void into(final ImageView imageview) {

        this.toFlowable().compose(toMain()).subscribe(new Consumer<ImageProcessor>() {
            @Override
            public void accept(@NonNull ImageProcessor imgaeData) throws Exception {
                imageview.setImageBitmap(imgaeData.getImage().toBitmap());
            }
        });
    }

    /**
     * RxImageData.bitmap(bitmap).addFilter(new ColorFilter()).toFlowable().compose
     *     (RxImageData.toMain()).subscribe(new Consumer<ImageData>() {
     *
     *
     *         @Override
     *        public void accept(@NonNull ImageData imgaeData) throws Exception {
     *
     *           image.setImageBitmap(imgaeData.toBitmap());
     *       }
     * });
     * @param <T>
     * @return
     */
    public static <T> FlowableTransformer<T, T> toMain() {

        return new FlowableTransformer<T, T>() {

            @Override
            public Publisher<T> apply(Flowable<T> upstream) {
                return upstream.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }
}
