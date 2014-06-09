package com.example.mymodule.foldingsample.sample;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;

import com.ptr.folding.FoldingLayout;

/**
 * Created by werwe on 2014. 6. 5..
 * ViewGroup Spec
 * 처음 높이는 0이어야함.
 * 너비는 match_parent
 * 뷰그룹의 크기를 조절 할 수 있어야함. sizeFactor( 0~1 )
 * 차일드뷰의 총 높이 만큼  커 질 수 있음. ( 0 ~ 차일드 뷰의 높이 합 )
 * 뷰 그룹의 크기와 관계없이 차일드 뷰의 크기를 보장 해야 함 ( 뷰그룹의 크기에 따라 차일드 뷰의 내용이 줄어들가나 하지 않고 지정된 크기를 유지 해야함)
 * CenterGravity를 가져야함. 자기 자신도 그렇고 차일드도 그렇다.
 * 오직 1개의 차일드를 가진다.
 */
public class FoldingItemViewGroup extends ViewGroup {

    private int mChildHeight = 0;
    public FoldingItemViewGroup(Context context) {
        super(context);
        //setBackgroundColor(Color.argb(33,0,255,0));
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                 unfold();
                if ( Build.VERSION.SDK_INT >= 16 ) // or Build.VERSION_CODES.JELLY_BEAN
                    getViewTreeObserver().removeOnGlobalLayoutListener( this );
                else
                    getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });
    }

    public FoldingItemViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FoldingItemViewGroup(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec,heightMeasureSpec);
        final int contentWidth = MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft() - getPaddingRight();
        int childCount = getChildCount();

        for(int i = 0 ; i < childCount ; i++)
        {
            View child = getChildAt(i);
            int size = MeasureSpec.getSize(heightMeasureSpec);
            measureChild(child, MeasureSpec.makeMeasureSpec(contentWidth,MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(size,MeasureSpec.EXACTLY));
            mChildHeight += child.getMeasuredHeight();
        }
        int measuredWidth = MeasureSpec.getSize(widthMeasureSpec);
        setMeasuredDimension(measuredWidth, mChildHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = getChildCount();
        for(int i = 0 ; i < childCount ; i++) {
            View child = getChildAt(i);
            child.layout(l,t,r,b+child.getMeasuredHeight());
        }
    }

    public void fold ()
    {
        PropertyValuesHolder bottomHolder = PropertyValuesHolder.ofInt("bottom", mChildHeight, 0);
        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(this, bottomHolder);
        animator.setDuration(500);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            FoldingLayout item = (FoldingLayout) getChildAt(0);

            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                Integer value = (Integer) valueAnimator.getAnimatedValue();
                float foldFactor = value.floatValue() / (float) mChildHeight;
                item.setFoldFactor(1 - foldFactor);
            }
        });
        animator.start();
    }

    public void unfold ()
    {
        PropertyValuesHolder bottomHolder = PropertyValuesHolder.ofInt("bottom", 0, mChildHeight);
        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(this, bottomHolder);
        animator.setDuration(500);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            FoldingLayout item = (FoldingLayout) getChildAt(0);

            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                Integer value = (Integer) valueAnimator.getAnimatedValue();
                float foldFactor = value.floatValue() / (float) mChildHeight;
                item.setFoldFactor(1 - foldFactor);
            }
        });
        animator.start();

    }

    public float getFoldFactor() {
        return ((FoldingLayout)getChildAt(0)).getFoldFactor();
    }
}
