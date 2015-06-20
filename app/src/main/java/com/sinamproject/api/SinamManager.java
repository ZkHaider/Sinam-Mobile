package com.sinamproject.api;

import android.content.Context;

import com.sinamproject.api.models.FormResponse;
import com.sinamproject.api.models.Forms;
import com.sinamproject.api.models.PatientUpload;
import com.sinamproject.events.FormResponseEvent;
import com.sinamproject.events.LoadFormsEvent;
import com.sinamproject.events.LoadedFormsEvent;
import com.sinamproject.events.UploadFormEvent;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by ZkHaider on 4/18/15.
 */
public class SinamManager {

    public static final String TAG = SinamManager.class.getSimpleName();

    private SinamClient sClient;
    private Context mContext;
    private Bus mBus;

    public SinamManager(Context context, Bus bus) {
        this.mContext = context;
        this.mBus = bus;
        sClient = SinamClient.getInstance();
    }

    @Subscribe
    public void onLoadFormsEvent(LoadFormsEvent loadFormsEvent) {

        Callback<Forms> callback = new Callback<Forms>() {
            @Override
            public void success(Forms forms, Response response) {
                mBus.post(new LoadedFormsEvent(forms));
            }

            @Override
            public void failure(RetrofitError error) {

            }
        };
        sClient.getForms(callback);
    }

    @Subscribe
    public void onUploadFormEvent(UploadFormEvent uploadFormEvent) {

        PatientUpload patientUpload = uploadFormEvent.getPatientUploadForm();
        Callback<FormResponse> callback = new Callback<FormResponse>() {
            @Override
            public void success(FormResponse formResponse, Response response) {
                mBus.post(new FormResponseEvent(formResponse));
            }

            @Override
            public void failure(RetrofitError error) {

            }
        };
        sClient.postForm(patientUpload, callback);
    }

}
