package com.sinamproject.api.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ZkHaider on 5/10/15.
 */
public class PatientUpload {

    @SerializedName("patient_name")
    private String mPatientName;
    public String getPatientName() {
        return mPatientName;
    }

    @SerializedName("date_of_birth")
    private String mDateOfBirth;
    public String getDateOfBirth() {
        return mDateOfBirth;
    }

    @SerializedName("sex")
    private String mSex;
    public String getSex() {
        return mSex;
    }

    @SerializedName("age")
    private int mAge;
    public int getAge() {
        return mAge;
    }

}
