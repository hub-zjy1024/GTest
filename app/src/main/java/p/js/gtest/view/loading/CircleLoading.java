package p.js.gtest.view.loading;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

/**
 Created by 张建宇 on 2019/9/4. */
public class CircleLoading extends DyingLightLoading {
    double singleDegree;
    float smallRate = 0.3f;

    /**
     Simple constructor to use when creating a view from code.
     @param context The Context the view is running in, through which it can
     access the current theme, resources, etc.
     */
    public CircleLoading(Context context) {
        super(context);
    }

    public CircleLoading(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     *
     */
    public CircleLoading(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void init(AttributeSet attrs) {
        super.init(attrs);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        float cx = width / 2;
        float raduis =cx*smallRate;

        int tempCount = 0;
        float tempSize = 0;
        float maxSize = (float) ((cx + raduis) * (cx + raduis) * Math.PI / 2);
        float innerSize = (float) ((cx) * (cx) * Math.PI / 2);
        while (tempSize <= maxSize) {
            tempSize = (float) (innerSize + tempCount * raduis * raduis * Math.PI);
            tempCount++;
        }
        singleDegree = tempCount - 1;
        Log.e("zjy", getClass() + "->onMeasure():singleDegree ==" + singleDegree);
    }


    AccelerateInterpolator accelerateInterpolator = new AccelerateInterpolator();
    AccelerateDecelerateInterpolator accelerateDecelerateInterpolator = new AccelerateDecelerateInterpolator();

    DecelerateInterpolator decelerateInterpolator = new DecelerateInterpolator();
    Interpolator newAccDecel = new AcDcInterpolator2();

    private static class AcDcInterpolator2 implements Interpolator {

        @Override
        public float getInterpolation(float input) {
            float y = 0;
            if (input < 0.5) {
                y = 2 * input;
            } else {
                y = -2f * input + 2;
            }
            return y;
        }
    }
    private static class AcDcInterpolator implements Interpolator {

        @Override
        public float getInterpolation(float input) {
            //
            return (float) (-4 * Math.pow(input, 2) + 4 * input);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //        super.onDraw(canvas);
        int width = getMeasuredWidth();
        float cx = width / 2;
        float cy = cx;
        int circleCounts = (int) singleDegree;
        float raduis = width / 5f / 2;
        float maxOffset = 180 / 1f / circleCounts;
        float minx = 180 / 1f / circleCounts/2;

        int maxAlpha=255;
        int nowAlpha = 0;

        int alphaOffset = (int) (maxAlpha * 0.8f / circleCounts);

        for (int i = 0; i < circleCounts; i++) {
            canvas.save();
            //            float interpolation =accelerateInterpolator.getInterpolation(stat);
            //            float interpolation = accelerateDecelerateInterpolator.getInterpolation(process);
            //            float interpolation = accelerateDecelerateInterpolator.getInterpolation(stat);
            //            if (process > 0.5) {
            //                interpolation = accelerateDecelerateInterpolator.getInterpolation(stat);
            //                //                stat = (float) Math.abs(stat - 1);
            //                interpolation = decelerateInterpolator.getInterpolation(stat);
            //            }
            //            float nowPos = interpolation * maxOffset;
            float v = minx *i;
            float interpolation = newAccDecel.getInterpolation(process);
            float nowOffset = i * maxOffset * interpolation;
            float startPos = v + process * 360;
            float finalDegree = startPos + nowOffset;
            if (finalDegree > 360) {
                finalDegree = 360;
            } else if (finalDegree < 0) {
                finalDegree = 0;
            }
            int degree = (int) (-1 * finalDegree);

            canvas.rotate(degree, cx, cy);
            int alpha = nowAlpha + alphaOffset;
            nowAlpha = alpha;
            centerPaint.setAlpha(alpha);
            canvas.drawCircle(cx, raduis, raduis, centerPaint);
            canvas.restore();
        }
        startAnim();
    }

    ObjectAnimator mAnim;
    final int delayTime = 100;
    void startAnim() {
        if (mAnim == null) {
            mAnim = ObjectAnimator.ofFloat(this, "process",  0, 1);
            mAnim.setDuration(1000);
            mAnim.setRepeatCount(ObjectAnimator.INFINITE);
            mAnim.start();
//            final Runnable mRun=new Runnable() {
//                @Override
//                public void run() {
//                    process = process + 0.2f;
//                    if (process > 1) {
//                        process = 0;
//                    }
//                    setProcess(process);
//                    postDelayed(this, delayTime);
//                }
//            };
//            postDelayed(mRun, delayTime);
        }
    }
}
