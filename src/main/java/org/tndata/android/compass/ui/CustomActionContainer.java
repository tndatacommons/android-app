package org.tndata.android.compass.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import org.tndata.android.compass.model.CustomAction;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by isma on 1/25/16.
 */
public class CustomActionContainer extends LinearLayout{
    private List<CustomAction> mActionList;


    public CustomActionContainer(Context context){
        this(context, null, 0);
    }

    public CustomActionContainer(Context context, AttributeSet attrs){
        this(context, attrs, 0);
    }

    public CustomActionContainer(Context context, AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        mActionList = new ArrayList<>();
        setOrientation(VERTICAL);
    }
}
