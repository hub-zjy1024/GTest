package p.js.gtest;

import android.content.Context;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 Created by 张建宇 on 2019/7/22. */
public class DisPlayUtils {
    public static int dp2px(int dp, Context mContext) {
        float desity = getDensity(mContext);
        return (int) (dp * desity);
    }

    private static float getDensity(Context mContext) {
        WindowManager systemService = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics defMetrics = new DisplayMetrics();
        systemService.getDefaultDisplay().getMetrics(defMetrics);
        float desity = defMetrics.density;
        return desity;
    }

    public static Point getScreenWh(Context mContext) {
        WindowManager systemService = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics defMetrics = new DisplayMetrics();
        systemService.getDefaultDisplay().getMetrics(defMetrics);
        return new Point(defMetrics.widthPixels, defMetrics.heightPixels);
    }

    public static int px2dp(int px, Context mContext) {
        float desity = getDensity(mContext);
        return (int) (px / desity);
    }
}
