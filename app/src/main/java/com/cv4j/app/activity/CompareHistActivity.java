package com.cv4j.app.activity;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.cv4j.app.R;
import com.cv4j.app.app.BaseActivity;
import com.cv4j.core.datamodel.ByteProcessor;
import com.cv4j.core.datamodel.CV4JImage;
import com.cv4j.core.datamodel.ImageProcessor;
import com.cv4j.core.hist.CalcHistogram;
import com.cv4j.core.hist.CompareHist;
import com.cv4j.core.hist.EqualHist;
import com.safframework.injectview.annotations.InjectExtra;
import com.safframework.injectview.annotations.InjectView;
import com.safframework.injectview.annotations.OnClick;

/**
 * Created by Tony Shen on 2017/6/4.
 */

public class CompareHistActivity extends BaseActivity {

    @InjectView(R.id.image0)
    ImageView image0;

    @InjectView(R.id.image1)
    ImageView image1;

    @InjectView(R.id.result)
    TextView result;

    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @InjectExtra(key = "Title")
    String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare_hist);

        initData();
    }

    private void initData() {
        toolbar.setTitle("< "+title);
        Resources res = getResources();
        final Bitmap bitmap = BitmapFactory.decodeResource(res, R.drawable.test_hist);
        image0.setImageBitmap(bitmap);

        CV4JImage cv4jImage = new CV4JImage(bitmap);
        ImageProcessor imageProcessor = cv4jImage.convert2Gray().getProcessor();

        int[][] source = null;
        int[][] target = null;

        CalcHistogram calcHistogram = new CalcHistogram();
        int bins = 180;
        source = new int[imageProcessor.getChannels()][bins];
        calcHistogram.calcHSVHist(imageProcessor,bins,source,true);

        if (imageProcessor instanceof ByteProcessor) {
            EqualHist equalHist = new EqualHist();
            equalHist.equalize((ByteProcessor) imageProcessor);
            image1.setImageBitmap(cv4jImage.getProcessor().getImage().toBitmap());

            target = new int[imageProcessor.getChannels()][bins];
            calcHistogram.calcHSVHist(imageProcessor,bins,target,true);
        }

        CompareHist compareHist = new CompareHist();
        StringBuilder sb = new StringBuilder();
        sb.append("巴氏距离:").append(compareHist.bhattacharyya(source[0],target[0])).append("\r\n")
                .append("协方差:").append(compareHist.covariance(source[0],target[0])).append("\r\n")
                .append("相关性因子:").append(compareHist.ncc(source[0],target[0]));

        result.setText(sb.toString());
    }

    @OnClick(id= R.id.toolbar)
    void clickToolbar() {

        finish();
    }
}
