package com.example.mymodule.foldingsample.sample;

import android.app.Activity;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.ptr.folding.FoldingLayout;
import com.ptr.folding.listener.OnFoldListener;

public class FoldingFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";

    // TODO: Rename and change types of parameters
    private float mAnchorFactor;
    private int mItemHeight;
    private float mItemPosY;

    private OnFragmentInteractionListener mListener;

    private ViewGroup mLayoutContainer;
    private FoldingLayout mFoldingLayout;

    public static FoldingFragment newInstance(float anchorFactor,int itemHeight, float itemPosY) {
        FoldingFragment fragment = new FoldingFragment();
        Bundle args = new Bundle();
        args.putFloat(ARG_PARAM1,anchorFactor);
        args.putInt(ARG_PARAM2, itemHeight);
        args.putFloat(ARG_PARAM3, itemPosY);
        fragment.setArguments(args);
        return fragment;
    }

    public FoldingFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mAnchorFactor = getArguments().getFloat(ARG_PARAM1);
            mItemHeight = getArguments().getInt(ARG_PARAM2);
            mItemPosY = getArguments().getFloat(ARG_PARAM3);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mLayoutContainer = new FrameLayout(getActivity());
        mLayoutContainer.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,mItemHeight));
        mLayoutContainer.setBackgroundColor(Color.argb(30, 255, 0, 0));
        Log.d("FoldlingFragment", "result Y :" + ((mItemPosY + mItemHeight) - (mItemHeight / 2)));
        //mLayoutContainer.setY((mItemPosY+mItemHeight) - (mItemHeight/2));

        mFoldingLayout = (FoldingLayout)inflater.inflate(R.layout.fragment_folding,mLayoutContainer,false);

        mFoldingLayout.setFoldListener(mOnFoldListener);
        mFoldingLayout.setNumberOfFolds(2);
        mFoldingLayout.setAnchorFactor(0.5f);
        mFoldingLayout.setOrientation(FoldingLayout.Orientation.VERTICAL);
        mFoldingLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    Log.d("FoldingFragment", "FoldFactor:" + mFoldingLayout.getFoldFactor());
                    if(mFoldingLayout.getFoldFactor() == 0) fold();
                    else unfold();
                }
                return true;
            }
        });
        return mLayoutContainer;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mFoldingLayout.setFoldFactor(1f);
        unfold();
        mFoldingLayout.post(new Runnable() {
            @Override
            public void run() {
                mFoldingLayout.findViewById(R.id.contents).setVisibility(View.VISIBLE);
            }
        });
    }

    private OnFoldListener mOnFoldListener =
            new OnFoldListener() {
                @Override
                public void onStartFold() {
                    Log.d("Fold", "fold start");
                }

                @Override
                public void onEndFold() {
                    Log.d("Fold", "fold end");
                    //((FoldingListActivity) FoldingFragment.this.getActivity()).getSupportFragmentManager().removeFragment();
                }
            };

    public void fold() {
        Log.d("Fold", "fold");
        mFoldingLayout.fold();
    }

    public void unfold() {
        Log.d("Fold", "UnFold");
        mFoldingLayout.unfold();
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }



}