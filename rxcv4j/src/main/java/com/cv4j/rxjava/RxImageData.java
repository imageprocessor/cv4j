package com.cv4j.rxjava;

import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

import com.cv4j.core.datamodel.CV4JImage;
import com.cv4j.core.datamodel.ImageProcessor;
import com.cv4j.core.filters.CommonFilter;

import org.reactivestreams.Publisher;

import java.util.ArrayList;
import java.util.List;

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
    MemCache memCache;
    boolean useCache = true;

    ImageView imageView;
    List<CommonFilter> filters;
    WrappedCV4JImage wrappedCV4JImage;

    private RxImageData(byte[] bytes) {
        this(new CV4JImage(bytes));
    }

    private RxImageData(Bitmap bitmap) {
        this(new CV4JImage(bitmap));
    }

    private RxImageData(CV4JImage image) {

        this.image = image;
        filters = new ArrayList<>();
        memCache = MemCache.getInstance();

        wrappedCV4JImage = new WrappedCV4JImage(image,filters);
        flowable = Flowable.just(wrappedCV4JImage);
    }

    public static RxImageData bytes(byte[] bytes) {

        return new RxImageData(bytes);
    }

    public static RxImageData bitmap(Bitmap bitmap) {

        return new RxImageData(bitmap);
    }

    public static RxImageData image(CV4JImage image) {

        return new RxImageData(image);
    }

    /**
     * 使用滤镜，支持链式调用多个滤镜
     * @param filter
     * @return
     */
    public RxImageData addFilter(final CommonFilter filter) {

        if (filter==null) {

            Log.e("RxImageData","filter is null");
            return this;
        }

        filters.add(filter);
        return this;
    }

    /**
     * 判断是否使用缓存，默认情况下是使用缓存。
     * 该方法需要在into()方法之前使用。
     * @param useCache
     * @return
     */
    public RxImageData isUseCache(boolean useCache) {

        this.useCache = useCache;
        return this;
    }

    /**
     * RxImageData.bitmap(bitmap).addFilter(new ColorFilter()).into(view);
     * @param imageview
     */
    public void into(final ImageView imageview) {

        this.imageView = imageview;
        render();
    }

    /**
     * 渲染imageview
     */
    private void render() {

        if (imageView == null) {
            return;
        }

        if (filters.size()==0) {
            this.flowable.compose(RxImageData.toMain()).subscribe(new Consumer<WrappedCV4JImage>() {
                @Override
                public void accept(@NonNull WrappedCV4JImage wrapped) throws Exception {
                    imageView.setImageBitmap(wrapped.image.toBitmap());
                }
            });
        } else if (filters.size() == 1) {
            this.flowable
                    .map(new Function<WrappedCV4JImage,ImageProcessor>() {

                @Override
                public ImageProcessor apply(@NonNull WrappedCV4JImage wrap) throws
                        Exception {

                    if (useCache) {

                        StringBuilder sb = new StringBuilder();
                        if (imageView.getContext()!=null) {
                            sb.append(imageView.getContext().getClass().getSimpleName());
                        }

                        sb.append(wrap.filters.get(0).getClass().getSimpleName()).append(imageView.getId());

                        // 目前key采用activity name + filter name + imageView id
                        String key = Utils.md5(sb.toString());

                        if (memCache.get(key)==null) {

                            ImageProcessor imageProcessor = wrap.filters.get(0).filter(image.getProcessor());
                            memCache.put(key,imageProcessor.getImage().toBitmap());

                            return imageProcessor;
                        } else {

                            image.getProcessor().getImage().setBitmap(memCache.get(key));
                            return image.getProcessor();
                        }
                    } else {

                        return wrap.filters.get(0).filter(image.getProcessor());
                    }
                }
            }).compose(RxImageData.toMain()).subscribe(new Consumer<ImageProcessor>() {
                @Override
                public void accept(@NonNull ImageProcessor processor) throws Exception {
                    imageView.setImageBitmap(processor.getImage().toBitmap());
                }
            });

        } else {

            this.flowable.map(new Function<WrappedCV4JImage,List<CommonFilter>>() {
                @Override
                public List<CommonFilter> apply(@NonNull WrappedCV4JImage wrap) throws Exception {
                    return wrap.filters;
                }
            }).map(new Function<List<CommonFilter>,ImageProcessor>() {
                @Override
                public ImageProcessor apply(@NonNull List<CommonFilter> filters) throws Exception {
                    return filter(image.getProcessor());
                }
            }).compose(RxImageData.toMain()).subscribe(new Consumer<ImageProcessor>() {
                @Override
                public void accept(@NonNull ImageProcessor processor) throws Exception {
                    imageView.setImageBitmap((processor.getImage().toBitmap()));
                }
            });
        }
    }

    private ImageProcessor filter(ImageProcessor imageData) {

        if (filters.size()>0) {
            return filter(imageData,filters.size());
        }

        return imageData;
    }

    private ImageProcessor filter(ImageProcessor imageData, int size) {

        if (size==1) {
            CommonFilter filter = filters.get(0);
            return filter.filter(imageData);
        }

        CommonFilter filter = filters.get(size-1);
        imageData = filter.filter(imageData);

        return filter(imageData,size-1);
    }

    /**
     * 
     * @param <T>
     * @return
     */
    private static <T> FlowableTransformer<T, T> toMain() {

        return new FlowableTransformer<T, T>() {

            @Override
            public Publisher<T> apply(Flowable<T> upstream) {
                return upstream.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

    /**
     * 释放资源
     */
    public void recycle() {

        if (image!=null) {
            image.recycle();
            image = null;
        }
    }
}
