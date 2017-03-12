package com.cv4j.app.activity;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.cv4j.app.R;
import com.cv4j.core.datamodel.ColorImage;
import com.safframework.injectview.Injector;
import com.safframework.injectview.annotations.InjectExtra;
import com.safframework.injectview.annotations.InjectView;


/**
 * io读写操作
 * Created by Tony Shen on 2017/3/7.
 */

public class IOActivity extends AppCompatActivity {

    @InjectView(R.id.image1)
    ImageView image1;

    @InjectView(R.id.image2)
    ImageView image2;

    @InjectExtra(key=TITLE)
    String title;

    public static final String TITLE = "Title";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_io);
        Injector.injectInto(this);

        initData();
    }

    private void initData() {
        setTitle(title);

        Resources res= getResources();
        Bitmap bitmap = BitmapFactory.decodeResource(res, R.drawable.test_io);
        image1.setImageBitmap(bitmap);

        ColorImage ci = new ColorImage(bitmap);
        image2.setImageBitmap(ci.toBitmap());
    }
}
