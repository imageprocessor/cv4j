package com.cv4j.app.activity;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;

import com.cv4j.app.R;
import com.cv4j.app.app.BaseActivity;
import com.cv4j.core.binary.HoughLinesP;
import com.cv4j.core.binary.Threshold;
import com.cv4j.core.datamodel.ByteProcessor;
import com.cv4j.core.datamodel.CV4JImage;
import com.cv4j.core.datamodel.Line;
import com.safframework.injectview.annotations.InjectExtra;
import com.safframework.injectview.annotations.InjectView;

/**
 * Created by Tony Shen on 2017/5/1.
 */

public class LineDetectionActivity extends BaseActivity {

    @InjectView(R.id.image0)
    ImageView image0;

    @InjectView(R.id.image1)
    ImageView image1;

    @InjectView(R.id.image2)
    ImageView image2;

    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @InjectExtra(key = "Title")
    String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_detection);

        initData();
    }

    private void initData() {
        toolbar.setTitle("< "+title);
        Resources res = getResources();
        final Bitmap bitmap = BitmapFactory.decodeResource(res, R.drawable.test_lines);
        image0.setImageBitmap(bitmap);

        CV4JImage cv4JImage = new CV4JImage(bitmap);
        Threshold threshold = new Threshold();
        threshold.process((ByteProcessor)(cv4JImage.convert2Gray().getProcessor()),Threshold.THRESH_OTSU,Threshold.METHOD_THRESH_BINARY,255);
        image1.setImageBitmap(cv4JImage.getProcessor().getImage().toBitmap());

        HoughLinesP houghLinesP = new HoughLinesP();

        Line[] lines = new Line[6];
        houghLinesP.process((ByteProcessor)cv4JImage.getProcessor(),lines,6);
        Bitmap bm2 = Bitmap.createBitmap(cv4JImage.getProcessor().getImage().toBitmap());
        Canvas canvas = new Canvas(bm2);
        Paint paint = new Paint();
        paint.setStrokeWidth(2);
        paint.setColor(Color.RED);
        for(Line line:lines) {
            canvas.drawLine(line.x1,line.y1,line.x2,line.y2,paint);
        }
        image2.setImageBitmap(bm2);
    }
}
