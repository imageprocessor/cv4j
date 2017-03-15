package com.cv4j.app.activity;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

import com.cv4j.app.R;
import com.cv4j.app.app.BaseActivity;
import com.cv4j.core.datamodel.ImageData;
import com.cv4j.core.filters.NatureFilter;
import com.cv4j.rxjava.RxImageData;
import com.safframework.injectview.annotations.InjectView;

import io.reactivex.functions.Consumer;

/**
 * Created by Tony Shen on 2017/3/13.
 */

public class UseFilterWithRxActivity extends BaseActivity {

    @InjectView(R.id.image)
    ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_use_filter_with_rx);

        initData();
    }

    private void initData() {

        Resources res = getResources();
        final Bitmap bitmap = BitmapFactory.decodeResource(res, R.drawable.test_io);


        RxImageData.imageData(bitmap)
                .addFilter(new NatureFilter())
                .toFlowable()
                .compose(RxImageData.toMain())
                .subscribe(new Consumer<ImageData>() {

            @Override
            public void accept(ImageData imageData) throws Exception {
                image.setImageBitmap(imageData.toBitmap());
            }
        });
    }
}
