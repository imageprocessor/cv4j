/*
 * Copyright (c) 2017 - present, CV4J Contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cv4j.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.cv4j.app.R;
import com.cv4j.app.app.BaseActivity;
import com.safframework.injectview.annotations.InjectView;
import com.safframework.injectview.annotations.OnClick;

/**
 * Created by tony on 2017/12/10.
 */

public class ArithmeticAndLogicOperationActivity extends BaseActivity {

    @InjectView(R.id.text1)
    TextView text1;

    @InjectView(R.id.text2)
    TextView text2;

    @InjectView(R.id.text3)
    TextView text3;

    @InjectView(R.id.text4)
    TextView text4;

    @InjectView(R.id.text5)
    TextView text5;

    @InjectView(R.id.text6)
    TextView text6;

    @InjectView(R.id.text7)
    TextView text7;

    @InjectView(R.id.text8)
    TextView text8;

    @InjectView(R.id.text9)
    TextView text9;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arithmetic_and_logic_operator);
    }

    @OnClick(id=R.id.text1)
    void clickText1() {

        Intent i = new Intent(this,PixelOperatorActivity.class);
        i.putExtra("Title",text1.getText().toString());
        i.putExtra("Type",PixelOperatorActivity.ADD);
        startActivity(i);
    }

    @OnClick(id=R.id.text2)
    void clickText2() {

        Intent i = new Intent(this,PixelOperatorActivity.class);
        i.putExtra("Title",text2.getText().toString());
        i.putExtra("Type",PixelOperatorActivity.SUBSTRACT);
        startActivity(i);
    }

    @OnClick(id=R.id.text3)
    void clickText3() {

        Intent i = new Intent(this,PixelOperatorActivity.class);
        i.putExtra("Title",text3.getText().toString());
        i.putExtra("Type",PixelOperatorActivity.MULTIPLE);
        startActivity(i);
    }

    @OnClick(id=R.id.text4)
    void clickText4() {

        Intent i = new Intent(this,PixelOperatorActivity.class);
        i.putExtra("Title",text4.getText().toString());
        i.putExtra("Type",PixelOperatorActivity.DIVISION);
        startActivity(i);
    }

    @OnClick(id=R.id.text5)
    void clickText5() {

        Intent i = new Intent(this,PixelOperatorActivity.class);
        i.putExtra("Title",text5.getText().toString());
        i.putExtra("Type",PixelOperatorActivity.BITWISE_AND);
        startActivity(i);
    }

    @OnClick(id=R.id.text6)
    void clickText6() {

        Intent i = new Intent(this,PixelOperatorActivity.class);
        i.putExtra("Title",text6.getText().toString());
        i.putExtra("Type",PixelOperatorActivity.BITWISE_OR);
        startActivity(i);
    }

    @OnClick(id=R.id.text7)
    void clickText7() {

        Intent i = new Intent(this,PixelOperatorActivity.class);
        i.putExtra("Title",text7.getText().toString());
        i.putExtra("Type",PixelOperatorActivity.BITWISE_NOT);
        startActivity(i);
    }

    @OnClick(id=R.id.text8)
    void clickText8() {

        Intent i = new Intent(this,PixelOperatorActivity.class);
        i.putExtra("Title",text8.getText().toString());
        i.putExtra("Type",PixelOperatorActivity.BITWISE_XOR);
        startActivity(i);
    }
}
