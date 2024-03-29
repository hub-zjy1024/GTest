package p.js.gtest;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.TextView;

import java.util.ArrayList;

import p.js.gtest.view.tablayout.TextTabLayout;
import p.js.gtest.view.tablayout.adapter.TextTabAdapter;

/**
 Created by 张建宇 on 2019/7/19. */
public class TabTestActivity extends AppCompatActivity {

    private TextTabLayout mTabLayout;
    private ViewPager mViewPager;
    private TextTabAdapter mAdapter;

    private ArrayList<String> mSource = new ArrayList<>();
    private LayoutInflater mInflater;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabtest);
        initView();
        bindData();
    }


    private void initView() {
        mTabLayout = (TextTabLayout) findViewById(R.id.tab_layout);
        mViewPager = (ViewPager) findViewById(R.id.ViewPager);
        mAdapter = new TextTabAdapter(this);
        mTabLayout.bindViewPager(mViewPager).setAdapter(mAdapter);
        mInflater = LayoutInflater.from(this);
    }

    private void bindData() {
        mSource.add("精选");
        mSource.add("全球");
        mSource.add("种草");
        mSource.add("科技范");
        mSource.add("时髦精");
        mSource.add("生活家");
        mSource.add("i运动");
        mSource.add("黑科技");
        mAdapter.bindData(mSource);
        mViewPager.setAdapter(new MyPagerAdapter());
    }



    private class MyPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return mSource == null ? 0 : mSource.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = mInflater.inflate(R.layout.tab_layout_item,null);
            TextView viewById = view.findViewById(R.id.hotel_tab_tv_title);
            viewById.setText("这是" + mSource.get(position));
            ViewParent parent = view.getParent();
            if (parent != null) {
                container.removeView(view);
            }
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}
