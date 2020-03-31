package com.iimswiss.corona;

import android.content.Context;
import android.content.pm.PackageManager;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import android.os.AsyncTask;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.Date;

public class WebApiCall {
    private String MethodName;
    private OnPostExecuteListener OnDone = new OnPostExecuteListener() {
        @Override
        public void OnPostExecute(WebApiReturn webApiReturn) {

        }
    };

    void setMethodName(String methodName) {
        MethodName = methodName;
    }
    private Context m_Context;

    void MakeCall(Parameters[] params) {
        new CallWebAPI().execute(params);
    }

    public WebApiCall(Context context) {
        this.m_Context = context;
    }

    void setOnPostExecuteListener(OnPostExecuteListener listener) {
        OnDone = listener;
    }

    private class CallWebAPI extends AsyncTask<Parameters, Boolean, WebApiReturn> {
        private String SOAP_ACTION = "http://tempuri.org/" + MethodName;
        private String NAMESPACE = "http://tempuri.org/";

        @Override
        public WebApiReturn doInBackground(Parameters... params) {
            //Initialize soap request + add parameters
            SoapObject request = new SoapObject(NAMESPACE, MethodName);
            WebApiReturn webApiReturn = new WebApiReturn();
            try {
                if (m_Context != null) {
                    Comm comm = new Comm();
                    if (!comm.isNetworkAvailable()) {
                        webApiReturn.setStatus("f");
                        webApiReturn.setResult("Internet is not available.");
                        return webApiReturn;
                    }
                }
            } catch (Exception ignored) {

            }
            if (MethodName.equals("")) {
                webApiReturn.setStatus("f");
                webApiReturn.setResult("Method name is not supplied.");
                return webApiReturn;
            }
            int i = 0;
            for (Parameters param : params) {
                i++;
                try {
                    if (param != null) {
                        PropertyInfo p = new PropertyInfo();
                        p.setName(param.getName());
                        p.setValue(param.getValue());
                        switch (param.getDataType()) {
                            case DataTypes._int: {
                                p.setType(int.class);
                                break;
                            }
                            case DataTypes._date: {
                                p.setType(Date.class);
                                break;
                            }
                            case DataTypes._double: {
                                p.setType(Double.class);
                                break;
                            }
                            default: {
                                p.setType(String.class);

                            }
                        }
                        request.addProperty(p);
                    }
                } catch (Exception e) {
                    webApiReturn.setStatus("f");
                    webApiReturn.setResult(e.getMessage());
                    return webApiReturn;
                }
            }
            //Declare the version of the SOAP request
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;
            try {
                HttpTransportSE androidHttpTransport = new HttpTransportSE("https://corona.iimswiss.com/corona.asmx?wsdl");
                try {
                    //It is wrapped in try catch because on low internet speed it troughs java.protocol error
                    // but call to function actually has been made successfully.
                    androidHttpTransport.call(SOAP_ACTION, envelope);
                } catch (Exception ex) {
                    webApiReturn.setStatus("f");
                    webApiReturn.setResult(ex.getMessage());
                    return webApiReturn;
                }
                SoapObject result = (SoapObject) envelope.bodyIn;
                if (result != null) {
                    webApiReturn.setStatus("s");
                    webApiReturn.setResult(result.getProperty(0).toString());
                } else {
                    webApiReturn.setStatus("f");
                    webApiReturn.setResult("No value was returned by this call");
                }
                webApiReturn.setParameters(params);//Just to return parameters back in case handle call back function requires these again.
            } catch (Exception e) {
                webApiReturn.setStatus("f");
                webApiReturn.setResult(e.getMessage());
            }
            return webApiReturn;
        }

        protected void onPostExecute(WebApiReturn result) {
            if (result == null) result = new WebApiReturn();
            OnDone.OnPostExecute(result);
        }
    }

    static class DataTypes {
        final static String _string = "string";
        final static String _int = "int";
        final static String _double = "double";
        final static String _date = "date";
    }

    static class Parameters {
        String Name;
        String Value;
        String DataType;

        String getName() {
            return Name;
        }

        void setName(String name) {
            Name = name;
        }

        String getValue() {
            return Value;
        }

        void setValue(String value) {
            Value = value;
        }

        String getDataType() {
            return DataType;
        }

        void setDataType(String dataType) {
            DataType = dataType;
        }
    }

    interface OnPostExecuteListener {
        void OnPostExecute(WebApiReturn webApiReturn);
    }

    /**
     * Returns result returned by onPostExecute handler of CallWebAPI. CallWebAPI is called from MakeCall function.
     */
    static class WebApiReturn {
        String status;
        String result;
        Parameters[] parameters;

        /**
         * Use this function to get parameters back that were passed to MakeCall function.
         *
         * @return Returns array of type Parameters that was passed to MakeCall function.
         */
        Parameters[] getParameters() {
            return parameters;
        }

        void setParameters(Parameters[] params) {
            this.parameters = params;
        }

        String getStatus() {
            return status;
        }

        String getResult() {
            return result;
        }

        void setResult(String result) {
            this.result = result;
        }

        public void setStatus(String status) {
            this.status = status;
        }

    }
}
abstract class _Context extends Context {
    Context context;

    public _Context() {
        try {
            context = createPackageContext(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            context = null;
        }
    }
    Context getContext() {
        return context;
    }
}