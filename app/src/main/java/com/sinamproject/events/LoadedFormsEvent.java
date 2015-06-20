package com.sinamproject.events;

import com.sinamproject.api.models.Form;
import com.sinamproject.api.models.Forms;

/**
 * Created by ZkHaider on 5/10/15.
 */
public class LoadedFormsEvent {

    private Forms mForms;

    public LoadedFormsEvent(Forms forms) {
        this.mForms = forms;
    }

    public Forms getForms() {
        return mForms;
    }
}
