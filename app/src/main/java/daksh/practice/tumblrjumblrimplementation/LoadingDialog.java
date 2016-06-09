package daksh.practice.tumblrjumblrimplementation;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;;
import android.view.ViewGroup;

public class LoadingDialog extends Dialog {

    public LoadingDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_loading);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
    }
}
