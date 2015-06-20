package com.sinamproject.api;

import com.sinamproject.api.api_interfaces.IForm;
import com.sinamproject.api.models.FormResponse;
import com.sinamproject.api.models.Form;
import com.sinamproject.api.models.Forms;
import com.sinamproject.api.models.PatientUpload;

import retrofit.Callback;
import retrofit.RestAdapter;

/**
 * Created by ZkHaider on 4/18/15.
 */
public class SinamClient {

    public static final String TAG = SinamClient.class.getSimpleName();
    private static final String API_URL = "https://rocky-springs-6486.herokuapp.com/api";

    private static SinamClient mSinamClient;
    private static RestAdapter mAsyncRestAdapter;

    private SinamClient() {
        mAsyncRestAdapter = new RestAdapter.Builder()
                .setEndpoint(API_URL)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();
    }

    public static SinamClient getInstance() {
        if (mSinamClient == null)
            mSinamClient = new SinamClient();
        return mSinamClient;
    }

    public void getForms(Callback<Forms> callback) {
        IForm forms = mAsyncRestAdapter.create(IForm.class);
        forms.getForms(callback);
    }

    public void postForm(PatientUpload patientUpload, Callback<FormResponse> callback) {
        IForm forms = mAsyncRestAdapter.create(IForm.class);
        forms.postForm(patientUpload, callback);
    }

}
