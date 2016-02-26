package org.tndata.android.compass.adapter;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

import org.tndata.android.compass.R;
import org.tndata.android.compass.model.CustomAction;
import org.tndata.android.compass.model.CustomGoal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Adapter to display and manage a list of custom actions.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class CustomContentManagerAdapter extends MaterialAdapter implements View.OnClickListener{
    private static final String TAG = "CustomActionAdapter";


    private Context mContext;
    private CustomGoal mCustomGoal;
    private CustomActionAdapterListener mListener;
    private List<CustomAction> mCustomActions;

    //A map of id->newTitle of actions being currently edited
    private Map<Long, String> mEditing;
    //The title of a new action
    private String mNewActionTitle;


    /**
     * Constructor.
     *
     * @param context a reference to the context.
     * @param customGoal the custom goal to be managed or {@code null} if this is a new goal
     * @param listener a listener.
     */
    public CustomContentManagerAdapter(@NonNull Context context, @Nullable CustomGoal customGoal,
                                       @NonNull CustomActionAdapterListener listener){

        super(context, ContentType.LIST, true);

        mContext = context;
        mCustomGoal = customGoal;
        mListener = listener;

        mEditing = new HashMap<>();
        mNewActionTitle = "";
    }

    @Override
    protected boolean isEmpty(){
        return true;
    }

    @Override
    protected @NonNull RecyclerView.ViewHolder getHeaderHolder(ViewGroup parent){
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View rootView = inflater.inflate(R.layout.card_create_goal, parent, false);
        return new CustomGoalHolder(rootView);
    }

    @Override
    protected void bindHeaderHolder(RecyclerView.ViewHolder rawHolder){
        CustomGoalHolder holder = (CustomGoalHolder)rawHolder;
        if (mCustomGoal == null){
            holder.setButton("Create", this);
        }
        else{
            holder.setGoalTitle(mCustomGoal.getTitle());
            holder.setButton("Edit", this);
        }
    }

    /*@Override
    public ActionHolder onCreateViewHolder(ViewGroup parent, int viewType){
        LayoutInflater inflater = LayoutInflater.from(mContext);
        return new ActionHolder(inflater.inflate(R.layout.item_custom_action, parent, false));
    }

    @Override
    public void onBindViewHolder(ActionHolder holder, int position){
        if (position == mCustomActions.size()){
            //If this is the last item, pass null to identify this item as the new action card
            holder.bind(null);
        }
        else{
            //If it isn't, pass the relevant custom action
            holder.bind(mCustomActions.get(position));
        }
    }*/

    /**
     * Called when the process of adding a custom action to the user's dataset has been
     * completed. Notifies the recycler view of the insertion of a new element to trigger
     * the proper animation.
     */
    public void customActionAdded(){
        //Note to future me: the action needn't to be added to the action list here because the
        //  list of actions held in this adapter comes from an instance of a custom goal stored
        //  in the master custom goal map. When adding an action through the Application or
        //  UserData classes adds this action to the list already. This is expected behavior and
        //  I am personally leaning to promote that behavior throughout the application.
        notifyItemInserted(mCustomActions.size()-1);
        notifyItemChanged(mCustomActions.size());
    }

    @Override
    public void onClick(View v){

    }

    private static class CustomGoalHolder extends RecyclerView.ViewHolder{
        private EditText mGoalTitle;
        private TextView mButton;


        public CustomGoalHolder(View rootView){
            super(rootView);
            mGoalTitle = (EditText)rootView.findViewById(R.id.create_goal_title);
            mButton = (TextView)rootView.findViewById(R.id.create_goal_button);
        }

        public void setGoalTitle(String title){
            mGoalTitle.setText(title);
        }

        public void setButton(String caption, View.OnClickListener onClickListener){
            mButton.setText(caption);
            mButton.setOnClickListener(onClickListener);
        }

        public void setEnabled(boolean enabled){
            mGoalTitle.setEnabled(enabled);
            mButton.setEnabled(enabled);
        }

        public @IdRes int getButtonId(){
            return mButton.getId();
        }
    }


    /**
     * View holder for an action or action-to-be.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    class ActionHolder extends RecyclerView.ViewHolder implements View.OnClickListener, TextWatcher{
        private EditText mTitle;

        private ImageView mEditAction;
        private ImageView mSaveAction;
        private ImageView mAddAction;
        private ImageView mDeleteAction;


        /**
         * Constructor.
         *
         * @param rootView the root view of the item held by this holder.
         */
        public ActionHolder(View rootView){
            super(rootView);

            //Grab the UI components
            mTitle = (EditText)rootView.findViewById(R.id.custom_action_title);
            mEditAction = (ImageView)rootView.findViewById(R.id.custom_action_edit);
            mSaveAction = (ImageView)rootView.findViewById(R.id.custom_action_save);
            mAddAction = (ImageView)rootView.findViewById(R.id.custom_action_add);
            mDeleteAction = (ImageView)rootView.findViewById(R.id.custom_action_delete);

            //Set the listeners
            mEditAction.setOnClickListener(this);
            mSaveAction.setOnClickListener(this);
            mAddAction.setOnClickListener(this);
            mDeleteAction.setOnClickListener(this);
        }

        @Override
        public void onClick(View v){
            //First off, grab or create the custom action, it is needed in all cases
            CustomAction customAction;
            if (getAdapterPosition() != mCustomActions.size()){
                customAction = mCustomActions.get(getAdapterPosition());
            }
            else{
                customAction = new CustomAction(mTitle.getText().toString().trim());
            }

            switch (v.getId()){
                //When the user taps on the title (only if it isn't focusable)
                case R.id.custom_action_title:
                    Log.d(TAG, "Editing the trigger of " + customAction);

                    //Open the trigger editor
                    mListener.onEditTrigger(mCustomActions.get(getAdapterPosition()));
                    break;

                //When the user enters edition mode
                case R.id.custom_action_edit:
                    Log.d(TAG, "Editing the title of " + customAction);

                    //Grab the action and put an entry in the map with its id and current title
                    mEditing.put(customAction.getId(), customAction.getTitle());

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
                    mEditAction.setVisibility(View.GONE);
                    mSaveAction.setVisibility(View.VISIBLE);
                    //Start recording the title to the map
                    recordTitle(true);
                    break;

                //When the user saves a currently existing goal (only from edition)
                case R.id.custom_action_save:
                    //Grab the title and check it ain't empty
                    String newTitle = mTitle.getText().toString().trim();
                    if (newTitle.length() > 0){
                        Log.d(TAG, "Saving the title of " + customAction);

                        //Remove the action from the title map
                        mEditing.remove(customAction.getId());

                        //If the title has changed, set it and send an update to the backend
                        if (!customAction.getTitle().equals(newTitle)){
                            customAction.setTitle(newTitle);
                            mListener.onSaveAction(customAction);
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
                        mEditAction.setVisibility(View.VISIBLE);
                        mSaveAction.setVisibility(View.GONE);

                        //Stop recording the title
                        recordTitle(false);
                    }
                    break;

                //When the user adds an action
                case R.id.custom_action_add:
                    //Grab the title and check that it ain't empty
                    if (mNewActionTitle.length() > 0){
                        Log.d(TAG, "Adding " + customAction);

                        //Temporarily disable the add action button
                        mAddAction.setEnabled(false);
                        //Let the listener know
                        mListener.onAddClicked(customAction);
                        //Reset the new title holder
                        mNewActionTitle = "";
                        //Temporarily disable the action title input
                        mTitle.setEnabled(false);
                    }
                    break;

                //When the user deletes a goal
                case R.id.custom_action_delete:
                    //Remove the action from the data set and notify the recycler view and listener
                    CustomAction deletedAction = mCustomActions.remove(getAdapterPosition());

                    Log.d(TAG, "Deleting " + deletedAction);

                    notifyItemRemoved(getAdapterPosition());
                    mListener.onRemoveClicked(deletedAction);
                    break;
            }
        }

        /**
         * Binds a custom action to the holder.
         *
         * @param customAction the action to bind or {@code null} if this is the new action card
         */
        public void bind(@Nullable CustomAction customAction){
            //For the new action card
            if (customAction == null){
                //Set the working title
                mTitle.setText(mNewActionTitle);
                //Enable the title input and make it focusable
                mTitle.setEnabled(true);
                mTitle.setFocusable(true);
                mTitle.setFocusableInTouchMode(true);
                //Remove the click listener and select it
                mTitle.setOnClickListener(null);
                mTitle.setSelected(true);
                //Enable the add action button and make it the only visible one
                mAddAction.setEnabled(true);
                mEditAction.setVisibility(View.GONE);
                mSaveAction.setVisibility(View.GONE);
                mAddAction.setVisibility(View.VISIBLE);
                mDeleteAction.setVisibility(View.GONE);

                //Start recording the title
                recordTitle(true);
            }
            //For an existing custom action
            else{
                //Enable the title
                mTitle.setEnabled(true);
                //If this action is in edition mode
                if (mEditing.containsKey(customAction.getId())){
                    //Set the working title
                    mTitle.setText(mEditing.get(customAction.getId()));
                    //Make the title input focusable and remove the click listener, if there is any
                    mTitle.setFocusable(true);
                    mTitle.setFocusableInTouchMode(true);
                    mTitle.setOnClickListener(null);

                    //Make the save button visible
                    mEditAction.setVisibility(View.GONE);
                    mSaveAction.setVisibility(View.VISIBLE);

                    //Start recording the title
                    recordTitle(true);
                }
                //If this action ain't in edition mode
                else{
                    //Stop recording the title
                    recordTitle(false);
                    //Set the title of the actual action
                    mTitle.setText(customAction.getTitle());
                    //Make the title input not focusable and set the click listener
                    mTitle.setFocusable(false);
                    mTitle.setOnClickListener(this);

                    //Show the edit button
                    mEditAction.setVisibility(View.VISIBLE);
                    mSaveAction.setVisibility(View.GONE);
                }
                //Show delete and hide add
                mAddAction.setVisibility(View.GONE);
                mDeleteAction.setVisibility(View.VISIBLE);
            }
        }

        /**
         * Switches the text watcher listener to record the title the user is typing.
         *
         * @param enabled true if the holder should record the title, false otherwise.
         */
        private void recordTitle(boolean enabled){
            mTitle.removeTextChangedListener(this);
            if (enabled){
                mTitle.addTextChangedListener(this);
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after){
            //Unused
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count){
            if (getAdapterPosition() == mCustomActions.size()){
                mNewActionTitle = s.toString();
            }
            else{
                mEditing.put(mCustomActions.get(getAdapterPosition()).getId(), s.toString());
            }
        }

        @Override
        public void afterTextChanged(Editable s){
            //Unused
        }
    }


    /**
     * Listener interface for this adapter.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    public interface CustomActionAdapterListener{
        /**
         * Called when the save action button is pressed.
         *
         * @param customAction the custom action to be saved.
         */
        void onSaveAction(CustomAction customAction);

        /**
         * Called when the add action button is pressed.
         *
         * @param customAction the custom action to be added.
         */
        void onAddClicked(CustomAction customAction);

        /**
         * Called when the remove action button is pressed.
         *
         * @param customAction the action to be removed from the dataset.
         */
        void onRemoveClicked(CustomAction customAction);

        /**
         * Called when the title of an action is pressed and the trigger editor
         * should be fired.
         *
         * @param customAction the custom action whose trigger is to be edited.
         */
        void onEditTrigger(CustomAction customAction);
    }
}
