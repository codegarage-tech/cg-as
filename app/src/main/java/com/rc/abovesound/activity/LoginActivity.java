package com.rc.abovesound.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.rc.abovesound.util.AppUtils;
import com.reversecoder.library.network.NetworkManager;
import com.reversecoder.library.storage.SessionManager;
import com.rc.abovesound.R;
import com.rc.abovesound.model.ResponseUserData;
import com.rc.abovesound.util.AllUrls;
import com.rc.abovesound.util.HttpRequestManager;

import static com.rc.abovesound.util.AllConstants.SESSION_IS_USER_LOGGED_IN;
import static com.rc.abovesound.util.AllConstants.SESSION_USER_DATA;

/**
 * @author Md. Rashadul Alam
 *         Email: rashed.droid@gmail.com
 */
public class LoginActivity extends AppCompatActivity {

    Button btnSignIn, btnRegistration;
    EditText edtEmail, edtPassword;
    TextView tvTitle;
    ProgressDialog loadingDialog;
    DoLogin doLoginUser;
    String TAG = AppUtils.getTagName(LoginActivity.class);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        initLoginUI();
        initLoginAction();
    }

    private void setupToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
    }

    private void initLoginUI() {
        btnSignIn = (Button) findViewById(R.id.btn_sign_in);
        btnRegistration = (Button) findViewById(R.id.btn_registration);
        edtEmail = (EditText) findViewById(R.id.edt_email);
        edtPassword = (EditText) findViewById(R.id.edt_password);
        tvTitle = (TextView) findViewById(R.id.text_title);

        tvTitle.setText("Welcome");

//        edtEmail.setText("rashed.droid@gmail.com");
//        edtPassword.setText("123456");

        setupToolBar();
    }

    private void initLoginAction() {
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mEmail = edtEmail.getText().toString(), mPassword = edtPassword.getText().toString();

                if (mEmail.equalsIgnoreCase("")) {
                    Toast.makeText(LoginActivity.this, getResources().getString(R.string.toast_empty_email_field), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mPassword.equalsIgnoreCase("")) {
                    Toast.makeText(LoginActivity.this, getResources().getString(R.string.toast_empty_password_field), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!NetworkManager.isConnected(LoginActivity.this)) {
                    Toast.makeText(LoginActivity.this, getResources().getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();
                    return;
                }

                doLoginUser = new DoLogin(LoginActivity.this, mEmail, mPassword);
                doLoginUser.execute();
            }
        });

        btnRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public class DoLogin extends AsyncTask<String, String, HttpRequestManager.HttpResponse> {

        private Context mContext;
        private String mEmail = "", mPassword = "";

        public DoLogin(Context context, String email, String password) {
            mContext = context;
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected void onPreExecute() {
            loadingDialog = new ProgressDialog(mContext);
            loadingDialog.setMessage(getResources().getString(
                    R.string.dialog_loading));
            loadingDialog.setIndeterminate(false);
            loadingDialog.setCancelable(true);
            loadingDialog.setCanceledOnTouchOutside(false);
            loadingDialog.show();
            loadingDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface arg0) {
                    if (loadingDialog != null
                            && loadingDialog.isShowing()) {
                        loadingDialog.dismiss();
                    }
                }
            });
        }

        @Override
        protected HttpRequestManager.HttpResponse doInBackground(String... params) {
            HttpRequestManager.HttpResponse response = HttpRequestManager.doRestPostRequest(AllUrls.getLoginUrl(), AllUrls.getLoginParameters(mEmail, mPassword), null);
            return response;
        }

        @Override
        protected void onPostExecute(HttpRequestManager.HttpResponse result) {

            if (loadingDialog != null
                    && loadingDialog.isShowing()) {
                loadingDialog.dismiss();
            }

            if (result.isSuccess() && !AppUtils.isNullOrEmpty(result.getResult().toString())) {
                Log.d(TAG, "success response: " + result.getResult().toString());
                ResponseUserData responseData = ResponseUserData.getResponseObject(result.getResult().toString(), ResponseUserData.class);

                if ((responseData.getStatus().equalsIgnoreCase("1")) && (responseData.getUser_details().size() == 1)) {
                    Log.d(TAG, "success wrapper: " + responseData.getUser_details().get(0).toString());
                    SessionManager.setStringSetting(LoginActivity.this, SESSION_USER_DATA, responseData.getUser_details().get(0).toString());
                    SessionManager.setBooleanSetting(LoginActivity.this, SESSION_IS_USER_LOGGED_IN, true);

                    Intent intent = new Intent(LoginActivity.this, ZoneActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, responseData.getMsg(), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(LoginActivity.this, getResources().getString(R.string.toast_could_not_retrieve_info), Toast.LENGTH_SHORT).show();
            }
        }
    }
}