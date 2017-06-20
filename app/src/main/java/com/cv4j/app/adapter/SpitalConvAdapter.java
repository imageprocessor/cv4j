package com.cv4j.app.adapter;

import android.graphics.Bitmap;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cv4j.app.R;
import com.cv4j.core.filters.CommonFilter;
import com.cv4j.rxjava.RxImageData;
import com.safframework.tony.common.utils.Preconditions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Tony Shen on 2017/3/27.
 */

public class SpitalConvAdapter extends RecyclerView.Adapter<SpitalConvAdapter.ViewHolder> {

    private List<String> mList;
    private Bitmap mBitmap;
    private Map<Integer,String> map;

    public SpitalConvAdapter(List<String> data, Bitmap bitmap) {

        mList = data;
        mBitmap = bitmap;
        map = new HashMap<>();
        map.put(0,"原图");
        map.put(1,"卷积");
        map.put(2,"最大最小值滤波");
        map.put(3,"椒盐噪声");
        map.put(4,"锐化");
        map.put(5,"中值滤波");
        map.put(6,"拉普拉斯");
        map.put(7,"寻找边缘");
        map.put(8,"梯度");
        map.put(9,"方差滤波");
        map.put(10,"马尔操作");
        map.put(11,"USM");
    }

    @Override
    public SpitalConvAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SpitalConvAdapter.ViewHolder(parent, R.layout.cell_gridview_filter);
    }

    @Override
    public void onBindViewHolder(final SpitalConvAdapter.ViewHolder holder, int position) {

        if (position == 0) {
            holder.image.setImageBitmap(mBitmap);
        } else {
            String filterName = mList.get(position);
            if (Preconditions.isNotBlank(filterName)) {
                CommonFilter filter = (CommonFilter)getFilter(filterName);
                RxImageData.bitmap(mBitmap)
//                        .placeHolder(R.drawable.test_spital_conv)
                        .addFilter(filter)
                        .into(holder.image);
            }
        }

        holder.text.setText(map.get(position));
    }

    private Object getFilter(String filterName) {

        Object object = null;
        try {
            object = Class.forName("com.cv4j.core.spatial.conv."+filterName+"Filter").newInstance();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return object;
    }

    @Override
    public int getItemCount() {
        return mList!=null?mList.size():0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView image;

        private TextView text;

        public ViewHolder(ViewGroup parent, @LayoutRes int resId) {
            super(LayoutInflater.from(parent.getContext()).inflate(resId, parent, false));

            image = (ImageView)itemView.findViewById(R.id.image);

            text = (TextView)itemView.findViewById(R.id.text);
        }
    }
}
