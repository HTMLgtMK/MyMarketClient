package com.gthncz.mymarketclient.main;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.gthncz.mymarketclient.R;
import com.gthncz.mymarketclient.beans.Params;
import com.gthncz.mymarketclient.beans.PointStoreBean;
import com.gthncz.mymarketclient.helper.MyUserJsonObjectRequest;
import com.gthncz.mymarketclient.main.PointStoreAdapter.OnClickedObtainListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by GT on 2018/5/18.
 */

public class PointStoreActivity extends AppCompatActivity implements OnClickedObtainListener {

    @BindView(R.id.toolbar_activity_point_store) protected Toolbar mToolbar;
    @BindView(R.id.swipeRefreshLayout_activity_point_store) protected SwipeRefreshLayout mRefresh;
    @BindView(R.id.recyclerView_activity_point_store) protected RecyclerView mRecyclerView;

    protected PointStoreAdapter mAdapter;
    protected LinearLayoutManager mLinearLayoutManager;
    protected ArrayList<PointStoreBean> mPoints;
    private int mPointIndex;

    private boolean isLoading;
    private RequestQueue mQueue;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pointstore);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        mAdapter = new PointStoreAdapter(this);
        mPoints = new ArrayList<>();
        mAdapter.setPoints(mPoints);
        mAdapter.setObtainListener(this);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mPointIndex = 1;
        isLoading = false;
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        mRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(isLoading) return;
                (new InitLoadTask()).execute((Void) null);
            }
        });
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(dy<0) return;
                int pos = mLinearLayoutManager.findLastVisibleItemPosition();
                int total = mLinearLayoutManager.getItemCount();
                if(total > 1 && pos == total-1){
                    if(isLoading) return;
                    (new LoadMoreTask()).execute((Void) null);
                }
            }
        });

        mQueue = Volley.newRequestQueue(this);
        mQueue.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        (new InitLoadTask()).execute((Void) null);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{
                onBackPressed();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void obtain(PointStoreBean bean) {
        HashMap<String, String> map = new HashMap<>();
        map.put("point", String.valueOf(bean.getPoint()));
        JSONObject params = new JSONObject(map);
        MyUserJsonObjectRequest request = new MyUserJsonObjectRequest(Request.Method.POST, Params.URL_USER_OBTAIN_POINT, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    int code = response.getInt("code");
                    if(code == 1){
                        Toast.makeText(PointStoreActivity.this, "获取积分成功!", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(PointStoreActivity.this, "获取积分失败!", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(PointStoreActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        mQueue.add(request);
    }

    private class InitLoadTask extends AsyncTask<Void, Void, ArrayList<PointStoreBean>>{

        @Override
        protected void onPreExecute() {
            if(isLoading) return ;
            mRefresh.measure(0,0);
            mRefresh.setRefreshing(true);
            mPoints.clear();
            mPointIndex = 1;
            isLoading = true;
        }

        @Override
        protected ArrayList<PointStoreBean> doInBackground(Void... voids) {
            try {
                Thread.sleep(2000); // 模拟网络环境
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ArrayList<PointStoreBean> points = new ArrayList<>();
            for(int i=0;i<10; ++i){
                PointStoreBean bean = new PointStoreBean(mPointIndex++);
                points.add(bean);
            }
            return points;
        }

        @Override
        protected void onPostExecute(ArrayList<PointStoreBean> pointStoreBeans) {
            mRefresh.setRefreshing(false);
            mPoints.addAll(pointStoreBeans);
            mAdapter.notifyDataSetChanged();
            isLoading = false;
        }
    }


    private class LoadMoreTask extends AsyncTask<Void, Void, ArrayList<PointStoreBean>>{

        @Override
        protected void onPreExecute() {
            mAdapter.setLoadStatus(FooterViewHolder.STATUS_LOADING, true);
            isLoading = true;
        }

        @Override
        protected ArrayList<PointStoreBean> doInBackground(Void... voids) {
            try {
                Thread.sleep(2000); // 模拟网络环境
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ArrayList<PointStoreBean> points = new ArrayList<>();
            for(int i=0;i<10; ++i){
                PointStoreBean bean = new PointStoreBean(mPointIndex++);
                points.add(bean);
            }
            return points;
        }

        @Override
        protected void onPostExecute(ArrayList<PointStoreBean> pointStoreBeans) {
            mPoints.addAll(pointStoreBeans);
            mAdapter.setLoadStatus(FooterViewHolder.STATUS_LOAD_DONE, false);
            mAdapter.notifyDataSetChanged();
            isLoading = false;
        }
    }
}
