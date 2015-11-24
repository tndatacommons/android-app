package org.tndata.android.compass.adapter.feed;

import android.view.View;
import android.widget.TextView;

import org.tndata.android.compass.R;


/**
 * Created by isma on 11/18/15.
 */
public class GoalSuggestionHolder extends MainFeedViewHolder implements View.OnClickListener{
    TextView mTitle;


    public GoalSuggestionHolder(MainFeedAdapter adapter, View rootView){
        super(adapter, rootView);

        mTitle = (TextView)rootView.findViewById(R.id.goal_suggestion_title);
        rootView.findViewById(R.id.goal_suggestion_overflow).setOnClickListener(this);
        rootView.findViewById(R.id.goal_suggestion_later).setOnClickListener(this);
        rootView.findViewById(R.id.goal_suggestion_show_me).setOnClickListener(this);
    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.goal_suggestion_later:
                mAdapter.dismissSuggestion();
                break;

            case R.id.goal_suggestion_show_me:
                mAdapter.viewSuggestion();
                break;

            case R.id.goal_suggestion_overflow:
                mAdapter.showSuggestionPopup(view);
        }
    }
}
