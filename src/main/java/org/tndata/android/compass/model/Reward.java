package org.tndata.android.compass.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AlignmentSpan;

import com.google.gson.annotations.SerializedName;


/**
 * Model class for reward content.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class Reward implements Parcelable{
    private static final String QUOTE = "quote";
    private static final String FORTUNE = "fortune";
    private static final String FACT = "fact";
    private static final String JOKE = "joke";


    @SerializedName("id")
    private long mId;
    @SerializedName("message_type")
    private String mMessageType;
    @SerializedName("message")
    private String mMessage;
    @SerializedName("author")
    private String mAuthor;


    public Reward(long id, String messageType, String message, String author){
        mId = id;
        mMessageType = messageType;
        mMessage = message;
        mAuthor = author;
    }

    public long getId(){
        return mId;
    }

    public String getMessage(){
        return mMessage;
    }

    public String getAuthor(){
        return mAuthor;
    }

    public boolean isQuote(){
        return mMessageType.equals(QUOTE);
    }

    public boolean isFortune(){
        return mMessageType.equals(FORTUNE);
    }

    public boolean isFunFact(){
        return mMessageType.equals(FACT);
    }

    public boolean isJoke(){
        return mMessageType.equals(JOKE);
    }

    public SpannableString format(){
        SpannableString string;
        if (isQuote()){
            string = new SpannableString(mMessage + "\n—" + mAuthor);
            int start = string.length()-mAuthor.length()-1;
            int end  = string.length();
            string.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_OPPOSITE), start, end,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        else{
            string = new SpannableString(mMessage);
        }
        return string;
    }

    @Override
    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags){
        dest.writeLong(mId);
        dest.writeString(mMessageType);
        dest.writeString(mMessage);
        dest.writeString(mAuthor);
    }

    public static final Parcelable.Creator<Reward> CREATOR = new Parcelable.Creator<Reward>(){
        @Override
        public Reward createFromParcel(Parcel in){
            return new Reward(in);
        }

        @Override
        public Reward[] newArray(int size){
            return new Reward[size];
        }
    };

    /**
     * Constructor to create from parcel.
     *
     * @param in the parcel where the object is stored.
     */
    private Reward(Parcel in){
        this(in.readLong(), in.readString(), in.readString(), in.readString());
    }
}
