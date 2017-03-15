package com.cv4j.app.activity;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.cv4j.app.R;
import com.cv4j.app.adapter.GridViewFilterAdapter;
import com.cv4j.app.app.BaseActivity;
import com.cv4j.app.ui.DividerGridItemDecoration;
import com.cv4j.app.ui.GridRecyclerView;
import com.safframework.injectview.Injector;
import com.safframework.injectview.annotations.InjectView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tony Shen on 2017/3/15.
 */

public class GridViewFilterActivity extends BaseActivity {

    @InjectView(R.id.recyclerview)
    GridRecyclerView recyclerview;

    Bitmap bitmap;

    String[] filterNames;

    List<String> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gridview_filter);
        Injector.injectInto(this);

        initData();
    }

    private void initData() {
        Resources res = getResources();
        filterNames = res.getStringArray(R.array.filterNames);
        bitmap = BitmapFactory.decodeResource(res, R.drawable.test_mm);
        for (String filter:filterNames) {
            list.add(filter);
        }

        GridLayoutManager manager = new GridLayoutManager(GridViewFilterActivity.this, 3);
        manager.setRecycleChildrenOnDetach(true);
        recyclerview.setLayoutManager(manager);
        recyclerview.setAdapter(new GridViewFilterAdapter(GridViewFilterActivity.this,
                list,bitmap));
        recyclerview.addItemDecoration(new DividerGridItemDecoration(GridViewFilterActivity.this));
        recyclerview.setRecycledViewPool(myPool);
    }

    static RecyclerView.RecycledViewPool myPool = new RecyclerView.RecycledViewPool();

    static {
        myPool.setMaxRecycledViews(0, 10);
    }
}
