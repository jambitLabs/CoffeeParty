package com.jambit.coffeeparty;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class ImageAdapter extends BaseAdapter {
    private static final int IMAGEVIEW_ID = 0;
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
    public View getView(int position, View convertView, final ViewGroup parent) {

        ImageView imageView = new ImageView(mContext) {
            @Override
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                setMinimumWidth((int) (parent.getHeight() * 0.8));
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            }

            @Override
            protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
                setMinimumWidth((int) (parent.getHeight() * 0.8));
                super.onLayout(changed, left, top, right, bottom);
            }

        };
        imageView.setId(IMAGEVIEW_ID);
        LayoutParams params = new Gallery.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT);
        LayoutParams imageParams = new Gallery.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
        imageView.setLayoutParams(imageParams);
        // imageView.setMinimumWidth((int) (parent.getHeight() * 0.8));
        imageView.setScaleType(ScaleType.FIT_CENTER);

        imageView.setImageBitmap(bitmaps.get(position));
        FrameLayout borderImg = new FrameLayout(mContext);

        borderImg.setPadding(1, 1, 1, 1);
        borderImg.setBackgroundResource(R.drawable.avatarbackground);

        borderImg.setLayoutParams(params);
        borderImg.addView(imageView);

        return borderImg;

    }
}
