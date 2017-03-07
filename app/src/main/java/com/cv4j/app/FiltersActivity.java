package com.cv4j.app;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import com.cv4j.core.datamodel.ColorImage;
import com.cv4j.core.filters.CommonFilter;
import com.safframework.aop.annotation.Trace;
import com.safframework.injectview.Injector;
import com.safframework.injectview.annotations.InjectView;

import java.util.ArrayList;
import java.util.List;

/**
 * 常用滤镜的使用
 * Created by Tony Shen on 2017/3/7.
 */

public class FiltersActivity extends Activity {

    @InjectView(R.id.image)
    ImageView image;

    @InjectView(R.id.spinner)
    Spinner spinner;

    Bitmap bitmap;

    String[] filterNames;

    List<String> list = new ArrayList<>();

    ArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filters);
        Injector.injectInto(this);

        initData();
    }

    private void initData() {
        Resources res = getResources();

        filterNames = res.getStringArray(R.array.filterNames);
        bitmap = BitmapFactory.decodeResource(res, R.drawable.test_filters);
        image.setImageBitmap(bitmap);

        for (String filter:filterNames) {
            list.add(filter);
        }

        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

                if (position == 0) {
                    image.setImageBitmap(bitmap);
                    return;
                }

                // 清除滤镜
                image.clearColorFilter();

                String filterName = (String) adapter.getItem(position);
                changeFilter(filterName);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private Object getFilter(String filterName) {

        Object object = null;
        try {
            object = Class.forName("com.cv4j.core.filters."+filterName+"Filter").newInstance();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return object;
    }

    @Trace
    public void changeFilter(String filterName) {
        ColorImage colorImage = new ColorImage(bitmap);
        CommonFilter filter = (CommonFilter)getFilter(filterName);
        colorImage = (ColorImage) filter.filter(colorImage);
        image.setImageBitmap(colorImage.toBitmap());
    }
}
