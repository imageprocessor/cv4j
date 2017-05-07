package com.cv4j.app.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;

import com.cv4j.app.R;
import com.cv4j.app.adapter.PaintAdapter;
import com.cv4j.app.app.BaseActivity;
import com.cv4j.app.fragment.PaintFragment;
import com.safframework.injectview.annotations.InjectExtra;
import com.safframework.injectview.annotations.InjectView;
import com.safframework.injectview.annotations.OnClick;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tony Shen on 2017/5/7.
 */

public class PaintActivity extends BaseActivity {

    @InjectView(R.id.tablayout)
    TabLayout mTabLayout;

    @InjectView(R.id.viewpager)
    ViewPager mViewPager;

    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @InjectExtra(key = "Title")
    String title;

    List<Fragment> mList = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oil_paint);

        initData();
    }

    private void initData() {

        toolbar.setTitle("< "+title);

        mList.add(PaintFragment.newInstance(0));
        mList.add(PaintFragment.newInstance(1));
        mList.add(PaintFragment.newInstance(2));
        mViewPager.setAdapter(new PaintAdapter(this, this.getSupportFragmentManager(),mList));
        mTabLayout.setupWithViewPager(mViewPager);
    }

    @OnClick(id= R.id.toolbar)
    void clickToolbar() {

        finish();
    }
}
