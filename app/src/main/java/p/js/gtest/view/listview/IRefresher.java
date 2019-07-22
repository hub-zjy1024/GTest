package p.js.gtest.view.listview;

import android.view.View;

/**
 Created by 张建宇 on 2019/7/22. */
public interface IRefresher {
    void startLoading();

    void loadingFinished();

    View initHeader();
}
