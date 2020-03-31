package com.iimswiss.corona;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

class Comm {
    private Context m_Context;


    /**
     * Use this function to resolve location locally if internet is not available. It returns nearest city to the current lat and long.
     * This function sets values of pd.locationInfo using lat and longs in pd.locationInfo
     */
    boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) m_Context.getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            NetworkInfo activeNetworkInfo = null;
            boolean blnResult;
            if (connectivityManager != null) {
                activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            }
            blnResult = activeNetworkInfo != null && activeNetworkInfo.isConnected();
            return blnResult;
        } catch (Exception e) {
            return false;
        }
    }
    String GetToken(){
        return "ccdyCGCET9s:APA91bHcivShsi5a8_GJiO3VrHA071pLfSJh8LJpmcn0JW3H8pVINHroSLTlzpjJ6v915mhGpqn6fHFcxSLth8GpCauOnVYA9gu73etzcuGbd2qkuf4GS";
    }

}
