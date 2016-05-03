package org.tndata.android.compass.model;

import android.content.Context;
import android.os.Parcel;
import android.widget.ImageView;

import com.google.gson.annotations.SerializedName;

import org.tndata.android.compass.R;
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
    @SerializedName("behaviors_count")
    private int mBehaviorCount = 0;

    private String mColor;


    /*---------*
     * SETTERS *
     *---------*/

    public void setOutcome(String outcome){
        this.mOutcome = outcome;
    }

    public void setCategories(Set<Long> categories){
        this.mCategoryIdSet = categories;
    }

    public void setBehaviorCount(int behaviorCount){
        this.mBehaviorCount = behaviorCount;
    }

    public void setColor(String color){
        mColor = color;
    }


    /*---------*
     * GETTERS *
     *---------*/

    public String getOutcome(){
        return mOutcome;
    }

    public Set<Long> getCategoryIdSet(){
        return mCategoryIdSet;
    }

    public int getBehaviorCount(){
        return mBehaviorCount;
    }

    public String getColor(Context context){
        if (mColor == null || mColor.isEmpty()){
            return String.format("#%06X", 0xFFFFFF & context.getResources().getColor(R.color.primary));
        }
        return mColor;
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
     * @param imageView: an ImageView
     */
    public void loadIconIntoView(ImageView imageView){
        String iconUrl = getIconUrl();
        if (iconUrl != null && !iconUrl.isEmpty()){
            ImageLoader.loadBitmap(imageView, iconUrl);
        }
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
        dest.writeInt(mBehaviorCount);
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
        mBehaviorCount = src.readInt();
    }
}
