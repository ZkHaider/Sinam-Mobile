package com.sinamproject.ocr;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.sinamproject.R;
import com.sinamproject.events.LoadOCREvent;
import com.sinamproject.events.LoadedOCREvent;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import fr.castorflex.android.smoothprogressbar.SmoothProgressDrawable;

/**
 * Created by ZkHaider on 4/18/15.
 */
public class TesseractManager {

    private static final String TAG = TesseractManager.class.getSimpleName();

    private TesseractClient sTesseractClient;
    private Bus mBus;
    private Context mContext;

    public TesseractManager(Context context, Bus bus) {
        mContext = context;
        mBus = bus;
        sTesseractClient = TesseractClient.getTesseractClient(mContext);
    }

    @Subscribe
    public void onLoadBitmapEvent(LoadOCREvent loadOCREvent) {
        Log.d(TAG, "onLoadBitmapEvent");
        Context context = loadOCREvent.getContext();
        Bitmap bitmap = loadOCREvent.getBitmap();
        if (bitmap != null) {
            Log.d(TAG, "bitmap is not null initiating OCRTask");
            new OCRTask(context, bitmap, new OCRCallback() {
                @Override
                public void onFinishRecognition(String recognizedText) {
                    if (recognizedText != null)
                        mBus.post(new LoadedOCREvent(recognizedText));
                }
            }).execute();
        }
    }

    private class OCRTask extends AsyncTask<Void, Integer, String> {

        private final String TAG = OCRTask.class.getSimpleName();

        private Context context;
        private Bitmap bitmap;
        private OCRCallback ocrCallback;
        private SmoothProgressBar smoothProgressBar;

        public OCRTask(Context context, Bitmap bitmap, OCRCallback callback) {
            Log.d(TAG, "New OCRTask created");
            this.context = context;
            this.bitmap = bitmap;
            this.ocrCallback = callback;

            View view = View.inflate(context, R.layout.activity_main, null);
            smoothProgressBar = (SmoothProgressBar) view.findViewById(R.id.sinamProgressBar);

            sTesseractClient = TesseractClient.getTesseractClient(mContext);
        }

        @Override
        protected void onPreExecute() {
            Log.d(TAG, "onPreExecute");
            sTesseractClient.setBitmapToTesseract(bitmap);
        }

        @Override
        protected String doInBackground(Void... params) {
            String result = sTesseractClient.getTextFromBitmap();
            return result;
        }

        protected void onPostExecute(String finish) {
            ocrCallback.onFinishRecognition(finish);
        }

    }

}
