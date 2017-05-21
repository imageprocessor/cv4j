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
import com.cv4j.core.filters.OilPaintFilter;
import com.cv4j.core.filters.StrokeAreaFilter;
import com.cv4j.rxjava.RxImageData;
import com.safframework.injectview.Injector;
import com.safframework.injectview.annotations.InjectView;

/**
 * Created by Tony Shen on 2017/5/7.
 */

public class PaintFragment extends BaseFragment {

    @InjectView(R.id.image)
    ImageView image;

    private final static String PAINT_TYPE = "type";

    private final static int DEFAULT_TYPE = 0;
    private final static int OIL_PAINT_TYPE = 1;
    private final static int PENCIL_PAINT_TYPE = 2;

    private int mType;

    public static PaintFragment newInstance(int type) {
        Bundle args = new Bundle();
        args.putInt(PAINT_TYPE, type);
        PaintFragment fragment = new PaintFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mType = bundle.getInt(PAINT_TYPE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_paint, container, false);
        Injector.injectInto(this, v);

        initData();
        return v;
    }

    private void initData() {
        Resources res= getResources();
        Bitmap bitmap = BitmapFactory.decodeResource(res, R.drawable.test_oil_paint);

        switch(mType) {

            case OIL_PAINT_TYPE:
                RxImageData.bitmap(bitmap).addFilter(new OilPaintFilter()).into(image);
                break;

            case PENCIL_PAINT_TYPE:
                RxImageData.bitmap(bitmap).addFilter(new StrokeAreaFilter()).into(image);
                break;

            default:
                image.setImageBitmap(bitmap);
                break;
        }
    }
}
