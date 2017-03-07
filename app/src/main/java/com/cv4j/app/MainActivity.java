package com.cv4j.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.safframework.injectview.Injector;
import com.safframework.injectview.annotations.OnClick;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Injector.injectInto(this);
    }

    @OnClick(id=R.id.text1)
    void clickText1() {

        Intent i = new Intent(MainActivity.this,IOActivity.class);
        startActivity(i);
    }

    @OnClick(id=R.id.text2)
    void clickText2() {

        Intent i = new Intent(MainActivity.this,FiltersActivity.class);
        startActivity(i);
    }

//    private void initData() {
//
//        Resources res= getResources();
//        Bitmap bitmap = BitmapFactory.decodeResource(res, R.drawable.activity_2);
//        imageView1.setImageBitmap(bitmap);
//
//        ColorImage colorImage1 = new ColorImage(bitmap);
//        SepiaToneFilter filter1 = new SepiaToneFilter();
//        ColorImage newImage1 = (ColorImage) filter1.filter(colorImage1);
//        imageView2.setImageBitmap(newImage1.toBitmap());
//
//        ColorImage colorImage2 = new ColorImage(bitmap);
//        SinCityFilter filter2 = new SinCityFilter();
//        ColorImage newImage2 = (ColorImage) filter2.filter(colorImage2);
//        imageView3.setImageBitmap(newImage2.toBitmap());
//    }
}
