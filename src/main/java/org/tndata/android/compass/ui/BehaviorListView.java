package org.tndata.android.compass.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import org.tndata.android.compass.R;
import org.tndata.android.compass.model.Behavior;
import org.tndata.android.compass.model.Category;
import org.tndata.android.compass.util.ImageCache;

public class BehaviorListView extends LinearLayout {
    private ImageView mIconImageView;
    private ImageView mAddImageView;
    private TextView mTitleTextView;
    private Behavior mBehavior;
    private Category mCategory;
    private Context mContext;

    // TODO: Implement a GoalDetailsViewListener (similar to ActionCellView.ActionViewListener)
    // TODO: that way I can call methods on the fragment when an item from the popup menu is selected.

    public BehaviorListView(Context context) {
        this(context, null);
    }

    public BehaviorListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BehaviorListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initViews(context, attrs);
    }

    private void initViews(Context context, AttributeSet attrs) {
        mContext = context;
        View view = inflate(context, R.layout.view_behavior_item, this);

        mTitleTextView = (TextView) view.findViewById(R.id.view_behavior_textview);
        mIconImageView = (ImageView) view.findViewById(R.id.view_behavior_icon_imageview);
        mAddImageView = (ImageView) view.findViewById(R.id.view_behavior_add_imageview);
        mAddImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup();
            }
        });
        if (mBehavior != null) {
            updateUi();
        }
    }

    public void setBehavior(Behavior behavior, Category category) {
        mBehavior = behavior;
        mCategory = category;
        if (mTitleTextView != null) {
            updateUi();
        }
    }

    private void updateUi() {
        try {
            mTitleTextView.setText(mBehavior.getTitle());
            if (mBehavior.getIconUrl() != null
                    && !mBehavior.getIconUrl().isEmpty()) {
                ImageCache.instance(mContext).loadBitmap(mIconImageView,
                        mBehavior.getIconUrl(), false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showPopup() {
        //Creating the instance of PopupMenu
        PopupMenu popup = new PopupMenu(mContext, mAddImageView);
        //Inflating the Popup using xml file
        popup.getMenuInflater()
                .inflate(R.menu.menu_popup_chooser, popup.getMenu());

        //registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_popup_remove_item:
                        //deleteUserAction();
                        Log.d("BehaviorListView", "Remove Behavior... ");
                        break;
                    case R.id.menu_popup_edit_item:
                        //mCallback.fireActionPicker();
                        Log.d("BehaviorListView", "Edit Behavior Reminder....");
                        break;
                }
                return true;
            }
        });
        popup.show(); //showing popup menu
    }

    public Behavior getBehavior() {
        return mBehavior;
    }
}
