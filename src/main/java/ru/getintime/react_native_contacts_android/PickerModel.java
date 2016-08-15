package ru.getintime.react_native_contacts_android;

import android.app.Activity;

import com.facebook.react.bridge.Callback;

public class PickerModel {

    private Callback callback;
    private Activity activity;

    public PickerModel(Callback callback, Activity activity){
        this.callback = callback;
        this.activity = activity;
    }

    public Callback getCallback() {
        return callback;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }
}
