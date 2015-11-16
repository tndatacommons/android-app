package org.tndata.android.compass.adapter.feed;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.MenuItem;
import android.view.View;

import org.tndata.android.compass.R;
import org.tndata.android.compass.model.Action;
import org.tndata.android.compass.model.Category;
import org.tndata.android.compass.service.ActionReportService;
import org.tndata.android.compass.ui.CompassPopupMenu;


/**
 * Created by isma on 11/4/15.
 */
class FeedUtil implements CompassPopupMenu.OnMenuItemClickListener{
    MainFeedAdapter mAdapter;

    private int mOpenPopup;


    FeedUtil(MainFeedAdapter adapter){
        mAdapter = adapter;
    }

    /**
     * Display the popup menu for a specific goal.
     *
     * @param anchor the view it should be anchored to.
     * @param position the position of the view.
     */
    void showActionPopup(View anchor, int position){
        mOpenPopup = position;
        CompassPopupMenu popup = CompassPopupMenu.newInstance(mAdapter.mContext, anchor);

        //The category of the selected action needs to be retrieved to determine which menu
        //  should be inflated.
        Action action;
        Category category;
        if (position == CardTypes.getUpNextPosition()){
            action = mAdapter.getDataHandler().getUpNext();
        }
        else{
            int actionPosition = mAdapter.getActionPosition(position);
            action = mAdapter.getDataHandler().getUpcoming().get(actionPosition);
        }
        category = mAdapter.getDataHandler().getActionCategory(action);

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
        }
        return true;
    }

    /**
     * Sends a request to the API to mark an action as complete.
     *
     * @param context a reference to the context.
     * @param action the action to be marked as complete.
     */
    void didIt(@NonNull Context context, @NonNull Action action){
        Intent completeAction = new Intent(context, ActionReportService.class)
                .putExtra(ActionReportService.MAPPING_ID_KEY, action.getMappingId())
                .putExtra(ActionReportService.STATE_KEY, ActionReportService.STATE_COMPLETED);
        context.startService(completeAction);
    }
}
