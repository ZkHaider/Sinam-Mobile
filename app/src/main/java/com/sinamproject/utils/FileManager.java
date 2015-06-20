package com.sinamproject.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by ZkHaider on 4/18/15.
 */
public class FileManager {

    public static final String STORAGE_PATH = Environment.getExternalStorageDirectory().toString()
                                                            + "/plug/";
    public static final String TESSERACT_PATH = STORAGE_PATH + "tessdata/";
    public static final String LANG = "eng";
    public static final String FULL_PATH = TESSERACT_PATH + LANG + ".traineddata";
    public static final String FULL_ASSET_PATH = "tessdata/" + LANG + ".traineddata";

    private Context mContext;
    private AssetManager mAssetManager;

    public FileManager(Context context) {
        this.mContext = context;
        this.mAssetManager = context.getAssets();
        initPath();
    }

    public void initPath() {
        String[] paths = new String[] { STORAGE_PATH, TESSERACT_PATH };
        // Make new directory
        for (String path : paths) {
            File dir = new File(path);
            dir.mkdirs();
        }
    }

    public void setContext(Context context) {
        this.mContext = context;
    }

    public Context getContext() {
        return mContext;
    }

    public void writeRawToSD(String output, String input) {
        if (!(new File(output)).exists()) {
            try {
                InputStream is = mAssetManager.open(input);
                OutputStream os = new FileOutputStream(output);

                byte[] buf = new byte[1024];
                int len;

                while ((len = is.read(buf)) > 0) {
                    os.write(buf, 0, len);
                }

                is.close();
                os.close();

                Log.i("SUCCESS: ", output);

            } catch (IOException e) {
                Log.e("FAILED TO COPY: ", e.getMessage());
            }
        }
    }


}
