package com.cv4j.app.fragment;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cv4j.app.R;
import com.cv4j.app.adapter.SpitalConvAdapter;
import com.cv4j.app.app.BaseFragment;
import com.cv4j.app.ui.DividerGridItemDecoration;
import com.cv4j.app.ui.GridRecyclerView;
import com.safframework.injectview.Injector;
import com.safframework.injectview.annotations.InjectView;
import com.safframwork.tony.common.utils.Preconditions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tony Shen on 2017/3/21.
 */

public class SpitalConvFragment extends BaseFragment {

    @InjectView(R.id.recyclerview)
    GridRecyclerView recyclerview;

    Bitmap bitmap;

    String[] filterNames;

    List<String> list = new ArrayList<>();

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
        if (Preconditions.isNotBlank(filterNames)) {
            for (String filter:filterNames) {
                list.add(filter);
            }
        }

        GridLayoutManager manager = new GridLayoutManager(mContext, 3);
        manager.setRecycleChildrenOnDetach(true);
        recyclerview.setLayoutManager(manager);
        recyclerview.setAdapter(new SpitalConvAdapter(mContext,
                list,bitmap));
        recyclerview.addItemDecoration(new DividerGridItemDecoration(mContext));
        recyclerview.setRecycledViewPool(myPool);
    }

    static RecyclerView.RecycledViewPool myPool = new RecyclerView.RecycledViewPool();

    static {
        myPool.setMaxRecycledViews(0, 10);
    }

}
