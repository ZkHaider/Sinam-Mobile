package com.sinamproject.application;

import android.app.Application;

import com.sinamproject.api.SinamManager;
import com.sinamproject.bus.BusProvider;
import com.sinamproject.ocr.TesseractManager;
import com.squareup.otto.Bus;

/**
 * Created by ZkHaider on 4/18/15.
 */
public class SinamApplication extends Application {

    private TesseractManager mTesseractManager;
    private Bus mBus = BusProvider.getInstance();
    private SinamManager mSinamManager;

    @Override
    public void onCreate() {
        mTesseractManager = new TesseractManager(this, mBus);
        mSinamManager = new SinamManager(this, mBus);
        mBus.register(mTesseractManager);
        mBus.register(this);
    }
}
