package com.cv4j.app.adapter;

import android.content.Context;
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

import java.util.List;

/**
 * Created by Tony Shen on 2017/3/15.
 */

public class GridViewFilterAdapter extends RecyclerView.Adapter<GridViewFilterAdapter.ViewHolder> {

    private List<String> mList;
    private Context mContext;
    private Bitmap mBitmap;

    public GridViewFilterAdapter(Context context, List<String> data, Bitmap bitmap) {
        mContext = context;
        mList = data;
        mBitmap = bitmap;
    }

    @Override
    public GridViewFilterAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new GridViewFilterAdapter.ViewHolder(parent, R.layout.cell_gridview_filter);
    }

    @Override
    public void onBindViewHolder(final GridViewFilterAdapter.ViewHolder holder, int position) {

        String filterName = mList.get(position);

        if (position == 0) {
            holder.image.setImageBitmap(mBitmap);
        } else {

            if (Preconditions.isNotBlank(filterName)) {
                CommonFilter filter = (CommonFilter)getFilter(filterName);
                RxImageData.bitmap(mBitmap)
                        .addFilter(filter)
                        .into(holder.image);
            }

        }

        holder.text.setText(filterName);
    }

    private Object getFilter(String filterName) {

        Object object = null;
        try {
            object = Class.forName("com.cv4j.core.filters."+filterName+"Filter").newInstance();
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

        ImageView image;

        TextView text;

        public ViewHolder(ViewGroup parent, @LayoutRes int resId) {
            super(LayoutInflater.from(parent.getContext()).inflate(resId, parent, false));

            image = (ImageView)itemView.findViewById(R.id.image);

            text = (TextView)itemView.findViewById(R.id.text);
        }
    }
}
