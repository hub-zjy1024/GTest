package p.js.gtest;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LayoutAnimationController;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import p.js.gtest.view.MyAnimationSet;
import p.js.gtest.view.ScrollableTabView;

public class MainActivity extends AppCompatActivity {


    ScrollableTabView scrTabView;
    ViewPager viewPager;

    private int pos;
    private int mLlimit = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        List<String> tabTitles = new ArrayList<>();
        List<String> contents = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            tabTitles.add("标题" + i);
            contents.add("内容" + i);
        }
        scrTabView = findViewById(R.id.scrollTabview);
        Animation topToBottom = MyAnimationSet.getTopToBottom();
        LayoutAnimationController controller = new LayoutAnimationController(topToBottom);
        controller.setOrder(LayoutAnimationController.ORDER_NORMAL);
        scrTabView.setLayoutAnimation(controller);
        viewPager = findViewById(R.id.m_mViewPage);
        scrTabView.setTitles(tabTitles);
        findViewById(R.id.test_tablayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TabTestActivity.class);
                startActivity(intent);

            }
        });
        findViewById(R.id.test_ac_mediacodec).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MediaCodecTestActivity.class);
                startActivity(intent);
            }
        });

        PAdapter adapter = new PAdapter(this, contents);
        viewPager.setAdapter(adapter);
        scrTabView.bindViewPager(viewPager);
        mLlimit = (int) (getScreenWidth() * 0.6);
        TextView tv;
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 100);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    static class PAdapter extends PagerAdapter {
        private Context mContext;
        private List<String> contents;

        public PAdapter(Context mContext, List<String> contents) {
            this.mContext = mContext;
            this.contents = contents;
        }

        @Override
        public int getCount() {
            return contents.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
            return view == o;
        }


        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            TextView mView = new TextView(mContext);
            String s = contents.get(position);
            ViewPager.LayoutParams layoutParams = new ViewPager.LayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;

            mView.setText(s);
            mView.setTextSize(30);
            container.addView(mView, layoutParams);
            return mView;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            //            super.destroyItem(container, position, object);
            container.removeView(container.getChildAt(position));
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            return POSITION_UNCHANGED;
        }

    }

    int poiX;


    public int getScreenWidth() {
        Display defaultDisplay = getWindowManager().getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        defaultDisplay.getMetrics(metrics);
        return metrics.widthPixels;
    }

    public void btnClick(View view) {
        Animation topToBottom = MyAnimationSet.getTopToBottom();
        LayoutAnimationController controller;
        switch (view.getId()) {
            case R.id.btn_animate_scalxSN:
                topToBottom = MyAnimationSet.getScalXSmallToNormal();
                controller = new LayoutAnimationController(topToBottom);
                controller.setOrder(LayoutAnimationController.ORDER_NORMAL);
                scrTabView.setLayoutAnimation(controller);
                scrTabView.scrollTo(0, 0);
                scrTabView.requestLayout();
                break;
            case R.id.btn_open_testlistview:
                startActivity(new Intent(this, PullListTestActivity.class));
                break;
            case R.id.btn_animate_top_bottom:
                topToBottom = MyAnimationSet.getTopToBottom();
                controller = new LayoutAnimationController(topToBottom);
                controller.setOrder(LayoutAnimationController.ORDER_NORMAL);
                scrTabView.setLayoutAnimation(controller);
                scrTabView.scrollTo(0, 0);
                scrTabView.requestLayout();
                break;

            case R.id.btn_animate_letfToRight_tx:
                topToBottom = MyAnimationSet.getLeftToRightTxAnimation();
                controller = new LayoutAnimationController(topToBottom);
                controller.setOrder(LayoutAnimationController.ORDER_NORMAL);
                scrTabView.setLayoutAnimation(controller);
                scrTabView.scrollTo(0, 0);
                scrTabView.requestLayout();
                break;
        }
    }
}
