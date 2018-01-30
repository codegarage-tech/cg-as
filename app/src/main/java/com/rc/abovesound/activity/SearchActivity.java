package com.rc.abovesound.activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.allattentionhere.fabulousfilter.AAH_FabulousFragment;
import com.rc.abovesound.model.Music;
import com.rc.abovesound.R;
import com.rc.abovesound.adapter.SearchListViewAdapter;
import com.rc.abovesound.fragment.FilterFragment;
import com.rc.abovesound.model.FilterData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Md. Rashadul Alam
 *         Email: rashed.droid@gmail.com
 */
public class SearchActivity extends AppCompatActivity implements AAH_FabulousFragment.Callbacks, AAH_FabulousFragment.AnimationListener {

    TextView tvTitle;
    ListView lvMusic;
    private ArrayMap<String, List<String>> applied_filters = new ArrayMap<>();

    FilterData mData;

    FilterFragment dialogFrag;
    FloatingActionButton fabFilter;
    List<Music> mList = new ArrayList<>();
    SearchListViewAdapter musicListViewAdapter;

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
        fabFilter = (FloatingActionButton) findViewById(R.id.fab_filter);
        fabFilter.setVisibility(View.VISIBLE);
        lvMusic = (ListView) findViewById(R.id.lv_music);

        mData = getFilterData();
        mList.addAll(mData.getAllMusics());

        musicListViewAdapter = new SearchListViewAdapter(SearchActivity.this);
        lvMusic.setAdapter(musicListViewAdapter);
        musicListViewAdapter.setData(new ArrayList<Music>(mList));

        fabFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialogFrag = FilterFragment.newInstance(getApplied_filters());
                dialogFrag.setParentFab(fabFilter);

                dialogFrag.show(getSupportFragmentManager(), dialogFrag.getTag());
            }
        });
    }

    public FilterData getData() {
        return mData;
    }

    public FilterData getFilterData() {

        List<Music> mList = new ArrayList<>();

        mList.add(new Music("Parbo na ami charte toke", "nice love song.", "Michigan", "Pop", "", "", "", "", "", "", "", "", ""));
        mList.add(new Music("I am India from Qaidi Band", "nice love song.", "Michigan", "Hip Hop", "", "", "", "", "", "", "", "", ""));
        mList.add(new Music("Kar ja Re Ya Mar Ja", "nice love song.", "Michigan", "Pop", "", "", "", "", "", "", "", "", ""));
        mList.add(new Music("Ek poloke valobese feleci", "nice love song.", "Michigan", "Pop", "", "", "", "", "", "", "", "", ""));
        mList.add(new Music("Ami to karor noi", "nice love song.", "Michigan", "Hip Hop", "", "", "", "", "", "", "", "", ""));
        mList.add(new Music("Ami bonno tor jonno", "nice love song.", "Michigan", "Pop", "", "", "", "", "", "", "", "", ""));

        return new FilterData(mList);
    }

    private void initZoneAction() {

    }

    public ArrayMap<String, List<String>> getApplied_filters() {
        return applied_filters;
    }

    @Override
    public void onResult(Object result) {
        Log.d("k9res", "onResult: " + result.toString());
        applied_filters = (ArrayMap<String, List<String>>) result;

        if (result.toString().equalsIgnoreCase("swiped_down")) {
            //do something or nothing
        } else {
            if (result != null) {
                ArrayMap<String, List<String>> appliedFilters = (ArrayMap<String, List<String>>) result;
                if (appliedFilters.size() != 0) {
                    List<Music> filteredList = mData.getAllMusics();
                    //iterate over arraymap
                    for (Map.Entry<String, List<String>> entry : appliedFilters.entrySet()) {
                        Log.d("k9res", "entry.key: " + entry.getKey());
                        switch (entry.getKey()) {
                            case "category":
                                filteredList = mData.getCategoryFilteredMusics(entry.getValue(), filteredList);
                                break;
                            case "state":
                                filteredList = mData.getStateFilteredMusics(entry.getValue(), filteredList);
                                break;
                        }
                    }
                    Log.d("k9res", "new size: " + filteredList.size());
                    mList.clear();
                    mList.addAll(filteredList);
                    musicListViewAdapter.setData(new ArrayList<Music>(mList));

                } else {
                    mList.addAll(mData.getAllMusics());
                    musicListViewAdapter.setData(new ArrayList<Music>(mList));
                }
            }
            //handle result
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (dialogFrag.isAdded()) {
            dialogFrag.dismiss();
            dialogFrag.show(getSupportFragmentManager(), dialogFrag.getTag());
        }
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
