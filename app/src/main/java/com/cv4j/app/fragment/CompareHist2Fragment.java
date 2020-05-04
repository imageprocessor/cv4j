package com.cv4j.app.fragment;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cv4j.app.R;
import com.cv4j.core.datamodel.CV4JImage;
import com.cv4j.core.datamodel.ImageProcessor;
import com.cv4j.core.hist.CalcHistogram;
import com.cv4j.core.hist.CompareHist;
import com.safframework.injectview.Injector;
import com.safframework.injectview.annotations.InjectView;

/**
 * Created by Tony Shen on 2017/6/10.
 */

public class CompareHist2Fragment extends BaseFragment {

    @InjectView(R.id.image0)
    ImageView image0;

    @InjectView(R.id.image1)
    ImageView image1;

    @InjectView(R.id.result)
    TextView result;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_compare_hist_2, container, false);
        Injector.injectInto(this, v);

        initData();
        return v;
    }

    private void initData() {

        Resources res = getResources();
        final Bitmap bitmap = BitmapFactory.decodeResource(res, R.drawable.test_hist2);
        image0.setImageBitmap(bitmap);
        image1.setImageBitmap(bitmap);

        CV4JImage cv4jImage = new CV4JImage(bitmap);
        ImageProcessor imageProcessor = cv4jImage.getProcessor();

        int[][] source = null;

        CalcHistogram calcHistogram = new CalcHistogram();
        int bins = 180;
        source = new int[imageProcessor.getChannels()][bins];
        calcHistogram.calcHSVHist(imageProcessor,bins,source,true);

        CompareHist compareHist = new CompareHist();
        StringBuilder sb = new StringBuilder();
        sb.append("巴氏距离:").append(compareHist.bhattacharyya(source[0],source[0])).append("\r\n")
                .append("协方差:").append(compareHist.covariance(source[0],source[0])).append("\r\n")
                .append("相关性因子:").append(compareHist.ncc(source[0],source[0]));

        result.setText(sb.toString());
    }
}
