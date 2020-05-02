package com.cv4j.app.app;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by Tony Shen on 2017/3/12.
 */

public class BaseActivity extends AppCompatActivity {

    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        Injector.injectInto(this);
    }
}