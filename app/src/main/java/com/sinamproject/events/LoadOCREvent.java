package com.sinamproject.events;

import android.content.Context;
import android.graphics.Bitmap;

/**
 * Created by ZkHaider on 4/18/15.
 */
public class LoadOCREvent {

    private Context mContext;
    private Bitmap mBitmap;

    public LoadOCREvent(Context context, Bitmap bitmap) {
        this.mContext = context;
        this.mBitmap = bitmap;
    }

    public Context getContext() {
        return mContext;
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }
}
