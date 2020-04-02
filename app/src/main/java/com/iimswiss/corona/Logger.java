package com.iimswiss.corona;

import android.content.Intent;
import android.util.Log;

class Logger {
    private Comm comm = new Comm();
    void WriteLog(int intCode, String strLog) {
        WebApiCall call = new WebApiCall(AppContext.getAppContext());
        WebApiCall.Parameters p[] = new WebApiCall.Parameters[3];

        p[0] = new WebApiCall.Parameters();
        p[0].setDataType(WebApiCall.DataTypes._string);
        p[0].setName("strToken");
        p[0].setValue(comm.GetTokenFromPhoneNumber(comm.ReadSettings("phone")));

        p[1] = new WebApiCall.Parameters();
        p[1].setDataType(WebApiCall.DataTypes._int);
        p[1].setName("intCode");
        p[1].setValue(intCode);

        p[2] = new WebApiCall.Parameters();
        p[2].setDataType(WebApiCall.DataTypes._string);
        p[2].setName("strLog");
        p[2].setValue(strLog);

        HandlePostback listener = new HandlePostback();
        call.setMethodName("WriteDebugLog");
        call.MakeCall(p);
        call.setOnPostExecuteListener(listener);
    }

    private class HandlePostback implements WebApiCall.OnPostExecuteListener {
        @Override
        public void OnPostExecute(WebApiCall.WebApiReturn webApiReturn) {
//            String result = webApiReturn.getResult();
//            Log.d("",result);
        }
    }
}
