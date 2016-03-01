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

import java.util.ArrayList;
import java.util.List;


/**
 * Adapter to display and manage a list of custom actions.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class CustomContentManagerAdapter extends MaterialAdapter{
    private static final String TAG = "CustomActionAdapter";


    private Context mContext;
    private CustomGoal mCustomGoal;
    private CustomContentManagerListener mListener;
    private List<CustomAction> mCustomActions;

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
        mListener = listener;
        mCustomActions = new ArrayList<>();
    }

    @Override
    protected boolean isEmpty(){
        return mCustomGoal == null;
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

    public void customGoalAdded(@NonNull CustomGoal customGoal){
        mCustomGoal = customGoal;
        mCustomActions = customGoal.getActions();
        mCustomGoalHolder.setButtonEnabled(true);
        mCustomGoalHolder.mButton.setText("Edit");
        notifyListInserted();
        updateLoading(false);
    }

    /**
     * Called when the process of adding a custom action to the user's dataset has been
     * completed. Notifies the recycler view of the insertion of a new element to trigger
     * the proper animation.
     */
    public void customActionAdded(@NonNull CustomAction customAction){
        Log.d(TAG, "Size: " +  mCustomActions.size());
        mActionListHolder.mAdapter.notifyItemInserted(mCustomActions.size() - 1);
        mActionListHolder.mAdapter.notifyItemChanged(mCustomActions.size());
        mActionListHolder.mSwitcher.showPrevious();
    }


    private class CustomGoalHolder
            extends RecyclerView.ViewHolder
            implements TextWatcher, View.OnClickListener{

        private EditText mTitle;
        private TextView mButton;

        private boolean mEditing;


        public CustomGoalHolder(View rootView){
            super(rootView);

            mTitle = (EditText)rootView.findViewById(R.id.create_goal_title);
            mButton = (TextView)rootView.findViewById(R.id.create_goal_button);

            mTitle.addTextChangedListener(this);
            mButton.setOnClickListener(this);

            mEditing = false;
        }

        public void bind(@Nullable CustomGoal customGoal){
            if (customGoal == null){
                mButton.setText("Create");
                setButtonEnabled(false);
            }
            else{
                mTitle.setText(customGoal.getTitle());
                mButton.setText("Edit");
                setButtonEnabled(true);
            }
        }

        private void setButtonEnabled(boolean enabled){
            if (enabled){
                mButton.setTextColor(mContext.getResources().getColor(R.color.grow_primary));
            }
            else{
                mButton.setTextColor(mContext.getResources().getColor(R.color.secondary_text_color));
            }
            mButton.setEnabled(enabled);
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after){
            //Unused
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count){
            setButtonEnabled(count != 0);
        }

        @Override
        public void afterTextChanged(Editable s){
            //Unused
        }

        @Override
        public void onClick(View view){
            switch (view.getId()){
                case R.id.create_goal_button:
                    if (mCustomGoal == null){
                        InputMethodManager imm2 = (InputMethodManager)mContext
                                .getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm2.hideSoftInputFromWindow(mTitle.getWindowToken(), 0);

                        mTitle.clearFocus();
                        mTitle.setFocusable(false);

                        setButtonEnabled(false);
                        mListener.createGoal(new CustomGoal(mTitle.getText().toString().trim()));
                        updateLoading(true);
                    }
                    else{
                        if (mEditing){
                            InputMethodManager imm2 = (InputMethodManager)mContext
                                    .getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm2.hideSoftInputFromWindow(mTitle.getWindowToken(), 0);

                            mTitle.clearFocus();
                            mTitle.setFocusable(false);

                            if (!mCustomGoal.getTitle().equals(mTitle.getText().toString().trim())){
                                mCustomGoal.setTitle(mTitle.getText().toString().trim());
                                mListener.saveGoal(mCustomGoal);
                            }
                            mButton.setText("Edit");
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

                            mButton.setText("Save");
                        }
                        mEditing = !mEditing;
                    }
                    break;
            }
        }
    }


    private class CustomActionListHolder
            extends RecyclerView.ViewHolder
            implements View.OnClickListener{

        private CustomActionAdapter mAdapter;

        private ViewSwitcher mSwitcher;
        private TextView mButton;


        public CustomActionListHolder(View rootView){
            this(rootView, new ArrayList<CustomAction>());
        }

        public CustomActionListHolder(View rootView, List<CustomAction> actions){
            super(rootView);

            mAdapter = new CustomActionAdapter();
            RecyclerView rv = (RecyclerView)rootView.findViewById(R.id.custom_action_list);
            rv.setLayoutManager(new LinearLayoutManager(mContext));
            rv.setAdapter(mAdapter);

            mSwitcher =(ViewSwitcher)rootView.findViewById(R.id.custom_action_list_switcher);
            mButton = (TextView)rootView.findViewById(R.id.custom_action_list_create);
            mButton.setOnClickListener(this);
            setButtonEnabled(false);
        }

        private void setButtonEnabled(boolean enabled){
            if (enabled){
                mButton.setTextColor(mContext.getResources().getColor(R.color.grow_primary));
            }
            else{
                mButton.setTextColor(mContext.getResources().getColor(R.color.secondary_text_color));
            }
            mButton.setEnabled(enabled);
        }

        @Override
        public void onClick(View view){
            switch (view.getId()){
                case R.id.custom_action_list_create:
                    String title = mAdapter.mNewActionHolder.mTitle.getText().toString().trim();
                    mSwitcher.showNext();
                    mListener.createAction(new CustomAction(title, mCustomGoal));
            }
        }
    }


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
            Log.d("ListBind", position+"");
            if (position == getItemCount()-1){
                mNewActionHolder = holder;
                holder.bind(null);
            }
            else{
                holder.bind(mCustomActions.get(position));
            }
        }
    }


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
            if (mCustomAction == null){
                mActionListHolder.setButtonEnabled(count != 0);
            }
        }

        @Override
        public void afterTextChanged(Editable s){

        }

        @Override
        public void onClick(View view){
            switch (view.getId()){
                case R.id.custom_action_trigger:
                    Log.d(TAG, "Editing the trigger of " + mCustomAction);

                    //Open the trigger editor
                    mListener.editTrigger(mCustomActions.get(getAdapterPosition()));
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
                            mListener.saveAction(mCustomAction);
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
        void createGoal(@NonNull CustomGoal customGoal);
        void saveGoal(@NonNull CustomGoal customGoal);
        void createAction(@NonNull CustomAction customAction);
        void saveAction(@NonNull CustomAction customAction);
        void editTrigger(@NonNull CustomAction customAction);
    }
}
