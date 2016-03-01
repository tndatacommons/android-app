package org.tndata.android.compass.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import org.tndata.android.compass.R;
import org.tndata.android.compass.model.CustomAction;
import org.tndata.android.compass.model.CustomGoal;

import java.util.List;


/**
 * Adapter to display and manage a custom goal and its list of custom actions.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class CustomContentManagerAdapter extends MaterialAdapter{
    private static final String TAG = "CustomActionAdapter";


    private Context mContext;
    private CustomGoal mCustomGoal;
    private List<CustomAction> mCustomActions;
    private CustomContentManagerListener mListener;

    private CustomGoalHolder mCustomGoalHolder;
    private CustomActionListHolder mActionListHolder;


    /**
     * Constructor.
     *
     * @param context a reference to the context.
     * @param customGoal the custom goal to be managed or {@code null} if this is a new goal
     * @param listener a listener.
     */
    public CustomContentManagerAdapter(@NonNull Context context, @Nullable CustomGoal customGoal,
                                       @NonNull CustomContentManagerListener listener){

        super(context, ContentType.LIST, customGoal != null);

        mContext = context;
        mCustomGoal = customGoal;
        mCustomActions = null;
        mListener = listener;
    }

    @Override
    protected boolean isEmpty(){
        return mCustomGoal == null || mCustomActions == null;
    }

    @Override
    protected @NonNull RecyclerView.ViewHolder getHeaderHolder(ViewGroup parent){
        if (mCustomGoalHolder == null){
            LayoutInflater inflater = LayoutInflater.from(getContext());
            View rootView = inflater.inflate(R.layout.card_create_goal, parent, false);
            mCustomGoalHolder = new CustomGoalHolder(rootView);
        }
        return mCustomGoalHolder;
    }

    @Override
    protected void bindHeaderHolder(RecyclerView.ViewHolder rawHolder){
        ((CustomGoalHolder)rawHolder).bind(mCustomGoal);
    }

    @Override
    protected @NonNull RecyclerView.ViewHolder getListHolder(ViewGroup parent){
        if (mActionListHolder == null){
            LayoutInflater inflater = LayoutInflater.from(getContext());
            View rootView = inflater.inflate(R.layout.card_custom_action_list, parent, false);
            mActionListHolder = new CustomActionListHolder(rootView);
        }
        return mActionListHolder;
    }

    /**
     * Notifies the adapter that the goal has been added to the user's data set. Displays
     * the action list, which will be empty.
     *
     * @param customGoal a reference to the recently added goal.
     */
    public void customGoalAdded(@NonNull CustomGoal customGoal){
        mCustomGoal = customGoal;
        mCustomActions = customGoal.getActions();
        mCustomGoalHolder.mButton.setText(mContext.getString(R.string.custom_goal_edit));
        setButtonEnabled(mCustomGoalHolder.mButton, true);
        notifyListInserted();
        updateLoading(false);
    }

    /**
     * Lets the adapter know that the list of actions has been fetched.
     */
    public void customActionsFetched(){
        mCustomActions = mCustomGoal.getActions();
        mCustomGoalHolder.mButton.setText(mContext.getString(R.string.custom_goal_edit));
        setButtonEnabled(mCustomGoalHolder.mButton, true);
        notifyListInserted();
        updateLoading(false);
    }

    /**
     * Called when the process of adding a custom action to the user's dataset has been
     * completed. Notifies the recycler view of the insertion of a new element to trigger
     * the proper animation.
     */
    public void customActionAdded(){
        mActionListHolder.mAdapter.notifyItemInserted(mCustomActions.size()-1);
        mActionListHolder.mAdapter.notifyItemChanged(mCustomActions.size());
        mActionListHolder.mSwitcher.showPrevious();
    }

    /**
     * Enables or disables a button, including a color change as a visual cue.
     *
     * @param button the button to be enabled or disabled.
     * @param enabled whether the button should be enabled or disabled.
     */
    private void setButtonEnabled(TextView button, boolean enabled){
        if (enabled){
            button.setTextColor(mContext.getResources().getColor(R.color.grow_primary));
        }
        else{
            button.setTextColor(mContext.getResources().getColor(R.color.secondary_text_color));
        }
        button.setEnabled(enabled);
    }


    /**
     * View holder for a custom goal.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    private class CustomGoalHolder
            extends RecyclerView.ViewHolder
            implements TextWatcher, View.OnClickListener{

        private EditText mTitle;
        private TextView mButton;

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
            mButton = (TextView)rootView.findViewById(R.id.create_goal_button);

            //Set the listeners
            mTitle.addTextChangedListener(this);
            mButton.setOnClickListener(this);

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
                //If no goal was provided, disable the button and set create as the label
                mButton.setText(mContext.getString(R.string.custom_goal_create));
                setButtonEnabled(mButton, false);
            }
            else{
                //Otherwise, set the title, enable the button, and set edit as the label.
                mTitle.setText(customGoal.getTitle());
                mTitle.clearFocus();
                mTitle.setFocusable(false);
                mButton.setText(mContext.getString(R.string.custom_goal_edit));
                setButtonEnabled(mButton, true);
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after){
            //Unused
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count){
            //If there is no text in the field, disable the button
            setButtonEnabled(mButton, count != 0);
        }

        @Override
        public void afterTextChanged(Editable s){
            //Unused
        }

        @Override
        public void onClick(View view){
            switch (view.getId()){
                case R.id.create_goal_button:
                    //If there is no custom goal, this is a create button
                    if (mCustomGoal == null){
                        //Hide the keyboard and clear the focus
                        InputMethodManager imm2 = (InputMethodManager)mContext
                                .getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm2.hideSoftInputFromWindow(mTitle.getWindowToken(), 0);
                        mTitle.clearFocus();
                        mTitle.setFocusable(false);

                        //Disable the button and let the listener know
                        setButtonEnabled(mButton, false);
                        mListener.onCreateGoal(new CustomGoal(mTitle.getText().toString().trim()));

                        //Display the progress indicator
                        updateLoading(true);
                    }
                    else{
                        //If the holder is in edition mode, this is a save button
                        if (mEditing){
                            //Hide the keyboard and clear the focus
                            InputMethodManager imm2 = (InputMethodManager)mContext
                                    .getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm2.hideSoftInputFromWindow(mTitle.getWindowToken(), 0);
                            mTitle.clearFocus();
                            mTitle.setFocusable(false);

                            //If the titles ain't the same, save the goal
                            if (!mCustomGoal.getTitle().equals(mTitle.getText().toString().trim())){
                                mCustomGoal.setTitle(mTitle.getText().toString().trim());
                                mListener.onSaveGoal(mCustomGoal);
                            }
                            //Change the button to edit
                            mButton.setText(mContext.getString(R.string.custom_goal_edit));
                        }
                        else{
                            //Make the title focusable and provide the focus
                            mTitle.setFocusable(true);
                            mTitle.setFocusableInTouchMode(true);
                            mTitle.requestFocus();
                            //Put the cursor at the end and open the keyboard
                            mTitle.setSelection(mTitle.getText().length());
                            InputMethodManager imm = (InputMethodManager)mContext
                                    .getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,
                                    InputMethodManager.HIDE_IMPLICIT_ONLY);

                            //Change the button to save
                            mButton.setText(mContext.getString(R.string.custom_goal_save));
                        }
                        //Finally, flip the flag
                        mEditing = !mEditing;
                    }
                    break;
            }
        }
    }


    /**
     * Holder for the list of custom actions.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    private class CustomActionListHolder
            extends RecyclerView.ViewHolder
            implements View.OnClickListener{

        private CustomActionAdapter mAdapter;

        private ViewSwitcher mSwitcher;
        private TextView mButton;


        /**
         * Constructor.
         *
         * @param rootView the view held by this adapter.
         */
        public CustomActionListHolder(View rootView){
            super(rootView);

            //Set the adapter and the inner recycler view
            mAdapter = new CustomActionAdapter();
            RecyclerView rv = (RecyclerView)rootView.findViewById(R.id.custom_action_list);
            rv.setLayoutManager(new LinearLayoutManager(mContext));
            rv.setAdapter(mAdapter);

            //Grab and set the rest of the UI
            mSwitcher =(ViewSwitcher)rootView.findViewById(R.id.custom_action_list_switcher);
            mButton = (TextView)rootView.findViewById(R.id.custom_action_list_create);
            mButton.setOnClickListener(this);
            setButtonEnabled(mButton, false);
        }

        @Override
        public void onClick(View view){
            switch (view.getId()){
                case R.id.custom_action_list_create:
                    String title = mAdapter.mNewActionHolder.mTitle.getText().toString().trim();
                    mSwitcher.showNext();
                    mListener.onCreateAction(new CustomAction(title, mCustomGoal));
            }
        }
    }


    /**
     * Adapter for the list of custom actions.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    private class CustomActionAdapter extends RecyclerView.Adapter<CustomActionHolder>{
        private CustomActionHolder mNewActionHolder;


        @Override
        public int getItemCount(){
            return mCustomActions.size()+1;
        }

        @Override
        public CustomActionHolder onCreateViewHolder(ViewGroup parent, int viewType){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View rootView = inflater.inflate(R.layout.item_custom_action, parent, false);
            return new CustomActionHolder(rootView);
        }

        @Override
        public void onBindViewHolder(CustomActionHolder holder, int position){
            //The last position hosts a new action item
            if (position == getItemCount()-1){
                mNewActionHolder = holder;
                holder.bind(null);
            }
            else{
                holder.bind(mCustomActions.get(position));
            }
        }
    }


    /**
     * Holder for a custom action.
     */
    private class CustomActionHolder
            extends RecyclerView.ViewHolder
            implements TextWatcher, View.OnClickListener{

        private CustomAction mCustomAction;

        private EditText mTitle;

        private ImageView mEditTrigger;
        private ImageView mEditAction;
        private ImageView mSaveAction;


        public CustomActionHolder(View rootView){
            super(rootView);

            //Grab the UI components
            mTitle = (EditText)rootView.findViewById(R.id.custom_action_title);
            mEditTrigger = (ImageView)rootView.findViewById(R.id.custom_action_trigger);
            mEditAction = (ImageView)rootView.findViewById(R.id.custom_action_edit);
            mSaveAction = (ImageView)rootView.findViewById(R.id.custom_action_save);

            //Set the listeners
            mTitle.addTextChangedListener(this);
            mEditTrigger.setOnClickListener(this);
            mEditAction.setOnClickListener(this);
            mSaveAction.setOnClickListener(this);
        }

        /**
         * Binds a custom action to the holder.
         *
         * @param customAction the custom action to be bound or {@code null} if this is a new action.
         */
        public void bind(@Nullable CustomAction customAction){
            mCustomAction = customAction;
            if (mCustomAction == null){
                mTitle.setText("");
            }
            else{
                mTitle.setText(mCustomAction.getTitle());
                mTitle.setFocusable(false);
                mEditTrigger.setVisibility(View.VISIBLE);
                mEditAction.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after){
            //Unused
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count){
            //Disable the add button if this is the new action and the title is empty
            if (mCustomAction == null){
                setButtonEnabled(mActionListHolder.mButton, count != 0);
            }
        }

        @Override
        public void afterTextChanged(Editable s){
            //Unused
        }

        @Override
        public void onClick(View view){
            switch (view.getId()){
                case R.id.custom_action_trigger:
                    Log.d(TAG, "Editing the trigger of " + mCustomAction);

                    //Open the trigger editor
                    mListener.onEditTrigger(mCustomActions.get(getAdapterPosition()));
                    break;

                //When the user enters edition mode
                case R.id.custom_action_edit:
                    Log.d(TAG, "Editing the title of " + mCustomAction);
                    //Set the title click listener to null to avoid going into the trigger editor
                    mTitle.setOnClickListener(null);
                    //Make the title focusable and give it focus
                    mTitle.setFocusable(true);
                    mTitle.setFocusableInTouchMode(true);
                    mTitle.requestFocus();
                    //Put the cursor at the end and open the keyboard
                    mTitle.setSelection(mTitle.getText().length());
                    InputMethodManager imm = (InputMethodManager)mContext
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

                    //Switch the edit button for the save button
                    mEditTrigger.setVisibility(View.GONE);
                    mEditAction.setVisibility(View.GONE);
                    mSaveAction.setVisibility(View.VISIBLE);
                    break;

                //When the user saves a currently existing goal (only from edition)
                case R.id.custom_action_save:
                    //Grab the title and check it ain't empty
                    String newTitle = mTitle.getText().toString().trim();
                    if (newTitle.length() > 0){
                        Log.d(TAG, "Saving the title of " + mCustomAction);

                        //If the title has changed, set it and send an update to the backend
                        if (!mCustomAction.getTitle().equals(newTitle)){
                            mCustomAction.setTitle(newTitle);
                            mListener.onSaveAction(mCustomAction);
                        }

                        //Hide the keyboard and make the title not focusable
                        InputMethodManager imm2 = (InputMethodManager)mContext
                                .getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm2.hideSoftInputFromWindow(mTitle.getWindowToken(), 0);
                        mTitle.clearFocus();
                        mTitle.setFocusable(false);
                        //Set the click listener again to start the trigger editor if the user taps
                        mTitle.setOnClickListener(this);

                        //Swap the save button for the edit button
                        mEditTrigger.setVisibility(View.VISIBLE);
                        mEditAction.setVisibility(View.VISIBLE);
                        mSaveAction.setVisibility(View.GONE);
                    }
                    break;
            }
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
         * @param customAction the newly created action.
         */
        void onCreateAction(@NonNull CustomAction customAction);

        /**
         * Called when the user saves an action he is editing.
         *
         * @param customAction the custom action to be saved.
         */
        void onSaveAction(@NonNull CustomAction customAction);

        /**
         * Called when the user taps the edi trigger button.
         *
         * @param customAction the custom action whose trigger is to be edited.
         */
        void onEditTrigger(@NonNull CustomAction customAction);
    }
}