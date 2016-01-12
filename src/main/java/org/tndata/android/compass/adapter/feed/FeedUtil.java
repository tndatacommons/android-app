package org.tndata.android.compass.adapter.feed;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.MenuItem;
import android.view.View;

import org.tndata.android.compass.R;
import org.tndata.android.compass.model.Category;
import org.tndata.android.compass.model.UserAction;
import org.tndata.android.compass.service.ActionReportService;
import org.tndata.android.compass.ui.CompassPopupMenu;


/**
 * Utility class for the main feed.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
class FeedUtil implements CompassPopupMenu.OnMenuItemClickListener{
    MainFeedAdapter mAdapter;

    private int mOpenPopup;


    FeedUtil(MainFeedAdapter adapter){
        mAdapter = adapter;
    }

    /**
     * Display the popup menu for a specific action.
     *
     * @param anchor the view it should be anchored to.
     * @param position the position of the view.
     */
    void showActionPopup(View anchor, int position){
        mOpenPopup = position;
        CompassPopupMenu popup = CompassPopupMenu.newInstance(mAdapter.mContext, anchor);

        //The category of the selected action needs to be retrieved to determine which menu
        //  should be inflated.
        UserAction userAction;
        Category category;
        if (position == CardTypes.getUpNextPosition()){
            userAction = mAdapter.getDataHandler().getUpNext();
        }
        else{
            int actionPosition = mAdapter.getActionPosition(position);
            userAction = mAdapter.getDataHandler().getUpcoming().get(actionPosition);
        }
        category = mAdapter.getDataHandler().getActionCategory(userAction);

        //If the category couldn't be found or it is packaged, exclude removal options.
        if (category == null || category.isPackagedContent()){
            popup.getMenuInflater().inflate(R.menu.popup_action_packaged, popup.getMenu());
        }
        else{
            popup.getMenuInflater().inflate(R.menu.popup_action, popup.getMenu());
        }

        //Set the listener
        popup.setOnMenuItemClickListener(this);

        //Show the menu.
        popup.show();
    }

    /**
     * Display the popup menu for the suggestion.
     *
     * @param anchor the view it should be anchored to.
     */
    void showSuggestionPopup(View anchor){
        CompassPopupMenu popup = CompassPopupMenu.newInstance(mAdapter.mContext, anchor);
        popup.getMenuInflater().inflate(R.menu.popup_goal_suggestion, popup.getMenu());

        //Set the listener
        popup.setOnMenuItemClickListener(this);

        //Show the menu.
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item){
        switch (item.getItemId()){
            case R.id.popup_action_did_it:
                mAdapter.didIt(mOpenPopup);
                break;

            case R.id.popup_action_reschedule:
                mAdapter.reschedule(mOpenPopup);
                break;

            case R.id.popup_action_remove:
                mAdapter.remove(mOpenPopup);
                break;

            case R.id.popup_action_view_goal:
                mAdapter.viewGoal(mOpenPopup);
                break;

            case R.id.popup_goal_suggestion_refresh:
                mAdapter.refreshSuggestion();
                break;
        }
        return true;
    }

    /**
     * Sends a request to the API to mark an action as complete.
     *
     * @param context a reference to the context.
     * @param userAction the action to be marked as complete.
     */
    void didIt(@NonNull Context context, @NonNull UserAction userAction){
        Intent completeAction = new Intent(context, ActionReportService.class)
                .putExtra(ActionReportService.USER_ACTION_KEY, userAction)
                .putExtra(ActionReportService.STATE_KEY, ActionReportService.STATE_COMPLETED);
        context.startService(completeAction);
    }
}
