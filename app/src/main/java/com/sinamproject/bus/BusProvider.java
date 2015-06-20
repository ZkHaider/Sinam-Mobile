package com.sinamproject.bus;

import android.util.Log;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

/**
 * Created by ZkHaider on 4/18/15.
 */
public class BusProvider {

    private static final String TAG = BusProvider.class.getSimpleName();
    private static final Bus BUS = new Bus(ThreadEnforcer.ANY);

    public static Bus getInstance() {
        Log.d(TAG, "getInstance for new Bus");
        return BUS;
    }

    private BusProvider() {

    }
}
