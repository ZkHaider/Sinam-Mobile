package com.sinamproject.ocr;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.googlecode.tesseract.android.TessBaseAPI;
import com.sinamproject.utils.FileManager;

import java.io.File;

/**
 * Created by ZkHaider on 4/18/15.
 */
public class TesseractClient {

    private static final String TAG = TesseractClient.class.getSimpleName();

    private static TesseractClient sTesseractClient;
    private static TessBaseAPI tessBaseAPI;

    public TesseractClient(Context context) {

        Log.d(TAG, "TesseractClient created");

        tessBaseAPI = new TessBaseAPI();

        // Check if the path file exists, create it if it doesn't
        if (!(new File(FileManager.FULL_PATH).exists())) {
            FileManager fileManager = new FileManager(context);
            fileManager.writeRawToSD(FileManager.FULL_PATH, FileManager.FULL_ASSET_PATH);
        }

        tessBaseAPI.setDebug(true);
        tessBaseAPI.init(FileManager.STORAGE_PATH, FileManager.LANG);

    }

    public static TesseractClient getTesseractClient(Context context) {
        Log.d(TAG, "getTesseractClient");
        if (sTesseractClient == null)
            sTesseractClient = new TesseractClient(context);
        return sTesseractClient;
    }

    public void setBitmapToTesseract(Bitmap bitmapToTesseract) {
        Log.d(TAG, "setBitmapToTesseract");
        tessBaseAPI.setImage(bitmapToTesseract);
    }

    public String getTextFromBitmap() {
        Log.d(TAG, "getTextFromBitmap");
        return tessBaseAPI.getUTF8Text();
    }

    public void endTessBaseAPI() {
        Log.d(TAG, "endTessBaseAPI");
        tessBaseAPI.end();
    }

}
