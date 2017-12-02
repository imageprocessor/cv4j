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
import com.cv4j.core.datamodel.Rect;
import com.cv4j.core.pixels.Operator;
import com.cv4j.exception.CV4JException;
import com.safframework.injectview.annotations.InjectExtra;
import com.safframework.injectview.annotations.InjectView;
import com.safframework.injectview.annotations.OnClick;

/**
 * Created by tony on 2017/11/13.
 */

public class SubImageActivity extends BaseActivity {

    @InjectView(R.id.image)
    ImageView image;

    @InjectView(R.id.result_image)
    ImageView result;

    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @InjectExtra(key = "Title")
    String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_image);

        initData();
    }

    private void initData() {

        toolbar.setTitle("< "+title);
        Resources res = getResources();

        final Bitmap bitmap = BitmapFactory.decodeResource(res, R.drawable.test_hist2);
        image.setImageBitmap(bitmap);

        CV4JImage cv4jImage = new CV4JImage(bitmap);
        ImageProcessor imageProcessor = cv4jImage.getProcessor();


        Rect rect = new Rect();
        rect.x = 300;
        rect.y = 200;
        rect.width = 300;
        rect.height = 450;

        ImageProcessor resultImageProcessor = null;

        try {
            resultImageProcessor = Operator.subImage(imageProcessor,rect);
        } catch (CV4JException e) {
        }

        if (resultImageProcessor!=null) {
            CV4JImage resultCV4JImage = new CV4JImage(resultImageProcessor.getWidth(), resultImageProcessor.getHeight(), resultImageProcessor.getPixels());
            result.setImageBitmap(resultCV4JImage.getProcessor().getImage().toBitmap());
        }
    }

    @OnClick(id= R.id.toolbar)
    void clickToolbar() {

        finish();
    }
}
