package com.reversecoder.mh.activity;

import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.allattentionhere.fabulousfilter.AAH_FabulousFragment;
import com.reversecoder.mh.R;

import java.util.List;

/**
 * @author Md. Rashadul Alam
 *         Email: rashed.droid@gmail.com
 */
public class SearchActivity extends AppCompatActivity implements AAH_FabulousFragment.Callbacks, AAH_FabulousFragment.AnimationListener {

    TextView tvTitle;
    private ArrayMap<String, List<String>> applied_filters = new ArrayMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        initZoneUI();
        initZoneAction();
    }

    private void initZoneUI() {
        tvTitle = (TextView) findViewById(R.id.text_title);
        tvTitle.setText("Search");
    }

    private void initZoneAction() {

    }

    public ArrayMap<String, List<String>> getApplied_filters() {
        return applied_filters;
    }

    @Override
    public void onResult(Object result) {
        Log.d("k9res", "onResult: " + result.toString());
    }

    @Override
    public void onOpenAnimationStart() {
        Log.d("aah_animation", "onOpenAnimationStart: ");
    }

    @Override
    public void onOpenAnimationEnd() {
        Log.d("aah_animation", "onOpenAnimationEnd: ");

    }

    @Override
    public void onCloseAnimationStart() {
        Log.d("aah_animation", "onCloseAnimationStart: ");

    }

    @Override
    public void onCloseAnimationEnd() {
        Log.d("aah_animation", "onCloseAnimationEnd: ");

    }
}
