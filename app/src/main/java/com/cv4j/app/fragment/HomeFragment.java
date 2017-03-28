package com.cv4j.app.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cv4j.app.R;
import com.cv4j.app.app.BaseFragment;
import com.safframework.injectview.Injector;
import com.safframework.injectview.annotations.InjectView;

import br.tiagohm.markdownview.MarkdownView;

/**
 * Created by Tony Shen on 2017/3/12.
 */

public class HomeFragment extends BaseFragment {

    @InjectView(R.id.markdownView)
    MarkdownView markdownView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        Injector.injectInto(this, v);

        markdownView.loadMarkdownFromAsset("README.md");

        return v;
    }

}
