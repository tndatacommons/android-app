package org.tndata.android.compass.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;


/**
 * Created by isma on 9/22/15.
 */
public class MainFeedAdapter extends RecyclerView.Adapter{
    private Context mContext;


    public MainFeedAdapter(Context context){
        mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        if (viewType == 0){
            return new RecyclerView.ViewHolder(new CardView(mContext)){};
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position){
        //if (position == 0){
            WindowManager wm = (WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, (int)((size.x*2/3)*0.8));
            holder.itemView.setLayoutParams(params);
        if (position == 0){
            //holder.itemView.setBackgroundColor(Color.RED);
            holder.itemView.setVisibility(View.INVISIBLE);
        }
        else{
            ((CardView)holder.itemView).setCardBackgroundColor(Color.GREEN);
        }
    }

    @Override
    public int getItemCount(){
        return 2;
    }
}
