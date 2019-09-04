package p.js.gtest.view.loading;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

import p.js.gtest.DisPlayUtils;

/**
 Created by 张建宇 on 2019/7/22. */
public class LoadingView extends View {

    private int mCellWidth = 0;
    private int mCellHeight = 0;
    private int mCircleY = 0;
    private int mCircleX0 = 0;
    private int mRadiu = 3;
    //    private int scalRate = ;
    private int cirCleMargin = 10;
    private int scalRadiu = (int) (1.5 * mRadiu);

    private int loadCounts = 4;
    int mode = MARGIN_MODE;
    public static final int MARGIN_MODE = 0;
    public static final int AVG_WIDTH_MODE = 1;

    private Paint mCirclePaint;

    int position = 0;

    /**
     Simple constructor to use when creating a view from code.
     */
    public LoadingView(Context context) {
        this(context, null);
    }

    /**
     Constructor that is called when inflating a view from XML. This is called
     when a view is being constructed from an XML file, supplying attributes
     that were specified in the XML file. This version uses a default style of
     0, so the only attribute values applied are those in the Context's Theme
     and the given AttributeSet.
     The method onFinishInflate() will be called after all children have been
     added.
     @param context The Context the view is running in, through which it can
     access the current theme, resources, etc.
     @param attrs   The attributes of the XML tag that is inflating the view.
     */
    public LoadingView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     Perform inflation from XML and apply a class-specific base style from a
     theme attribute. This constructor of View allows subclasses to use their
     own base style when they are inflating. For example, a Button class's
     constructor would call this version of the super class constructor and
     supply <code>R.attr.buttonStyle</code> for <var>defStyleAttr</var>; this
     allows the theme's button style to modify all of the base view attributes
     (in particular its background) as well as the Button class's attributes.
     @param context      The Context the view is running in, through which it can
     access the current theme, resources, etc.
     @param attrs        The attributes of the XML tag that is inflating the view.
     @param defStyleAttr An attribute in the current theme that contains a
     reference to a style resource that supplies default values for
     the view. Can be 0 to not look for defaults.
     */
    public LoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mRadiu = DisPlayUtils.dp2px(mRadiu, getContext());
        cirCleMargin = DisPlayUtils.dp2px(cirCleMargin, getContext());
        scalRadiu = (int) (1.5 * mRadiu);
        mCirclePaint = new Paint();
        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setColor(Color.BLACK);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int hoPadding = getPaddingLeft() + getPaddingRight();
        int vetPadding = getPaddingBottom() + getPaddingTop();
        int mRadiu2 = mRadiu * 2;
        //        scalRadiu = (int) (1.5 * mRadiu);
        mCellWidth = mRadiu2 + cirCleMargin;
        mCellHeight = mRadiu2;
        int finalWidht = hoPadding + mCellWidth * loadCounts - cirCleMargin;
        setMeasuredDimension(finalWidht, vetPadding + scalRadiu * 2);
        mCircleY = getPaddingTop() + mRadiu;
        mCircleX0 = mRadiu + getPaddingLeft();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int startX = mCircleX0;
        for (int i = 0; i < loadCounts; i++) {
            mCirclePaint.setColor(Color.BLACK);
            if (position == i) {
                mCirclePaint.setColor(Color.GRAY);
                canvas.drawCircle(startX, mCircleY, scalRadiu, mCirclePaint);
            } else {
                canvas.drawCircle(startX, mCircleY, mRadiu, mCirclePaint);
            }
            startX += mCellWidth;
        }
//        startAnim();
        postDelayed(new Runnable() {
            @Override
            public void run() {
                position = position + 1;
                if (position >= loadCounts) {
                    position = 0;
                }
                invalidate();
            }
        }, 200);
    }

    public void setPosition(int position) {
        this.position = position;
        invalidate();
    }

    public void setLoadCounts(int loadCounts) {
        this.loadCounts = loadCounts;
        invalidate();
    }

    ObjectAnimator animator;
    void startAnim(){
        if (animator == null) {
            animator = ObjectAnimator.ofInt(this, "position", 0, loadCounts);
            animator.setInterpolator(new AccelerateInterpolator());
            animator.setDuration(150 * loadCounts);
            animator.setRepeatCount(ObjectAnimator.INFINITE);
            animator.start();
        }

    }

}
