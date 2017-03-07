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
}
