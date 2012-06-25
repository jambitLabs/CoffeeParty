package com.jambit.coffeeparty;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter {
    int mGalleryItemBackground;
    private Context mContext;

    private Integer[] mImageIds = { R.drawable.droid_green, R.drawable.droid_red, R.drawable.droid_blue,
            R.drawable.droid_yellow };

    public ImageAdapter(Context c) {
        mContext = c;

        TypedArray a = c.obtainStyledAttributes(R.styleable.Gallery1);
        mGalleryItemBackground = a.getResourceId(R.styleable.Gallery1_android_galleryItemBackground, 0);
        a.recycle();
    }

    @Override
    public int getCount() {
        return mImageIds.length;
    }

    @Override
    public Object getItem(int position) {
        return mContext.getResources().getDrawable(mImageIds[position]);
    }

    @Override
    public long getItemId(int position) {
        return mImageIds[position];
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView i = new ImageView(mContext);

        i.setImageResource(mImageIds[position]);
        i.setLayoutParams(new Gallery.LayoutParams(150, 100));
        i.setScaleType(ImageView.ScaleType.FIT_XY);
        i.setBackgroundResource(mGalleryItemBackground);

        return i;
    }

}
