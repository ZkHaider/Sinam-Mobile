package com.sinamproject.utils;

import android.content.Context;

/**
 * Created by Haider on 3/15/2015.
 */

public class PixelToDP {

    private static Context mContext;
    private static float mScale;

    private static PixelToDP mPixelToDP;

    public PixelToDP() {

    }

    public static PixelToDP getInstance(Context context) {
        mContext = context;
        mScale = mContext.getResources().getDisplayMetrics().density;
        if (mPixelToDP == null)
            mPixelToDP = new PixelToDP();
        return mPixelToDP;
    }

    public int getDp(int dp) {
        return (int) (dp * mScale + 0.5f);
    }


}