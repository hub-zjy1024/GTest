package p.js.gtest.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.LayoutAnimationController;

/**
 Created by 张建宇 on 2019/7/19. */
public class CustLayoutAnimation extends LayoutAnimationController {
    /**
     Creates a new layout animation controller from external resources.
     @param context the Context the view  group is running in, through which
     it can access the resources
     @param attrs   the attributes of the XML tag that is inflating the */
    public CustLayoutAnimation(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     Creates a new layout animation controller with a delay of 50%
     and the specified animation. */
    public CustLayoutAnimation(Animation animation) {
        super(animation);
    }

    /**
     Creates a new layout animation controller with the specified delay
     and the specified animation.
     @param animation the animation to use on each child of the view group
     @param delay     the delay by which each child's animation must be offset */
    public CustLayoutAnimation(Animation animation, float delay) {
        super(animation, delay);
    }


    Animation DEF_Animation;


}
