package com.example.mymodule.foldingsample.sample;

import android.animation.LayoutTransition;
import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import com.ptr.folding.FoldingLayout;
import com.ptr.folding.listener.OnFoldListener;

import java.util.zip.Inflater;

public class FoldingContainerWrapper  {

    private FrameLayout mFoldingContainer;
    private LayoutInflater inflater;


    @SuppressLint("NewApi")
    public FoldingContainerWrapper(FrameLayout container) {
        mFoldingContainer = container;
        inflater = LayoutInflater.from(container.getContext());
    }

    @SuppressLint("NewApi")
    public View createFoldingView(float itemPosY, int itemHeight) {
        final FoldingItemViewGroup itemGroup = new FoldingItemViewGroup(mFoldingContainer.getContext());
        final FoldingLayout foldingLayout = (FoldingLayout) inflater.inflate( R.layout.fragment_folding, null,false);
        foldingLayout.setBackgroundColor(Color.argb(255,0,0,255));
        foldingLayout.setNumberOfFolds(2);
        foldingLayout.setAnchorFactor(0);
        foldingLayout.setOrientation(FoldingLayout.Orientation.VERTICAL);

//        itemGroup.setOnTouchListener(new View.OnTouchListener() {
//            FoldingItemViewGroup item = itemGroup;
//
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                Rect hitRect = new Rect();
//                view.getHitRect(hitRect);
//                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
//                    if (item.getFoldFactor() == 0) item.fold();
//                    else item.unfold();
//                }
//                return true;
//            }
//        });

        itemGroup.setY(itemPosY);
        itemGroup.addView(foldingLayout);
        mFoldingContainer.addView(itemGroup, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,itemHeight));
        return mFoldingContainer;
    }
}