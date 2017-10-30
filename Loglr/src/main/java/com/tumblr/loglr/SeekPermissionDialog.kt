package com.tumblr.loglr

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.Window
import com.tumblr.loglr.Interfaces.DialogCallbackListener
import kotlinx.android.synthetic.main.dialog_seek_permission.*

class SeekPermissionDialog(context: Context): Dialog(context), View.OnClickListener {

    /**
     * DialogCallback  listener to execute on tapping positive / negative items
     */
    private var dialogCallbackListener: DialogCallbackListener? = null

    /**
     * A method to accept callback listener for positive and negative button presses
     * @param dialogCallbackListener the callback listener that registered calls for options selected
     *                               on the dialog
     */
    fun setCallback(dialogCallbackListener: DialogCallbackListener) {
        this@SeekPermissionDialog.dialogCallbackListener = dialogCallbackListener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.requestFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_seek_permission)

        val btnOkay = dialog_seek_permission_okay
        btnOkay.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        dismiss()
        dialogCallbackListener?.onButtonOkay()
    }
}