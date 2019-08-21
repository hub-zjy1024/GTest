package p.js.gtest.view.listview;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import p.js.gtest.view.LoadingView;

/**
 Created by 张建宇 on 2019/7/22. */
public class Refresher implements IRefresher {
    ListView mListview;
    private Context mContext;
    public static final int stat_Pull = 0;
    public static final int stat_RealseToRefresh = 1;
    public static final int stat_startRefresh = 2;
    public static final int stat_RefreshFinished = 3;
    public int nStat = -1;

    private View mLoadingImg;
    private TextView mLoadingText;
    private LinearLayout mHeader;

    public Refresher(ListView mListview) {
        this.mListview = mListview;
        mContext = mListview.getContext();
        mHeader = new LinearLayout(mContext);
        mHeader.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, mHeight));
        mHeader.setGravity(Gravity.CENTER_VERTICAL);
        mLoadingImg = new LoadingView(mContext);
        mHeader.addView(mLoadingImg);
        mLoadingText = new TextView(mListview.getContext());
        mHeader.addView(mLoadingText);
    }

    public boolean isLoading = false;
    private int mHeight = 100;

    @Override
    public void startLoading() {
        isLoading = true;
        Log.e("zjy", getClass() + "->startLoading(): ==");
//        int headerViewsCount = mListview.getHeaderViewsCount();
//        if (headerViewsCount > 0) {
//            mListview.removeHeaderView(mListview.getChildAt(0));
//        }
//        LinearLayout linearLayout = new LinearLayout(mContext);
//        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, mHeight));
//        linearLayout.setGravity(Gravity.CENTER_VERTICAL);
//        TextView mtextView = new TextView(mContext);
//
//        linearLayout.addView(mtextView);
//        LoadingView mView = new LoadingView(mContext);
//        linearLayout.addView(mView);
//        mListview.addHeaderView(linearLayout);
//        Animation animation = new AlphaAnimation(0,1f);
//        linearLayout.startAnimation(animation);
        mLoadingText.setText("正在加载");
        mLoadingImg.setVisibility(View.VISIBLE);
    }

    @Override
    public void loadingFinished() {
        Animation animation = new TranslateAnimation(1f, 0, 1f, 1f);
        animation.setInterpolator(new AccelerateInterpolator());
        animation.setDuration((long) (0.5 * 1000));
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mListview.removeHeaderView(mHeader);
                isDragging = false;
                isLoading = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mLoadingText.setText("---加载完成---");
        mLoadingImg.setVisibility(View.GONE);
        mHeader.startAnimation(animation);
    }
    public void loadingFinished2() {
        if (mListview != null) {
            int headerViewsCount = mListview.getHeaderViewsCount();
            if (headerViewsCount > 0) {
                mListview.removeHeaderView(mListview.getChildAt(0));
            }
            final View view = initHeader();
            mListview.addHeaderView(view);
            Animation animation = view.getAnimation();
            if (animation != null) {
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        mListview.removeHeaderView(view);
                        isDragging = false;
                        isLoading = false;
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                view.startAnimation(animation);
            }

        }
    }

    public void onStateChanged(int stat) {
        switch (stat) {
            case stat_RealseToRefresh:
                if (nStat == stat_RealseToRefresh) {
                    return;
                }
                showTitle("释放以刷新");
                break;
            case stat_startRefresh:
                startLoading();
                break;
            case stat_Pull:
                int headerViewsCount = mListview.getHeaderViewsCount();
                if (headerViewsCount == 0) {
                    mListview.addHeaderView(mHeader);
                }
                showTitle("继续下拉开始刷新");
                break;
            case stat_RefreshFinished:
                break;
        }
        nStat = stat;
    }

    private void showTitle(String msg) {
        if (mListview != null) {
//            int headerViewsCount = mListview.getHeaderViewsCount();
//            if (headerViewsCount > 0) {
//                mListview.removeHeaderView(mHeader);
//            }else{
//                mListview.addHeaderView(mHeader);
//            }
            mLoadingImg.setVisibility(View.GONE);
            mLoadingText.setText(msg);
        }
    }

    @Override
    public View initHeader() {
        LinearLayout mLayout = new LinearLayout(mContext);
        TextView mText = new TextView(mContext);
        mText.setText("加载完成了----");
        LinearLayout.LayoutParams mpar = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                mHeight);
        mLayout.addView(mText, mpar);
        Animation animation = new TranslateAnimation(1f, 0, 1f, 1f);
        //        Animation animation = new RotateAnimation(0, 90, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation
        //        .RELATIVE_TO_SELF, 0);
        //        Animation animation = new RotateAnimation(0, 90, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation
        //        .RELATIVE_TO_SELF, 0);
        animation.setInterpolator(new AccelerateInterpolator());
        animation.setDuration((long) (0.5 * 1000));
        mLayout.setAnimation(animation);
        return mLayout;
    }

    public boolean isDragging = false;


    public void setDraging() {
        isDragging = true;
    }

    public boolean isDragging() {
        return isDragging;
    }
}
