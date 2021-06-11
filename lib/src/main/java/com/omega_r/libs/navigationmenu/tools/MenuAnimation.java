package com.omega_r.libs.navigationmenu.tools;

import android.content.res.Configuration;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;

import com.omega_r.navigationmenu.R;

import java.util.Locale;

public class MenuAnimation extends Animation {

    private static final int MAX_DURATION = 3000;

    private View mContentView;
    private final float mScaleCoef;
    private final float mTranslationCoefEn = 0.7f;
    private final float mTranslationCoefAR = - 0.7f;
    private float mScaleXDiff;
    private float mScaleYDiff;
    private float mTranslateXDiff;
    private float mScaleXStart;
    private float mScaleYStart;
    private float mTranslateXStart;
    private final float mMaxTranslationZ;

    private boolean mInit = true;
    private boolean mShowing;
    @Nullable
    private OnAnimationTimeChangedListener mOnAnimationTimeChangedListener;

    public MenuAnimation(View contentView, float scaleCoef) {
        mContentView = contentView;
        mMaxTranslationZ = contentView.getResources().getDimension(R.dimen.menu_max_translaion_z);
        mScaleCoef = scaleCoef;
        setInterpolator(new DecelerateInterpolator());
    }

    private void recountScale() {
        mScaleXStart = mContentView.getScaleX();
        mScaleYStart = mContentView.getScaleY();
        mTranslateXStart = mContentView.getX();
    }

    private float getDirection(){
        Configuration config = mContentView.getResources().getConfiguration();
        if(config.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL) {
            return mTranslationCoefAR;
        }else {
            return mTranslationCoefEn;
        }
//        if (Locale.getDefault().getLanguage().equals("ar")){
//            return mTranslationCoefAR;
//        }else {
//            return mTranslationCoefEn;
//        }
    }

    public MenuAnimation show() {
        recountScale();
        mScaleXDiff = mScaleCoef - mScaleXStart;
        mScaleYDiff = mScaleCoef - mScaleYStart;
        mTranslateXDiff = mContentView.getWidth() * getDirection() - mTranslateXStart;
        setDuration(Math.abs((long) (MAX_DURATION * mScaleXDiff)));
        mShowing = true;
        return this;
    }

    public MenuAnimation hide() {
        recountScale();
        mScaleXDiff = 1 - mScaleXStart;
        mScaleYDiff = 1 - mScaleYStart;
        mTranslateXDiff = 0 - mTranslateXStart;
        setDuration(Math.abs((long) (MAX_DURATION * mScaleXDiff)));
        mShowing = false;
        return this;
    }

    public void applyTo(final float interpolatedTime) {
        apply(interpolatedTime);
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        apply(interpolatedTime);
    }


    private void apply(float interpolatedTime) {
        if (mInit) {
            show();
            mInit = false;
        }

        Log.d("TAG", "apply1: "+mTranslateXStart );
        Log.d("TAG", "apply2: "+mTranslateXDiff );
        Log.d("TAG", "apply3: "+interpolatedTime );
//        Log.d("TAG", "apply1: "+(mTranslateXStart + mTranslateXDiff * interpolatedTime));
//        Log.d("TAG", "apply2: "+(mScaleXStart + mScaleXDiff * interpolatedTime));
//        Log.d("TAG", "apply3: "+(mScaleYStart + mScaleYDiff * interpolatedTime));

        mContentView.setX(mTranslateXStart + mTranslateXDiff * interpolatedTime);
        mContentView.setScaleX(mScaleXStart + mScaleXDiff * interpolatedTime);
        mContentView.setScaleY(mScaleYStart + mScaleYDiff * interpolatedTime);
        if (mShowing) {
            ViewCompat.setTranslationZ(mContentView, interpolatedTime * mMaxTranslationZ);
        } else {
            ViewCompat.setTranslationZ(mContentView, (1 - interpolatedTime) * mMaxTranslationZ);
        }

        if (mOnAnimationTimeChangedListener != null) {
            mOnAnimationTimeChangedListener.onAnimationTimeChanged(interpolatedTime);
        }
    }

    public void setOnAnimationTimeChangedListener(@Nullable OnAnimationTimeChangedListener listener) {
        mOnAnimationTimeChangedListener = listener;
    }

    public interface OnAnimationTimeChangedListener {
        void onAnimationTimeChanged(float time);
    }

}
