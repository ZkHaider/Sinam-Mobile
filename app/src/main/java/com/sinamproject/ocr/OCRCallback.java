package com.sinamproject.ocr;

/**
 * Created by ZkHaider on 4/19/15.
 */
public interface OCRCallback {

    public abstract void onFinishRecognition(String recognizedText);

}