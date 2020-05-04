package com.cv4j.app.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;

import com.cv4j.app.R;
import com.cv4j.app.adapter.ProjectHistAdapter;
import com.safframework.injectview.annotations.InjectExtra;
import com.safframework.injectview.annotations.InjectView;
import com.safframework.injectview.annotations.OnClick;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tony Shen on 2017/6/4.
 */

public class ProjectHistActivity extends BaseActivity {

    @InjectView(R.id.tablayout)
    TabLayout mTabLayout;

    @InjectView(R.id.viewpager)
    ViewPager mViewPager;

    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @InjectExtra(key = "Title")
    String title;

    private List<Fragment> mList = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_hist);

        initData();
    }

    private void initData() {
        toolbar.setTitle("< "+title);

        mList.add(new BackProjectFragment());
        mList.add(new GaussianBackFragment());
        mViewPager.setAdapter(new ProjectHistAdapter(this.getSupportFragmentManager(),mList));
        mTabLayout.setupWithViewPager(mViewPager);
    }

    @OnClick(id= R.id.toolbar)
    void clickToolbar() {

        finish();
    }
}
