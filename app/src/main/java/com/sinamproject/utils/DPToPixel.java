package com.sinamproject.utils;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * Created by ZkHaider on 4/2/15.
 */
public class DPToPixel {

    public static int dpToPx(Context context, int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int mPixels = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_XHIGH));
        return mPixels;
    }


}