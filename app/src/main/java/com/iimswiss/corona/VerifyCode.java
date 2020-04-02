package com.iimswiss.corona;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class VerifyCode extends AppCompatActivity {
    Comm comm = new Comm();
    private Context m_Context;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_code);
        Button butVerify = findViewById(R.id.butVerify);
        butVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView tvCode = findViewById(R.id.txtCode);
                if (tvCode.getText().toString().isEmpty()) {
                    showMessage("Error!", "Please enter verification code that you received as text message.");
                    return;
                }
                Intent i = getIntent();
                String phone = i.getExtras().getString("phone");
                String email = i.getExtras().getString("email");
                progressDialog = ProgressDialog.show(VerifyCode.this, "Working!", "Please wait...");
                progressDialog.show();
                RegisterStep2(phone, email, tvCode.getText().toString());
            }
        });
    }

    void showMessage(String title, String msg) {
        new android.app.AlertDialog.Builder(VerifyCode.this)
                .setTitle(title)
                .setMessage(msg)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    void RegisterStep2(String strPhone, String strEmail, String strVerificationCode) {
        WebApiCall call = new WebApiCall(m_Context);
        WebApiCall.Parameters p[] = new WebApiCall.Parameters[4];

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

        p[3] = new WebApiCall.Parameters();
        p[3].setDataType(WebApiCall.DataTypes._string);
        p[3].setName("strVerificationCode");
        p[3].setValue(strVerificationCode);

        HandlePostback listener = new HandlePostback();
        call.setMethodName("RegisterStep2");
        call.MakeCall(p);
        call.setOnPostExecuteListener(listener);
    }

    private class HandlePostback implements WebApiCall.OnPostExecuteListener {
        @Override
        public void OnPostExecute(WebApiCall.WebApiReturn webApiReturn) {
            progressDialog.dismiss();
            String result = webApiReturn.getResult();
            if (result.equals("f")) {
                showMessage("Error!", webApiReturn.getStatus());
            } else if (result.equals("success")) {
                Intent i = getIntent();
                String phone = i.getExtras().getString("phone");
                String email = i.getExtras().getString("email");
                comm.SaveSettings("phone",phone);
                comm.SaveSettings("email",email);
                Intent intent = new Intent(VerifyCode.this, FirstWorker.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
            } else {
                showMessage("Error!", result.split("\\*")[1]);
            }
        }
    }
}
