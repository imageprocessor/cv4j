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

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

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
        Resources res= getResources();
        bitmap = BitmapFactory.decodeResource(res, R.drawable.qrcode_04);
        imageView.setImageBitmap(bitmap);
    }

    @OnClick(id= R.id.detect_btn)
    void clickDetectButton() {

        CV4JImage cv4JImage = new CV4JImage(bitmap);

        QRCodeScanner qrCodeScanner = new QRCodeScanner();
        Rect rect = qrCodeScanner.findQRCodeBounding(cv4JImage.getProcessor());

        Bitmap bm = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(bm);
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);

        android.graphics.Rect androidRect = new android.graphics.Rect(rect.x,rect.y,rect.br().x,rect.br().y);
        canvas.drawRect(androidRect,paint);
        imageView.setImageBitmap(bm);
    }

    @OnClick(id= R.id.toolbar)
    void clickToolbar() {

        finish();
    }

    private void saveDebugImage(Bitmap bitmap) {
        File filedir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "myOcrImages");
        String name = String.valueOf(System.currentTimeMillis()) + "_ocr.jpg";
        File tempFile = new File(name);
        FileOutputStream output = null;
        try {
            output = new FileOutputStream(tempFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
        }catch (IOException ioe) {
            Log.e("DEBUG-ERR", ioe.getMessage());
        } finally {
            try {
                output.flush();
                output.close();
            } catch (IOException e) {
                Log.i("DEBUG-INFO", e.getMessage());
            }
        }
    }
}
