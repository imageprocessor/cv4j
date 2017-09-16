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

package com.cv4j.app.fragment;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.cv4j.app.R;
import com.cv4j.app.app.BaseFragment;
import com.cv4j.core.datamodel.CV4JImage;
import com.cv4j.core.datamodel.ImageProcessor;
import com.cv4j.core.datamodel.Point;
import com.cv4j.core.tpl.TemplateMatch;
import com.safframework.injectview.Injector;
import com.safframework.injectview.annotations.InjectView;

import java.util.ArrayList;

/**
 * Created by tony on 2017/9/16.
 */

public class TemplateMatchFragment extends BaseFragment {

    @InjectView(R.id.target_image)
    ImageView targetImage;

    @InjectView(R.id.template_image)
    ImageView templateImage;

    @InjectView(R.id.result)
    ImageView result;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_template_match, container, false);
        Injector.injectInto(this, v);

        initData();
        return v;
    }

    private void initData() {

        Resources res = getResources();
        Bitmap bitmap1 = BitmapFactory.decodeResource(res, R.drawable.test_tpl_target);
        targetImage.setImageBitmap(bitmap1);

        Bitmap bitmap2 = BitmapFactory.decodeResource(res, R.drawable.tpl);
        templateImage.setImageBitmap(bitmap2);

        CV4JImage targetCV4J = new CV4JImage(bitmap1);
        ImageProcessor targetImageProcessor = targetCV4J.convert2Gray().getProcessor();

        CV4JImage templateCV4J = new CV4JImage(bitmap2);
        ImageProcessor templateProcessor = templateCV4J.convert2Gray().getProcessor();

        TemplateMatch match = new TemplateMatch();
        match.match(targetImageProcessor,templateProcessor,new ArrayList<Point>(),TemplateMatch.TM_SQDIFF_NORMED,0.5);

    }
}
