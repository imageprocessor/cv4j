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
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;

import com.cv4j.app.R;
import com.cv4j.app.app.BaseActivity;
import com.cv4j.core.datamodel.CV4JImage;
import com.cv4j.core.datamodel.ColorProcessor;
import com.cv4j.core.datamodel.ImageProcessor;
import com.cv4j.core.datamodel.Scalar;
import com.cv4j.core.pixels.PrincipalColorExtractor;
import com.safframework.injectview.annotations.InjectExtra;
import com.safframework.injectview.annotations.InjectView;
import com.safframework.log.L;

import java.util.List;

/**
 * Created by tony on 2017/12/2.
 */

public class PrincipalColorExtractorActivity extends BaseActivity {

    @InjectView(R.id.image)
    ImageView image;

    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @InjectView(R.id.result0)
    ImageView result0;

    @InjectView(R.id.result1)
    ImageView result1;

    @InjectView(R.id.result2)
    ImageView result2;

    @InjectView(R.id.result3)
    ImageView result3;

    @InjectView(R.id.result4)
    ImageView result4;

    @InjectExtra(key = "Title")
    String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal_color_extractor);

        initData();
    }

    private void initData() {

        toolbar.setTitle("< "+title);
        Resources res = getResources();

        final Bitmap bitmap = BitmapFactory.decodeResource(res, R.drawable.test_hist2);
        image.setImageBitmap(bitmap);

        CV4JImage cv4jImage = new CV4JImage(bitmap);
        ImageProcessor imageProcessor = cv4jImage.getProcessor();

        PrincipalColorExtractor extractor = new PrincipalColorExtractor();
        List<Scalar> scalars = extractor.extract((ColorProcessor)imageProcessor);

        Scalar scalar0 = scalars.get(0);
        result0.setBackgroundColor(Color.rgb(scalar0.red, scalar0.green, scalar0.blue));

        Scalar scalar1 = scalars.get(1);
        result1.setBackgroundColor(Color.rgb(scalar1.red, scalar1.green, scalar1.blue));

        Scalar scalar2 = scalars.get(2);
        result2.setBackgroundColor(Color.rgb(scalar2.red, scalar2.green, scalar2.blue));

        Scalar scalar3 = scalars.get(3);
        result3.setBackgroundColor(Color.rgb(scalar3.red, scalar3.green, scalar3.blue));

        Scalar scalar4 = scalars.get(4);
        result4.setBackgroundColor(Color.rgb(scalar4.red, scalar4.green, scalar4.blue));
    }
}
