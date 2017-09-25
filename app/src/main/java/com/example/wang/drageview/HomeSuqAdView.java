package com.example.wang.drageview;

import android.content.Context;
import android.support.v4.view.ViewConfigurationCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

/**
 * 首页悬浮广告
 * Created by wang on 2017/9/13.
 */

public class HomeSuqAdView extends ViewGroup {

    private int mTouchSlop;//小于这个值认为不是滑懂
    private float mXDown;
    private float mXLastMove;
    private float mXMove;
    private float mYDown;
    private float mYLastMove;
    private float mYMove;
    private Scroller mScroller;
    private View mChildAt;
    private int mBottom = 100;

    public HomeSuqAdView(Context context) {
        super(context);
    }

    public HomeSuqAdView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HomeSuqAdView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mScroller = new Scroller(getContext());
        ViewConfiguration configuration = ViewConfiguration.get(getContext());
        mTouchSlop = ViewConfigurationCompat.getScaledPagingTouchSlop(configuration);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measureChildren(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mXDown = ev.getRawX();
                mYDown = ev.getRawY();
                mXLastMove = mXDown;
                mYLastMove = mYDown;
                break;
            case MotionEvent.ACTION_MOVE:
                mXMove = ev.getRawX();
                mYMove = ev.getRawY();
                float diffX = Math.abs(mXMove - mXDown);
                float diffY = Math.abs(mYMove - mYDown);
                mXLastMove = mXMove;
                // 当手指拖动值大于TouchSlop值时，认为应该进行滚动，拦截子控件的事件
                if (diffX > mTouchSlop || diffY > mTouchSlop) {
                    return true;
                }
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                mXMove = event.getRawX();
                mYMove = event.getRawY();
                int scrolledX = (int) (mXLastMove - mXMove);
                int scrolledY = (int) (mYLastMove - mYMove);
                scrollBy(scrolledX, scrolledY);
                if (getScrollY() >= 0 && getScrollY() >=
                        getMeasuredHeight() - mChildAt.getMeasuredHeight() - mBottom) {//滑到顶部
                    scrollTo(getScrollX(), getMeasuredHeight() - mChildAt.getMeasuredHeight() - mBottom);
                } else if (getScrollY() <= 0 && -getScrollY() >= mBottom) {//滑到底部
                    scrollTo(getScrollX(), -mBottom);
                }
                mXLastMove = mXMove;
                mYLastMove = mYMove;
                break;
            case MotionEvent.ACTION_UP:
                int scrollX = getScrollX();
                if ((getMeasuredWidth() - mChildAt.getMeasuredWidth()) * 0.5f < -scrollX) {//向右复位
                    mScroller.startScroll(getScrollX(), getScrollY(), -(getMeasuredWidth() - mChildAt.getMeasuredWidth() + getScrollX()), 0);
                    invalidate();
                } else {//向左复位
                    mScroller.startScroll(getScrollX(), getScrollY(), -getScrollX(), 0);
                    invalidate();
                }
                break;
        }

        return super.onTouchEvent(event);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        /*
         * 只考虑一个子View
         */
        getChildAt(0).layout(0, getMeasuredHeight() - mBottom - mChildAt.getMeasuredHeight(),
                getChildAt(0).getMeasuredWidth(), getMeasuredHeight() - mBottom);

    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            invalidate();
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() > 1) {
            throw new NullPointerException("此ViewGroup 只能一个子控件");
        }
        mChildAt = getChildAt(0);
    }
}
