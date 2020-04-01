package com.iimswiss.corona;

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.core.app.ActivityCompat;

import static android.content.Context.LOCATION_SERVICE;
import static java.util.UUID.randomUUID;

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

    String GetToken() {
        return "ccdyCGCET9s:APA91bHcivShsi5a8_GJiO3VrHA071pLfSJh8LJpmcn0JW3H8pVINHroSLTlzpjJ6v915mhGpqn6fHFcxSLth8GpCauOnVYA9gu73etzcuGbd2qkuf4GS";
    }

    /**
     * Check whether Location (GPS) service is enabled on device or not?
     *
     * @param mContext context
     * @return true or false
     */
    private boolean isGPSEnabled(Context mContext) {
        LocationManager locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
        return locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    LocationData getLocation() {
        LocationData locationData = new LocationData();
        Context context = AppContext.getAppContext();
        if (!isGPSEnabled(context)) {
            //gps is not enabled
            locationData.setBlnDataOK(false);
            return locationData;
        }
        final LocationManager locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            locationData.setBlnDataOK(false);
            return locationData;
        }
        Location lastLocation;
        if (locationManager != null) {
            if (locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER) == null) {
                lastLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            } else {
                lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }
            try {
                if (lastLocation == null) {
                    lastLocation = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
                }
                if (lastLocation != null) {
                    float latitude = (float) (lastLocation.getLatitude() / 10000000) * 10000000;
                    float longitude = ((float) (lastLocation.getLongitude() / 10000000)) * 10000000;
                    float speed = (lastLocation.getSpeed() / 1000); // converting meters to kilometers
                    float altitude = (float) lastLocation.getAltitude();
                    float bearing = 0;
                    if (lastLocation.hasBearing()) {
                        bearing = lastLocation.getBearing();
                    }
                    /*Location temp = new Location(LocationManager.GPS_PROVIDER);
                    temp.setLatitude(43.739562);
                    temp.setLongitude(-79.6153132);

                    float tempAngle = lastLocation.bearingTo(temp);
                    Log.d("temp ",String.valueOf(tempAngle));*/
                    if (speed <= 1 || speed >= 8) {
                        locationData.setBlnDataOK(false);
                        return locationData;
                    }
                    locationData.setFltAltitude(altitude);
                    locationData.setFltBearing(bearing);
                    locationData.setFltLatitude(latitude);
                    locationData.setFltLongitude(longitude);
                    locationData.setFltSpeed(speed);
                    //locationData.setIntLat(latitude);
                    locationData.setBlnDataOK(true);
                    return locationData;
                } else {
                    locationData.setBlnDataOK(false);
                    return locationData;
                }
            } catch (Exception e) {
                locationData.setBlnDataOK(false);
                return locationData;
            }

        } else {
            locationData.setBlnDataOK(false);
            return locationData;
        }
    }

    void SaveSettings(String strKey, String strValue) {
        SharedPreferences.Editor editor = AppContext.getAppContext().getSharedPreferences("CoronaFinderPrefs", Context.MODE_PRIVATE).edit();
        editor.putString(strKey, strKey);
        editor.apply();
    }

    String ReadSettings(String strKey) {
        //for getContext search for this file Android get context from anywhere without activity shared preferences
        return AppContext.getAppContext().getSharedPreferences("CoronaFinderPrefs", Context.MODE_PRIVATE).getString(strKey, "");
    }

    void SendLocations() {
        String strPhone = ReadSettings("phone");
        WebApiCall call = new WebApiCall(m_Context);
        WebApiCall.Parameters p[] = new WebApiCall.Parameters[4];

        p[0] = new WebApiCall.Parameters();
        p[0].setDataType(WebApiCall.DataTypes._string);
        p[0].setName("strToken");
        p[0].setValue(GetToken());

        p[1] = new WebApiCall.Parameters();
        p[1].setDataType(WebApiCall.DataTypes._string);
        p[1].setName("strEmail");
        // p[1].setValue(strEmail);

        p[2] = new WebApiCall.Parameters();
        p[2].setDataType(WebApiCall.DataTypes._string);
        p[2].setName("strPhone");
        //    p[2].setValue(strPhone);

        p[3] = new WebApiCall.Parameters();
        p[3].setDataType(WebApiCall.DataTypes._string);
        p[3].setName("strVerificationCode");
        //  p[3].setValue(strVerificationCode);

        HandlePostBack listener = new HandlePostBack();
        call.setMethodName("RegisterStep2");
        call.MakeCall(p);
        call.setOnPostExecuteListener(listener);
    }

    private class HandlePostBack implements WebApiCall.OnPostExecuteListener {
        @Override
        public void OnPostExecute(WebApiCall.WebApiReturn webApiReturn) {

            String result = webApiReturn.getResult();
            if (result.equals("f")) {

            } else if (result.equals("success")) {

            } else {
                // showMessage("Error!", result.split("\\*")[1]);
            }
        }
    }

    String GetTokenFromPhoneNumber(String strPhone) {
        try {
            String strToken = "";
            for (int i = 0; i < strPhone.length(); i++) {
                strToken += String.valueOf(strPhone.charAt(i)) + randomUUID().toString().replace("-", "").substring(0, 1);
                ;
            }
            //wrap around
            strToken = randomUUID().toString().replace("-", "").substring(0, 5) + strToken + randomUUID().toString().replace("-", "").substring(0, 5);
            return strToken;
        } catch (Exception e) {
            return "";
        }
    }

    class LocationData {
        float fltLatitude;
        float fltLongitude;
        float fltSpeed;
        float fltAltitude;
        float fltBearing = 0;
        int intLat;
        int intLng;
        boolean blnDataOK = false;

        public boolean isBlnDataOK() {
            return blnDataOK;
        }

        public void setBlnDataOK(boolean blnDataOK) {
            this.blnDataOK = blnDataOK;
        }

        public float getFltLatitude() {
            return fltLatitude;
        }

        public void setFltLatitude(float fltLatitude) {
            this.fltLatitude = fltLatitude;
        }

        public float getFltLongitude() {
            return fltLongitude;
        }

        public void setFltLongitude(float fltLongitude) {
            this.fltLongitude = fltLongitude;
        }

        public float getFltSpeed() {
            return fltSpeed;
        }

        public void setFltSpeed(float fltSpeed) {
            this.fltSpeed = fltSpeed;
        }

        public float getFltAltitude() {
            return fltAltitude;
        }

        public void setFltAltitude(float fltAltitude) {
            this.fltAltitude = fltAltitude;
        }

        public float getFltBearing() {
            return fltBearing;
        }

        public void setFltBearing(float fltBearing) {
            this.fltBearing = fltBearing;
        }

        public int getIntLat() {
            return intLat;
        }

        public void setIntLat(int intLat) {
            this.intLat = intLat;
        }

        public int getIntLng() {
            return intLng;
        }

        public void setIntLng(int intLng) {
            this.intLng = intLng;
        }
    }
}