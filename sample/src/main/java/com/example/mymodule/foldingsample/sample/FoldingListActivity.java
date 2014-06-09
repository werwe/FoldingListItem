/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.mymodule.foldingsample.sample;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.ptr.folding.FoldingLayout;

import java.util.ArrayList;


public class FoldingListActivity extends FragmentActivity {
    public static final String Debug = "FoldingListActivity";

    private int mTouchItemHeight = 0;
    private float mTouchItemPosY  = 0;

    private FrameLayout mFoldingContainer;
    private ListView mListView;

    private FoldingContainerWrapper mFoldingWrapper;

    private FoldGestureDetector mDetector;
    private  ArrayAdapter<String> mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        mDetector = new FoldGestureDetector(this,mScaleListener);

        mFoldingContainer = (FrameLayout) findViewById(R.id.root_layout);
        mFoldingWrapper = new FoldingContainerWrapper(mFoldingContainer);

        mListView = (ListView) findViewById(R.id.list);
        ArrayList<String> stringList = new ArrayList<String>();
        for(int i = 0 ; i < 20 ; i++)
            stringList.add(i+"");

        mAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,stringList);
        mListView.setAdapter(mAdapter);
//        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                mTouchItemHeight = view.getHeight();
//                mTouchItemPosY = view.getY();
//                mFoldingWrapper.createFoldingView(mTouchItemPosY, mTouchItemHeight);
//            }
//        });
    }

    @SuppressLint("NewApi")
    public View createFoldingView() {
        final FoldingItemViewGroup itemGroup = new FoldingItemViewGroup(mFoldingContainer.getContext());
        final FoldingLayout foldingLayout = (FoldingLayout) View.inflate(getApplicationContext(), R.layout.fragment_folding, null);
        foldingLayout.setBackgroundColor(Color.argb(255, 0, 0, 255));
        foldingLayout.setNumberOfFolds(2);
        foldingLayout.setAnchorFactor(0);
        foldingLayout.setOrientation(FoldingLayout.Orientation.VERTICAL);
        itemGroup.addView(foldingLayout);
        //mFoldingContainer.addView(itemGroup, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,itemHeight));
        return itemGroup;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        mDetector.onTouchEvent(event);
        return super.dispatchTouchEvent(event);
    }

    class FoldGestureDetector extends ScaleGestureDetector {
        public static final int BEGIN = 100;
        public static final int UPDATE = 101;
        public static final int END = 102;

        float mBeginSpanSize = 0;
        int mGestureState = END;
        View mSelectItem = null;
        MotionEvent mStoredMotionEvent;

        public FoldGestureDetector(Context context, OnScaleGestureListener listener) {
            super(context, listener);
        }

        public FoldGestureDetector(Context context, OnScaleGestureListener listener, Handler handler) {
            super(context, listener, handler);
        }

        public boolean updateScale(ScaleGestureDetector detector) {
            mGestureState = UPDATE;
            //debugDetector(detector);
            return true;
        }

        public boolean beginScale(ScaleGestureDetector detector) {
            mGestureState = BEGIN;
            mBeginSpanSize = detector.getCurrentSpan();
            addFoldingItem(mStoredMotionEvent);
            //debugDetector(detector);
            //1.AddItem
            return true;
        }

        public void endScale(ScaleGestureDetector detector) {
            mGestureState = END;
            //debugDetector(detector);
            mSelectItem = null;
            //2. 절반 이상 펼쳐지면 열리고 아니면 닫힘.
        }

        public void addFoldingItem(MotionEvent event) {
            int index = 0;
            int pointerCount = MotionEventCompat.getPointerCount(event);
            if(pointerCount > 2)
                return;

            PointF p1 = new PointF(MotionEventCompat.getX(event,0),MotionEventCompat.getY(event,0));
            PointF p2 = new PointF(MotionEventCompat.getX(event,1),MotionEventCompat.getY(event,1));
            PointF result;
            if(p1.y <= p2.y)
                result = p1;
            else
                result = p2;

            int selectedPosition = mListView.pointToPosition((int) result.x, (int) (result.y - mListView.getY()));
            Log.d(Debug, "selected Position:" + selectedPosition);
            mSelectItem = mListView.getChildAt(selectedPosition);
            if(mSelectItem == null)
                return;

            mTouchItemHeight = mSelectItem.getHeight();
            mTouchItemPosY = mSelectItem.getY();

            mAdapter.insert("new:" + (selectedPosition + 1), selectedPosition + 1);
            mAdapter.notifyDataSetChanged();
            //View v = createFoldingView();
            //mListView.addView(v, selectedPosition + 1);

            //mFoldingWrapper.createFoldingView(mTouchItemPosY, mTouchItemHeight);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            mStoredMotionEvent = event;
            return super.onTouchEvent(event);
        }

        public void debugDetector(ScaleGestureDetector detector)
        {
            float span = detector.getCurrentSpan();
            float factor = detector.getScaleFactor();
            Log.d("ScaleDetector",String.format("span(%f),factor(%f)",span,factor));
        }
    }

    ScaleGestureDetector.OnScaleGestureListener mScaleListener = new ScaleGestureDetector.OnScaleGestureListener() {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            return mDetector.updateScale(detector);
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            return mDetector.beginScale(detector);
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            mDetector.endScale(detector);
        }
    };
}