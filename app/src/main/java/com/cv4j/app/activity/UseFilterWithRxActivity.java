package com.cv4j.app.activity;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

import com.cv4j.app.R;
import com.cv4j.app.app.BaseActivity;
import com.cv4j.app.utils.ObservableUtils;
import com.cv4j.core.datamodel.ColorImage;
import com.cv4j.core.datamodel.ImageData;
import com.cv4j.core.filters.NatureFilter;
import com.safframework.injectview.annotations.InjectView;

import rx.functions.Action1;
import rx.functions.Func1;

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

        ObservableUtils.wrap(new ColorImage(bitmap))
                .map(new Func1<ColorImage,ImageData>() {
                    @Override
                    public ImageData call(ColorImage ci) {
                        return new NatureFilter().filter(ci);
                    }
                })
                .compose(ObservableUtils.toMain())
                .subscribe(new Action1<ImageData>() {

            @Override
            public void call(ImageData imageData) {
                image.setImageBitmap(imageData.toBitmap());
            }
        });
    }
}
