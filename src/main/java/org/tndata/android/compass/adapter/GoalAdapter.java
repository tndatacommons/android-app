package org.tndata.android.compass.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.R;
import org.tndata.android.compass.util.CompassUtil;


/**
 * Created by isma on 9/24/15.
 */
public class GoalAdapter extends RecyclerView.Adapter{
    private Context mContext;
    private int mSpacingRowHeight;


    public GoalAdapter(@NonNull Context context, int spacingRowHeight){
        mContext = context;
        mSpacingRowHeight = spacingRowHeight;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        return new RecyclerView.ViewHolder(new CardView(mContext)){};
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position){
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mSpacingRowHeight);
        holder.itemView.setLayoutParams(params);
        if (position == 0 || position == 2){
            holder.itemView.setVisibility(View.INVISIBLE);
        }
        else{
            CardView cv = (CardView)holder.itemView;
            cv.setVisibility(View.VISIBLE);
            cv.setCardElevation(CompassUtil.getPixels(mContext, 2));
            if (position%2 == 0)
                cv.setCardBackgroundColor(Color.GREEN);
            else
                cv.setCardBackgroundColor(Color.CYAN);

            if (position == 1){
                cv.setBackgroundResource(R.drawable.top_corner_radius_card);
            }
            else{
                cv.setRadius(0);
            }
        }
    }

    @Override
    public int getItemCount(){
        return 3;
    }
}
