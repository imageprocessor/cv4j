package com.cv4j.app.fragment;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cv4j.app.R;
import com.cv4j.app.app.BaseFragment;
import com.cv4j.core.filters.CommonFilter;
import com.cv4j.rxjava.RxImageData;
import com.safframework.injectview.Injector;
import com.safframework.injectview.annotations.InjectView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Tony Shen on 2017/3/21.
 */

public class SpitalConvFragment extends BaseFragment {

    @InjectView(R.id.image)
    ImageView image;

    @InjectView(R.id.scroll_view)
    HorizontalScrollView scrollView;

    @InjectView(R.id.linear)
    LinearLayout linear;

    Bitmap bitmap;

    String[] filterNames;

    List<String> list = new ArrayList<>();

    Map map = new HashMap();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_spital_conv, container, false);
        Injector.injectInto(this, v);

        initData();

        return v;
    }

    private void initData() {

        Resources res = getResources();
        bitmap = BitmapFactory.decodeResource(res, R.drawable.test_spital_conv);

        filterNames = res.getStringArray(R.array.spatialConvNames);
        for (String filter:filterNames) {
            list.add(filter);
        }

        image.setImageBitmap(bitmap);

        map.put(0," 卷积 ");
        map.put(1," 寻找边缘 ");
        map.put(2," 拉普拉斯 ");
        map.put(3," 中值滤波 ");
        map.put(4," 最大最小值滤波 ");
        map.put(5," 椒盐噪声 ");
        map.put(6," 锐化 ");
        map.put(7," 梯度 ");
        map.put(8," 方差滤波 ");

        int len = map.size();
        for (int i = 0; i < len; i++) {
            LinearLayout.LayoutParams linearLp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            LinearLayout myLinear = new LinearLayout(mContext);
            linearLp.setMargins(5, 0, 5, 20);
            myLinear.setOrientation(LinearLayout.HORIZONTAL);
            myLinear.setTag(i);
            linear.addView(myLinear, linearLp);

            LinearLayout.LayoutParams textViewLp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            TextView textView = new TextView(mContext);
            textView.setText(map.get(i) + "");
            textView.setGravity(Gravity.CENTER);
            myLinear.addView(textView, textViewLp);

            final int index = i;
            myLinear.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    Toast.makeText(mContext, map.get((int)v.getTag())+"", Toast.LENGTH_SHORT).show();

                    String filterName = list.get(index);
                    CommonFilter filter = (CommonFilter)getFilter(filterName);
                    RxImageData.bitmap(bitmap).addFilter(filter).into(image);
                }
            });
        }
    }

    private Object getFilter(String filterName) {

        Object object = null;
        try {
            object = Class.forName("com.cv4j.core.spatial.conv."+filterName+"Filter").newInstance();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (java.lang.InstantiationException e) {
            e.printStackTrace();
        }

        return object;
    }

}
