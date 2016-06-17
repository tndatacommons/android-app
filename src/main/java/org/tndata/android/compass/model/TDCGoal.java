package org.tndata.android.compass.model;

import android.os.Parcel;
import android.widget.ImageView;

import com.google.gson.annotations.SerializedName;

import org.tndata.android.compass.util.ImageLoader;

import java.util.HashSet;
import java.util.Set;


/**
 * Model class for goals.
 *
 * @author Edited by Ismael Alonso
 * @version 1.0.0
 */
public class TDCGoal extends TDCContent{
    public static final String TYPE = "goal";


    @SerializedName("outcome")
    private String mOutcome = "";
    @SerializedName("categories")
    private Set<Long> mCategoryIdSet;


    /*---------*
     * GETTERS *
     *---------*/

    public String getOutcome(){
        return mOutcome;
    }

    public Set<Long> getCategoryIdSet(){
        return mCategoryIdSet;
    }

    @Override
    protected String getType(){
        return TYPE;
    }


    /*---------*
     * UTILITY *
     *---------*/

    /**
     * Given a Context and an ImageView, load this Goal's icon (if the user has selected
     * no Behaviors) or load the goal's Progress Icons.
     *
     * @param target the image view to load the icon into
     */
    public void loadIconIntoView(ImageView target){
        ImageLoader.loadBitmap(target, getIconUrl());
    }

    @Override
    public String toString(){
        return "GoalContent #" + getId() + ": " + getTitle();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags){
        super.writeToParcel(dest, flags);
        dest.writeString(mOutcome);
        dest.writeInt(mCategoryIdSet.size());
        for (Long categoryId:mCategoryIdSet){
            dest.writeLong(categoryId);
        }
    }

    public static final Creator<TDCGoal> CREATOR = new Creator<TDCGoal>(){
        @Override
        public TDCGoal createFromParcel(Parcel source){
            return new TDCGoal(source);
        }

        @Override
        public TDCGoal[] newArray(int size){
            return new TDCGoal[size];
        }
    };

    /**
     * Constructor to create from a parcel.
     *
     * @param src the source parcel.
     */
    private TDCGoal(Parcel src){
        super(src);
        mOutcome = src.readString();
        mCategoryIdSet = new HashSet<>();
        for (int i = 0, length = src.readInt(); i < length; i++){
            mCategoryIdSet.add(src.readLong());
        }
    }
}
