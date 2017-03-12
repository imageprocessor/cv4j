package com.cv4j.app.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cv4j.app.R;
import com.cv4j.app.activity.CompositeFilersActivity;
import com.cv4j.app.activity.SelectFilterActivity;
import com.cv4j.app.app.BaseFragment;
import com.safframework.injectview.Injector;
import com.safframework.injectview.annotations.InjectView;
import com.safframework.injectview.annotations.OnClick;

/**
 * Created by Tony Shen on 2017/3/12.
 */

public class FiltersFragment extends BaseFragment {

    @InjectView(R.id.text1)
    TextView text1;

    @InjectView(R.id.text2)
    TextView text2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_filters, container, false);
        Injector.injectInto(this, v);

        return v;
    }

    @OnClick(id=R.id.text1)
    void clickText1() {

        Intent i = new Intent(mContext,SelectFilterActivity.class);
        i.putExtra("Title",text1.getText().toString());
        startActivity(i);
    }

    @OnClick(id=R.id.text2)
    void clickText2() {

        Intent i = new Intent(mContext,CompositeFilersActivity.class);
        i.putExtra("Title",text2.getText().toString());
        startActivity(i);
    }
}
