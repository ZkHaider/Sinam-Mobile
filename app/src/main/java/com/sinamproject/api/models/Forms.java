package com.sinamproject.api.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by ZkHaider on 5/10/15.
 */
public class Forms {

    @SerializedName("")
    private List<Form> mForms;
    public List<Form> getForms() {
        return mForms;
    }

}
