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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.rc.abovesound.adapter.CommonSpinnerAdapter;
import com.rc.abovesound.model.Country;
import com.rc.abovesound.model.ResponseCountry;
import com.rc.abovesound.model.ResponseUserData;
import com.rc.abovesound.model.TimeZone;
import com.rc.abovesound.util.AllConstants;
import com.rc.abovesound.util.AllUrls;
import com.rc.abovesound.util.AppUtils;
import com.rc.abovesound.util.HttpRequestManager;
import com.reversecoder.library.network.NetworkManager;
import com.reversecoder.library.storage.SessionManager;
import com.rc.abovesound.R;
import com.rc.abovesound.model.City;

/**
 * @author Md. Rashadul Alam
 *         Email: rashed.droid@gmail.com
 */
public class SignUpActivity extends AppCompatActivity {

    Button btnLogin, btnSignUp;
    EditText edtFirstName, edtLastName, edtEmail, edtPassword, edtBio, edtFacebookId, edtTwitterId, edtYoutubeChannelId;
    Spinner spinnerCountry, spinnerZone, spinnerCity;
    TextView tvTitle;
    ProgressDialog loadingDialog;
    DoSignUp doSignUpUser;
    String TAG = AppUtils.getTagName(SignUpActivity.class);
    CommonSpinnerAdapter spinnerCountryAdapter, spinnerZoneAdapter, spinnerCityAdapter;
    ResponseCountry wrapperCityWithCountryData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        initRegistrationUI();
        initRegistrationAction();
    }

    private void initRegistrationUI() {
        tvTitle = (TextView) findViewById(R.id.text_title);
        btnSignUp = (Button) findViewById(R.id.btn_sign_up);
        btnLogin = (Button) findViewById(R.id.btn_login);
        edtFirstName = (EditText) findViewById(R.id.edt_first_name);
        edtLastName = (EditText) findViewById(R.id.edt_last_name);
        edtEmail = (EditText) findViewById(R.id.edt_email);
        edtPassword = (EditText) findViewById(R.id.edt_password);
        spinnerCountry = (Spinner) findViewById(R.id.spinner_country);
        spinnerZone = (Spinner) findViewById(R.id.spinner_zone);
        spinnerCity = (Spinner) findViewById(R.id.spinner_city);
        edtBio = (EditText) findViewById(R.id.edt_bio);
        edtFacebookId = (EditText) findViewById(R.id.edt_facebook_id);
        edtTwitterId = (EditText) findViewById(R.id.edt_twitter_id);
        edtYoutubeChannelId = (EditText) findViewById(R.id.edt_youtube_channel_id);

        spinnerCountryAdapter = new CommonSpinnerAdapter(SignUpActivity.this, CommonSpinnerAdapter.ADAPTER_TYPE.COUNTRY);
        spinnerCountry.setAdapter(spinnerCountryAdapter);

        spinnerZoneAdapter = new CommonSpinnerAdapter(SignUpActivity.this, CommonSpinnerAdapter.ADAPTER_TYPE.TIME_ZONE);
        spinnerZone.setAdapter(spinnerZoneAdapter);

        spinnerCityAdapter = new CommonSpinnerAdapter(SignUpActivity.this, CommonSpinnerAdapter.ADAPTER_TYPE.CITY);
        spinnerCity.setAdapter(spinnerCityAdapter);

        if (NetworkManager.isConnected(SignUpActivity.this)) {
            new GetCityWithCountry(SignUpActivity.this).execute();
        } else {
            if (AppUtils.isNullOrEmpty(SessionManager.getStringSetting(SignUpActivity.this, AllConstants.SESSION_CITY_WITH_COUNTRY))) {
                SessionManager.setStringSetting(SignUpActivity.this, AllConstants.SESSION_CITY_WITH_COUNTRY, AllConstants.getDefaultCountryData());
            }

            setSpinnerData();
        }

        tvTitle.setText("Sign Up");
//        edtFirstName.setText("Md. Rashadul");
//        edtLastName.setText("Alam");
//        edtEmail.setText("rashed.droid@gmail.com");
//        edtPassword.setText("123456");

        setupToolBar();
    }

    private void setupToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
    }

    private void initRegistrationAction() {

        spinnerCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Country item = (Country) parent.getItemAtPosition(position);
//                Toast.makeText(SignUpActivity.this, item.getName(), Toast.LENGTH_LONG).show();

                spinnerZoneAdapter.setData(wrapperCityWithCountryData.getTimezone(item.getName()));
                spinnerCityAdapter.setData(wrapperCityWithCountryData.getCity(item.getName(), ((TimeZone) spinnerZone.getSelectedItem()).getName()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spinnerZone.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TimeZone item = (TimeZone) parent.getItemAtPosition(position);
//                Toast.makeText(SignUpActivity.this, item.getName(), Toast.LENGTH_LONG).show();

                spinnerCityAdapter.setData(wrapperCityWithCountryData.getCity(((Country) spinnerCountry.getSelectedItem()).getName(), item.getName()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spinnerCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                City item = (City) parent.getItemAtPosition(position);
//                Toast.makeText(SignUpActivity.this, item.getName(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String mEmail = edtEmail.getText().toString(),
                        mPassword = edtPassword.getText().toString(),
                        mFirstName = edtFirstName.getText().toString(),
                        mLastName = edtLastName.getText().toString(),
                        mCountry = (((Country) spinnerCountry.getSelectedItem()).getName().equalsIgnoreCase(getString(R.string.txt_default_country)) ? "" : ((Country) spinnerCountry.getSelectedItem()).getName()),
                        mCity = (((City) spinnerCity.getSelectedItem()).getName().equalsIgnoreCase(getString(R.string.txt_default_city)) ? "" : ((City) spinnerCity.getSelectedItem()).getName()),
                        mBio = edtBio.getText().toString(),
                        mFacebookId = edtFacebookId.getText().toString(),
                        mTwitterId = edtTwitterId.getText().toString(),
                        mYoutubeChannelId = edtYoutubeChannelId.getText().toString();

                if (mFirstName.equalsIgnoreCase("")) {
                    Toast.makeText(SignUpActivity.this, getResources().getString(R.string.toast_empty_first_name_field), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mLastName.equalsIgnoreCase("")) {
                    Toast.makeText(SignUpActivity.this, getResources().getString(R.string.toast_empty_last_name_field), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mCountry.equalsIgnoreCase("")) {
                    Toast.makeText(SignUpActivity.this, getResources().getString(R.string.toast_empty_country_field), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mCity.equalsIgnoreCase("")) {
                    Toast.makeText(SignUpActivity.this, getResources().getString(R.string.toast_empty_city_field), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mEmail.equalsIgnoreCase("")) {
                    Toast.makeText(SignUpActivity.this, getResources().getString(R.string.toast_empty_email_field), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mPassword.equalsIgnoreCase("")) {
                    Toast.makeText(SignUpActivity.this, getResources().getString(R.string.toast_empty_password_field), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!NetworkManager.isConnected(SignUpActivity.this)) {
                    Toast.makeText(SignUpActivity.this, getResources().getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();
                    return;
                }

                doSignUpUser = new DoSignUp(SignUpActivity.this, mEmail, mPassword, mFirstName, mLastName, mCity, mBio, mFacebookId, mTwitterId, mYoutubeChannelId);
                doSignUpUser.execute();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public class DoSignUp extends AsyncTask<String, String, HttpRequestManager.HttpResponse> {

        private Context mContext;
        private String mEmail = "", mPassword = "", mFirstName = "", mLastName = "", mCity = "", mBio = "", mFacebookId = "", mTwitterId = "", mYoutubeChannelID = "";

        public DoSignUp(Context context, String email, String password, String firstName, String lastName, String city, String bio, String facebook, String twitter, String youtubeChannelID) {
            this.mContext = context;
            this.mEmail = email;
            this.mPassword = password;
            this.mFirstName = firstName;
            this.mLastName = lastName;
            this.mCity = city;
            this.mBio = bio;
            this.mFacebookId = facebook;
            this.mTwitterId = twitter;
            this.mYoutubeChannelID = youtubeChannelID;
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
            HttpRequestManager.HttpResponse response = HttpRequestManager.doRestPostRequest(AllUrls.getSignUpUrl(), AllUrls.getSignUpParameters(mEmail, mPassword, mFirstName, mLastName, mCity, mBio, mFacebookId, mTwitterId, mYoutubeChannelID), null);
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
                    SessionManager.setStringSetting(SignUpActivity.this, AllConstants.SESSION_USER_DATA, responseData.getUser_details().get(0).toString());

                    Toast.makeText(SignUpActivity.this, responseData.getMsg(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SignUpActivity.this, responseData.getMsg(), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(SignUpActivity.this, getResources().getString(R.string.toast_could_not_retrieve_info), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class GetCityWithCountry extends AsyncTask<String, String, HttpRequestManager.HttpResponse> {

        private Context mContext;

        public GetCityWithCountry(Context context) {
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected HttpRequestManager.HttpResponse doInBackground(String... params) {
            HttpRequestManager.HttpResponse response = HttpRequestManager.doGetRequest(AllUrls.getAllCityWithCountryUrl());
            return response;
        }

        @Override
        protected void onPostExecute(HttpRequestManager.HttpResponse result) {
            if (result.isSuccess() && !AppUtils.isNullOrEmpty(result.getResult().toString())) {
                Log.d(TAG, "success response: " + result.getResult().toString());
                ResponseCountry responseData = ResponseCountry.getResponseObject(result.getResult().toString(), ResponseCountry.class);
                Log.d(TAG, "success response from object: " + responseData.toString());

                if ((responseData.getStatus().equalsIgnoreCase("1")) && (responseData.getData().size() > 0)) {
                    Log.d(TAG, "success response from cityWithCountry: " + responseData.toString());
                    SessionManager.setStringSetting(mContext, AllConstants.SESSION_CITY_WITH_COUNTRY, responseData.toString());
                    Log.d(TAG, "success response from session: " + SessionManager.getStringSetting(mContext, AllConstants.SESSION_CITY_WITH_COUNTRY));

                    setSpinnerData();
                } else {
                    Toast.makeText(SignUpActivity.this, getResources().getString(R.string.toast_no_info_found), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(SignUpActivity.this, getResources().getString(R.string.toast_could_not_retrieve_info), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setSpinnerData() {
        wrapperCityWithCountryData = ResponseCountry.getResponseObject(SessionManager.getStringSetting(SignUpActivity.this, AllConstants.SESSION_CITY_WITH_COUNTRY), ResponseCountry.class);

        spinnerCountryAdapter.setData(wrapperCityWithCountryData.getData());
        spinnerCountry.setSelection(0);

        spinnerZoneAdapter.setData(wrapperCityWithCountryData.getTimezone(((Country) spinnerCountry.getSelectedItem()).getName()));
        spinnerZone.setSelection(0);

        spinnerCityAdapter.setData(wrapperCityWithCountryData.getCity(((Country) spinnerCountry.getSelectedItem()).getName(), ((TimeZone) spinnerZone.getSelectedItem()).getName()));
        spinnerCity.setSelection(0);
    }
}
