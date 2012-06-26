package com.jambit.coffeeparty;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter {
    int mGalleryItemBackground;
    private Context mContext;

    private List<Bitmap> bitmaps = new ArrayList<Bitmap>();

    public ImageAdapter(Context c) {
        mContext = c;
        bitmaps.add(((BitmapDrawable) mContext.getResources().getDrawable(R.drawable.droid_green)).getBitmap());
        bitmaps.add(((BitmapDrawable) mContext.getResources().getDrawable(R.drawable.droid_red)).getBitmap());
        bitmaps.add(((BitmapDrawable) mContext.getResources().getDrawable(R.drawable.droid_blue)).getBitmap());
        bitmaps.add(((BitmapDrawable) mContext.getResources().getDrawable(R.drawable.droid_yellow)).getBitmap());
        TypedArray a = c.obtainStyledAttributes(R.styleable.Gallery1);
        mGalleryItemBackground = a.getResourceId(R.styleable.Gallery1_android_galleryItemBackground, 0);
        a.recycle();
    }

    @Override
    public int getCount() {
        return bitmaps.size();
    }

    @Override
    public Object getItem(int position) {
        return bitmaps.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO what to return here when Bitmaps are stored
        assert (false);
        return 0;
    }

    public void addBitmap(Bitmap bitmap) {
        bitmaps.add(bitmap);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // ImageView i = new ImageView(mContext);
        //
        // i.setImageResource(mResImageIds[position]);
        // i.setLayoutParams(new Gallery.LayoutParams(150, 100));
        // i.setScaleType(ImageView.ScaleType.FIT_XY);
        // i.setBackgroundResource(mGalleryItemBackground);
        //
        // return i;

        if (convertView == null) {
            convertView = new ImageView(mContext);
        }
        ((ImageView) convertView).setImageBitmap(bitmaps.get(position));
        return convertView;

    }

}
