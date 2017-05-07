package com.cv4j.app.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.cv4j.app.fragment.PaintFragment;

/**
 * Created by Tony Shen on 2017/5/7.
 */

public class PaintAdapter extends FragmentPagerAdapter {

    private static final int PAGE_COUNT = 3;

    private Context mContext;

    public PaintAdapter(Context context, FragmentManager fm) {
        super(fm);
        this.mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        return PaintFragment.newInstance(position);
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
