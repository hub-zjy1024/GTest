package p.js.gtest.view.listview;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
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

    public Refresher(ListView mListview) {
        this.mListview = mListview;
        mContext = mListview.getContext();
    }

    public boolean isLoading = false;

    @Override
    public void startLoading() {
        isLoading = true;
        Log.e("zjy", getClass() + "->startLoading(): ==");
        int headerViewsCount = mListview.getHeaderViewsCount();
        if (headerViewsCount > 0) {
            mListview.removeHeaderView(mListview.getChildAt(0));
        }
        LinearLayout linearLayout = new LinearLayout(mContext);

        TextView mtextView = new TextView(mContext);
        mtextView.setText("正在加载");
        linearLayout.addView(mtextView);
        LoadingView mView = new LoadingView(mContext);
        linearLayout.addView(mView);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, mHeight));
        linearLayout.setGravity(Gravity.CENTER_VERTICAL);
        mListview.addHeaderView(linearLayout);
        Animation animation = new ScaleAnimation(0, 1f, 1f, 1f, 0.5f, 0.5f);
        linearLayout.startAnimation(animation);
    }

    @Override
    public void loadingFinished() {
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

    @Override
    public View initHeader() {
        LinearLayout mLayout = new LinearLayout(mContext);
        TextView mText = new TextView(mContext);
        mText.setText("加载完成了----");
        LinearLayout.LayoutParams mpar = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                mHeight);
        mLayout.addView(mText, mpar);
        Animation animation = new TranslateAnimation(1f, 0, 1f, 1f);
//        Animation animation = new RotateAnimation(0, 90, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0);
//        Animation animation = new RotateAnimation(0, 90, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0);
        animation.setInterpolator(new AccelerateInterpolator());
        animation.setDuration((long) (0.5 * 1000));
        mLayout.setAnimation(animation);
        return mLayout;
    }

    public boolean isDragging = false;

    private int mHeight = 100;

    public void setDraging() {
        isDragging = true;
    }

    public boolean isDragging() {
        return isDragging;
    }
}
