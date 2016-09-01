package org.tndata.android.compass.tour;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.StringRes;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import org.tndata.android.compass.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;


/**
 * Created by isma on 8/30/16.
 */
public class Tour{
    private static Context sContext;

    private static Queue<CoachMark> mCoachMarks;
    private static Activity mActivity;
    private static CoachMarkView mCoachMarkView;


    public static void init(Context context){
        sContext = context.getApplicationContext();
    }

    public static List<Tooltip> getTooltipsFor(Section section){
        List<Tooltip> tooltips = new ArrayList<>();
        for (Tooltip tooltip:section.mTooltips){
            if (!hasBeenSeen(tooltip)){
                tooltips.add(tooltip);
            }
        }
        return tooltips;
    }

    private static boolean hasBeenSeen(Tooltip tooltip){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(sContext);
        return preferences.getBoolean(tooltip.getKey(), false);
    }

    public static void markSeen(Tooltip tooltip){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(sContext);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(tooltip.getKey(), true);
        editor.apply();
    }

    public static void display(Activity activity, Queue<CoachMark> marks){
        if (!marks.isEmpty()){
            mCoachMarks = marks;
            mActivity = activity;
            mCoachMarkView = new CoachMarkView(activity, new TourListener(){
                @Override
                public void onTooltipClick(Tooltip tooltip){
                    markSeen(tooltip);
                    next();
                }
            });
            mCoachMarkView.setCoachMark(marks.remove().setHost(activity));
            ViewGroup container = (ViewGroup)activity.findViewById(android.R.id.content);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT);
            container.addView(mCoachMarkView, params);
            container.invalidate();
        }
    }

    private static void next(){
        if (mCoachMarks.isEmpty()){
            ViewGroup container = (ViewGroup)mActivity.findViewById(android.R.id.content);
            container.removeView(mCoachMarkView);
        }
        else{
            mCoachMarkView.setCoachMark(mCoachMarks.remove().setHost(mActivity));
        }
    }

    public static void reset(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(sContext);
        SharedPreferences.Editor editor = preferences.edit();
        for (Tooltip tooltip:Tooltip.values()){
            editor.putBoolean(tooltip.getKey(), false);
        }
        editor.apply();
    }


    public enum Section{
        ORGANIZATION(Tooltip.ORG_GENERAL, Tooltip.ORG_SKIP),
        CATEGORY(Tooltip.CAT_GENERAL, Tooltip.CAT_SKIP),
        LIBRARY_PRE(Tooltip.LIB_GENERAL),
        GOAL(Tooltip.GOAL_GENERAL),
        LIBRARY_POST(Tooltip.LIB_GOAL_ADDED),
        FEED(Tooltip.FEED_GENERAL, Tooltip.FEED_UP_NEXT, Tooltip.FEED_PROGRESS, Tooltip.FEED_FAB),
        ACTION(Tooltip.ACTION_GOT_IT);


        private final Tooltip[] mTooltips;


        Section(Tooltip... tooltips){
            mTooltips = tooltips;
        }
    }


    interface TourListener{
        void onTooltipClick(Tooltip tooltip);
    }


    public enum Tooltip{
        ORG_GENERAL("org_general", R.string.tour_org_general_title, R.string.tour_org_general_description),
        ORG_SKIP("org_skip", R.string.tour_org_skip_title, R.string.tour_org_skip_description),
        CAT_GENERAL("cat_general", R.string.tour_cat_general_title, R.string.tour_cat_general_description),
        CAT_SKIP("cat_skip", R.string.tour_cat_skip_title, R.string.tour_cat_skip_description),
        LIB_GENERAL("lib_general", R.string.tour_lib_general_title, R.string.tour_lib_general_description),
        GOAL_GENERAL("goal_general", R.string.tour_goal_add_title, R.string.tour_goal_add_description),
        LIB_GOAL_ADDED("lib_goal_added", R.string.tour_lib_added_title, R.string.tour_lib_added_description),
        FEED_GENERAL("feed_general", R.string.tour_feed_general_title, R.string.tour_feed_general_description),
        FEED_UP_NEXT("feed_up_next", R.string.tour_feed_up_next_title, R.string.tour_feed_up_next_description),
        FEED_PROGRESS("feed_progress", R.string.tour_feed_progress_title, R.string.tour_feed_progress_description),
        FEED_FAB("feed_fab", R.string.tour_feed_fab_title, R.string.tour_feed_fab_description),
        ACTION_GOT_IT("action_got_it", R.string.tour_action_got_it_title, R.string.tour_action_got_it_description);


        private final String mKey;
        private final int mTitleId;
        private final int mDescriptionId;


        Tooltip(String key, @StringRes int titleId, @StringRes int descriptionId){
            mKey = key;
            mTitleId = titleId;
            mDescriptionId = descriptionId;
        }

        public String getKey(){
            return mKey;
        }

        public int getTitleId(){
            return mTitleId;
        }

        public int getDescriptionId(){
            return mDescriptionId;
        }

        public String getTitle(){
            return sContext.getString(mTitleId);
        }

        public String getDescription(){
            return sContext.getString(mDescriptionId);
        }
    }
}
