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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import org.tndata.android.compass.R;


/**
 * A button that includes indeterminate progress. Can be set to rotate and change colors.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class TransitionButton extends ImageView implements Animation.AnimationListener{
    //States and state variables
    private static final int STATE_UNDEFINED = 0;
    private static final int STATE_ACTIVE = 1;
    private static final int STATE_TRANSITIONING = 2;
    private static final int STATE_INACTIVE = 3;

    private int mState;
    private int mPreviousState;
    private boolean mAnimateStateChange;


    //Colors
    private int mColorActive;
    private int mColorTransition;
    private int mColorInactive;

    private boolean mApplyColorToBackground;

    //Animations
    private Animation rotationLeft;
    private Animation rotationRight;
    private Animation rotationLeftEighth;
    private Animation rotationRightEighth;


    /**
     * Constructor.
     *
     * @param context the application context.
     */
    public TransitionButton(Context context){
        super(context);
        init(null);
    }

    /**
     * Constructor.
     *
     * @param context the application context.
     * @param attrs the attribute set.
     */
    public TransitionButton(Context context, AttributeSet attrs){
        super(context, attrs);
        init(attrs);
    }

    /**
     * Constructor.
     *
     * @param context the application context.
     * @param attrs the attribute set.
     * @param defStyleAttr the style definition id.
     */
    public TransitionButton(Context context, AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    /**
     * Initializes the widget.
     *
     * @param attrs the attribute set.
     */
    private void init(AttributeSet attrs){
        //Default color set
        mColorActive = getResources().getColor(R.color.grow_accent);
        mColorTransition = getResources().getColor(R.color.grey_placeholder);
        mColorInactive = getResources().getColor(R.color.grow_primary);

        mApplyColorToBackground = false;

        if (attrs != null){
            retrieveAttributes(attrs);
        }

        //The drawable needs a state of its own
        if (mApplyColorToBackground){
            getBackground().mutate();
        }
        else{
            getDrawable().mutate();
        }

        //The initial state is inactive
        mState = STATE_INACTIVE;
        setDrawableColor(mColorInactive);

        //The animations are loaded and the listeners set
        rotationLeft = AnimationUtils.loadAnimation(getContext(), R.anim.rotation_left);
        rotationLeft.setAnimationListener(this);

        rotationRight = AnimationUtils.loadAnimation(getContext(), R.anim.rotation_right);
        rotationRight.setAnimationListener(this);

        rotationRightEighth = AnimationUtils.loadAnimation(getContext(), R.anim.rotation_right_eighth);
        rotationLeftEighth = AnimationUtils.loadAnimation(getContext(), R.anim.rotation_left_eighth);
    }

    /**
     * Retrieves and populates the attributes from the attribute set.
     *
     * @param attrs the relevant attribute set.
     */
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
                mApplyColorToBackground = ta.getInt(R.styleable.TransitionButton_color_apply_to, 0) == 1;
            }
            finally{
                ta.recycle();
            }
        }
    }

    /**
     * Transitions to active with an animation.
     */
    public void setActive(){
        setActive(true);
    }

    /**
     * Transitions to active.
     *
     * @param animate true if the transition should be animated.
     */
    public void setActive(boolean animate){
        mAnimateStateChange = animate;
        mState = STATE_ACTIVE;
    }

    /**
     * Transitions to the transitioning to active state with an animation.
     */
    public void setTransitioningToActive(){
        setTransitioningToActive(true);
    }

    /**
     * Transitions to the transitioning to active state.
     *
     * @param animate true if the transition should be animated.
     */
    public void setTransitioningToActive(boolean animate){
        mAnimateStateChange = animate;
        mPreviousState = STATE_INACTIVE;
        mState = STATE_TRANSITIONING;
        startAnimations();
    }

    /**
     * Transitions to the transitioning to inactive state with an animation.
     */
    public void setTransitioningToInactive(){
        setTransitioningToInactive(true);
    }

    /**
     * Transitions to the transitioning to inactive state.
     *
     * @param animate true if the transition should be animated.
     */
    public void setTransitioningToInactive(boolean animate){
        mAnimateStateChange = animate;
        mPreviousState = STATE_ACTIVE;
        mState = STATE_TRANSITIONING;
        startAnimations();
    }

    /**
     * Transitions to inactive with an animation.
     */
    public void setInactive(){
        setInactive(true);
    }

    /**
     * Transitions to inactive.
     *
     * @param animate true if the transition should be animated.
     */
    public void setInactive(boolean animate){
        mAnimateStateChange = animate;
        mState = STATE_INACTIVE;
    }

    /**
     * Starts the animations to the transitioning state.
     */
    private void startAnimations(){
        clearAnimation();

        //If the change should be animated
        if (mAnimateStateChange){
            //Dissolve the colors from the previous state to the transitioning color
            if (mPreviousState == STATE_INACTIVE){
                startColorAnimation(mColorInactive, mColorTransition, 600);
            }
            else if (mPreviousState == STATE_ACTIVE){
                startColorAnimation(mColorActive, mColorTransition, 600);
            }
        }
        //Otherwise just set the color.
        else{
            setDrawableColor(mColorTransition);
        }

        //The rotation animations should happen regardless
        if (mPreviousState == STATE_INACTIVE){
            startAnimation(rotationRight);
        }
        else if (mPreviousState == STATE_ACTIVE){
            startAnimation(rotationLeft);
        }
    }

    /**
     * Fires the color transition animation.
     *
     * @param src the source color.
     * @param dst the destination color.
     * @param time the transition time.
     */
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

    /**
     * Sets the color of the drawable.
     *
     * @param color the color of the drawable.
     */
    private void setDrawableColor(int color){
        Drawable drawable;
        if (mApplyColorToBackground){
            drawable = getBackground();
        }
        else{
            drawable = getDrawable();
        }

        if (drawable != null){
            drawable.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
        }
    }

    @Override
    public void onAnimationStart(Animation animation){
        //Unused
    }

    @Override
    public void onAnimationEnd(Animation animation){
        //If the current state is transition
        if (mState == STATE_TRANSITIONING){
            //The relevant animation is fired
            if (mPreviousState == STATE_INACTIVE){
                startAnimation(rotationRight);
            }
            else if (mPreviousState == STATE_ACTIVE){
                //IMPORTANT NOTE: this is here because onAnimationEnd gets called before this
                //  particular animation actually finishes. This is but a hackish workaround
                //  over some of Android's broken baloney, not actually what I actually
                //  intended to do.
                new Handler().postDelayed(new Runnable(){
                    @Override
                    public void run(){
                        startAnimation(rotationLeft);
                    }
                }, 50);
            }
        }
        //Otherwise
        else{
            //If there was a previous state
            if (mPreviousState != STATE_UNDEFINED){
                //It is deleted (pretty much used as a flag for this purpose)
                mPreviousState = STATE_UNDEFINED;

                //The relevant transition animation is fired
                if (mState == STATE_ACTIVE){
                    if (mAnimateStateChange){
                        startAnimation(rotationRightEighth);
                        startColorAnimation(mColorTransition, mColorActive, 675);
                    }
                    else{
                        setDrawableColor(mColorActive);
                    }
                }
                else if (mState == STATE_INACTIVE){
                    if (mAnimateStateChange){
                        startAnimation(rotationLeftEighth);
                        startColorAnimation(mColorTransition, mColorInactive, 675);
                    }
                    else{
                        setDrawableColor(mColorInactive);
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
