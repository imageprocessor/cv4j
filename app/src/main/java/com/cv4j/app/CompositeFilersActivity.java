package com.cv4j.app;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.cv4j.core.datamodel.ColorImage;
import com.cv4j.core.filters.CompositeFilters;
import com.cv4j.core.filters.NatureFilter;
import com.cv4j.core.filters.SpotlightFilter;
import com.safframework.injectview.Injector;
import com.safframework.injectview.annotations.InjectView;

/**
 * Created by Tony Shen on 2017/3/11.
 */

public class CompositeFilersActivity extends AppCompatActivity {

    @InjectView(R.id.image1)
    ImageView image1;

    @InjectView(R.id.image2)
    ImageView image2;

    @InjectView(R.id.image3)
    ImageView image3;

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

        ColorImage ci1 = new ColorImage(bitmap);
        image1.setImageBitmap(new NatureFilter().filter(ci1).toBitmap());

        ColorImage ci2 = new ColorImage(bitmap);
        image2.setImageBitmap(new SpotlightFilter().filter(ci2).toBitmap());

        CompositeFilters compositeFilters = new CompositeFilters();
        Bitmap newBitmap = compositeFilters.addFilter(new NatureFilter())
                        .addFilter(new SpotlightFilter())
                        .filter(new ColorImage(bitmap)).toBitmap();

        image3.setImageBitmap(newBitmap);
    }
}