package p.js.gtest;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;

import java.util.ArrayList;

import p.js.gtest.view.listview.PullRefreshListView;

public class PullListTestActivity extends AppCompatActivity {

    BaseAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pull_list_test);
        final PullRefreshListView mLv = findViewById(R.id.activity_p_r_listview_lv);
        ArrayList<String> mListData = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            mListData.add("item" + i);
        }
        adapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, mListData);
        mLv.setAdapter(adapter);
        mLv.setmFRLisnter(new PullRefreshListView.FreshListner() {
            @Override
            public void startRefresh() {

                Task mtask = new Task(mLv);
                mtask.execute();
            }
        });
    }

    static class Task extends AsyncTask<Void, Void, Void> {

        private PullRefreshListView mListView;

        public Task(PullRefreshListView mListView) {
            this.mListView = mListView;
        }

        /**
         Override this method to perform a computation on a background thread. The
         specified parameters are the parameters passed to {@link #execute}
         by the caller of this task.
         This method can call {@link #publishProgress} to publish updates
         on the UI thread.
         @param voids The parameters of the task.
         @return A result, defined by the subclass of this task.
         @see #onPreExecute()
         @see #onPostExecute
         @see #publishProgress
         */
        @SuppressLint("WrongThread")
        @Override
        protected Void doInBackground(Void... voids) {

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mListView.loadingFinish();
        }
    }
}
