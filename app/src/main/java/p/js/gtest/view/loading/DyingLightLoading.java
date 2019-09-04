package p.js.gtest.view.loading;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import p.js.gtest.R;

/**
 Created by 张建宇 on 2019/9/4. */
public class DyingLightLoading extends View {
    int limitMax = 20;
    protected float process = 0;

    int minWidth = 100;
    protected float innerRate = 0.6f;
    float outWidth;
    float outRate;
    float outPadding;

    int colorCenter = Color.WHITE;
    int rectColor = Color.WHITE;
    ObjectAnimator objectAnimator;
    protected Paint centerPaint;
    Paint outerPaint;

    public float getProcess() {
        return process;
    }

    public void setProcess(float process) {
        this.process = process;
        invalidate();
    }

    /**
     Simple constructor to use when creating a view from code.
     @param context The Context the view is running in, through which it can
     access the current theme, resources, etc.
     */
    public DyingLightLoading(Context context) {
        this(context, null);
    }

    public DyingLightLoading(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DyingLightLoading(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }
/*
     <declare-styleable name="DyingLightLoading">
        <attr name="colorInner" format="color|reference" />
        <attr name="colorRect" format="color|reference" />
        <attr name="boderWidth" format="reference|dimension" />
    </declare-styleable>*/
    public void init(AttributeSet attrs) {
        Context context = getContext();
        Resources resources = context.getResources();
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.DyingLightLoading);
        centerPaint = new Paint();
        outerPaint = new Paint();
        outWidth = 4;
        outRate = 0.4f;
        outPadding = 2;
        innerRate = 0.4f;
        rectColor = typedArray.getColor(R.styleable.DyingLightLoading_colorRect, resources.getColor(R.color.colorPrimary));
        outerPaint.setColor(rectColor);
        colorCenter = typedArray.getColor(R.styleable.DyingLightLoading_colorInner, resources.getColor(R.color.colorAccent));
        outWidth = typedArray.getDimension(R.styleable.DyingLightLoading_boderWidth, outWidth);
        Log.e("zjy", getClass() + "->init(): outWidth==" + outWidth);
        outerPaint.setStrokeWidth(outWidth);
        outerPaint.setAntiAlias(true);
        centerPaint.setColor(colorCenter);
        centerPaint.setAntiAlias(true);
        typedArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int mode = MeasureSpec.getMode(widthMeasureSpec);
        int hMode = MeasureSpec.getMode(heightMeasureSpec);
        if (mode == MeasureSpec.EXACTLY || hMode == MeasureSpec.EXACTLY) {
            int finalWidth = MeasureSpec.getSize(widthMeasureSpec);
            int finalHeight = MeasureSpec.getSize(heightMeasureSpec);
            finalHeight = Math.max(finalWidth, finalHeight);
            double sqrt = Math.sqrt(2);
            minWidth = (int) ((finalHeight / sqrt)) - getPaddingTop() - getPaddingBottom();
            setMeasuredDimension(finalHeight, finalHeight);
        } else {
            double sqrt = Math.sqrt(2);
            int finalHeight = (int) (minWidth * sqrt);
            setMeasuredDimension(finalHeight, finalHeight);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int tottalWidth = getMeasuredWidth();
        float innerWidth = minWidth * innerRate;
        float outLeft = (tottalWidth - minWidth) / 2;
        float left = outLeft + minWidth * (1 - innerRate) / 2;

        float right = innerWidth + left;
        int tempLeft = (int) (left - outLeft);

        float lineLen = innerWidth * 0.4f;
        float alpha = 0.5f;
        float widthoffset = outWidth / 2;
        if (process > 1) {
            process = Math.abs(process - 2);
            if (1 - process > alpha) {
                alpha = 1 - process;
            }
            centerPaint.setAlpha((int) (alpha * 255));
            canvas.drawRect(left, left, right, right, centerPaint);
            canvas.save();
            float degress = 90 * process;
            canvas.rotate(degress, tottalWidth / 2, tottalWidth / 2);
            float y1 = outLeft + tempLeft * (1 - process) - widthoffset;
            float x1 = outLeft + tempLeft * (1 - process) - outWidth;
            float x2 = right + tempLeft * process + outWidth;
            //横向
            canvas.drawLine(x1, y1, x1 + lineLen, y1, outerPaint);
            canvas.drawLine(x2, y1, x2 - lineLen, y1, outerPaint);

            canvas.drawLine(x1, y1, x1, y1 + lineLen, outerPaint);
            canvas.drawLine(x2, y1, x2, y1 + lineLen, outerPaint);

            y1 = right + tempLeft * process + widthoffset;
            canvas.drawLine(x1, y1, x1 + lineLen, y1, outerPaint);
            canvas.drawLine(x2, y1, x2 - lineLen, y1, outerPaint);

            canvas.drawLine(x1, y1, x1, y1 - lineLen, outerPaint);
            canvas.drawLine(x2, y1, x2, y1 - lineLen, outerPaint);
            canvas.restore();
        } else {
            if (1 - process > alpha) {
                alpha = 1 - process;
            }
            centerPaint.setAlpha((int) (alpha * 255));
            canvas.drawRect(left, left, right, right, centerPaint);
            float y1 = outLeft + tempLeft * (1 - process) - widthoffset;
            float x1 = outLeft + tempLeft * (1 - process) - outWidth;
            float x2 = right + tempLeft * process + outWidth;
            //横向
            canvas.drawLine(x1, y1, x1 + lineLen, y1, outerPaint);
            canvas.drawLine(x2, y1, x2 - lineLen, y1, outerPaint);

            canvas.drawLine(x1, y1, x1, y1 + lineLen, outerPaint);
            canvas.drawLine(x2, y1, x2, y1 + lineLen, outerPaint);

            y1 = right + tempLeft * process + widthoffset;
            canvas.drawLine(x1, y1, x1 + lineLen, y1, outerPaint);
            canvas.drawLine(x2, y1, x2 - lineLen, y1, outerPaint);

            canvas.drawLine(x1, y1, x1, y1 - lineLen, outerPaint);
            canvas.drawLine(x2, y1, x2, y1 - lineLen, outerPaint);
        }
        startAnima();
    }

    void stopAnima() {
        if (objectAnimator != null) {
            objectAnimator.cancel();
            objectAnimator = null;
        }
    }

    void startAnima() {
        if (objectAnimator == null) {
            objectAnimator = ObjectAnimator.ofFloat(this, "process", 0, 2);
            objectAnimator.setDuration(800);
            objectAnimator.setRepeatCount(ObjectAnimator.INFINITE);
            objectAnimator.start();
        }
    }
}
