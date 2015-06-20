package com.sinamproject.events;

/**
 * Created by ZkHaider on 4/18/15.
 */
public class LoadedOCREvent {

    private String mOCRText;

    public LoadedOCREvent(String text) {
        this.mOCRText = text;
    }

    public String getOCRText() {
        return mOCRText;
    }
}
