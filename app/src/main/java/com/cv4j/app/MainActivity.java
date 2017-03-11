package com.cv4j.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.safframework.injectview.Injector;
import com.safframework.injectview.annotations.InjectView;
import com.safframework.injectview.annotations.OnClick;

public class MainActivity extends AppCompatActivity {

    @InjectView(R.id.text1)
    TextView text1;

    @InjectView(R.id.text2)
    TextView text2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Injector.injectInto(this);
    }

    @OnClick(id=R.id.text1)
    void clickText1() {

        Intent i = new Intent(MainActivity.this,IOActivity.class);
        i.putExtra("Title",text1.getText().toString());
        startActivity(i);
    }

    @OnClick(id=R.id.text2)
    void clickText2() {

        Intent i = new Intent(MainActivity.this,FiltersActivity.class);
        i.putExtra("Title",text2.getText().toString());
        startActivity(i);
    }
}
