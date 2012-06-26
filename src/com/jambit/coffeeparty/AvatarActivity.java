package com.jambit.coffeeparty;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Gallery;
import android.widget.TextView;

public class AvatarActivity extends Activity {
    private static final int TAKE_PHOTO_ACTIONCODE = 0;
    public static String PLAYERNAME_EXTRA = "playerName";
    public static String SELECTED_AVATAR_EXTRA = "selected_avatar";
    private Intent data = new Intent();
    private ImageAdapter imageAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.avataractivity);
        imageAdapter = new ImageAdapter(this);
        Gallery g = (Gallery) findViewById(R.id.gallery1);
        g.setAdapter(imageAdapter);
        TextView playerNameTextView = (TextView) findViewById(R.id.playerNameTextView);
        CharSequence playerName = getIntent().getCharSequenceExtra(PLAYERNAME_EXTRA);
        playerNameTextView.setText(playerName);
        data.putExtra(PLAYERNAME_EXTRA, playerName);
        data.putExtra(SELECTED_AVATAR_EXTRA, (Bitmap) imageAdapter.getItem(0));

        g.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(@SuppressWarnings("rawtypes") AdapterView parent, View v, int position, long id) {
                data.putExtra(SELECTED_AVATAR_EXTRA, (Bitmap) imageAdapter.getItem(position));
            }
        });
    }

    public void applyButtonOnClick(View v) {
        setResult(RESULT_OK, data);
        finish();
    }

    public void cameraButtonOnClick(View v) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePictureIntent, TAKE_PHOTO_ACTIONCODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bundle extras = data.getExtras();
        if (requestCode == TAKE_PHOTO_ACTIONCODE) {
            Bitmap originalBitmap = (Bitmap) extras.get("data");
            Matrix matrix = new Matrix();
            float aspectRatio = (float) originalBitmap.getHeight() / (float) originalBitmap.getWidth();
            float xScale = 100.0f / (float) originalBitmap.getWidth();
            float yScale = 100.0f / (float) originalBitmap.getHeight() * aspectRatio;
            matrix.postScale(xScale, yScale);
            Bitmap scaledBitmap = Bitmap.createBitmap(originalBitmap,
                                                      0,
                                                      0,
                                                      originalBitmap.getWidth(),
                                                      originalBitmap.getHeight(),
                                                      matrix,
                                                      true);
            data.putExtra(SELECTED_AVATAR_EXTRA, scaledBitmap);
            imageAdapter.addBitmap(scaledBitmap);
            ((Gallery) findViewById(R.id.gallery1)).setSelection(imageAdapter.getCount());
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
