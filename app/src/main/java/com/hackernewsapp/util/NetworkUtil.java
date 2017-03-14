package com.hackernewsapp.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * @author Tosin Onikute.
 */

public class NetworkUtil {

    // this is to check either connected or Wifi or Mobile data, NetworkUtil.isOnNetwork(this, ConnectivityManager.TYPE_WIFI)
    public static boolean isOnNetwork(Context context, int networkType) {
        boolean isConnected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        if (activeNetwork != null) {
            if (activeNetwork.getType() == networkType) {
                isConnected = true;
            }
        }
        return isConnected;
    }

    public static boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null) {
            // There are no active networks.
            return false;
        } else {
            return true;
        }
    }



}
