package com.cv4j.app.activity;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;

import com.cv4j.app.R;
import com.cv4j.app.app.BaseActivity;
import com.cv4j.core.filters.BeautySkinFilter;
import com.cv4j.rxjava.RxImageData;
import com.safframework.injectview.annotations.InjectExtra;
import com.safframework.injectview.annotations.InjectView;
import com.safframework.injectview.annotations.OnClick;

/**
 * Created by Tony Shen on 2017/4/23.
 */

public class BeautySkinActivity extends BaseActivity {

    @InjectView(R.id.image1)
    ImageView image1;

    @InjectView(R.id.image2)
    ImageView image2;

    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @InjectExtra(key = "Title")
    String title;

    RxImageData rxImageData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beauty_skin);

        initData();
    }

    private void initData() {
        toolbar.setTitle("< "+title);
        Resources res= getResources();
        Bitmap bitmap = BitmapFactory.decodeResource(res, R.drawable.test_beauty_skin2);
        image1.setImageBitmap(bitmap);

        rxImageData = RxImageData.bitmap(bitmap);
        rxImageData.addFilter(new BeautySkinFilter()).into(image2);
    }

    @OnClick(id= R.id.toolbar)
    void clickToolbar() {

        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        rxImageData.recycle();
    }
}
