package com.cv4j.app.activity;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

import com.cv4j.app.R;
import com.cv4j.app.app.BaseActivity;
import com.cv4j.core.filters.NatureFilter;
import com.cv4j.rxjava.RxImageData;
import com.safframework.injectview.annotations.InjectView;

import thereisnospon.codeview.CodeView;
import thereisnospon.codeview.CodeViewTheme;

/**
 * Created by Tony Shen on 2017/3/13.
 */

public class UseFilterWithRxActivity extends BaseActivity {

    @InjectView(R.id.image)
    ImageView image;

    @InjectView(R.id.codeview)
    CodeView codeView;

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
                .into(image);

        codeView.setTheme(CodeViewTheme.ANDROIDSTUDIO).fillColor();

        StringBuilder code = new StringBuilder();
        code.append("RxImageData.imageData(bitmap)")
                .append("\r\n")
                .append("    .addFilter(new NatureFilter())")
                .append("\r\n")
                .append("    .into(image)");

        codeView.showCode(code.toString());
    }
}
