package com.sinamproject.data;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ZkHaider on 4/18/15.
 */

public class Document {

    public Document() {}

    public Document(int id, String title, String recognizedText) {
        this.mId = id;
        this.mTitle = title;
        this.mRecognizedText = recognizedText;
    }


    @SerializedName("id")
    private int mId;

    @SerializedName("title")
    private String mTitle;

    @SerializedName("recognized_text")
    private String mRecognizedText;


    public int getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getRecognizedText() {
        return mRecognizedText;
    }

    public void setId(int id) {
        this.mId = id;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public void setRecognizedText(String result) {
        this.mRecognizedText = result;
    }
}
