package com.rc.abovesound.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.rc.abovesound.R;
import com.rc.abovesound.util.AllConstants;
import com.rc.abovesound.util.AppUtils;
import com.reversecoder.library.storage.SessionManager;
import com.reversecoder.permission.activity.PermissionListActivity;
import com.rodolfonavalon.shaperipplelibrary.ShapeRipple;
import com.rodolfonavalon.shaperipplelibrary.model.Circle;

/**
 * @author Md. Rashadul Alam
 *         Email: rashed.droid@gmail.com
 */
public class SplashActivity extends AppCompatActivity {

    SplashCountDownTimer splashCountDownTimer;
    private final long startTime = 4 * 1000;
    private final long interval = 1 * 1000;
    ShapeRipple ripple;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        initSplashUI();
    }

    private void initSplashUI() {
        ((TextView) findViewById(R.id.application_version)).setText("version: " + getString(R.string.app_version_name));
        ripple = (ShapeRipple) findViewById(R.id.background_ripple);
        ripple.setRippleShape(new Circle());
        ripple.setEnableColorTransition(true);
        ripple.setEnableSingleRipple(false);
        ripple.setEnableRandomPosition(true);
        ripple.setEnableRandomColor(true);
        ripple.setEnableStrokeStyle(false);

        ripple.setRippleDuration(2500);
        ripple.setRippleCount(10);
        ripple.setRippleMaximumRadius(184);

        splashCountDownTimer = new SplashCountDownTimer(startTime, interval);
        splashCountDownTimer.start();
    }

    public class SplashCountDownTimer extends CountDownTimer {
        public SplashCountDownTimer(long startTime, long interval) {
            super(startTime, interval);
        }

        @Override
        public void onFinish() {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Intent intent = new Intent(SplashActivity.this, PermissionListActivity.class);
                startActivityForResult(intent, PermissionListActivity.REQUEST_CODE_PERMISSIONS);
            } else {
                navigateMainActivity();
            }
        }

        @Override
        public void onTick(long millisUntilFinished) {
        }
    }

    private void navigateMainActivity() {
        Intent intent;
        if (SessionManager.getBooleanSetting(SplashActivity.this, AllConstants.SESSION_IS_USER_LOGGED_IN, false)) {
            if (AppUtils.isNullOrEmpty(SessionManager.getStringSetting(SplashActivity.this, AllConstants.SESSION_SELECTED_ZONE))) {
                intent = new Intent(SplashActivity.this, ZoneActivity.class);
            } else {
                intent = new Intent(SplashActivity.this, HomeActivity.class);
            }
        } else {
            intent = new Intent(SplashActivity.this, LoginActivity.class);
        }
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PermissionListActivity.REQUEST_CODE_PERMISSIONS) {
            if (resultCode == RESULT_OK) {
                navigateMainActivity();
            } else if (resultCode == RESULT_CANCELED) {
                finish();
            }
        }
    }
}
