package com.cv4j.app.activity;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cv4j.app.R;
import com.cv4j.app.app.BaseActivity;
import com.cv4j.core.filters.ColorFilter;
import com.cv4j.rxjava.RxImageData;
import com.safframework.injectview.Injector;
import com.safframework.injectview.annotations.InjectView;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Tony Shen on 2017/3/15.
 */

public class ColorFilterActivity extends BaseActivity {

    @InjectView(R.id.image)
    ImageView image;

    @InjectView(R.id.scroll_view)
    HorizontalScrollView scrollView;

    @InjectView(R.id.linear)
    LinearLayout linear;

    Bitmap bitmap;

    Map colorStyles = new HashMap();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_filter);
        Injector.injectInto(this);

        initData();
    }

    private void initData() {

        Resources res = getResources();
        bitmap = BitmapFactory.decodeResource(res, R.drawable.test_color_filter);

        RxImageData.bitmap(bitmap).addFilter(new ColorFilter()).isUseCache(false).into(image);

        colorStyles.put(ColorFilter.AUTUMN_STYLE," 秋天风格 ");
        colorStyles.put(ColorFilter.BONE_STYLE," 硬朗风格 ");
        colorStyles.put(ColorFilter.COOL_STYLE," 凉爽风格 ");
        colorStyles.put(ColorFilter.HOT_STYLE," 热带风格 ");
        colorStyles.put(ColorFilter.HSV_STYLE," 色彩空间变换风格 ");
        colorStyles.put(ColorFilter.JET_STYLE," 高亮风格 ");
        colorStyles.put(ColorFilter.OCEAN_STYLE," 海洋风格 ");
        colorStyles.put(ColorFilter.PINK_STYLE," 粉色风格 ");
        colorStyles.put(ColorFilter.RAINBOW_STYLE," 彩虹风格 ");
        colorStyles.put(ColorFilter.SPRING_STYLE," 春天风格 ");
        colorStyles.put(ColorFilter.SUMMER_STYLE," 夏天风格 ");
        colorStyles.put(ColorFilter.WINTER_STYLE," 冬天风格 ");

        int len = colorStyles.size();
        for (int i = 0; i < len; i++) {
            LinearLayout.LayoutParams linearLp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            LinearLayout myLinear = new LinearLayout(this);
            linearLp.setMargins(5, 0, 5, 20);
            myLinear.setOrientation(LinearLayout.HORIZONTAL);
            myLinear.setTag(i);
            linear.addView(myLinear, linearLp);

            LinearLayout.LayoutParams textViewLp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            TextView textView = new TextView(this);
            textView.setText(colorStyles.get(i) + "");
            textView.setGravity(Gravity.CENTER);
            myLinear.addView(textView, textViewLp);

            myLinear.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    Toast.makeText(ColorFilterActivity.this, colorStyles.get((int)v.getTag())+"", Toast.LENGTH_SHORT).show();

                    ColorFilter colorFilter = new ColorFilter();
                    colorFilter.setStyle((int)v.getTag());

                    RxImageData.bitmap(bitmap).addFilter(colorFilter).isUseCache(false).into(image);
                }
            });
        }
    }


}
