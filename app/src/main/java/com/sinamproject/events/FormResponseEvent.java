package com.sinamproject.events;

import com.sinamproject.api.models.FormResponse;

/**
 * Created by ZkHaider on 5/10/15.
 */
public class FormResponseEvent {

    private FormResponse mFormResponse;

    public FormResponseEvent(FormResponse formResponse) {
        this.mFormResponse = formResponse;
    }

    public FormResponse getFormResponse() {
        return mFormResponse;
    }
}
