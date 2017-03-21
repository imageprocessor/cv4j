package com.cv4j.rxjava;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.cv4j.core.datamodel.ColorImage;
import com.cv4j.core.datamodel.ImageData;
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

    public static RxImageData colorImage(ColorImage colorImage) {

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

    /**
     * RxImageData.imageData(bitmap).addFilter(new ColorFilter()).into(view);
     * @param imageview
     */
    public void into(final ImageView imageview) {

        this.toFlowable().compose(toMain()).subscribe(new Consumer<ImageData>() {
            @Override
            public void accept(@NonNull ImageData imgaeData) throws Exception {
                imageview.setImageBitmap(imgaeData.toBitmap());
            }
        });
    }

    /**
     * RxImageData.imageData(bitmap).addFilter(new ColorFilter()).toFlowable().compose
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
