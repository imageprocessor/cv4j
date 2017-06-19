package com.cv4j.app.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by Tony Shen on 2017/6/10.
 */

public class CompareHistAdapter extends FragmentPagerAdapter {

    private static final int PAGE_COUNT = 3;

    private List<Fragment> mList;

    public CompareHistAdapter(Context context, FragmentManager fm, List<Fragment> list) {
        super(fm);
        this.mList = list;
    }

    @Override
    public Fragment getItem(int position) {
        return mList.get(position);
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {

            case 0:
                return "原图和直方图均衡化";
            case 1:
                return "两张相同的图";
            case 2:
                return "两张完全不同的图";
            default:
                return "原图和直方图均衡化";
        }
    }
}
