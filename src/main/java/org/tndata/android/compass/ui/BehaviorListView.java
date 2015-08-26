package org.tndata.android.compass.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.tndata.android.compass.R;
import org.tndata.android.compass.model.Behavior;
import org.tndata.android.compass.model.Category;
import org.tndata.android.compass.util.ImageLoader;

public class BehaviorListView extends LinearLayout {
    private ImageView mIconImageView;
    private ImageView mAddImageView;
    private TextView mTitleTextView;
    private Behavior mBehavior;
    private Category mCategory;
    private Context mContext;
    private BehaviorListViewListener mCallback;

    public interface BehaviorListViewListener {
        public void deleteUserBehavior(Behavior behavior);
    }

    public void setListener(BehaviorListViewListener listener) {
        mCallback = listener;
    }

    public BehaviorListView(Context context) {
        this(context, null, 0);
    }

    public BehaviorListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BehaviorListView(Context context, AttributeSet attrs, int defStyle){
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
            if (mBehavior.getIconUrl() != null && !mBehavior.getIconUrl().isEmpty()){
                ImageLoader.loadBitmap(mIconImageView, mBehavior.getIconUrl(), new ImageLoader.Options());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showPopup() {
        //Creating the instance of PopupMenu
        CompassPopupMenu popup = CompassPopupMenu.newInstance(mContext, mAddImageView);
        popup.getMenuInflater()
                .inflate(R.menu.menu_behavior_popup_chooser, popup.getMenu());
        popup.setOnMenuItemClickListener(new CompassPopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_behavior_popup_remove_item:
                        mCallback.deleteUserBehavior(mBehavior);
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
