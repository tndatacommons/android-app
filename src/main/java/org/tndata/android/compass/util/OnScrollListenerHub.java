package org.tndata.android.compass.util;

import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;


/**
 * A class to relay onScrolled() and onScrollStateChanged() events to multiple OnScrollListeners.
 * The rationale of this class is that RecyclerView takes a single OnScrollListener, and when a
 * new one is set, the old one is replaced.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class OnScrollListenerHub extends RecyclerView.OnScrollListener{
    private List<RecyclerView.OnScrollListener> mListeners;


    /**
     * Constructor.
     */
    public OnScrollListenerHub(){
        mListeners = new ArrayList<>();
    }

    /**
     * Adds a new listener to the list.
     *
     * @param listener the listener to be added to the list.
     */
    public void addOnScrollListener(RecyclerView.OnScrollListener listener){
        mListeners.add(listener);
    }

    /**
     * Removes a listener from the list.
     *
     * @param listener the listener to be removed from the list.
     */
    public void removeOnScrollListener(RecyclerView.OnScrollListener listener){
        mListeners.remove(listener);
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy){
        for (RecyclerView.OnScrollListener listener:mListeners){
            listener.onScrolled(recyclerView, dx, dy);
        }
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState){
        for (RecyclerView.OnScrollListener listener:mListeners){
            listener.onScrollStateChanged(recyclerView, newState);
        }
    }
}
