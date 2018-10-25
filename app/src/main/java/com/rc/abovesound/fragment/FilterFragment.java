package com.rc.abovesound.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.ArrayMap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.flexbox.FlexboxLayout;
import com.allattentionhere.fabulousfilter.AAH_FabulousFragment;
import com.rc.abovesound.R;
import com.rc.abovesound.activity.HomeActivity;
import com.rc.abovesound.service.MediaService;
import com.rc.abovesound.util.AppUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Md. Rashadul Alam
 */
public class FilterFragment extends AAH_FabulousFragment {

    static ArrayMap<String, List<String>> applied_filters = new ArrayMap<>();
    List<TextView> textviews = new ArrayList<>();

    TabLayout tabs_types;

    ImageButton imgbtn_apply;
    SectionsPagerAdapter mAdapter;

    private static String TAG = FilterFragment.class.getSimpleName();

    public static FilterFragment newInstance(ArrayMap<String, List<String>> data) {
        applied_filters = data;

        Log.d(TAG, "=====================Opening======================");
        for (Map.Entry<String, List<String>> entry : applied_filters.entrySet()) {
            Log.d(TAG, "saved filter key: " + entry.getKey());
            for (String s : entry.getValue()) {
                Log.d(TAG, "saved item: " + s);
            }
        }
        Log.d(TAG, "======================Opening=====================");

        FilterFragment mff = new FilterFragment();

        return mff;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

//    @Override
//    public void onCancel(DialogInterface dialog) {
//        super.onCancel(dialog);
//        clearAllSelectedData();
//    }

    @Override

    public void setupDialog(Dialog dialog, int style) {
        View contentView = View.inflate(getContext(), R.layout.filter_view, null);

        RelativeLayout rl_content = (RelativeLayout) contentView.findViewById(R.id.rl_content);
//        LinearLayout ll_buttons = (LinearLayout) contentView.findViewById(R.id.ll_buttons);
//        imgbtn_refresh = (ImageButton) contentView.findViewById(R.id.imgbtn_refresh);
        imgbtn_apply = (ImageButton) contentView.findViewById(R.id.imgbtn_apply);
        ViewPager vp_types = (ViewPager) contentView.findViewById(R.id.vp_types);
        tabs_types = (TabLayout) contentView.findViewById(R.id.tabs_types);

        imgbtn_apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (AppUtils.isServiceRunning(getActivity().getApplicationContext(), MediaService.class)) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.toast_please_stop_music_before_searching), Toast.LENGTH_SHORT).show();
                    return;
                }

                ArrayMap<String, List<String>> selectedItem = getSelectedData(new ArrayList<String>() {{
                    add("category");
                    add("state");
                }});

                Log.d(TAG, "=====================Saving======================");
                for (Map.Entry<String, List<String>> entry : selectedItem.entrySet()) {
                    Log.d(TAG, "saved filter key: " + entry.getKey());
                    for (String s : entry.getValue()) {
                        Log.d(TAG, "saved item: " + s);
                    }
                }
                Log.d(TAG, "======================Saving=====================");

                closeFilter(selectedItem);
            }
        });
//        imgbtn_refresh.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                clearAllSelectedData();
//            }
//        });

        mAdapter = new SectionsPagerAdapter();
        vp_types.setOffscreenPageLimit(4);
        vp_types.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        tabs_types.setupWithViewPager(vp_types);

        //params to set
        setAnimationDuration(600); //optional; default 500ms
        setPeekHeight(300); // optional; default 400dp
        setCallbacks((Callbacks) getActivity()); //optional; to get back result
        setAnimationListener((AnimationListener) getActivity()); //optional; to get animation callbacks
//        setViewgroupStatic(ll_buttons); // optional; layout to stick at bottom on slide
        setViewPager(vp_types); //optional; if you use viewpager that has scrollview
        setViewMain(rl_content); //necessary; main bottomsheet view
        setMainContentView(contentView); // necessary; call at end before super
        super.setupDialog(dialog, style); //call super at last
    }

    public class SectionsPagerAdapter extends PagerAdapter {

        @Override
        public Object instantiateItem(ViewGroup collection, int position) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.view_filters_sorters, collection, false);
            FlexboxLayout fbl = (FlexboxLayout) layout.findViewById(R.id.fbl);
//            LinearLayout ll_scroll = (LinearLayout) layout.findViewById(R.id.ll_scroll);
//            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (metrics.heightPixels-(104*metrics.density)));
//            ll_scroll.setLayoutParams(lp);
            switch (position) {
                case 0:
                    inflateLayoutWithFilters("category", fbl);
                    break;
                case 1:
                    inflateLayoutWithFilters("state", fbl);
                    break;
            }
            collection.addView(layout);
            return layout;

        }

        @Override
        public void destroyItem(ViewGroup collection, int position, Object view) {
            collection.removeView((View) view);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "   CATEGORY";
                case 1:
                    return "   STATE";
            }
            return "";
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

    }

    private void inflateLayoutWithFilters(final String filter_category, FlexboxLayout fbl) {
        List<String> keys = new ArrayList<>();
        switch (filter_category) {
            case "state":
                keys = ((HomeActivity) getActivity()).getStateKey();
                break;
            case "category":
                keys = ((HomeActivity) getActivity()).getCategoryKey();
                break;
        }

        for (int i = 0; i < keys.size(); i++) {
            View subchild = getActivity().getLayoutInflater().inflate(R.layout.single_chip, null);
            final TextView tv = ((TextView) subchild.findViewById(R.id.txt_title));
            tv.setText(keys.get(i));
            final int finalI = i;
            final List<String> finalKeys = keys;
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //For multiple selection
//                    multiChoiceSelectedMap(tv);

                    //For single selection
                    singleChoiceSelectedMap(filter_category, tv);
                }
            });
            try {
                Log.d(TAG, "key: " + filter_category + " |val:" + keys.get(finalI));
                Log.d(TAG, "applied_filters != null: " + (applied_filters != null));
                Log.d(TAG, "applied_filters.get(key) != null: " + (applied_filters.get(filter_category) != null));
                Log.d(TAG, "applied_filters.get(key).contains(keys.get(finalI)): " + (applied_filters.get(filter_category).contains(keys.get(finalI))));
            } catch (Exception e) {

            }
            if (applied_filters != null && applied_filters.get(filter_category) != null && applied_filters.get(filter_category).contains(keys.get(finalI))) {
                tv.setTag("selected");
                tv.setBackgroundResource(R.drawable.chip_selected);
                tv.setTextColor(ContextCompat.getColor(getContext(), android.R.color.white));
            } else {
                tv.setTag("unselected");
                tv.setBackgroundResource(R.drawable.chip_unselected);
                tv.setTextColor(ContextCompat.getColor(getContext(), R.color.filters_header));
            }
            textviews.add(tv);

            fbl.addView(subchild);
        }

    }

    private ArrayMap<String, List<String>> getSelectedData(ArrayList<String> filterKeys) {
        ArrayMap<String, List<String>> tempSelectedData = new ArrayMap<>();

        for (int i = 0; i < filterKeys.size(); i++) {
            List<String> keys = null;
            switch (filterKeys.get(i)) {
                case "state":
                    keys = ((HomeActivity) getActivity()).getStateKey();
                    break;
                case "category":
                    keys = ((HomeActivity) getActivity()).getCategoryKey();
                    break;
            }

            for (TextView textView : textviews) {
                for (int j = 0; j < keys.size(); j++) {
                    if (textView.getText().toString().equalsIgnoreCase(keys.get(j))) {
                        if (textView.getTag().equals("selected")) {
                            String currentFilterKey = filterKeys.get(i);
                            String currentKey = keys.get(j);
                            if (tempSelectedData.get(currentFilterKey) != null && !tempSelectedData.get(currentFilterKey).contains(currentKey)) {
                                tempSelectedData.get(currentFilterKey).add(currentKey);
                            } else {
                                List<String> temp = new ArrayList<>();
                                temp.add(currentKey);
                                tempSelectedData.put(currentFilterKey, temp);
                            }
                        }
                    }
                }
            }
        }

        return tempSelectedData;
    }

    private void multiChoiceSelectedMap(TextView textView) {
        if (textView.getTag().equals("selected")) {
            unSelectTextView(textView);
        } else {
            selectTextView(textView);
        }
    }

    private void singleChoiceSelectedMap(String filterKey, TextView textView) {
        if (filterKey.equalsIgnoreCase("state")) {
            clearAllSelectedData(filterKey);
        } else {
            clearAllSelectedDataExceptCurrent(filterKey, textView);
        }

        if (textView.getTag().equals("selected")) {
            unSelectTextView(textView);
        } else {
            selectTextView(textView);
        }
    }

    private void selectTextView(TextView textView) {
        textView.setTag("selected");
        textView.setBackgroundResource(R.drawable.chip_selected);
        textView.setTextColor(ContextCompat.getColor(getContext(), android.R.color.white));

        updateTextView(textView);
    }

    private void unSelectTextView(TextView textView) {
        textView.setTag("unselected");
        textView.setBackgroundResource(R.drawable.chip_unselected);
        textView.setTextColor(ContextCompat.getColor(getContext(), R.color.filters_header));

        updateTextView(textView);
    }

    private TextView getTextView(String value) {
        for (TextView tv : textviews) {
            if (tv.getText().toString().equalsIgnoreCase(value)) {
                return tv;
            }
        }
        return null;
    }

    private int getTextViewPosition(String value) {
        for (int i = 0; i < textviews.size(); i++) {
            if (textviews.get(i).getText().toString().equalsIgnoreCase(value)) {
                return i;
            }
        }
        return -1;
    }

    private TextView updateTextView(TextView textView) {
        int position = getTextViewPosition(textView.getText().toString());
        textviews.remove(position);
        textviews.add(position, textView);
        return textviews.get(position);
    }

    private void clearAllSelectedData() {
        for (TextView tv : textviews) {
            tv.setTag("unselected");
            tv.setBackgroundResource(R.drawable.chip_unselected);
            tv.setTextColor(ContextCompat.getColor(getContext(), R.color.filters_header));
        }
        applied_filters.clear();
    }

    private void clearAllSelectedDataExceptCurrent(String filterKey, TextView textView) {
        List<String> keys = null;
        switch (filterKey) {
            case "state":
                keys = ((HomeActivity) getActivity()).getStateKey();
                break;
            case "category":
                keys = ((HomeActivity) getActivity()).getCategoryKey();
                break;
        }

        List<TextView> tempTextViews = new ArrayList<>(textviews);

        for (TextView mTextView : tempTextViews) {
            for (int i = 0; i < keys.size(); i++) {
                if (mTextView.getText().toString().equalsIgnoreCase(keys.get(i))) {

                    if (!mTextView.getText().toString().equalsIgnoreCase(textView.getText().toString())) {

                        if (mTextView.getTag().equals("selected")) {

                            mTextView.setTag("unselected");
                            mTextView.setBackgroundResource(R.drawable.chip_unselected);
                            mTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.filters_header));

                            updateTextView(mTextView);
                        }
                    }
                }
            }
        }
    }

    private void clearAllSelectedData(String filterKey) {
        List<String> keys = null;
        switch (filterKey) {
            case "state":
                keys = ((HomeActivity) getActivity()).getStateKey();
                break;
            case "category":
                keys = ((HomeActivity) getActivity()).getCategoryKey();
                break;
        }

        List<TextView> tempTextViews = new ArrayList<>(textviews);

        for (TextView textView : tempTextViews) {
            for (int i = 0; i < keys.size(); i++) {
                if (textView.getText().toString().equalsIgnoreCase(keys.get(i))) {
                    if (textView.getTag().equals("selected")) {

                        textView.setTag("unselected");
                        textView.setBackgroundResource(R.drawable.chip_unselected);
                        textView.setTextColor(ContextCompat.getColor(getContext(), R.color.filters_header));

                        updateTextView(textView);
                    }
                }
            }
        }
    }
}
