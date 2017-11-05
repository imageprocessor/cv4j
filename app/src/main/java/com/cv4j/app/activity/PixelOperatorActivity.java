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
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;

import com.cv4j.app.R;
import com.cv4j.app.app.BaseActivity;
import com.cv4j.core.datamodel.CV4JImage;
import com.cv4j.core.datamodel.ImageProcessor;
import com.cv4j.core.pixels.Operator;
import com.safframework.injectview.annotations.InjectExtra;
import com.safframework.injectview.annotations.InjectView;
import com.safframework.injectview.annotations.OnClick;
import com.safframework.log.L;

/**
 * Created by tony on 2017/11/5.
 */

public class PixelOperatorActivity extends BaseActivity {

    @InjectView(R.id.image1)
    ImageView image1;

    @InjectView(R.id.image2)
    ImageView image2;

    @InjectView(R.id.result_image)
    ImageView result;

    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @InjectExtra(key = "Title")
    String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pixel_operator);

        initData();
    }

    private void initData() {

        toolbar.setTitle("< "+title);
        Resources res = getResources();

        final Bitmap bitmap1 = BitmapFactory.decodeResource(res, R.drawable.pixel_test_1);
        image1.setImageBitmap(bitmap1);

        final Bitmap bitmap2 = BitmapFactory.decodeResource(res, R.drawable.pixel_test_2);
        image2.setImageBitmap(bitmap2);

        CV4JImage cv4jImage1 = new CV4JImage(bitmap1);
        ImageProcessor imageProcessor1 = cv4jImage1.getProcessor();

        CV4JImage cv4jImage2 = new CV4JImage(bitmap2);
        ImageProcessor imageProcessor2 = cv4jImage2.getProcessor();

        ImageProcessor imageProcessor = Operator.add(imageProcessor1,imageProcessor2);
        CV4JImage resultCV4JImage = new CV4JImage(imageProcessor.getWidth(), imageProcessor.getHeight(), imageProcessor.getPixels());
        result.setImageBitmap(resultCV4JImage.getProcessor().getImage().toBitmap());
    }

    @OnClick(id= R.id.toolbar)
    void clickToolbar() {

        finish();
    }
}
