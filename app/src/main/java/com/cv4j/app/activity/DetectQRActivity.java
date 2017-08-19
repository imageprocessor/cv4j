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

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.ImageView;

import com.cv4j.app.R;
import com.cv4j.app.app.BaseActivity;
import com.cv4j.core.datamodel.CV4JImage;
import com.cv4j.core.datamodel.Rect;
import com.cv4j.image.util.QRCodeScanner;
import com.safframework.injectview.annotations.InjectExtra;
import com.safframework.injectview.annotations.InjectView;
import com.safframework.injectview.annotations.OnClick;

/**
 * Created by Tony Shen on 2017/6/25.
 */

public class DetectQRActivity extends BaseActivity {

    @InjectView(R.id.image)
    ImageView imageView;

    @InjectView(R.id.detect_btn)
    Button detectButton;

    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @InjectExtra(key = "Title")
    String title;

    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detect_qr);

        initData();
    }

    private void initData() {
        toolbar.setTitle("< "+title);
        bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.qr_applepay);
        imageView.setImageBitmap(bitmap);
    }

    @OnClick(id= R.id.detect_btn)
    void clickDetectButton() {

        CV4JImage cv4JImage = new CV4JImage(bitmap);

        QRCodeScanner qrCodeScanner = new QRCodeScanner();
        Rect rect = qrCodeScanner.findQRCodeBounding(cv4JImage.getProcessor(),1,6);

        Bitmap bm = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(bm);
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStrokeWidth((float) 10.0);
        paint.setStyle(Paint.Style.STROKE);

        android.graphics.Rect androidRect = new android.graphics.Rect(rect.x-20,rect.y-20,rect.br().x+20,rect.br().y+20);
        canvas.drawRect(androidRect,paint);
        imageView.setImageBitmap(bm);
    }

    @OnClick(id= R.id.toolbar)
    void clickToolbar() {

        finish();
    }
}
