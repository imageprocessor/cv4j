package com.cv4j.app.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by Tony Shen on 2017/5/7.
 */

public class PaintAdapter extends FragmentPagerAdapter {

    private static final int PAGE_COUNT = 3;

    private List<Fragment> mList;

    public PaintAdapter(FragmentManager fm, List<Fragment> list) {
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
                return "原图";
            case 1:
                return "油画";
            case 2:
                return "铅笔画";
            default:
                return "原图";
        }
    }
}
