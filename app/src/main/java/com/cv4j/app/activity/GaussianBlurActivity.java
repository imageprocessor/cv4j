package com.cv4j.app.activity;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.widget.ImageView;

import com.cv4j.app.R;
import com.cv4j.app.app.BaseActivity;
import com.cv4j.core.filters.GaussianBlurFilter;
import com.cv4j.rxjava.RxImageData;
import com.safframework.aop.annotation.Trace;
import com.safframework.injectview.Injector;
import com.safframework.injectview.annotations.InjectView;


/**
 * Created by Tony Shen on 2017/3/21.
 */

public class GaussianBlurActivity extends BaseActivity {

    @InjectView(R.id.image1)
    ImageView image1;

    @InjectView(R.id.image2)
    ImageView image2;

    Resources res;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gaussian_blur);
        Injector.injectInto(this);

        initData();
        useRenderScript();
        // 由于blur()中将bitmap回收啦，所以要重新赋值
        bitmap = BitmapFactory.decodeResource(res, R.drawable.test_filters);
        useCV4j();
    }

    private void initData() {

        res = getResources();
        bitmap = BitmapFactory.decodeResource(res, R.drawable.test_filters);
    }

    @Trace
    private void useRenderScript() {
        image1.setImageBitmap(blur(bitmap));
    }

    @Trace
    private void useCV4j() {

        RxImageData.imageData(bitmap)
                .placeHolder(image2, R.drawable.test_filters)
                .addFilter(new GaussianBlurFilter(20))
                .into(image2);
    }

    /**
     * 使用RenderScript实现高斯模糊的算法
     * @param bitmap
     * @return
     */
    public Bitmap blur(Bitmap bitmap){
        //Let's create an empty bitmap with the same size of the bitmap we want to blur
        Bitmap outBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        //Instantiate a new Renderscript
        RenderScript rs = RenderScript.create(getApplicationContext());
        //Create an Intrinsic Blur Script using the Renderscript
        ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        //Create the Allocations (in/out) with the Renderscript and the in/out bitmaps
        Allocation allIn = Allocation.createFromBitmap(rs, bitmap);
        Allocation allOut = Allocation.createFromBitmap(rs, outBitmap);
        //Set the radius of the blur: 0 < radius <= 25
        blurScript.setRadius(20.0f);
        //Perform the Renderscript
        blurScript.setInput(allIn);
        blurScript.forEach(allOut);
        //Copy the final bitmap created by the out Allocation to the outBitmap
        allOut.copyTo(outBitmap);
        //recycle the original bitmap
        bitmap.recycle();
        //After finishing everything, we destroy the Renderscript.
        rs.destroy();

        return outBitmap;

    }
}
