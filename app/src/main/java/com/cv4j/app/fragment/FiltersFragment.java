package com.cv4j.app.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cv4j.app.R;
import com.cv4j.app.activity.BeautySkinActivity;
import com.cv4j.app.activity.ColorFilterActivity;
import com.cv4j.app.activity.CompositeFilersActivity;
import com.cv4j.app.activity.GaussianBlurActivity;
import com.cv4j.app.activity.GridViewFilterActivity;
import com.cv4j.app.activity.SelectFilterActivity;
import com.cv4j.app.activity.UseFilterWithRxActivity;
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

    @InjectView(R.id.text3)
    TextView text3;

    @InjectView(R.id.text4)
    TextView text4;

    @InjectView(R.id.text5)
    TextView text5;

    @InjectView(R.id.text6)
    TextView text6;

    @InjectView(R.id.text7)
    TextView text7;

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

    @OnClick(id=R.id.text3)
    void clickText3() {

        Intent i = new Intent(mContext,UseFilterWithRxActivity.class);
        i.putExtra("Title",text3.getText().toString());
        startActivity(i);
    }

    @OnClick(id=R.id.text4)
    void clickText4() {

        Intent i = new Intent(mContext,GridViewFilterActivity.class);
        i.putExtra("Title",text4.getText().toString());
        startActivity(i);
    }

    @OnClick(id=R.id.text5)
    void clickText5() {

        Intent i = new Intent(mContext,ColorFilterActivity.class);
        i.putExtra("Title",text5.getText().toString());
        startActivity(i);
    }

    @OnClick(id=R.id.text6)
    void clickText6() {

        Intent i = new Intent(mContext,GaussianBlurActivity.class);
        i.putExtra("Title",text6.getText().toString());
        startActivity(i);
    }

    @OnClick(id=R.id.text7)
    void clickText7() {

        Intent i = new Intent(mContext,BeautySkinActivity.class);
        i.putExtra("Title",text7.getText().toString());
        startActivity(i);
    }
}
