package com.cv4j.app.activity;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

import com.cv4j.app.R;
import com.cv4j.app.app.BaseActivity;
import com.cv4j.core.datamodel.CV4JImage;
import com.cv4j.core.filters.CompositeFilters;
import com.cv4j.core.filters.NatureFilter;
import com.cv4j.core.filters.SpotlightFilter;
import com.safframework.injectview.Injector;
import com.safframework.injectview.annotations.InjectView;

import thereisnospon.codeview.CodeView;
import thereisnospon.codeview.CodeViewTheme;

/**
 * Created by Tony Shen on 2017/3/11.
 */

public class CompositeFilersActivity extends BaseActivity {

    @InjectView(R.id.image1)
    ImageView image1;

    @InjectView(R.id.image2)
    ImageView image2;

    @InjectView(R.id.image3)
    ImageView image3;

    @InjectView(R.id.codeview)
    CodeView codeView;

    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_composite_filters);
        Injector.injectInto(this);

        initData();
    }

    private void initData() {
        Resources res = getResources();
        bitmap = BitmapFactory.decodeResource(res, R.drawable.test_filters);

        CV4JImage ci1 = new CV4JImage(bitmap);
        image1.setImageBitmap(new NatureFilter().filter(ci1.getProcessor()).getImage().toBitmap());

        CV4JImage ci2 = new CV4JImage(bitmap);
        image2.setImageBitmap(new SpotlightFilter().filter(ci2.getProcessor()).getImage().toBitmap());

        CompositeFilters compositeFilters = new CompositeFilters();
        Bitmap newBitmap = compositeFilters
                        .addFilter(new NatureFilter())
                        .addFilter(new SpotlightFilter())
                        .filter(new CV4JImage(bitmap).getProcessor())
                        .getImage()
                        .toBitmap();

        image3.setImageBitmap(newBitmap);

        codeView.setTheme(CodeViewTheme.ANDROIDSTUDIO).fillColor();

        StringBuilder code = new StringBuilder();
        code.append("CompositeFilters compositeFilters = new CompositeFilters();")
                .append("\r\n")
                .append("Bitmap newBitmap = compositeFilters")
                .append("\r\n")
                .append(".addFilter(new NatureFilter())").append("\r\n")
                .append(".addFilter(new SpotlightFilter())").append("\r\n")
                .append(".filter(new ColorImage(bitmap))").append("\r\n")
                .append(".toBitmap();").append("\r\n").append("\r\n")
                .append("image3.setImageBitmap(newBitmap);");

        codeView.showCode(code.toString());
    }
}