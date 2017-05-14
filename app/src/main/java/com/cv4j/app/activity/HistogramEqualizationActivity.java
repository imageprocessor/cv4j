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
import com.cv4j.core.datamodel.ImageProcessor;
import com.cv4j.core.hist.EqualHist;
import com.safframework.injectview.annotations.InjectExtra;
import com.safframework.injectview.annotations.InjectView;

/**
 * Created by Tony Shen on 2017/5/14.
 */

public class HistogramEqualizationActivity extends BaseActivity {

    @InjectView(R.id.image0)
    ImageView image0;

    @InjectView(R.id.image1)
    ImageView image1;

    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @InjectExtra(key = "Title")
    String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_histogram_equalization);

        initData();
    }

    private void initData() {

        toolbar.setTitle("< "+title);
        Resources res = getResources();
        final Bitmap bitmap = BitmapFactory.decodeResource(res, R.drawable.test_hist);
        image0.setImageBitmap(bitmap);

        CV4JImage cv4jImage = new CV4JImage(bitmap);
        ImageProcessor imageProcessor = cv4jImage.convert2Gray().getProcessor();
        if (imageProcessor instanceof ByteProcessor) {
            EqualHist equalHist = new EqualHist();
            equalHist.equalize((ByteProcessor) imageProcessor);
            image1.setImageBitmap(cv4jImage.getProcessor().getImage().toBitmap());
        }
    }
}
