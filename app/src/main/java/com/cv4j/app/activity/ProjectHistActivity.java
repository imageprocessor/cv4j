package com.cv4j.app.activity;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;

import com.cv4j.app.R;
import com.cv4j.app.app.BaseActivity;
import com.cv4j.core.datamodel.ByteProcessor;
import com.cv4j.core.datamodel.CV4JImage;
import com.cv4j.core.datamodel.ColorProcessor;
import com.cv4j.core.hist.BackProjectHist;
import com.cv4j.core.hist.CalcHistogram;
import com.safframework.injectview.annotations.InjectExtra;
import com.safframework.injectview.annotations.InjectView;
import com.safframework.injectview.annotations.OnClick;

/**
 * Created by Tony Shen on 2017/6/4.
 */

public class ProjectHistActivity extends BaseActivity {

    @InjectView(R.id.target_image)
    ImageView targetImage;

    @InjectView(R.id.sample_image)
    ImageView sampleImage;

    @InjectView(R.id.result)
    ImageView result;

    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @InjectExtra(key = "Title")
    String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_hist);

        initData();
    }

    private void initData() {
        toolbar.setTitle("< "+title);
        Resources res = getResources();
        Bitmap bitmap1 = BitmapFactory.decodeResource(res, R.drawable.test_project_target);
        targetImage.setImageBitmap(bitmap1);

        Bitmap bitmap2 = BitmapFactory.decodeResource(res, R.drawable.test_project_sample);
        sampleImage.setImageBitmap(bitmap2);

        CV4JImage cv4jImage = new CV4JImage(bitmap1);
        ColorProcessor colorProcessor = (ColorProcessor)cv4jImage.getProcessor();

        int w = colorProcessor.getWidth();
        int h = colorProcessor.getHeight();

        // 反向投影结果
        CV4JImage resultCV4JImage = new CV4JImage(w,h);
        ByteProcessor byteProcessor = (ByteProcessor)resultCV4JImage.getProcessor();

         // sample
        CV4JImage sample = new CV4JImage(bitmap2);
        ColorProcessor sampleProcessor = (ColorProcessor)sample.getProcessor();

        int bins = 16;

        BackProjectHist backProjectHist = new BackProjectHist();
        backProjectHist.backProjection(colorProcessor,byteProcessor,CalcHistogram.calculateNormHist(sampleProcessor,bins),bins);

        result.setImageBitmap(byteProcessor.getImage().toBitmap());
    }

    @OnClick(id= R.id.toolbar)
    void clickToolbar() {

        finish();
    }
}
