package p.js.gtest.view;

import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

/**
 Created by 张建宇 on 2019/7/19. */
public class MyAnimationSet {
    public static int RELATIVE_TO_SELF = TranslateAnimation.RELATIVE_TO_SELF;

    /**
     横向膨胀
     @return
     */
    public static Animation getScalXSmallToNormal() {
        AnimationSet animationSet = new AnimationSet(true);
        ScaleAnimation scaleAnimation = new ScaleAnimation(0.7f, 1.0f, 1f, 1f);
        scaleAnimation.setDuration(300);
        scaleAnimation.setInterpolator(new AccelerateInterpolator());
        scaleAnimation.setStartOffset(0);
        animationSet.addAnimation(scaleAnimation);
        return animationSet;
    }

    public static Animation getTopToBottom() {
        AnimationSet animationSet = new AnimationSet(true);
        TranslateAnimation translateY1 = new TranslateAnimation(RELATIVE_TO_SELF, 0, RELATIVE_TO_SELF, 0,
                RELATIVE_TO_SELF, -1f, RELATIVE_TO_SELF, 1f);
        translateY1.setDuration(300);
        translateY1.setInterpolator(new AccelerateInterpolator());
        translateY1.setStartOffset(0);
        animationSet.addAnimation(translateY1);
        return animationSet;
    }

    public static Animation getLeftToRightTxAnimation() {
        Animation animation;
        AnimationSet animationSet = new AnimationSet(true);
        TranslateAnimation translateX1 = new TranslateAnimation(RELATIVE_TO_SELF, -1.0f, RELATIVE_TO_SELF, 0.1f,
                RELATIVE_TO_SELF, 0, RELATIVE_TO_SELF, 0);
        translateX1.setDuration(300);
        translateX1.setInterpolator(new DecelerateInterpolator());
        translateX1.setStartOffset(0);

        TranslateAnimation translateX2 = new TranslateAnimation(RELATIVE_TO_SELF, 0.1f, RELATIVE_TO_SELF, -0.1f,
                RELATIVE_TO_SELF, 0, RELATIVE_TO_SELF, 0);
        translateX2.setStartOffset(300);
        translateX2.setInterpolator(new DecelerateInterpolator());
        translateX2.setDuration(50);

        TranslateAnimation translateX3 = new TranslateAnimation(RELATIVE_TO_SELF, -0.1f, RELATIVE_TO_SELF, 0f,
                RELATIVE_TO_SELF, 0, RELATIVE_TO_SELF, 0);
        translateX3.setStartOffset(350);
        translateX3.setInterpolator(new DecelerateInterpolator());
        translateX3.setDuration(50);

        animationSet.addAnimation(translateX1);
        animationSet.addAnimation(translateX2);
        animationSet.addAnimation(translateX3);
        animationSet.setDuration(400);
        return animationSet;
    }
}
