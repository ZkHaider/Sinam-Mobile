package com.sinamproject.events;

import com.sinamproject.api.models.PatientUpload;

/**
 * Created by ZkHaider on 5/10/15.
 */
public class UploadFormEvent {

    private PatientUpload mPatientUpload;

    public UploadFormEvent(PatientUpload patientUpload) {
        this.mPatientUpload = patientUpload;
    }

    public PatientUpload getPatientUploadForm() {
        return mPatientUpload;
    }

}
