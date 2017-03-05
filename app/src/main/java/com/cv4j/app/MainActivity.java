package com.cv4j.app;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.cv4j.core.datamodel.ColorImage;
import com.safframework.aop.annotation.Async;

public class MainActivity extends AppCompatActivity {

    ImageView imageView1;
    ImageView imageView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        initData();
    }

    private void initViews() {
        imageView1 = (ImageView)findViewById(R.id.image1);
        imageView2 = (ImageView)findViewById(R.id.image2);
    }

    @Async
    private void initData() {

        Resources res= getResources();
        Bitmap bitmap=BitmapFactory.decodeResource(res, R.drawable.activity_2);

        imageView1.setImageBitmap(bitmap);

        ColorImage colorImage = new ColorImage(bitmap);

        imageView2.setImageBitmap(colorImage.toBitmap());
    }
}
