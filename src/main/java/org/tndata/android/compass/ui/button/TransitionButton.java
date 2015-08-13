package org.tndata.android.compass.ui.button;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import org.tndata.android.compass.R;


/**
 * Created by isma on 8/12/15.
 */
public class TransitionButton extends ImageView implements Animation.AnimationListener{
    private static final int STATE_UNDEFINED = 0;
    private static final int STATE_ACTIVE = 1;
    private static final int STATE_TRANSITIONING = 2;
    private static final int STATE_INACTIVE = 3;


    private int mColorActive;
    private int mColorTransition;
    private int mColorInactive;

    private int mState;
    private int mPreviousState;
    private boolean mAnimateStateChange;

    private Animation rotationLeft;
    private Animation rotationRight;
    private Animation rotationLeftEighth;
    private Animation rotationRightEighth;


    public TransitionButton(Context context){
        super(context);
    }

    public TransitionButton(Context context, AttributeSet attrs){
        super(context, attrs);
        retrieveAttributes(attrs);
        init();
    }

    public TransitionButton(Context context, AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
        retrieveAttributes(attrs);
        init();
    }

    private void retrieveAttributes(AttributeSet attrs){
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.TransitionButton, 0, 0);
        if (ta != null){
            try{
                mColorActive = ta.getColor(R.styleable.TransitionButton_color_active,
                        getResources().getColor(R.color.grow_accent));
                mColorTransition = ta.getColor(R.styleable.TransitionButton_color_transition,
                        getResources().getColor(R.color.grey_placeholder));
                mColorInactive = ta.getColor(R.styleable.TransitionButton_color_inactive,
                        getResources().getColor(R.color.grow_primary));
            }
            finally{
                ta.recycle();
            }
        }
    }

    private void init(){
        getDrawable().mutate();
        mState = STATE_INACTIVE;
        setDrawableColor(mColorInactive);

        rotationLeft = AnimationUtils.loadAnimation(getContext(), R.anim.rotation_left);
        rotationLeft.setAnimationListener(this);

        rotationRight = AnimationUtils.loadAnimation(getContext(), R.anim.rotation_right);
        rotationRight.setAnimationListener(this);

        rotationRightEighth = AnimationUtils.loadAnimation(getContext(), R.anim.rotation_right_eighth);
        rotationRightEighth.setAnimationListener(this);
    }

    public void setActive(){
        setActive(true);
    }

    public void setActive(boolean animate){
        mAnimateStateChange = animate;
        mState = STATE_ACTIVE;
    }

    public void setTransitioningToActive(){
        setTransitioningToActive(true);
    }

    public void setTransitioningToActive(boolean animate){
        mAnimateStateChange = animate;
        mPreviousState = STATE_INACTIVE;
        mState = STATE_TRANSITIONING;
        startAnimations();
    }

    public void setTransitioningToInactive(){
        setTransitioningToInactive(true);
    }

    public void setTransitioningToInactive(boolean animate){
        mAnimateStateChange = animate;
        mPreviousState = STATE_ACTIVE;
        mState = STATE_TRANSITIONING;
        startAnimations();
    }

    public void setInactive(){
        setInactive(true);
    }

    public void setInactive(boolean animate){
        mAnimateStateChange = animate;
        mState = STATE_INACTIVE;
    }

    private void startAnimations(){
        clearAnimation();
        if (mAnimateStateChange){
            if (mPreviousState == STATE_INACTIVE){
                startColorAnimation(mColorInactive, mColorTransition, 600);
            }
        }
        else{
            setDrawableColor(mColorTransition);
        }

        if (mPreviousState == STATE_INACTIVE){
            startAnimation(rotationRight);
        }
        else if (mPreviousState == STATE_ACTIVE){
            startAnimation(rotationLeft);
        }
    }

    private void startColorAnimation(int src, int dst, int time){
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), src, dst);
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(){
            @Override
            public void onAnimationUpdate(ValueAnimator animator){
                setDrawableColor((Integer)animator.getAnimatedValue());
            }

        });
        colorAnimation.setDuration(time);
        colorAnimation.start();
    }

    private void setDrawableColor(int color){
        Drawable drawable = getDrawable();
        if (drawable != null){
            drawable.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
        }
    }

    @Override
    public void onAnimationStart(Animation animation){
        if (mPreviousState == STATE_UNDEFINED){
            if (mState == STATE_ACTIVE){
                //setRotation(45);
            }
        }
    }

    @Override
    public void onAnimationEnd(Animation animation){
        if (mState == STATE_TRANSITIONING){
            if (mPreviousState == STATE_INACTIVE){
                startAnimation(rotationRight);
            }
            else if (mPreviousState == STATE_ACTIVE){
                startAnimation(rotationRight);
            }
        }
        else{
            if (mPreviousState != STATE_UNDEFINED){
                mPreviousState = STATE_UNDEFINED;
                if (mState == STATE_ACTIVE){
                    if (mAnimateStateChange){
                        startAnimation(rotationRightEighth);
                        startColorAnimation(mColorTransition, mColorActive, 675);
                    }
                    else{
                        setDrawableColor(mColorActive);
                    }
                }
            }
        }
    }

    @Override
    public void onAnimationRepeat(Animation animation){
        //Unused, this is real broken
    }
}
