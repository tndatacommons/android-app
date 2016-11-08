package org.tndata.android.compass.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Space;
import android.widget.TextView;

import org.tndata.android.compass.R;
import org.tndata.android.compass.databinding.CardEditableListBinding;
import org.tndata.android.compass.databinding.ItemProgressFooterBinding;
import org.tndata.android.compass.holder.EditableListCardHolder;
import org.tndata.android.compass.holder.ProgressFooterHolder;
import org.tndata.compass.model.CustomAction;
import org.tndata.compass.model.CustomGoal;
import org.tndata.android.compass.util.CompassUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * Adapter to display and manage a custom goal and its list of custom actions.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class CustomContentAdapter
        extends RecyclerView.Adapter
        implements EditableListCardHolder.Listener{

    private static final String TAG = "CustomActionAdapter";

    private static final int TYPE_BLANK = 0;
    private static final int TYPE_GOAL = TYPE_BLANK + 1;
    private static final int TYPE_ACTIONS = TYPE_GOAL + 1;
    private static final int TYPE_FOOTER = TYPE_ACTIONS + 1;


    private Context mContext;
    private CustomGoal mCustomGoal;
    private List<CustomAction> mCustomActions;
    private String mGoalTitle;
    private CustomContentManagerListener mListener;

    private CustomGoalHolder mCustomGoalHolder;

    private EditableListCardHolder mCustomActionListHolder;
    private ProgressFooterHolder mProgressFooterHolder;

    private boolean mLoading;


    /**
     * Constructor.
     *
     * @param context a reference to the context.
     * @param customGoal the custom goal to be managed or {@code null} if this is a new goal
     * @param listener a listener.
     */
    public CustomContentAdapter(@NonNull Context context, @Nullable CustomGoal customGoal,
                                @NonNull CustomContentManagerListener listener){

        mContext = context;
        mCustomGoal = customGoal;
        mCustomActions = null;
        mGoalTitle = null;
        mListener = listener;

        mLoading = false;
    }

    /**
     * Constructor.
     *
     * @param context a reference to the context.
     * @param title the title of the input in search when the user fired the manager.
     * @param listener a listener.
     */
    public CustomContentAdapter(@NonNull Context context, @NonNull String title,
                                @NonNull CustomContentManagerListener listener){

        mContext = context;
        mCustomGoal = null;
        mCustomActions = null;
        mGoalTitle = title;
        mListener = listener;

        mLoading = false;
    }

    @Override
    public int getItemViewType(int position){
        if (position == 0){
            return TYPE_BLANK;
        }
        else if (position == 1){
            return TYPE_GOAL;
        }
        else{// if (position == 2){
            if (mCustomActions == null){
                return TYPE_FOOTER;
            }
            else{
                return TYPE_ACTIONS;
            }
        }
    }

    @Override
    public int getItemCount(){
        //Blank, goal, and either the footer or the list of actions.
        if (mCustomGoal == null && !mLoading){
            return 2;
        }
        else{
            return 3;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        if (viewType == TYPE_BLANK){
            return new RecyclerView.ViewHolder(new Space(mContext)){};
        }
        else if (viewType == TYPE_GOAL){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View rootView = inflater.inflate(R.layout.card_create_goal, parent, false);
            mCustomGoalHolder = new CustomGoalHolder(rootView);
            return mCustomGoalHolder;
        }
        else if (viewType == TYPE_ACTIONS){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            CardEditableListBinding binding = DataBindingUtil.inflate(
                    inflater, R.layout.card_editable_list, parent, false
            );
            //Generate the dataset
            List<String> dataset = new ArrayList<>();
            for (CustomAction customAction:mCustomActions){
                dataset.add(customAction.getTitle());
            }
            mCustomActionListHolder = new EditableListCardHolder(
                    binding, this, dataset, R.menu.menu_custom_action
            );
            return mCustomActionListHolder;
        }
        else if (viewType == TYPE_FOOTER){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            ItemProgressFooterBinding binding = DataBindingUtil.inflate(
                    inflater, R.layout.item_progress_footer, parent, false
            );
            mProgressFooterHolder = new ProgressFooterHolder(binding);
            return mProgressFooterHolder;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder rawHolder, int position){
        switch (getItemViewType(position)){
            case TYPE_BLANK:
                int width = CompassUtil.getScreenWidth(mContext);
                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        (int)((width*2/3)*0.8)
                );
                rawHolder.itemView.setLayoutParams(params);
                rawHolder.itemView.setVisibility(View.INVISIBLE);
                break;

            case TYPE_GOAL:
                if (mCustomGoal != null){
                    ((CustomGoalHolder)rawHolder).bind(mCustomGoal);
                }
                else{
                    ((CustomGoalHolder)rawHolder).bind(mGoalTitle);
                }
                break;

            case TYPE_ACTIONS:
                EditableListCardHolder customActionsHolder = (EditableListCardHolder)rawHolder;
                customActionsHolder.setTitle(R.string.custom_action_list_header);
                customActionsHolder.setInputHint(R.string.custom_action_list_hint);
                break;
        }
    }

    /**
     * Notifies the adapter that the goal has been added to the user's data set. Displays
     * the action list, which will be empty.
     *
     * @param customGoal a reference to the recently added goal.
     */
    public void customGoalAdded(@NonNull CustomGoal customGoal){
        mCustomGoal = customGoal;
        mCustomActions = mCustomGoal.getActions();
        mCustomGoalHolder.mCreate.setVisibility(View.GONE);
        mCustomGoalHolder.mEdit.setVisibility(View.VISIBLE);
        notifyItemRemoved(2);
        notifyItemInserted(2);
    }

    public void customActionsSet(){
        mCustomActions = mCustomGoal.getActions();
        mCustomGoalHolder.mCreate.setVisibility(View.GONE);
        mCustomGoalHolder.mEdit.setVisibility(View.VISIBLE);
        notifyItemRemoved(2);
        notifyItemInserted(2);
    }

    public void contentLoadError(){
        mProgressFooterHolder.displayMessage(R.string.content_load_error);
    }

    /**
     * Called when the process of adding a custom action to the user's dataset has been
     * completed. Notifies the recycler view of the insertion of a new element to trigger
     * the proper animation.
     */
    public void customActionAdded(){
        mCustomActionListHolder.addInputToDataset();
    }

    /**
     * Enables or disables a button, including a color change as a visual cue.
     *
     * @param button the button to be enabled or disabled.
     * @param enabled whether the button should be enabled or disabled.
     */
    @SuppressWarnings("deprecation")
    private void setButtonEnabled(TextView button, boolean enabled){
        if (enabled){
            button.setTextColor(mContext.getResources().getColor(R.color.primary));
        }
        else{
            button.setTextColor(mContext.getResources().getColor(R.color.secondary_text_color));
        }
        button.setEnabled(enabled);
    }

    @Override
    public void onCreateItem(String name){
        mListener.onCreateAction(name);
    }

    @Override
    public void onEditItem(String newName, int index){
        CustomAction customAction = mCustomActions.get(index);
        customAction.setTitle(newName);
        mListener.onSaveAction(customAction);
    }

    @Override
    public void onDeleteItem(int index){
        mListener.onRemoveAction(mCustomActions.remove(index));
    }

    @Override
    public void onItemClick(int index){
        mListener.onEditTrigger(mCustomActions.get(index));
    }

    @Override
    public boolean onMenuItemClick(MenuItem item, int index){
        switch (item.getItemId()){
            case R.id.custom_action_reschedule:
                mListener.onEditTrigger(mCustomActions.get(index));
                return true;
        }
        return false;
    }

    /**
     * View holder for a custom goal.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    private class CustomGoalHolder
            extends RecyclerView.ViewHolder
            implements
                    TextWatcher,
                    EditText.OnEditorActionListener,
                    View.OnClickListener{

        private EditText mTitle;
        private TextView mCreate;
        private ImageView mEdit;

        private Drawable mTitleDefaultBackground;

        private boolean mEditing;


        /**
         * Constructor.
         *
         * @param rootView the view held by this adapter.
         */
        public CustomGoalHolder(View rootView){
            super(rootView);

            //Grab the UI components
            mTitle = (EditText)rootView.findViewById(R.id.create_goal_title);
            mCreate = (TextView)rootView.findViewById(R.id.create_goal_button);
            mEdit = (ImageView)rootView.findViewById(R.id.create_goal_edit);

            mTitleDefaultBackground = mTitle.getBackground();

            //Set the listeners
            mTitle.addTextChangedListener(this);
            mTitle.setOnEditorActionListener(this);
            mCreate.setOnClickListener(this);
            mEdit.setOnClickListener(this);

            //Set the flags
            mEditing = false;
        }

        /**
         * Binds a custom goal to this adapter.
         *
         * @param customGoal the custom goal to be bound.
         */
        public void bind(@Nullable CustomGoal customGoal){
            if (customGoal == null){
                //If no goal was provided, disable the button
                setButtonEnabled(mCreate, false);
            }
            else{
                //Otherwise, set the title and swap the create for the edit button
                mTitle.setBackgroundResource(0);
                mTitle.setText(customGoal.getTitle());
                mTitle.clearFocus();
                mTitle.setFocusable(false);
                mCreate.setVisibility(View.GONE);
                mEdit.setVisibility(View.VISIBLE);
            }
        }

        /**
         * Binds a title to this adapter.
         *
         * @param title the title of the goal to be.
         */
        public void bind(@NonNull String title){
            mTitle.setText(title);
            setButtonEnabled(mCreate, true);
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after){
            //Unused
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count){
            //If there is no text in the field, disable the button
            setButtonEnabled(mCreate, count != 0);
        }

        @Override
        public void afterTextChanged(Editable s){
            //Unused
        }

        @Override
        public void onClick(View view){
            switch (view.getId()){
                case R.id.create_goal_button:
                    //Hide the keyboard and clear the focus
                    InputMethodManager imm = (InputMethodManager)mContext
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mTitle.getWindowToken(), 0);
                    mTitle.clearFocus();
                    mTitle.setFocusable(false);
                    mTitle.setBackgroundResource(0);

                    //Disable the button and let the listener know
                    setButtonEnabled(mCreate, false);
                    mListener.onCreateGoal(new CustomGoal(mTitle.getText().toString().trim()));

                    //Display the progress indicator
                    mLoading = true;
                    notifyItemInserted(2);
                    break;

                case R.id.create_goal_edit:
                    //If the holder is in edition mode, this is a save button
                    if (mEditing){
                        //Hide the keyboard and clear the focus
                        InputMethodManager imm2 = (InputMethodManager)mContext
                                .getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm2.hideSoftInputFromWindow(mTitle.getWindowToken(), 0);
                        mTitle.clearFocus();
                        mTitle.setFocusable(false);
                        mTitle.setBackgroundResource(0);

                        //If the titles ain't the same, save the goal
                        if (!mCustomGoal.getTitle().equals(mTitle.getText().toString().trim())){
                            mCustomGoal.setTitle(mTitle.getText().toString().trim());
                            mListener.onSaveGoal(mCustomGoal);
                        }
                        //Change the button to edit
                        mEdit.setImageResource(R.drawable.ic_edit_white_24dp);
                    }
                    else{
                        //Set the proper background, make the title focusable and provide the focus
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
                            mTitle.setBackground(mTitleDefaultBackground);
                        }
                        else{
                            mTitle.setBackgroundDrawable(mTitleDefaultBackground);
                        }
                        mTitle.setFocusable(true);
                        mTitle.setFocusableInTouchMode(true);
                        mTitle.requestFocus();
                        //Put the cursor at the end and open the keyboard
                        mTitle.setSelection(mTitle.getText().length());
                        InputMethodManager imm2 = (InputMethodManager)mContext
                                .getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm2.toggleSoftInput(InputMethodManager.SHOW_FORCED,
                                InputMethodManager.HIDE_IMPLICIT_ONLY);

                        //Change the button to save
                        mEdit.setImageResource(R.drawable.ic_check_white_24dp);
                    }
                    //Finally, flip the flag
                    mEditing = !mEditing;
                    break;
            }
        }

        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event){
            if (actionId == EditorInfo.IME_ACTION_DONE){
                if (mTitle.getText().toString().trim().length() > 0){
                    if (mEditing){
                        onClick(mEdit);
                    }
                    else{
                        onClick(mCreate);
                    }
                    return true;
                }
            }
            return false;
        }
    }


    /**
     * Listener interface for this adapter.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    public interface CustomContentManagerListener{
        /**
         * Called when the user wants to create a custom goal.
         *
         * @param customGoal the custom goal to be created.
         */
        void onCreateGoal(@NonNull CustomGoal customGoal);

        /**
         * Called when the user saves an goal he is editing.
         *
         * @param customGoal the custom goal to be saved.
         */
        void onSaveGoal(@NonNull CustomGoal customGoal);

        /**
         * Called when the user creates a new action.
         *
         * @param name the name of the action to be created.
         */
        void onCreateAction(@NonNull String name);

        /**
         * Called when the user saves an action he is editing.
         *
         * @param customAction the custom action to be saved.
         */
        void onSaveAction(@NonNull CustomAction customAction);

        /**
         * Called when the user chooses to remove an action.
         *
         * @param customAction the action to be removed.
         */
        void onRemoveAction(@NonNull CustomAction customAction);

        /**
         * Called when the user taps the edi trigger button.
         *
         * @param customAction the custom action whose trigger is to be edited.
         */
        void onEditTrigger(@NonNull CustomAction customAction);
    }
}
