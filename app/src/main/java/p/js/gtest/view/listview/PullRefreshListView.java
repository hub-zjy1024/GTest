package p.js.gtest.view.listview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ListView;

import p.js.gtest.DisPlayUtils;

/**
 Created by 张建宇 on 2019/7/22. */
public class PullRefreshListView extends ListView {
    //回弹效果
    private static int MAX_SCROLL = 100;

    private static final float SCROLL_RATIO = 0.5f;// 阻尼系数

    private int nowScrollY;


    public PullRefreshListView(Context context) {
        this(context, null);
    }

    public PullRefreshListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }


    public PullRefreshListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        MAX_SCROLL = DisPlayUtils.dp2px(MAX_SCROLL, getContext());
        mRefresher = new Refresher(this);
    }

    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        nowScrollY = t;

        if (nowScrollY > -100) {
            if (mRefresher.isDragging) {
                mRefresher.isDragging = false;
                if (mFRLisnter != null) {
                    mRefresher.onStateChanged(Refresher.stat_startRefresh);
                    mFRLisnter.startRefresh();
                }
            } else {
                if (!mRefresher.isLoading) {
                    mRefresher.onStateChanged(Refresher.stat_Pull);
                }
            }
        } else {
            mRefresher.onStateChanged(Refresher.stat_RealseToRefresh);
        }

        Log.e("zjy", getClass() + "->onScrollChanged(): ==vetical" + t);
    }

    @Override
    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY, int
            maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
        int newDeltaX = deltaX;
        int delta = (int) (deltaX * SCROLL_RATIO);
        if ((scrollX + deltaX) == 0 || (scrollX - scrollRangeX + deltaX) == 0) {
            newDeltaX = deltaX;//回弹最后一次滚动，复位
        } else {
            newDeltaX = delta;      //增加阻尼效果
        }

        int newDeltaY = deltaY;
        int deltaYZl = (int) (deltaY * SCROLL_RATIO);
        if ((scrollY + deltaY) == 0 || (scrollY - scrollRangeY + deltaY) == 0) {
            newDeltaY = deltaY;
            //回弹最后一次滚动，复位
        } else {
            newDeltaY = deltaYZl;      //增加阻尼效果
        }
        //        return super.overScrollBy(newDeltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, MAX_SCROLL,
        // maxOverScrollY,
        //                isTouchEvent);
        Log.e("zjy", getClass() + "->overScrollBy(): maxY==" + MAX_SCROLL);
        return super.overScrollBy(deltaX, newDeltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX, MAX_SCROLL,
                isTouchEvent);
    }

    public void setmFRLisnter(FreshListner mFRLisnter) {
        this.mFRLisnter = mFRLisnter;
    }

    public interface FreshListner {
        void startRefresh();
    }

    private FreshListner mFRLisnter;


    private float tY;

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_UP:
                if (!mRefresher.isLoading) {
                    if (nowScrollY < -100) {
                        mRefresher.setDraging();
                    }
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    Refresher mRefresher;

    public synchronized void loadingFinish() {
        mRefresher.loadingFinished();
    }
}
