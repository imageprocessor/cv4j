package com.cv4j.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.safframework.injectview.Injector;
import com.safframework.injectview.annotations.InjectView;
import com.safframework.injectview.annotations.OnClick;

/**
 * 常用滤镜的使用
 * Created by Tony Shen on 2017/3/7.
 */

public class FiltersActivity extends AppCompatActivity {

    @InjectView(R.id.text1)
    TextView text1;

    @InjectView(R.id.text2)
    TextView text2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filters);
        Injector.injectInto(this);

    }

    @OnClick(id=R.id.text1)
    void clickText1() {

        Intent i = new Intent(FiltersActivity.this,SelectFilterActivity.class);
        i.putExtra("Title",text1.getText().toString());
        startActivity(i);
    }

    @OnClick(id=R.id.text2)
    void clickText2() {

        Intent i = new Intent(FiltersActivity.this,CompositeFilersActivity.class);
        i.putExtra("Title",text2.getText().toString());
        startActivity(i);
    }

}
