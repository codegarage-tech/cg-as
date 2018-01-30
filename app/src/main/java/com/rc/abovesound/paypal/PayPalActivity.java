package com.rc.abovesound.paypal;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.reversecoder.library.event.OnSingleClickListener;
import com.reversecoder.library.network.NetworkManager;
import com.reversecoder.library.storage.SessionManager;
import com.rc.abovesound.R;
import com.rc.abovesound.model.Music;
import com.rc.abovesound.model.ResponseUserBuyMusic;
import com.rc.abovesound.model.UserData;
import com.rc.abovesound.util.AllUrls;
import com.rc.abovesound.util.AppUtils;
import com.rc.abovesound.util.HttpRequestManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;

import static com.rc.abovesound.util.AllConstants.INTENT_KEY_PAYPAL_MUSIC_ITEM;
import static com.rc.abovesound.util.AllConstants.INTENT_KEY_PAYPAL_UPDATE_MUSIC_ITEM;
import static com.rc.abovesound.util.AllConstants.SESSION_USER_DATA;

public class PayPalActivity extends AppCompatActivity {

    public static final int PAYPAL_REQUEST_CODE = 123;
    private static PayPalConfiguration config;

    private Button buttonPay;
    private EditText editTextAmount;
    private Dialog dialog;
    TextView tvTitle;

    private String paymentAmount = "";
    private String paymentDetails = "";
    private String id = "";
    private String state = "";

    Music mMusic;
    private String TAG = PayPalActivity.class.getSimpleName();
    UserData mUser;

    int buyType = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paypal);

        initPayPalUI();

        initPayPalActions();
    }

    private void initPayPalUI() {
        Intent data = getIntent();
        mMusic = data.getParcelableExtra(INTENT_KEY_PAYPAL_MUSIC_ITEM);

        if (!AppUtils.isNullOrEmpty(SessionManager.getStringSetting(PayPalActivity.this, SESSION_USER_DATA))) {
            Log.d(TAG, "Session data: " + SessionManager.getStringSetting(PayPalActivity.this, SESSION_USER_DATA));
            mUser = UserData.getResponseObject(SessionManager.getStringSetting(PayPalActivity.this, SESSION_USER_DATA), UserData.class);
        }

        tvTitle = (TextView) findViewById(R.id.text_title);
        tvTitle.setText(getString(R.string.title_activity_paypal));
        buttonPay = (Button) findViewById(R.id.buttonPay);
        editTextAmount = (EditText) findViewById(R.id.editTextAmount);

        config = new PayPalConfiguration()
                .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
                .clientId(PayPalConfig.PAYPAL_CLIENT_ID);

        startPayPalService();

        showBuyTypeDialog();
    }

    private void setUserBuyTypeAmount(String amount) {
        editTextAmount.setText(amount);
        editTextAmount.setEnabled(false);
    }

    private void initPayPalActions() {
        buttonPay.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                if (editTextAmount.length() > 0) {
                    getPayment();
                } else {
                    Toast.makeText(PayPalActivity.this, "Please provide valid amount.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        stopPayPalService();
        super.onDestroy();
    }

    public void startPayPalService() {
        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        startService(intent);
    }

    public void stopPayPalService() {
        stopService(new Intent(this, PayPalService.class));
    }

    public void getPayment() {
        paymentAmount = editTextAmount.getText().toString();
        Log.d(TAG, "paymentAmount: " + paymentAmount);

        PayPalPayment payment = new PayPalPayment(
                new BigDecimal(String.valueOf(paymentAmount)),
                "USD", "Random item",
                PayPalPayment.PAYMENT_INTENT_SALE);

        Intent intent = new Intent(this, PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);
        startActivityForResult(intent, PAYPAL_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.d(TAG, "onActivityResult");
        if (requestCode == PAYPAL_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {

                PaymentConfirmation confirm = data
                        .getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);

                if (confirm != null) {
                    try {
                        paymentDetails = confirm.toJSONObject().toString(4);
                        Log.d(TAG, "paymentDetails" + paymentDetails);

                        JSONObject jsonObject = new JSONObject(paymentDetails);
                        Log.d(TAG, "jsonObject" + jsonObject + "");
                        JSONObject jo = jsonObject.getJSONObject("response");

                        id = jo.getString("id");
                        state = jo.getString("state");

                        Log.d(TAG, "jo" + jo + "");

                        showSuccessDialog();
                    } catch (JSONException e) {
                        Log.d(TAG, "JSONException" + "error", e);
                    }
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    Log.d(TAG, "RESULT_CANCELED" + "User Canceled");
                } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                    Log.d(TAG, "RESULT_EXTRAS_INVALID" + "Invalid Payment of configuration");
                }
            }
        }
    }

    private void showBuyTypeDialog() {
        // tf = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Nikosh.ttf");
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setTitle("");
        dialog.setContentView(R.layout.dialog_user_buy_type);
        dialog.setCancelable(false);
//        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_left_right;
        Button btnClose = (Button) dialog.getWindow().findViewById(R.id.btn_close);
        Button btnSuccessOk = (Button) dialog.getWindow().findViewById(R.id.btn_ok);

        final RadioGroup rgUserBuyType = (RadioGroup) dialog.getWindow().findViewById(R.id.rg_user_buy_type);
        final RadioButton rbUserBuyTypeOneTime = (RadioButton) dialog.getWindow().findViewById(R.id.rb_user_buy_one_time);
        final RadioButton rbUserBuyTypeLifeTime = (RadioButton) dialog.getWindow().findViewById(R.id.rb_user_buy_life_time);

        rbUserBuyTypeOneTime.setText("One time buy: " + "$" + mMusic.getPrice());
        rbUserBuyTypeLifeTime.setText("Life time buy: " + "$" + mMusic.getFull_paid_price());

        btnSuccessOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedId = rgUserBuyType.getCheckedRadioButtonId();

                if (selectedId == rbUserBuyTypeOneTime.getId()) {
                    buyType = 0;
                    setUserBuyTypeAmount(mMusic.getPrice());
                } else if (selectedId == rbUserBuyTypeLifeTime.getId()) {
                    buyType = 1;
                    setUserBuyTypeAmount(mMusic.getFull_paid_price());
                }
                dialog.dismiss();

            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedId = rgUserBuyType.getCheckedRadioButtonId();

                if (selectedId == rbUserBuyTypeOneTime.getId()) {
                    buyType = 0;
                    setUserBuyTypeAmount(mMusic.getPrice());
                } else if (selectedId == rbUserBuyTypeLifeTime.getId()) {
                    buyType = 1;
                    setUserBuyTypeAmount(mMusic.getFull_paid_price());
                }
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void showSuccessDialog() {
        // tf = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Nikosh.ttf");
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setTitle("");
        dialog.setContentView(R.layout.dialog_successful_payment_confirmation);
        dialog.setCancelable(false);
//        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_left_right;
        Button btnClose = (Button) dialog.getWindow().findViewById(R.id.btn_close);
        Button btnSuccessOk = (Button) dialog.getWindow().findViewById(R.id.btn_ok);
        TextView tvPaymentAmount = (TextView) dialog.getWindow().findViewById(R.id.txt_payment_amount);
        TextView tvPaymentStatus = (TextView) dialog.getWindow().findViewById(R.id.txt_payment_status);
        TextView tvPaymentId = (TextView) dialog.getWindow().findViewById(R.id.txt_payment_id);

        tvPaymentAmount.setText("$" + paymentAmount);
        tvPaymentStatus.setText(state);
        tvPaymentId.setText(id);

        btnSuccessOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                returnHome();

            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                returnHome();
            }
        });

        dialog.show();
    }

    private void returnHome() {
        if (state.equalsIgnoreCase("approved")) {
            Log.d(TAG, "Executing DoUserBuyMusic");
            Log.d(TAG, "user: " + mUser.getId());
            Log.d(TAG, "music: " + mMusic.getId());
            Log.d(TAG, "buyType: " + buyType);

            if (!NetworkManager.isConnected(PayPalActivity.this)) {
                Toast.makeText(PayPalActivity.this, getResources().getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();
                return;
            }

            new DoUserBuyMusic(PayPalActivity.this, mUser.getId(), mMusic.getId(), buyType+"").execute();
        }
    }

    public class DoUserBuyMusic extends AsyncTask<String, String, HttpRequestManager.HttpResponse> {

        private Context mContext;
        private String mUserId = "", mMusicId = "", mFullyPaid = "";

        public DoUserBuyMusic(Context mContext, String mUserId, String mMusicId, String mFullyPaid) {
            this.mContext = mContext;
            this.mUserId = mUserId;
            this.mMusicId = mMusicId;
            this.mFullyPaid = mFullyPaid;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected HttpRequestManager.HttpResponse doInBackground(String... params) {
            HttpRequestManager.HttpResponse response = HttpRequestManager.doRestPostRequest(AllUrls.getUserBuyUrl(), AllUrls.getUserBuyParameters(mUserId, mMusicId, mFullyPaid), null);
            return response;
        }

        @Override
        protected void onPostExecute(HttpRequestManager.HttpResponse result) {
            if (result.isSuccess() && !AppUtils.isNullOrEmpty(result.getResult().toString())) {
                Log.d(TAG, "success response: " + result.getResult().toString());
                ResponseUserBuyMusic responseData = ResponseUserBuyMusic.getResponseObject(result.getResult().toString(), ResponseUserBuyMusic.class);

                if (responseData.getStatus().equalsIgnoreCase("1")) {
                    Log.d(TAG, "success wrapper: " + responseData.getMsg());

                    Toast.makeText(PayPalActivity.this, responseData.getMsg(), Toast.LENGTH_SHORT).show();

                    mMusic.setIs_paid("0");
                    Intent data = new Intent();
                    data.putExtra(INTENT_KEY_PAYPAL_UPDATE_MUSIC_ITEM, mMusic);
                    setResult(RESULT_OK, data);
                    finish();
                } else {
                    Toast.makeText(PayPalActivity.this, responseData.getMsg(), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(PayPalActivity.this, getResources().getString(R.string.toast_could_not_retrieve_info), Toast.LENGTH_SHORT).show();
            }
        }
    }
}