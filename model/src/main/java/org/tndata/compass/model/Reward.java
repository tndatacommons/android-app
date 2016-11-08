package org.tndata.compass.model;

import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AlignmentSpan;

import com.google.gson.annotations.SerializedName;


/**
 * Model class for reward content.
 *
 * @author Ismael Alonso
 * @version 1.1.0
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

    public String getMessageType(){
        return mMessageType;
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

    @DrawableRes
    public int getIcon(){
        if (isFortune()){
            return R.drawable.ic_fortune_150dp;
        }
        else if (isFunFact()){
            return R.drawable.ic_fact_150dp;
        }
        else if (isJoke()){
            return R.drawable.ic_joke_150dp;
        }
        else if (isQuote()){
            return R.drawable.ic_quote_150dp;
        }
        return -1;
    }

    @StringRes
    public int getHeader(){
        if (isFortune()){
            return R.string.reward_fortune_header;
        }
        else if (isFunFact()){
            return R.string.reward_fact_header;
        }
        else if (isJoke()){
            return R.string.reward_joke_header;
        }
        else if (isQuote()){
            return R.string.reward_quote_header;
        }
        return -1;
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

    /**
     * Shares this reward via Intent.
     *
     * @param context a reference to the context.
     */
    public void share(Context context){
        //Build the content string
        String content = "";
        if (isJoke()){
            content = context.getString(R.string.reward_joke) + ": ";
        }
        if (isFunFact()){
            content = context.getString(R.string.reward_fact) + ": ";
        }
        if (isFortune()){
            content = context.getString(R.string.reward_fortune) + ": ";
        }
        if (isQuote()){
            content = getAuthor() + ": \"";
        }
        content += getMessage();
        if (isQuote()){
            content += "\"";
        }

        //Send the intent
        Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, content);
        context.startActivity(Intent.createChooser(
                shareIntent, context.getString(R.string.reward_share_via)
        ));
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

    public static final Creator<Reward> CREATOR = new Creator<Reward>(){
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
