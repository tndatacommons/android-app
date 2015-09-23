package org.tndata.android.compass.activity;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;

import org.tndata.android.compass.R;
import org.tndata.android.compass.adapter.MainFeedAdapter;
import org.tndata.android.compass.util.ParallaxEffect;


/**
 * Created by isma on 9/22/15.
 */
public class NewMainActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_main);

        View header = findViewById(R.id.main_illustration);

        RecyclerView feed = (RecyclerView)findViewById(R.id.main_feed);
        feed.setAdapter(new MainFeedAdapter(this));
        feed.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        feed.addItemDecoration(new ItemPadding());
        feed.setOnScrollListener(new ParallaxEffect(header, 0.5f));
    }

    /**
     * Decoration class to establish the grid's items margin.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    final class ItemPadding extends RecyclerView.ItemDecoration{
        private int margin;


        /**
         * Constructor.
         */
        public ItemPadding(){
            margin = (int)Math.ceil(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    8, getResources().getDisplayMetrics()));
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                   RecyclerView.State state){

            outRect.top = margin / 2;
            outRect.left = margin / 2;
            outRect.bottom = margin / 2;
            outRect.right = margin / 2;
        }
    }
}
