package p.js.gtest.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LayoutAnimationController;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.OverScroller;
import android.widget.TextView;

import java.util.List;

import p.js.gtest.R;

/**
 Created by 张建宇 on 2019/7/19. */
public class ScrollableTabView extends HorizontalScrollView {
    public ScrollableTabView(Context context) {
        this(context, null);
    }

    public ScrollableTabView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScrollableTabView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //setOrientation(HORIZONTAL);
        init();
    }

    private static final int MAX_SCROLL = 200;
    private static final float SCROLL_RATIO = 0.5f;// 阻尼系数

    private LinearLayout mLayout;
    RectF mUnderLine = new RectF();
    int underlineHeight = 10;

    int mUnderLineMarginTop = 5;
    int mUnderLineRadiuX = 10;
    private AccelerateInterpolator mAccelerateInterpolator;
    private DecelerateInterpolator mDecelerateInterpolator;

    private int scrollOffset = 0;


    //整个的ScrollView的高度
    private int scrollHeight = 0;
    private int horizontalScroll = 0;

    private float lastX = 0;
    private float lastY = 0;
    /**
     在自定义view中经常使用的是重新绘制
     在viewGroup中经常使用scrollBy等进行移动
     */

    private int windowHeight;
    //这是进行记录横向或者竖向是否进行滑动了
    private float moveX = 0;
    private float moveY = 0;

    private VelocityTracker mVelocityTracker;
    OverScroller mScroller;

    ViewPager mViewPager;

    private void initScrollView() {
        mScroller = new OverScroller(getContext());
        setFocusable(true);
        setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);
        setWillNotDraw(false);
        //        final ViewConfiguration configuration = ViewConfiguration.get(mContext);
        //        mTouchSlop = configuration.getScaledTouchSlop();
        //        mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();
        //        mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
        //        mOverscrollDistance = configuration.getScaledOverscrollDistance();
        //        mOverflingDistance = configuration.getScaledOverflingDistance();
        //        mVerticalScrollFactor = configuration.getScaledVerticalScrollFactor();
    }

    private static final String TAG = "ScrollableTabView";
    private List<String> titles;

    Paint underlinePaint;

    public int limit = 600;
    int underLineWidth = 50;
    int underLineMax = 25;

    int stat = 0;

    private int veticalPadding = dp2px(5);
    private int horizPadding = dp2px(10);

    void init() {
        initScrollView();
        mDecelerateInterpolator = new DecelerateInterpolator();
        mAccelerateInterpolator = new AccelerateInterpolator();
        //        mdetecter.onTouchEvent();
        underlinePaint = new Paint();
        underlinePaint.setStrokeWidth(5);
        underlinePaint.setColor(getResources().getColor(R.color.scroTabView_underlineColor));
    }

    int position = 0;
    int nextPositon = 0;


    public int getScrollOffset() {
        return scrollOffset;
    }

    public void bindViewPager(ViewPager mVp) {
        this.mViewPager = mVp;
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
                //滑动距离超过跳转限制，onPageSelected

                //                Log.e("zjy", getClass() + "->onPageScrolled(): ==poi=" + i + ",offset=" + v + ",offsetPx=" +
                // i1);
                updateWithViewPager(i, v);
            }

            @Override
            public void onPageSelected(int i) {
                int nowItem = i;
                int offset = 1;
                Log.e("zjy", getClass() + "->onPageSelected(): ==" + nowItem + ",last=" + position);
                if (nowItem < position) {
                    offset = -1;
                }
                scrollAuto(nowItem, offset);
                position = nowItem;
            }

            @Override
            public void onPageScrollStateChanged(int i) {
                if (i == ViewPager.SCROLL_STATE_IDLE) {
                    //                    int nowItem = mViewPager.getCurrentItem();
                    //                    int offset = 1;
                    //                    Log.e("zjy", getClass() + "->onPageScrollStateChanged(): ==" + nowItem + ",last=" +
                    // position);
                    //                    if (nowItem < position) {
                    //                        offset = -1;
                    //                    }
                    //                    scrollAuto(nowItem, offset);
                    //                    position = nowItem;
                }
            }
        });
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        scrollAuto(position, 1);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        horizontalScroll = l;
    }

    public static int getScreenWidth(Context context) {
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        return dm.widthPixels;
    }
    private void scrollAuto(int position, int offset) {
        View view = getItemAtPosition(position);
        int nowLeft =view.getLeft();
        int width = view.getWidth();

//        int scrollWidth = mLayout.getWidth();
        int scrollWidth = getWidth() - getPaddingLeft() - getPaddingRight();

        int viewCenter = nowLeft + width / 2;
        int realteLeft = viewCenter % scrollWidth;
        int x = width * offset;
        if (offset > 0) {
            Log.e("zjy", getClass() + "->scrollAuto(): ==Right");
            int count = viewCenter / scrollWidth;
            int targetCenter = count * scrollWidth + scrollWidth / 2;
//            if (realteLeft > scrollWidth / 2) {
//                //                int rX = x;
//                if (position + 1 <= getItemCounts()) {
//                    //                    scrollBy(rX, 0);
//                    //                    scrollTo(targetCenter, 0);
//                    int rX2 = viewCenter - targetCenter;
//                    scrollBy(rX2, 0);
//                }
//            }
            int rX2 = viewCenter - targetCenter;
            int scrollX = getScrollX();
            Log.e("zjy", getClass() + "->scrollAuto(): getRawX==" + view.getX());

            Log.e("zjy", getClass() + "->scrollAuto(): ==rx2" + rX2 + "," + scrollX);
//            horizontalScroll
//            scrollBy(rX2, 0);
//            int mCenter = nowLeft - scrollWidth / 2 - width / 2;
//            int screenWidth = getScreenWidth(getContext());
//            int mCenter = nowLeft - screenWidth / 2 - width / 2;
            int mCenter = nowLeft - 2 * width;
            scrollTo(mCenter, 0);
//            scrollTo(nowLeft, 0);
            Log.e("zjy", getClass() + "->scrollAuto(): ==after" + rX2 + "," + horizontalScroll);
        } else {
            if (position + 2 < getItemCounts()) {
                scrollBy(x, 0);
            }
        }
    }

    int getViewLeft(int position) {
        return getPaddingLeft() + getItemAtPosition(position).getLeft();
    }

    private void scrollToChild(int position, int offset) {
        if (getItemCounts() == 0) {
            return;
        }
        int newScrollX = 0;
        if (position >= 0 && position <= getItemCounts() - 1) {
            newScrollX = getItemAtPosition(position).getLeft() + offset;
        }
        scrollTo(newScrollX, 0);
    }

    public void setScrollOffset(int scrollOffset) {
        this.scrollOffset = scrollOffset;
        scrollBy(scrollOffset, 0);
    }

    @Override
    public void setLayoutAnimation(LayoutAnimationController controller) {
        super.setLayoutAnimation(controller);
    }

    @Override
    protected void attachLayoutAnimationParameters(View child, ViewGroup.LayoutParams params, int index, int count) {
        super.attachLayoutAnimationParameters(child, params, index, count);

    }


    private float mOffset = 0;

    private int underlineX = 0;
    private boolean isScroll = false;

    public void setTitles(List<String> titles) {
        if (titles != null && titles.size() > 0) {
            this.titles = titles;
            position = 0;
            removeAllViews();
            int childCount = titles.size();
            //            LinearLayout mConatinner = new LinearLayout(getContext());
            mLayout = new LinearLayout(getContext());
            mLayout.setOrientation(LinearLayout.HORIZONTAL);

            for (int i = 0; i < childCount; i++) {
                TextView tv = new TextView(getContext());
                tv.setText(titles.get(i));
                tv.setTextColor(getResources().getColor(R.color.scroTabView_textColor));
                tv.setTextSize(20);
                tv.setPadding(horizPadding, veticalPadding, horizPadding, veticalPadding);
                final int mPoi = i;
                tv.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        JumpToPosition(mPoi);
                    }
                });

                mLayout.addView(tv);
            }
            addView(mLayout);
        }
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet params) {
        return (LayoutParams) new MarginLayoutParams(getContext(), params);
    }

    //    @Override
    //    public void addView(View child) {
    //        ((ViewGroup) (this)).addView(child);
    //    }

    public void updateWithViewPager(int pageIndex, float rate) {

        View curView = getItemAtPosition(pageIndex);
        View nextView = getItemCounts() - 1 > pageIndex ? getItemAtPosition(pageIndex) : curView;
        if (curView == null) {
            return;
        }
        float nWidth = curView.getMeasuredWidth();

        float nLeft = curView.getLeft();

        float nRight = curView.getRight();
        float nerWidth = 0;
        nerWidth = nextView.getMeasuredWidth();

        //        float tempUnderlineWidht = nWidth * 2 / 3;
        mOffset = rate;
        float tempUnderlineWidht = underLineWidth;
        mUnderLine.left = getPaddingLeft() + nLeft + nWidth / 2 - tempUnderlineWidht / 2 + nerWidth * mAccelerateInterpolator
                .getInterpolation
                        (mOffset);
        mUnderLine.right = getPaddingLeft() + nRight - nWidth / 2 + tempUnderlineWidht / 2 + nerWidth *
                mDecelerateInterpolator.getInterpolation(mOffset);
        //        float y = curView.getY();
        //        float nHeight = curView.getMeasuredHeight();
        //        mUnderLine.top = y + nHeight + mUnderLineMarginTop;
        //        mUnderLine.bottom = mUnderLine.top + underlineHeight;
        mUnderLine.bottom = getMeasuredHeight();
        mUnderLine.top = mUnderLine.bottom - underlineHeight;
        invalidate();
    }

    int scroll;
    long touchTime;

    public boolean onTouchEvent2(MotionEvent ev) {
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_UP:
                Log.e("zjy", getClass() + "->onTouchEvent(): ==up");
                break;
            case MotionEvent.ACTION_DOWN:
                scroll = (int) getX();
                Log.e("zjy", getClass() + "->onTouchEvent(): ==down");
                if (getItemCounts() == 0) {
                    return false;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                int offset = (int) (getX() - scroll);
                //                scrollBy(offset, 0);
                Log.e("zjy", getClass() + "->onTouchEvent(): ==move");
                break;
        }
        return super.onTouchEvent(ev);
    }

    GestureDetector mdetecter;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev);
    }

    public boolean dispatchTouchEvent2(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastX = moveX = ev.getX();
                lastY = moveY = ev.getY();
                mScroller.forceFinished(true);
                if (mVelocityTracker == null) {
                    mVelocityTracker = VelocityTracker.obtain();
                } else {
                    mVelocityTracker.clear();
                }
                mVelocityTracker.addMovement(ev);
                break;
            case MotionEvent.ACTION_MOVE:
                Log.e("TAG", "MyScrollView---getScrollY====" + getScrollY());
                mVelocityTracker.addMovement(ev);
                float curX = ev.getX();
                float curY = ev.getY();
                //垂直滑动的时候进行移动
                if (Math.abs(curY - lastY) > Math.abs(curX - lastX)) {
                    scrollBy(0, -(int) (curY - lastY));
                    //                  或者使用下面的方法，因为设置了平滑过渡时间为0
                    //                   mScroller.startScroll(0,getScrollY(),0,-(int)(curY-lastY),0);

                    invalidate();
                }
                lastX = curX;
                lastY = curY;
                break;
            case MotionEvent.ACTION_UP:

                //这里做了一个回弹的效果，在第二个参数中设置了滚动了多少距离，然后dy为它的负值，立马就回弹回去
                //这里肯定不能传递ev.getX()，因为getX()是获取的手指点击的位置,因此一定要使用getScrollY()，这是获取的滚动后的距离。
                //这里getScrollY()是在scrollTo()或scrollBy()中进行赋值。因此要调用这个方法，一定要先调用这两个方法。
                //startScroll()方法不适合在action_move中调用，因为这个方法默认的时间就是250毫秒，
                // 频繁的使用postInvalidate()进行刷新，就会导致移动动作的
                //覆盖，反而出现很难移动的效果。因为action_move的回调很快，每个十几像素就回调了。
                // 如果将startScroll()的第五个参数设置为0，也就是间隔时间设置为0
                //mScroller.startScroll(0,(int)getScrollY(),0,-(int)(curY-lastY),0);  那么就出现了和scrollBy()相似的效果
                mScroller.abortAnimation();
                if (getScrollY() < 0) {//证明达到上边界了，这时候要进行回弹处理
                    mScroller.startScroll(0, getScrollY(), 0, -getScrollY());
                    postInvalidate();// 重绘执行computeScroll()
                } else if (windowHeight + getScrollY() > scrollHeight) {//达到最底部
                    mScroller.startScroll(0, getScrollY(), 0, -(getScrollY() - (scrollHeight - windowHeight)));
                    postInvalidate();// 重绘执行computeScroll()
                } else {
                    mVelocityTracker.computeCurrentVelocity(1000);
                    float yVelocity = mVelocityTracker.getYVelocity();
                    //                    Log.e("TAG", "MyScrollView---yVelocity====" + yVelocity);
                    /**
                     * fling 方法参数注解
                     * startX 滚动起始点X坐标
                     * startY 滚动起始点Y坐标
                     * velocityX   当滑动屏幕时X方向初速度，以每秒像素数计算
                     * velocityY   当滑动屏幕时Y方向初速度，以每秒像素数计算
                     * minX    X方向的最小值，scroller不会滚过此点。
                     *　maxX    X方向的最大值，scroller不会滚过此点。
                     *　minY    Y方向的最小值，scroller不会滚过此点。
                     *　maxY    Y方向的最大值，scroller不会滚过此点。
                     */
                    if (Math.abs(yVelocity) > 50) {
                        //                        mScroller.extendDuration(2000);
                        mScroller.fling(0, getScrollY(), 0, -(int) yVelocity,
                                0, 0,
                                0, (scrollHeight - windowHeight));
                        postInvalidate();
                    }
                }
                //进行计算移动的距离
                moveX = Math.abs(ev.getX() - moveX);
                moveY = Math.abs(ev.getY() - moveY);
                //如果横向或者竖向已经移动了一段距离，那么就不能响应子控件的点击事件
                if (moveY > 10) {
                    return true;
                }
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY, int
            maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
        int newDeltaX = deltaX;
        int delta = (int) (deltaX * SCROLL_RATIO);
        if ((scrollX + deltaX) == 0 || (scrollX - scrollRangeX + deltaX) == 0) {
            newDeltaX = deltaX;     //回弹最后一次滚动，复位
        } else {
            newDeltaX = delta;      //增加阻尼效果
        }
        return super.overScrollBy(newDeltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, MAX_SCROLL, maxOverScrollY,
                isTouchEvent);
    }

    //    public boolean dispatchTouchEvent3(MotionEvent ev) {
    public boolean dispatchTouchEvent3(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_UP:
                long dur = System.currentTimeMillis() - touchTime;
                Log.e("zjy", getClass() + "->dispatchTouchEvent() time: ==" + dur);
                if (dur > 100) {
                    return true;
                }
                break;
            case MotionEvent.ACTION_DOWN:
                touchTime = System.currentTimeMillis();
                scroll = (int) getX();
                Log.e("zjy", getClass() + "->dispatchTouchEvent(): ==down");
                if (getItemCounts() == 0) {
                    return false;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                int offset = (int) (getX() - scroll);
                //                scrollBy(offset, 0);
                mScroller.startScroll((int) getX(), 0, scroll, 0);
                //                scroll = getX();
                Log.e("zjy", getClass() + "->dispatchTouchEvent(): ==move");
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    int dp2px(int dp) {
        WindowManager systemService = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics defMetrics = new DisplayMetrics();
        systemService.getDefaultDisplay().getMetrics(defMetrics);
        float desity = defMetrics.density;
        return (int) (dp * desity);
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        //如果scroller还没停止，那么就还进行不停的绘制
        if (mScroller.computeScrollOffset()) {
            //注意这里的getCurrY()的源码获取的是进行微移后的当前的坐标，不是相对距离
            scrollTo(0, mScroller.getCurrY());
            postInvalidate();
        }
    }

    View getItemAtPosition(int pos) {
        View childAt = mLayout.getChildAt(pos);
        return childAt;
    }

    int getItemCounts() {
        return mLayout.getChildCount();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawRoundRect(mUnderLine, mUnderLineRadiuX, mUnderLineRadiuX, underlinePaint);
    }

    /**
     测量各个子View的大小
     */
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.e("zjy", getClass() + "->onMeasure(): =super-w-h=" + getMeasuredWidth() + "," + getMeasuredHeight());
        int mWidth = getMeasuredWidth();
        int mHeight = getMeasuredHeight();
        setMeasuredDimension(mWidth, mHeight + underlineHeight);
        int mode = MeasureSpec.getMode(0);
        Log.e("zjy", getClass() + "->onMeasure(): 0 mode==" + mode);
//        measure(0, 0);
    }


    public void JumpToPosition(int position) {
        mViewPager.setCurrentItem(position, true);
    }

    public void scrollToPosition(int position, boolean isRight) {
        int childCount = getChildCount();
        if (position < childCount) {
            View childAt = getChildAt(position);
            if (isRight) {
                scrollBy(childAt.getMeasuredWidth(), 0);
            } else {
                scrollBy(-childAt.getMeasuredWidth(), 0);
            }
        }
    }
}
