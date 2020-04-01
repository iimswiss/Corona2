package com.iimswiss.corona;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private ProgressDialog progressDialog;
    private Context m_Context;
    Comm comm = new Comm();
    TextView phone;
    TextView email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        phone = findViewById(R.id.phone);
        email = findViewById(R.id.email);
        Button b = (Button) findViewById(R.id.butGo);
        String s = comm.ReadSettings("phone");
        if(!comm.ReadSettings("phone").equals("")) {
            Intent intent = new Intent(MainActivity.this, FirstWorker.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (phone.getText().toString().isEmpty()) {
                    showMessage("Error!", "Please enter your valid phone number.");
                    return;
                }

                if (email.getText().toString().isEmpty()) {
                    showMessage("Error!", "Please enter your email to continue.");
                    return;
                }
                if (!isValidEmail(email.getText().toString())) {
                    showMessage("Invalid Email!", "Please enter a valid email to continue.");
                    return;
                }
                progressDialog = ProgressDialog.show(MainActivity.this, "Working!", "Please wait...");
                progressDialog.show();
                RegisterStep1(phone.getText().toString(), email.getText().toString());
            }
        });
    }

    void showMessage(String title, String msg) {
        new android.app.AlertDialog.Builder(MainActivity.this)
                .setTitle(title)
                .setMessage(msg)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    void RegisterStep1(String strPhone, String strEmail) {
        WebApiCall call = new WebApiCall(m_Context);
        WebApiCall.Parameters p[] = new WebApiCall.Parameters[3];

        p[0] = new WebApiCall.Parameters();
        p[0].setDataType(WebApiCall.DataTypes._string);
        p[0].setName("strToken");
        p[0].setValue(comm.GetToken());

        p[1] = new WebApiCall.Parameters();
        p[1].setDataType(WebApiCall.DataTypes._string);
        p[1].setName("strEmail");
        p[1].setValue(strEmail);

        p[2] = new WebApiCall.Parameters();
        p[2].setDataType(WebApiCall.DataTypes._string);
        p[2].setName("strPhone");
        p[2].setValue(strPhone);

        HandlePostback listener = new HandlePostback();
        call.setMethodName("RegisterStep1");
        call.MakeCall(p);
        call.setOnPostExecuteListener(listener);
    }

    private boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private class HandlePostback implements WebApiCall.OnPostExecuteListener {

        @Override
        public void OnPostExecute(WebApiCall.WebApiReturn webApiReturn) {
            progressDialog.dismiss(); //dismiss it later on
            String result = webApiReturn.getResult();
            if (result.equals("f")) {
                showMessage("Error!", webApiReturn.getStatus());
            } else if (result.equals("messageSent")) {
                Intent intent = new Intent(MainActivity.this, VerifyCode.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("phone", phone.getText().toString());
                intent.putExtra("email", email.getText().toString());
                startActivity(intent);
            } else if (result.equals("alreadyRegistered")) {
                // showMessage("Success!","Oh! you are already registered. Lets go!");
                Intent intent = new Intent(MainActivity.this, FirstWorker.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                comm.SaveSettings("phone",phone.getText().toString());
                comm.SaveSettings("email",email.getText().toString());
                startActivity(intent);
            } else {
                showMessage("Error!", result.split("\\*")[1]);
            }
    /*        try {
                JSONArray jArray = new JSONArray(result);
                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject jObj = jArray.getJSONObject(i);
                    if (jObj.has("error")) {

                        return;
                    }
                }

            } catch (Exception e) {

            }*/

        }

    }
}
