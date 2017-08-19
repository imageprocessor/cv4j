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

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.cv4j.app.R;
import com.cv4j.app.app.BaseFragment;
import com.cv4j.core.datamodel.CV4JImage;
import com.cv4j.core.datamodel.Rect;
import com.cv4j.image.util.QRCodeScanner;
import com.safframework.injectview.Injector;
import com.safframework.injectview.annotations.InjectView;
import com.safframework.injectview.annotations.OnClick;

/**
 * Created by Tony Shen on 2017/8/19.
 */

public class QRFragment extends BaseFragment {

    @InjectView(R.id.image)
    ImageView image;

    @InjectView(R.id.detect_btn)
    Button detectButton;

    private final static String QR_INDEX = "index";

    private int mIndex;

    private Bitmap bitmap;

    public static QRFragment newInstance(int index) {
        Bundle args = new Bundle();
        args.putInt(QR_INDEX, index);
        QRFragment fragment = new QRFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mIndex = bundle.getInt(QR_INDEX);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_qr, container, false);
        Injector.injectInto(this, v);

        initData();
        return v;
    }

    private void initData() {

        switch(mIndex) {

            case 0:
                bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.qr_1);
                break;

            case 1:
                bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.qr_applepay);
                break;

            case 2:
                bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.qr_jiazhigang);
                break;

            case 3:
                bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.qr_tony);
                break;

            default:
                bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.qr_1);
                break;
        }

        image.setImageBitmap(bitmap);
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
        image.setImageBitmap(bm);
    }
}
