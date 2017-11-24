package com.reversecoder.mh.fragment;

import android.app.Dialog;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.ArrayMap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.allattentionhere.fabulousfilter.AAH_FabulousFragment;
import com.google.android.flexbox.FlexboxLayout;
import com.reversecoder.mh.R;
import com.reversecoder.mh.activity.SearchActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by krupenghetiya on 23/06/17.
 */

public class FilterFragment extends AAH_FabulousFragment {

    static ArrayMap<String, List<String>> applied_filters = new ArrayMap<>();
    List<TextView> textviews = new ArrayList<>();

    TabLayout tabs_types;

    ImageButton imgbtn_refresh, imgbtn_apply;
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
//        refreshAllSelectedData();
//    }

    @Override

    public void setupDialog(Dialog dialog, int style) {
        View contentView = View.inflate(getContext(), R.layout.filter_view, null);

        RelativeLayout rl_content = (RelativeLayout) contentView.findViewById(R.id.rl_content);
        LinearLayout ll_buttons = (LinearLayout) contentView.findViewById(R.id.ll_buttons);
        imgbtn_refresh = (ImageButton) contentView.findViewById(R.id.imgbtn_refresh);
        imgbtn_apply = (ImageButton) contentView.findViewById(R.id.imgbtn_apply);
        ViewPager vp_types = (ViewPager) contentView.findViewById(R.id.vp_types);
        tabs_types = (TabLayout) contentView.findViewById(R.id.tabs_types);

        imgbtn_apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        imgbtn_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshAllSelectedData();
            }
        });

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
        setViewgroupStatic(ll_buttons); // optional; layout to stick at bottom on slide
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
                keys = ((SearchActivity) getActivity()).getData().getUniqueStateKeys();
                break;
            case "category":
                keys = ((SearchActivity) getActivity()).getData().getUniqueCategoryKeys();
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
                    if (tv.getTag() != null && tv.getTag().equals("selected")) {
                        removeFromMultiSelectedMap(filter_category, finalKeys.get(finalI), tv);
                    } else {
                        addToMultiSelectedMap(filter_category, finalKeys.get(finalI), tv);
                    }

//                    //For single selection
//                    singleChoiseSelectedMap(filter_category, finalKeys.get(finalI), tv);
                }
            });
            try {
                Log.d("k9res", "key: " + filter_category + " |val:" + keys.get(finalI));
                Log.d("k9res", "applied_filters != null: " + (applied_filters != null));
                Log.d("k9res", "applied_filters.get(key) != null: " + (applied_filters.get(filter_category) != null));
                Log.d("k9res", "applied_filters.get(key).contains(keys.get(finalI)): " + (applied_filters.get(filter_category).contains(keys.get(finalI))));
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
                    keys = ((SearchActivity) getActivity()).getData().getUniqueStateKeys();
                    break;
                case "category":
                    keys = ((SearchActivity) getActivity()).getData().getUniqueCategoryKeys();
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

//    private void singleChoiseSelectedMap(String key, String value, TextView textView) {
//
//        refreshAllSelectedData(key);
//
//        TextView updatedTextView;
//        if (textView.getTag() != null && textView.getTag().equals("selected")) {
//            updatedTextView = removeFromMultiSelectedMap(key, value, textView);
//        } else {
//            updatedTextView = addToMultiSelectedMap(key, value, textView);
//        }
//        updateTextView(updatedTextView);
//    }

    private void addToMultiSelectedMap(String key, String value, TextView textView) {
//        if (applied_filters.get(key) != null && !applied_filters.get(key).contains(value)) {
//            applied_filters.get(key).add(value);
//        } else {
//            List<String> temp = new ArrayList<>();
//            temp.add(value);
//            applied_filters.put(key, temp);
//        }

        textView.setTag("selected");
        textView.setBackgroundResource(R.drawable.chip_selected);
        textView.setTextColor(ContextCompat.getColor(getContext(), android.R.color.white));

        updateTextView(textView);
    }

    private void removeFromMultiSelectedMap(String key, String value, TextView textView) {
//        if (applied_filters.get(key) != null) {
//            if (applied_filters.get(key).size() == 1) {
//                applied_filters.remove(key);
//            } else {
//                applied_filters.get(key).remove(value);
//            }

        textView.setTag("unselected");
        textView.setBackgroundResource(R.drawable.chip_unselected);
        textView.setTextColor(ContextCompat.getColor(getContext(), R.color.filters_header));

        updateTextView(textView);

//        }
    }

    /*private void addToSingleSelectedMap(String key, String value, TextView textView) {

        //Remove all previous selected data
        List<String> values = null;
        switch (key) {
            case "state":
                values = ((SearchActivity) getActivity()).getData().getUniqueStateKeys();
                break;
            case "category":
                values = ((SearchActivity) getActivity()).getData().getUniqueCategoryKeys();
                break;
        }

        for (int i = 0; i < values.size(); i++) {
            for (TextView tv : textviews) {
                if (tv.getText().toString().equalsIgnoreCase(values.get(i)) && !tv.getText().toString().equalsIgnoreCase(textView.getText().toString())) {
                    if (tv.getTag().equals("selected")) {
                        removeFromMultiSelectedMap(key, values.get(i), tv);
                    }
                }
            }
        }

        if (textView.getTag().equals("selected")) {
            removeFromMultiSelectedMap(key, value, textView);
        } else {
            addToMultiSelectedMap(key, value, textView);
        }

//        for (int i = 0; i < values.size(); i++) {
//            for (TextView tv : textviews) {
//                if (tv.getText().toString().equalsIgnoreCase(values.get(i))) {
//                    Log.d(TAG, "data matched");
////                    if (tv.getTag() != null && tv.getTag().equals("selected")) {
////                        Log.d(TAG, "data selected");
//                    removeFromMultiSelectedMap(key, tv.getText().toString(), tv);
////                    }
//                }
//            }
//        }
//
//        //Input current selected data
////        if (textView.getTag() != null && textView.getTag().equals("selected")) {
////            removeFromMultiSelectedMap(key, value, textView);
////        } else {
//        addToMultiSelectedMap(key, value, textView);
////        }

//        Log.d(TAG, "=========================after============================");
//        if (applied_filters.get(key) != null) {
//            Log.d(TAG, "selected date size: " + applied_filters.get(key).size() + "");
//            for (int i = 0; i < applied_filters.get(key).size(); i++) {
//                Log.d(TAG, "selected data " + i + " " + applied_filters.get(key).get(i));
//            }
//        }
//        Log.d(TAG, "==========================================================");
    }*/

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

    private void updateTextView(TextView textView) {
        int position = getTextViewPosition(textView.getText().toString());
        textviews.remove(position);
        textviews.add(position, textView);

//        mAdapter.notifyDataSetChanged();
    }
//
//    private void refreshAllSelectedData(String key) {
//        for (TextView tv : textviews) {
//            if (tv.getTag().equals("selected")) {
//                TextView updatedTextView = removeFromMultiSelectedMap(key, tv.getText().toString(), tv);
//
//                updateTextView(updatedTextView);
//            }
//        }
//    }
//
//    private void refreshAllSelectedData(String key, String value) {
//        for (TextView tv : textviews) {
//            if (value.equalsIgnoreCase(tv.getText().toString())) {
//                if (tv.getTag().equals("selected")) {
//
//                    TextView updatedTextView = removeFromMultiSelectedMap(key, value, tv);
//
//                    updateTextView(updatedTextView);
//                }
//            }
//        }
//    }

    private void refreshAllSelectedData() {
        for (TextView tv : textviews) {
            tv.setTag("unselected");
            tv.setBackgroundResource(R.drawable.chip_unselected);
            tv.setTextColor(ContextCompat.getColor(getContext(), R.color.filters_header));
        }
        applied_filters.clear();
    }
}
