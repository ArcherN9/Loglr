package com.daksh.loglr;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

public class LoglrFragment extends DialogFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_tumblr_login, null, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
    }
}
