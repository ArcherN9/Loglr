package com.tumblr.loglr

import android.Manifest
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.support.v4.app.ActivityCompat

class Utils {

    companion object {

        /**
         * A method to check if the android version is Marshmallow or above
         * @return A boolean value
         */
        fun isMarshmallowAbove(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M

        /**
         * A method to check if SMS read permissions have been granted to the application
         * @param context Context of the calling Activity
         * @return Returns a boolean value determining if SMS read permisisons have been granted or not
         */
        fun isSMSReadPermissionGranted(context: Context): Boolean
                = ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED

        /**
         * A method to check if the android version is Kitkat or above
         * @return a Boolean value
         */
        fun isKitkatAbove(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT

        /**
         * A method to construct the loading dialog passed on by the developer to Loglr
         * @param context context of the calling activity
         * @return returns the Loading Dialog to display to the user when authentication tokens are being
         *         exchanged and the user is expected to wait.
         */
        fun getLoadingDialog(context: Context): Dialog {
            //Test if Loading Dialog was receiver
            if(Loglr.instance.getLoadingDialog() != null) {
                //Extract Loading Dialog class passed by the Activity
                val classDialog = Loglr.instance.getLoadingDialog()
                //get default constructor and create new instance for the Dialog
                return classDialog?.getConstructor(Context::class.java)!!.newInstance(context)
//        e.printStackTrace();
//            LoadingDialog = new ProgressDialog(context);
//            LoadingDialog.setTitle(context.getString(R.string.tumblrlogin_loading));
//            if(Loglr.getInstance().getFirebase() != null)
//                Loglr.getInstance().getFirebase().logEvent(context.getString(R.string.FireBase_Event_CustomDialog_Fail), null);
//            return LoadingDialog;
            }
            else {
                val progressDialog: ProgressDialog = ProgressDialog(context)
                progressDialog.setMessage(context.getString(R.string.tumblrlogin_loading))
                return progressDialog
            }
        }
    }
}