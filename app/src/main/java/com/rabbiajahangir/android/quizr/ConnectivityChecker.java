package com.rabbiajahangir.android.quizr;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.widget.Toast;

/**
 * Created by Jahangir on 11/23/2015.
 */
public class ConnectivityChecker {

    public static boolean checkConnectivity(View view, Context c) {
        ConnectivityManager connMgr = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        return false;
    }

    public static void makeNotConnectedToast(Context context) {
        Toast.makeText(context, R.string.internet_not_connected, Toast.LENGTH_SHORT).show();
    }

}
