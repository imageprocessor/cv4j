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
import com.cv4j.image.util.Tools;
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

        BackProjectHist backProjectHist = new BackProjectHist();

        int w = colorProcessor.getWidth();
        int h = colorProcessor.getHeight();

        CV4JImage resultCV4JImage = new CV4JImage(w,h);
        ByteProcessor byteProcessor = (ByteProcessor)resultCV4JImage.getProcessor();

         // sample
        CV4JImage sample = new CV4JImage(bitmap2);
        ColorProcessor sampleProcessor = (ColorProcessor)sample.getProcessor();
        CalcHistogram calcHistogram = new CalcHistogram();
        int bins = 32;
        int[][] hist = new int[sampleProcessor.getChannels()][bins];
        calcHistogram.calcHSVHist(sampleProcessor,bins,hist,true);

        byte[][] source = new byte[][]{colorProcessor.getRed(),colorProcessor.getGreen(),colorProcessor.getBlue()};
        byte[][] target = new byte[3][w*h];

        Tools.rgb2hsv(source,target);
        ByteProcessor hsvByteProcessor = new ByteProcessor(target[0],w,h);
        backProjectHist.backProjection(hsvByteProcessor,byteProcessor,hist[0],new int[]{0,180});

        result.setImageBitmap(byteProcessor.getImage().toBitmap());
    }

    @OnClick(id= R.id.toolbar)
    void clickToolbar() {

        finish();
    }
}
