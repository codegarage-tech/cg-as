package com.rc.abovesound.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.rc.abovesound.adapter.CommonSpinnerAdapter;
import com.rc.abovesound.model.Country;
import com.rc.abovesound.model.ResponseCountry;
import com.rc.abovesound.model.ResponseUserData;
import com.rc.abovesound.model.TimeZone;
import com.rc.abovesound.model.UserData;
import com.rc.abovesound.util.AllConstants;
import com.rc.abovesound.util.AllUrls;
import com.rc.abovesound.util.AppUtils;
import com.rc.abovesound.util.HttpRequestManager;
import com.reversecoder.library.event.OnSingleClickListener;
import com.reversecoder.library.network.NetworkManager;
import com.reversecoder.library.storage.SessionManager;
import com.rc.abovesound.R;
import com.rc.abovesound.model.City;

/**
 * @author Md. Rashadul Alam
 *         Email: rashed.droid@gmail.com
 */
public class ProfileActivity extends AppCompatActivity {

    private Button btnUpdateProfile;
    EditText edtFirstName, edtLastName, edtEmail, edtPassword, edtBio, edtFacebookId, edtTwitterId, edtYoutubeChannelId;
    Spinner spinnerCountry, spinnerZone, spinnerCity;
    ProgressDialog loadingDialog;
    DoUpdateProfile doUpdateUser;
    UserData user;
    String TAG = AppUtils.getTagName(ProfileActivity.class);
    CommonSpinnerAdapter spinnerCityAdapter, spinnerZoneAdapter, spinnerCountryAdapter;
    ResponseCountry wrapperCityWithCountryData;

    TextView tvTitle;
    ImageView ivBack;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initUpdateProfileUI();
        initUpdateProfileActions();
    }

    private void initUpdateProfileUI() {
        tvTitle = (TextView) findViewById(R.id.text_title);
        ivBack = (ImageView) findViewById(R.id.menu_hamburger);
        btnUpdateProfile = (Button) findViewById(R.id.btn_update_profile);
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

        tvTitle.setText("Profile");

        spinnerCountryAdapter = new CommonSpinnerAdapter(ProfileActivity.this, CommonSpinnerAdapter.ADAPTER_TYPE.COUNTRY);
        spinnerCountry.setAdapter(spinnerCountryAdapter);

        spinnerZoneAdapter = new CommonSpinnerAdapter(ProfileActivity.this, CommonSpinnerAdapter.ADAPTER_TYPE.TIME_ZONE);
        spinnerZone.setAdapter(spinnerZoneAdapter);

        spinnerCityAdapter = new CommonSpinnerAdapter(ProfileActivity.this, CommonSpinnerAdapter.ADAPTER_TYPE.CITY);
        spinnerCity.setAdapter(spinnerCityAdapter);

        if (!AppUtils.isNullOrEmpty(SessionManager.getStringSetting(ProfileActivity.this, AllConstants.SESSION_USER_DATA))) {
            Log.d(TAG, "Session data: " + SessionManager.getStringSetting(ProfileActivity.this, AllConstants.SESSION_USER_DATA));
            user = UserData.getResponseObject(SessionManager.getStringSetting(ProfileActivity.this, AllConstants.SESSION_USER_DATA), UserData.class);

            if (NetworkManager.isConnected(ProfileActivity.this)) {
                new GetCityWithCountry(ProfileActivity.this).execute();
            } else {
                if (AppUtils.isNullOrEmpty(SessionManager.getStringSetting(ProfileActivity.this, AllConstants.SESSION_CITY_WITH_COUNTRY))) {
                    SessionManager.setStringSetting(ProfileActivity.this, AllConstants.SESSION_CITY_WITH_COUNTRY, AllConstants.getDefaultCountryData());
                }

                wrapperCityWithCountryData = ResponseCountry.getResponseObject(SessionManager.getStringSetting(ProfileActivity.this, AllConstants.SESSION_CITY_WITH_COUNTRY), ResponseCountry.class);
                Log.d(TAG, "success response from session Object: " + wrapperCityWithCountryData.getData().size());

                setUserData();
            }
        }
    }

    private void initUpdateProfileActions() {

        ivBack.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                onBackPressed();
            }
        });

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

        btnUpdateProfile.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {

                String mId = "",
                        mEmail = edtEmail.getText().toString(),
                        mPassword = edtPassword.getText().toString(),
                        mFirstName = edtFirstName.getText().toString(),
                        mLastName = edtLastName.getText().toString(),
                        mCountry = (((Country) spinnerCountry.getSelectedItem()).getName().equalsIgnoreCase(getString(R.string.txt_default_country)) ? "" : ((Country) spinnerCountry.getSelectedItem()).getName()),
                        mCity = (((City) spinnerCity.getSelectedItem()).getName().equalsIgnoreCase(getString(R.string.txt_default_city)) ? "" : ((City) spinnerCity.getSelectedItem()).getName()),
                        mBio = edtBio.getText().toString(),
                        mFacebookId = edtFacebookId.getText().toString(),
                        mTwitterId = edtTwitterId.getText().toString(),
                        mYoutubeChannelId = edtYoutubeChannelId.getText().toString();

                if (!AppUtils.isNullOrEmpty(SessionManager.getStringSetting(ProfileActivity.this, AllConstants.SESSION_USER_DATA))) {
                    Log.d(TAG, "Session data: " + SessionManager.getStringSetting(ProfileActivity.this, AllConstants.SESSION_USER_DATA));
                    user = UserData.getResponseObject(SessionManager.getStringSetting(ProfileActivity.this, AllConstants.SESSION_USER_DATA), UserData.class);
                }
                mId = user.getId();
                Log.d(TAG, "User id: " + mId);

                if (mFirstName.equalsIgnoreCase("")) {
                    Toast.makeText(ProfileActivity.this, getResources().getString(R.string.toast_empty_first_name_field), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mLastName.equalsIgnoreCase("")) {
                    Toast.makeText(ProfileActivity.this, getResources().getString(R.string.toast_empty_last_name_field), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mCountry.equalsIgnoreCase("")) {
                    Toast.makeText(ProfileActivity.this, getResources().getString(R.string.toast_empty_country_field), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mCity.equalsIgnoreCase("")) {
                    Toast.makeText(ProfileActivity.this, getResources().getString(R.string.toast_empty_city_field), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mEmail.equalsIgnoreCase("")) {
                    Toast.makeText(ProfileActivity.this, getResources().getString(R.string.toast_empty_email_field), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mPassword.equalsIgnoreCase("")) {
                    Toast.makeText(ProfileActivity.this, getResources().getString(R.string.toast_empty_password_field), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!NetworkManager.isConnected(ProfileActivity.this)) {
                    Toast.makeText(ProfileActivity.this, getResources().getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();
                    return;
                }

                doUpdateUser = new DoUpdateProfile(ProfileActivity.this, mId, mEmail, mPassword, mFirstName, mLastName, mCity, mBio, mFacebookId, mTwitterId, mYoutubeChannelId);
                doUpdateUser.execute();
            }
        });
    }

    public class DoUpdateProfile extends AsyncTask<String, String, HttpRequestManager.HttpResponse> {

        private Context mContext;
        private String mId = "", mEmail = "", mPassword = "", mFirstName = "", mLastName = "", mCity = "", mBio = "", mFacebookId = "", mTwitterId = "", mYoutubeChannelID = "";

        public DoUpdateProfile(Context context, String userID, String email, String password, String firstName, String lastName, String city, String bio, String facebook, String twitter, String youtubeChannelID) {
            this.mContext = context;
            this.mId = userID;
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
            loadingDialog.setMessage(mContext.getResources().getString(
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
            HttpRequestManager.HttpResponse response = HttpRequestManager.doRestPostRequest(AllUrls.getUpdateUserUrl(), AllUrls.getUpdateUserParameters(mId, mEmail, mPassword, mFirstName, mLastName, mCity, mBio, mFacebookId, mTwitterId, mYoutubeChannelID), null);
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
                    SessionManager.setStringSetting(ProfileActivity.this, AllConstants.SESSION_USER_DATA, responseData.getUser_details().get(0).toString());

                    Toast.makeText(ProfileActivity.this, responseData.getMsg(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ProfileActivity.this, responseData.getMsg(), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(ProfileActivity.this, getResources().getString(R.string.toast_could_not_retrieve_info), Toast.LENGTH_SHORT).show();
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
                    wrapperCityWithCountryData = ResponseCountry.getResponseObject(SessionManager.getStringSetting(ProfileActivity.this, AllConstants.SESSION_CITY_WITH_COUNTRY), ResponseCountry.class);

                    setUserData();
                } else {
                    Toast.makeText(ProfileActivity.this, getResources().getString(R.string.toast_no_info_found), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(ProfileActivity.this, getResources().getString(R.string.toast_could_not_retrieve_info), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setUserData() {

        if (user != null) {
            edtFirstName.setText(user.getFirst_name());
            edtLastName.setText(user.getLast_name());
            edtEmail.setText(user.getEmail());
            edtPassword.setText(user.getPassword());

            wrapperCityWithCountryData = ResponseCountry.getResponseObject(SessionManager.getStringSetting(ProfileActivity.this, AllConstants.SESSION_CITY_WITH_COUNTRY), ResponseCountry.class);

            spinnerCountryAdapter.setData(wrapperCityWithCountryData.getData());
            spinnerCountry.setSelection(0);

            spinnerZoneAdapter.setData(wrapperCityWithCountryData.getTimezone(((Country) spinnerCountry.getSelectedItem()).getName()));
            if (!AppUtils.isNullOrEmpty(user.getCity())) {
                spinnerZone.setSelection(spinnerZoneAdapter.getItemPosition(wrapperCityWithCountryData.getTimezone(((Country) spinnerCountry.getSelectedItem()).getName(), user.getCity()).getName()));
            }

            spinnerCityAdapter.setData(wrapperCityWithCountryData.getCity(((Country) spinnerCountry.getSelectedItem()).getName(), ((TimeZone) spinnerZone.getSelectedItem()).getName()));
            if (!AppUtils.isNullOrEmpty(user.getCity())) {
                spinnerCity.setSelection(spinnerCityAdapter.getItemPosition(user.getCity()));
            }

            edtBio.setText(user.getBio());
            edtFacebookId.setText(user.getFacebook());
            edtTwitterId.setText(user.getTwitter());
            edtYoutubeChannelId.setText(user.getYoutube());
        }
    }
}
