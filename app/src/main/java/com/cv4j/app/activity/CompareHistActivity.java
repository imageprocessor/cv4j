package com.cv4j.app.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;

import com.cv4j.app.R;
import com.cv4j.app.adapter.CompareHistAdapter;
import com.cv4j.app.fragment.CompareHist1Fragment;
import com.cv4j.app.fragment.CompareHist2Fragment;
import com.cv4j.app.fragment.CompareHist3Fragment;
import com.safframework.injectview.annotations.InjectExtra;
import com.safframework.injectview.annotations.InjectView;
import com.safframework.injectview.annotations.OnClick;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tony Shen on 2017/6/4.
 */

public class CompareHistActivity extends BaseActivity {

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
        setContentView(R.layout.activity_compare_hist);

        initData();
    }

    private void initData() {

        toolbar.setTitle("< "+title);

        mList.add(new CompareHist1Fragment());
        mList.add(new CompareHist2Fragment());
        mList.add(new CompareHist3Fragment());
        mViewPager.setAdapter(new CompareHistAdapter(this.getSupportFragmentManager(),mList));
        mTabLayout.setupWithViewPager(mViewPager);
    }

    @OnClick(id= R.id.toolbar)
    void clickToolbar() {

        finish();
    }
}
