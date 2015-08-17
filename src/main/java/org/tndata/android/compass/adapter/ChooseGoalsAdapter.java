package org.tndata.android.compass.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.GradientDrawable;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.R;
import org.tndata.android.compass.model.Category;
import org.tndata.android.compass.model.Goal;
import org.tndata.android.compass.ui.button.TransitionButton;
import org.tndata.android.compass.ui.parallaxrecyclerview.HeaderLayoutManagerFixed;
import org.tndata.android.compass.ui.parallaxrecyclerview.ParallaxRecyclerAdapter;
import org.tndata.android.compass.util.CompassTagHandler;
import org.tndata.android.compass.util.ImageLoader;

import java.util.ArrayList;
import java.util.List;


/**
 * Adapter for the goal picker.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class ChooseGoalsAdapter
        extends ParallaxRecyclerAdapter<Goal>
        implements ParallaxRecyclerAdapter.OnClickEvent{

    private static final byte STATE_NOT_ADDED = 0;
    private static final byte STATE_ADDING = 1;
    private static final byte STATE_ADDED = 2;
    private static final byte STATE_REMOVING = 3;

    private byte goalStates[];
    

    private Context mContext;
    private ChooseGoalsListener mListener;
    private CompassApplication mApplication;
    private RecyclerView mRecyclerView;
    private Category mCategory;
    private List<Goal> mGoals;

    private int mExpandedGoal;


    public ChooseGoalsAdapter(@NonNull Context context, @NonNull ChooseGoalsListener listener,
                              @NonNull CompassApplication app, @NonNull RecyclerView recyclerView,
                              @NonNull Category category){
        super(new ArrayList<Goal>());

        mContext = context;
        mListener = listener;
        mApplication = app;
        mRecyclerView = recyclerView;
        mCategory = category;
        mGoals = new ArrayList<>();

        Goal headerGoal = new Goal();
        headerGoal.setDescription(mCategory.getDescription());
        headerGoal.setId(0);
        mGoals.add(headerGoal);
        setData(mGoals);

        mExpandedGoal = -1;

        setHeader();

        implementRecyclerAdapterMethods(new ChooseGoalsAdapterMethods());
        setOnClickEvent(this);
    }

    private void populateStateArray(){
        if (mGoals != null && mGoals.size() > 1){
            goalStates = new byte[mGoals.size()-1];
            List<Goal> userGoals = mApplication.getGoals();
            for (int i = 1; i < mGoals.size(); i++){
                goalStates[i-1] = userGoals.contains(getItem(i)) ? STATE_ADDED : STATE_NOT_ADDED;
            }
        }
    }

    public void addGoals(List<Goal> goals){
        mGoals.addAll(goals);
        populateStateArray();
        notifyDataSetChanged();
    }

    public Goal getItem(int position){
        return mGoals.get(position);
    }

    public void goalAdded(Goal goal){
        int index = mGoals.indexOf(goal);
        goalStates[index-1] = STATE_ADDED;
        ChooseGoalsViewHolder holder = (ChooseGoalsViewHolder)mRecyclerView.findViewHolderForLayoutPosition(index+1);
        if (holder != null){
            holder.select.setActive(true);
        }
    }

    public void goalNotAdded(Goal goal){
        int index = mGoals.indexOf(goal);
        goalStates[index-1] = STATE_NOT_ADDED;
        ChooseGoalsViewHolder holder = (ChooseGoalsViewHolder)mRecyclerView.findViewHolderForLayoutPosition(index+1);
        if (holder != null){
            holder.select.setInactive(true);
        }
    }

    public void goalDeleted(Goal goal){
        int index = mGoals.indexOf(goal);
        ChooseGoalsViewHolder holder = (ChooseGoalsViewHolder)mRecyclerView.findViewHolderForLayoutPosition(index+1);

        goalStates[index-1] = STATE_NOT_ADDED;
        if (holder != null){
            holder.select.setInactive(true);
        }
    }

    private void setHeader(){
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View header = inflater.inflate(R.layout.header_choose_goals, mRecyclerView, false);

        ImageView headerImageView = (ImageView)header.findViewById(R.id.choose_goals_header_imageview);
        mCategory.loadImageIntoView(mContext, headerImageView);
        Bitmap bmp = ((BitmapDrawable)headerImageView.getDrawable()).getBitmap();
        if (bmp != null){
            int size = (int)mContext.getResources().getDimension(R.dimen.header_category_icon_image_size);
            Bitmap thumbnail = ThumbnailUtils.extractThumbnail(bmp, size, size);
            Bitmap output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(output);

            int color = Color.RED;
            Paint paint = new Paint();
            Rect rect = new Rect(0, 0, size, size);
            RectF rectF = new RectF(rect);

            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(color);
            canvas.drawOval(rectF, paint);

            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(thumbnail, rect, rect, paint);

            headerImageView.setImageBitmap(output);
        }

        ((HeaderLayoutManagerFixed)mRecyclerView.getLayoutManager()).setHeaderIncrementFixer(header);
        setShouldClipView(false);
        setParallaxHeader(header, mRecyclerView);
        setOnParallaxScroll(new ParallaxRecyclerAdapter.OnParallaxScroll(){
            @Override
            public void onParallaxScroll(float percentage, float offset, View parallax){
                mListener.onScroll(percentage, offset);
            }
        });
    }


    class ChooseGoalsAdapterMethods implements RecyclerAdapterMethods{
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int position){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View rootView = inflater.inflate(R.layout.item_choose_goal, parent, false);
            return new ChooseGoalsViewHolder(rootView);
        }

        @Override
        @SuppressWarnings("deprecation")
        public void onBindViewHolder(RecyclerView.ViewHolder rawHolder, int position){
            ChooseGoalsViewHolder holder = (ChooseGoalsViewHolder)rawHolder;
            final Goal goal = getItem(position);

            //If the goal is the first item, display the header card
            if (position == 0 && goal.getId() == 0){
                holder.title.setVisibility(View.GONE);
                holder.iconContainer.setVisibility(View.GONE);
                holder.detailContainer.setVisibility(View.GONE);
                holder.description.setVisibility(View.GONE);
                holder.header.setVisibility(View.VISIBLE);
                if (!goal.getHTMLDescription().isEmpty()){
                    holder.header.setText(Html.fromHtml(goal.getHTMLDescription(), null,
                            new CompassTagHandler()));
                }
                else{
                    holder.header.setText(goal.getDescription());
                }
            }
            //Otherwise, handle all other cards
            else{
                holder.title.setText(goal.getTitle());
                if (mExpandedGoal == position){
                    holder.iconContainer.setVisibility(View.GONE);
                    holder.description.setVisibility(View.VISIBLE);
                }
                else{
                    holder.description.setVisibility(View.GONE);
                    holder.iconContainer.setVisibility(View.VISIBLE);
                }

                if (!goal.getHTMLDescription().isEmpty()){
                    holder.description.setText(Html.fromHtml(goal.getHTMLDescription(), null,
                            new CompassTagHandler()));
                }
                else{
                    holder.description.setText(goal.getDescription());
                }

                if (goal.getIconUrl() != null && !goal.getIconUrl().isEmpty()){
                    ImageLoader.loadBitmap(holder.icon, goal.getIconUrl(), false);
                }

                switch (goalStates[position-1]){
                    case STATE_NOT_ADDED:
                        holder.select.setInactive(false);
                        break;

                    case STATE_ADDING:
                        holder.select.setTransitioningToActive(false);
                        break;

                    case STATE_ADDED:
                        holder.select.setActive(false);
                        break;

                    case STATE_REMOVING:
                        holder.select.setTransitioningToInactive(false);
                        break;
                }

                GradientDrawable gradientDrawable = (GradientDrawable)holder.iconContainer.getBackground();
                String colorString = mCategory.getColor();
                if (colorString != null && !colorString.isEmpty()){
                    gradientDrawable.setColor(Color.parseColor(colorString));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
                        holder.iconContainer.setBackground(gradientDrawable);
                    }
                    else{
                        holder.iconContainer.setBackgroundDrawable(gradientDrawable);
                    }
                }
            }
        }

        @Override
        public int getItemCount(){
            return mGoals.size();
        }
    }


    @Override
    public void onClick(View v, int position){
        if (position > 0){
            if (mExpandedGoal == position){
                mExpandedGoal = -1;
            }
            else{
                if (mExpandedGoal >= 0){
                    notifyItemChanged(mExpandedGoal);
                }
            }
            try{
                //Let us redraw the item that has changed, this forces the RecyclerView to
                //  respect the layout of each item, and none will overlap. Add 1 to position
                //  to account for the header view.
                mExpandedGoal = position+1;
                notifyItemChanged(mExpandedGoal);
                mRecyclerView.scrollToPosition(mExpandedGoal);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }


    class ChooseGoalsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView title;
        private TextView description;
        private RelativeLayout iconContainer;
        private ImageView icon;
        private TransitionButton select;
        private RelativeLayout detailContainer;
        private TextView header;


        public ChooseGoalsViewHolder(View itemView) {
            super(itemView);
            icon = (ImageView)itemView.findViewById(R.id.choose_goal_icon);
            iconContainer = (RelativeLayout)itemView.findViewById(R.id.choose_goal_icon_container);
            title = (TextView)itemView.findViewById(R.id.choose_goal_title);
            select = (TransitionButton)itemView.findViewById(R.id.choose_goal_select);
            description = (TextView)itemView.findViewById(R.id.choose_goal_description);
            detailContainer = (RelativeLayout)itemView.findViewById(R.id.choose_goal_detail_container);
            header = (TextView)itemView.findViewById(R.id.choose_goal_header);

            select.setOnClickListener(this);
        }

        @Override
        public void onClick(View v){
            //Account for header only
            Goal goal = getItem(getAdapterPosition() - 1);

            //Account for the header and the category description
            switch (goalStates[getAdapterPosition()-2]){
                case STATE_NOT_ADDED:
                    if (goal.getBehaviorCount() > 0){
                        goalStates[getAdapterPosition()-2] = STATE_ADDED;
                        notifyItemChanged(getAdapterPosition());
                    }
                    else{
                        goalStates[getAdapterPosition()-2] = STATE_ADDING;
                        select.setTransitioningToActive();
                        Toast.makeText(mContext, R.string.goal_selected, Toast.LENGTH_SHORT).show();
                    }
                    mListener.onGoalAddClicked(goal);
                    break;

                case STATE_ADDED:
                    goalStates[getAdapterPosition()-2] = STATE_REMOVING;
                    select.setTransitioningToInactive();
                    mListener.onGoalDeleteClicked(goal);
                    break;
            }
        }
    }


    public interface ChooseGoalsListener{
        void onGoalAddClicked(Goal goal);
        void onGoalDeleteClicked(Goal goal);
        void onScroll(float percentage, float offset);
    }
}
