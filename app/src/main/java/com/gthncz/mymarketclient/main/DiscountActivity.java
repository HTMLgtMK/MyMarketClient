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
import android.util.Log;
import android.util.Pair;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;
import com.gthncz.mymarketclient.R;
import com.gthncz.mymarketclient.beans.DiscountUserBean;
import com.gthncz.mymarketclient.beans.Params;
import com.gthncz.mymarketclient.helper.MyLocalUserHelper;
import com.gthncz.mymarketclient.helper.MyUserJsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 优惠广场
 * Created by GT on 2018/5/19.
 */

public class DiscountActivity extends AppCompatActivity {

    @BindView(R.id.toolbar_activity_mydiscount) protected Toolbar mToolbar;
    @BindView(R.id.swipeRefreshLayout_activity_mydiscount_refresh) protected SwipeRefreshLayout mRefresh;
    @BindView(R.id.recyclerView_activity_mydiscount) protected RecyclerView mRecyclerView;

    private int mPage;
    private int mTotalPage;

    private LinearLayoutManager mLinearLayoutManager;
    private DiscountAdapter mAdapter;
    private ArrayList<DiscountUserBean> mDiscounts;

    private RequestQueue mQueue;

    private boolean isLoading;

    private String mToken;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mydiscount);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        mPage = 1;
        mTotalPage = 1;
        isLoading = false;
        mLinearLayoutManager = new LinearLayoutManager(this);
        mDiscounts = new ArrayList<>();
        mAdapter = new DiscountAdapter(this);
        mAdapter.setDiscounts(mDiscounts);
        mAdapter.setFooterViewClickedListener(new FooterViewHolder.OnMyFooterViewClickedListener() {
            @Override
            public void onClickedLoadFail() {
                if(isLoading) return;
                (new LoadMoreTask()).execute((Void) null);
            }
        });
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
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
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState == RecyclerView.SCROLL_STATE_IDLE){
                    int lastItem = mLinearLayoutManager.findLastVisibleItemPosition();
                    int total = mLinearLayoutManager.getItemCount();
                    if(lastItem == total-1 && mPage < mTotalPage){
                        if(isLoading) return ;
                        (new LoadMoreTask()).execute((Void) null);
                    }
                }
            }
        });


        mQueue = Volley.newRequestQueue(this);
        mQueue.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mToken = MyLocalUserHelper.getLocalToken(this);
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

    /*初始化加载任务*/
    protected class InitLoadTask extends AsyncTask<Void, Void, ArrayList<DiscountUserBean>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mRefresh.measure(0,0);
            mRefresh.setRefreshing(true);
//            mDiscounts.clear(); // 不要在这里清空数据,,,否则会出现bug: java.lang.IndexOutOfBoundsException: Inconsistency detected.
            mPage = 1;
            mTotalPage = 1;
            isLoading = true;
        }

        @Override
        protected void onPostExecute(ArrayList<DiscountUserBean> DiscountUserBeans) {
            super.onPostExecute(DiscountUserBeans);
            if(mTotalPage == mPage){
                mAdapter.setLoadStatus(FooterViewHolder.STATUS_LOAD_FULL, false);
            }else{
                if(DiscountUserBeans != null){
                    mAdapter.setLoadStatus(FooterViewHolder.STATUS_LOAD_DONE, false);
                }else{
                    mAdapter.setLoadStatus(FooterViewHolder.STATUS_LOAD_FAIL, false);
                }
            }
            mRefresh.setRefreshing(false);
            mDiscounts.clear();
            if(DiscountUserBeans != null){
                mDiscounts.addAll(DiscountUserBeans);
                mAdapter.notifyDataSetChanged();
            }
            isLoading = false;
        }

        @Override
        protected ArrayList<DiscountUserBean> doInBackground(Void... voids) {
            RequestFuture future = RequestFuture.newFuture();
            MyUserJsonObjectRequest request = new MyUserJsonObjectRequest(Request.Method.POST,
                    Params.URL_USER_DISCOUNS, null, future, future) {
                @Override
                public String getUserToken() {
                    return mToken;
                }
            };
            mQueue.add(request);
            ArrayList<DiscountUserBean> list = null;
            try {
                JSONObject response = (JSONObject) future.get();
                int code = response.getInt("code");
                if(code == 1){
                    list = new ArrayList<>();
                    JSONObject data = response.getJSONObject("data");
                    mPage = data.getInt("current_page");
                    mTotalPage = data.getInt("last_page");
                    JSONArray jsonArray = data.getJSONArray("data");
                    int size = jsonArray.length();
                    for(int i= 0; i< size;++i){
                        JSONObject obj = jsonArray.getJSONObject(i);
                        DiscountUserBean bean = new DiscountUserBean();
                        // discount_user info
                        bean.setId(obj.getInt("id"));
                        bean.setCount(obj.getInt("count"));
                        bean.setRest(obj.getInt("rest"));
                        bean.setDiscount_id(obj.getInt("discount_id"));
                        // discount info
                        bean.setName(obj.getString("name"));
                        bean.setExtent((float) obj.getDouble("extent"));
                        bean.setCoin(obj.getInt("coin"));
                        bean.setCreate_time(obj.getLong("discount_create_time") * 1000);
                        bean.setExpire_time(obj.getLong("discount_expire_time") * 1000);
                        bean.setOpen(obj.getInt("open"));
                        bean.setRemark(obj.getString("remark"));
                        list.add(bean);
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return list;
        }
    }

    /*加载更多任务*/
    protected class LoadMoreTask extends AsyncTask<Void, Void, ArrayList<DiscountUserBean>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mAdapter.setLoadStatus(FooterViewHolder.STATUS_LOADING);
            isLoading = true;
        }

        @Override
        protected void onPostExecute(ArrayList<DiscountUserBean> DiscountUserBeans) {
            super.onPostExecute(DiscountUserBeans);
            if(mTotalPage == mPage){
                mAdapter.setLoadStatus(FooterViewHolder.STATUS_LOAD_FULL, false);
            }else{
                if(DiscountUserBeans != null){
                    mAdapter.setLoadStatus(FooterViewHolder.STATUS_LOAD_DONE, false);
                }else{
                    mAdapter.setLoadStatus(FooterViewHolder.STATUS_LOAD_FAIL, false);
                }
            }
            if(DiscountUserBeans != null){
                mDiscounts.addAll(DiscountUserBeans);
                mAdapter.notifyDataSetChanged();
            }
            isLoading = false;
        }

        @Override
        protected ArrayList<DiscountUserBean> doInBackground(Void... voids) {
            RequestFuture future = RequestFuture.newFuture();
            HashMap<String, String> map = new HashMap<>();
            map.put("page", String.valueOf(mPage + 1));
            JSONObject params = new JSONObject(map);
            MyUserJsonObjectRequest request = new MyUserJsonObjectRequest(Request.Method.POST,
                    Params.URL_USER_DISCOUNS, params, future, future) {
                @Override
                public String getUserToken() {
                    return mToken;
                }
            };
            mQueue.add(request);
            ArrayList<DiscountUserBean> list = null;
            try {
                JSONObject response = (JSONObject) future.get();
                int code = response.getInt("code");
                if(code == 1){
                    list = new ArrayList<>();
                    JSONObject data = response.getJSONObject("data");
                    mPage = data.getInt("current_page");
                    mTotalPage = data.getInt("last_page");
                    JSONArray jsonArray = data.getJSONArray("data");
                    int size = jsonArray.length();
                    for(int i= 0; i< size;++i){
                        JSONObject obj = jsonArray.getJSONObject(i);
                        DiscountUserBean bean = new DiscountUserBean();
                        // discount_user info
                        bean.setId(obj.getInt("id"));
                        bean.setCount(obj.getInt("count"));
                        bean.setRest(obj.getInt("rest"));
                        bean.setDiscount_id(obj.getInt("discount_id"));
                        // discount info
                        bean.setName(obj.getString("name"));
                        bean.setExtent((float) obj.getDouble("extent"));
                        bean.setCoin(obj.getInt("coin"));
                        bean.setCreate_time(obj.getLong("discount_create_time") * 1000);
                        bean.setExpire_time(obj.getLong("discount_expire_time") * 1000);
                        bean.setOpen(obj.getInt("open"));
                        bean.setRemark(obj.getString("remark"));

                        list.add(bean);
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return list;
        }
    }
}
