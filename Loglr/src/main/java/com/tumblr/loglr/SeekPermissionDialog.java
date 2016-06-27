package com.tumblr.loglr;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.tumblr.loglr.Interfaces.DialogCallbackListener;

public class SeekPermissionDialog extends Dialog implements View.OnClickListener {

    /**
     * DialogCallback  listener to execute on tapping positive / negative items
     */
    private DialogCallbackListener dialogCallbackListener;

    public SeekPermissionDialog(Context context) {
        super(context);
    }

    public SeekPermissionDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected SeekPermissionDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    /**
     * A method to accept callback listener for positive and negative button presses
     * @param dialogCallbackListener the callback listener that registered calls for options selected
     *                               on the dialog
     */
    void setCallback(DialogCallbackListener dialogCallbackListener) {
        this.dialogCallbackListener = dialogCallbackListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_seek_permission);

        TextView txOkay = (TextView) findViewById(R.id.dialog_seek_permission_okay);
        txOkay.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        dismiss();
        dialogCallbackListener.onButtonOkay();
    }
}