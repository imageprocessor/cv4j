package com.cv4j.app.fragment;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.cv4j.app.R;
import com.cv4j.app.app.BaseFragment;
import com.cv4j.core.datamodel.ColorImage;
import com.safframework.injectview.Injector;
import com.safframework.injectview.annotations.InjectView;

import thereisnospon.codeview.CodeView;
import thereisnospon.codeview.CodeViewTheme;

/**
 * Created by Tony Shen on 2017/3/12.
 */

public class IOFragment extends BaseFragment {

    @InjectView(R.id.image1)
    ImageView image1;

    @InjectView(R.id.image2)
    ImageView image2;

    @InjectView(R.id.codeview)
    CodeView codeView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_io, container, false);
        Injector.injectInto(this, v);

        initData();

        return v;
    }

    private void initData() {

        Resources res= mContext.getResources();
        Bitmap bitmap = BitmapFactory.decodeResource(res, R.drawable.test_io);
        image1.setImageBitmap(bitmap);

        ColorImage ci = new ColorImage(bitmap);
        image2.setImageBitmap(ci.toBitmap());

        codeView.setTheme(CodeViewTheme.ANDROIDSTUDIO).fillColor();

        StringBuilder code = new StringBuilder();
        code.append("ColorImage ci = new ColorImage(bitmap);")
                .append("\r\n")
                .append("image2.setImageBitmap(ci.toBitmap());");

        codeView.showCode(code.toString());
    }
}
