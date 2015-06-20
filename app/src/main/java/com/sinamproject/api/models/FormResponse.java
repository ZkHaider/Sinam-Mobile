package com.sinamproject.api.models;

import com.google.gson.annotations.SerializedName;

import retrofit.RestAdapter;

/**
 * Created by ZkHaider on 5/10/15.
 */
public class FormResponse {

    @SerializedName("_v")
    private int mVersion;
    public int getVersion() {
        return mVersion;
    }

    @SerializedName("_id")
    private String mId;
    public String getId() {
        return mId;
    }

}
