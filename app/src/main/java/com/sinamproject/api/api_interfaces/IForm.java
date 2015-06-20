package com.sinamproject.api.api_interfaces;

import com.sinamproject.api.models.FormResponse;
import com.sinamproject.api.models.Form;
import com.sinamproject.api.models.Forms;
import com.sinamproject.api.models.PatientUpload;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.POST;

/**
 * Created by ZkHaider on 5/10/15.
 */
public interface IForm {

    @GET("/forms")
    void getForms(Callback<Forms> callback);

    @POST("/forms")
    void postForm(PatientUpload patientUpload, Callback<FormResponse> callback);

}
