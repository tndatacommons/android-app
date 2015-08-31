package org.tndata.android.compass.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.R;
import org.tndata.android.compass.model.Action;
import org.tndata.android.compass.model.Behavior;
import org.tndata.android.compass.ui.parallaxrecyclerview.HeaderLayoutManagerFixed;
import org.tndata.android.compass.ui.parallaxrecyclerview.ParallaxRecyclerAdapter;
import org.tndata.android.compass.util.CompassTagHandler;
import org.tndata.android.compass.util.ImageLoader;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by isma on 8/31/15.
 */
public class ChooseActionsAdapter
        extends ParallaxRecyclerAdapter<Action>
        implements ParallaxRecyclerAdapter.OnClickEvent{

    private Context mContext;
    private CompassApplication mApplication;
    private ChooseActionsListener mListener;
    private RecyclerView mRecyclerView;
    private Behavior mBehavior;

    private CompassTagHandler mTagHandler;

    private List<Action> mActions;
    private int mExpandedAction;

    public ChooseActionsAdapter(@NonNull Context context, @NonNull ChooseActionsListener listener,
                                @NonNull CompassApplication app, @NonNull RecyclerView recyclerView,
                                @NonNull Behavior behavior){
        super(new ArrayList<Action>());

        //Assign the references
        mContext = context;
        mApplication = app;
        mListener = listener;
        mRecyclerView = recyclerView;
        mBehavior = behavior;

        //The tag handler is used in a couple of places, so previous instantiation and
        //  reuse might help performance.
        mTagHandler = new CompassTagHandler(mContext);

        //Create an empty list and "nullify" the expanded action.
        mActions = new ArrayList<>();
        mExpandedAction = -1;

        //Create and set the headers
        Action actionHeader = new Action();
        actionHeader.setDescription(mBehavior.getDescription());
        actionHeader.setId(0);
        mActions.add(actionHeader);
        setHeader();

        //Add listeners and interfaces
        implementRecyclerAdapterMethods(new ChooseActionsAdapterMethods());
        setOnClickEvent(this);
    }

    private void setHeader(){
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View header = inflater.inflate(R.layout.header_choose_actions, mRecyclerView, false);
        ImageView goalIconView = (ImageView)header.findViewById(R.id.choose_actions_header_icon);
        mBehavior.loadIconIntoView(mContext, goalIconView);

        ((HeaderLayoutManagerFixed)mRecyclerView.getLayoutManager()).setHeaderIncrementFixer(header);
        setParallaxHeader(header, mRecyclerView);
        setOnParallaxScroll(new ParallaxRecyclerAdapter.OnParallaxScroll(){
            @SuppressLint("NewApi")
            @Override
            public void onParallaxScroll(float percentage, float offset, View parallax){
                mListener.onScroll(percentage, offset);
            }
        });
    }

    public void setActions(List<Action> actions){
        mActions.clear();

        Action headerAction = new Action();
        headerAction.setDescription(mBehavior.getDescription());
        headerAction.setId(0);
        mActions.add(headerAction);

        mActions.addAll(actions);
        notifyDataSetChanged();
    }

    private void moreInfoClicked(int position){
        mListener.moreInfo(mActions.get(position));
    }

    private void editReminderClicked(int position){
        mListener.editReminder(mActions.get(position));
    }

    private void selectActionClicked(ActionViewHolder holder){
        Action action = mActions.get(holder.getAdapterPosition()-1);
        boolean isActionSelected = mApplication.getActions().contains(action);

        if (mBehavior.areCustomTriggersAllowed()){
            if (isActionSelected){
                mListener.deleteAction(action);
            }
            else{
                mListener.addAction(action);
            }
        }
    }

    private void doItNowClicked(int position){
        mListener.doItNow(mActions.get(position));
    }

    @Override
    public void onClick(View v, int position){
        if (position > 0){
            int lastExpanded = mExpandedAction;

            //Add one to the position to account for the header
            if (mExpandedAction == position+1){
                mExpandedAction = -1;
                notifyItemChanged(lastExpanded);
            }
            else{
                mExpandedAction = position+1;
                notifyItemChanged(mExpandedAction);
                //Let us redraw the item that has changed, this forces the RecyclerView to
                //  respect the layout of each item, and none will overlap
                if (lastExpanded != -1){
                    notifyItemChanged(lastExpanded);
                }
                mRecyclerView.scrollToPosition(mExpandedAction);
            }
        }
    }


    private class ChooseActionsAdapterMethods implements ParallaxRecyclerAdapter.RecyclerAdapterMethods{
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View rootView = inflater.inflate(R.layout.item_choose_action, viewGroup, false);
            return new ActionViewHolder(rootView);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder rawHolder, int position){
            ActionViewHolder holder = (ActionViewHolder)rawHolder;

            Action action = mActions.get(position);
            final boolean action_is_selected = mApplication.getActions().contains(action);

            if (position == 0 && action.getId() == 0){
                //Display the header card
                if (!action.getHTMLDescription().isEmpty()){
                    holder.mHeader.setText(Html.fromHtml(action.getHTMLDescription(), null, mTagHandler));
                }
                else{
                    holder.mHeader.setText(action.getDescription());
                }

                holder.mHeader.setVisibility(View.VISIBLE);
                holder.mIcon.setVisibility(View.GONE);
                holder.mTitle.setVisibility(View.GONE);
                holder.mDescription.setVisibility(View.GONE);
                holder.mActionWrapper.setVisibility(View.GONE);
            }
            else{
                //Handle all other cards
                holder.mTitle.setText(action.getTitle());
                if (!action.getHTMLDescription().isEmpty()){
                    holder.mDescription.setText(Html.fromHtml(action.getHTMLDescription(), null, mTagHandler));
                }
                else{
                    holder.mDescription.setText(action.getDescription());
                }

                if (mExpandedAction == position+1){
                    holder.mIcon.setVisibility(View.GONE);
                    holder.mDescription.setVisibility(View.VISIBLE);
                    holder.mActionWrapper.setVisibility(View.VISIBLE);
                }
                else{
                    holder.mIcon.setVisibility(View.VISIBLE);
                    holder.mDescription.setVisibility(View.GONE);
                    holder.mActionWrapper.setVisibility(View.GONE);
                }

                if (action.getMoreInfo().equals("")){
                    holder.mMoreInfo.setVisibility(View.GONE);
                }
                else{
                    holder.mMoreInfo.setVisibility(View.VISIBLE);
                }

                if (action.getExternalResource().isEmpty()){
                    holder.mDoItNow.setVisibility(View.GONE);
                }
                else{
                    holder.mDoItNow.setVisibility(View.VISIBLE);
                }

                if (action.getIconUrl() != null && !action.getIconUrl().isEmpty()){
                    ImageLoader.loadBitmap(holder.mIcon, action.getIconUrl(), new ImageLoader.Options());
                }

                if (action_is_selected){
                    holder.mSelectAction.setImageResource(R.drawable.ic_blue_check_circle);
                    holder.mEditReminder.setVisibility(View.VISIBLE);
                }
                else{
                    holder.mSelectAction.setImageResource(R.drawable.ic_blue_plus_circle);
                    holder.mEditReminder.setVisibility(View.GONE);
                }
            }
        }

        @Override
        public int getItemCount(){
            return mActions.size();
        }
    }


    private class ActionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView mHeader;
        private ImageView mIcon;
        private TextView mTitle;
        private TextView mDescription;

        private LinearLayout mActionWrapper;
        private ImageView mMoreInfo;
        private ImageView mEditReminder;
        private ImageView mSelectAction;
        private TextView mDoItNow;


        public ActionViewHolder(View itemView){
            super(itemView);

            mHeader = (TextView)itemView.findViewById(R.id.choose_action_header);
            mIcon = (ImageView)itemView.findViewById(R.id.choose_action_icon);
            mTitle = (TextView)itemView.findViewById(R.id.choose_icon_title);
            mDescription = (TextView)itemView.findViewById(R.id.choose_action_description);

            mActionWrapper = (LinearLayout)itemView.findViewById(R.id.choose_action_action_wrapper);
            mMoreInfo = (ImageView)itemView.findViewById(R.id.choose_action_more_info);
            mEditReminder = (ImageView)itemView.findViewById(R.id.choose_action_edit_reminder);
            mSelectAction = (ImageView)itemView.findViewById(R.id.choose_action_select);
            mDoItNow = (TextView)itemView.findViewById(R.id.choose_action_do_it_now);

            mMoreInfo.setOnClickListener(this);
            mEditReminder.setOnClickListener(this);
            mSelectAction.setOnClickListener(this);
            mDoItNow.setOnClickListener(this);
        }

        @Override
        public void onClick(View view){
            switch (view.getId()){
                case R.id.choose_action_more_info:
                    moreInfoClicked(getAdapterPosition()-1);
                    break;

                case R.id.choose_action_edit_reminder:
                    editReminderClicked(getAdapterPosition()-1);
                    break;

                case R.id.choose_action_select:
                    selectActionClicked(this);
                    break;

                case R.id.choose_action_do_it_now:
                    doItNowClicked(getAdapterPosition()-1);
                    break;
            }
        }
    }


    public interface ChooseActionsListener{
        void moreInfo(Action action);
        void editReminder(Action action);
        void addAction(Action action);
        void deleteAction(Action action);
        void doItNow(Action action);
        void onScroll(float percentage, float offset);
    }
}
