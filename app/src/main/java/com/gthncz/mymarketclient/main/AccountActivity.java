package com.gthncz.mymarketclient.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.gthncz.mymarketclient.R;
import com.gthncz.mymarketclient.beans.DealBean;
import com.gthncz.mymarketclient.beans.Params;
import com.gthncz.mymarketclient.helper.MyUserJsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by GT on 2018/5/13.
 */

public class AccountActivity extends AppCompatActivity {

    @BindView(R.id.linearLayout_activity_account_wrapper) protected LinearLayout mWrapper;
    @BindView(R.id.toolBar_activity_account) protected Toolbar mToolbar;
    @BindView(R.id.swipeRefreshLayout_activity_account_refresh) protected SwipeRefreshLayout mRefresh;
    @BindView(R.id.recycleView_activity_account_list) protected RecyclerView mRecyclerView;

    private AccountListAdapter mAccountListAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    private ArrayList<DealBean> mDealList;
    private int mPage;
    private int mTotalPage;

    private boolean mLoading;// 加载标识

    private RequestQueue mRequestQueue;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);

        mDealList = new ArrayList<>();
        mPage = 1;
        mTotalPage = 1;
        mLoading = false;

        mRequestQueue = Volley.newRequestQueue(this);
        mRequestQueue.start();

        mRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initLoad();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        mAccountListAdapter = new AccountListAdapter(this);
        mAccountListAdapter.setDealList(mDealList);
        mAccountListAdapter.setFooterViewClickedListener(new FooterViewHolder.OnMyFooterViewClickedListener() {
            @Override
            public void onClickedLoadFail() {
                loadMore();
            }
        });
        mRecyclerView.setAdapter(mAccountListAdapter);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(dy<0) return;
                int pos = mLinearLayoutManager.findLastVisibleItemPosition();
                int total = mLinearLayoutManager.getItemCount();
                if(pos == total-1){
                    loadMore();
                }
            }
        });
        initLoad();
    }

    /**
     * 刷新加载
     */
    private void initLoad(){
        if(mLoading) return;
        mPage = 1;
        mDealList.clear();
        mLoading = true;
        mRefresh.measure(0,0);
        mRefresh.setRefreshing(true);
        HashMap<String, String> map = new HashMap<>();
        map.put("page", String.valueOf(mPage));
        JSONObject jsonObject = new JSONObject(map);
        MyUserJsonObjectRequest request = new MyUserJsonObjectRequest(Request.Method.POST, Params.URL_ACCOUNT_LIST, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                mRefresh.setRefreshing(false);
                mLoading = false;
                Log.e(AccountActivity.class.getSimpleName(), response.toString());
                try {
                    int code = response.getInt("code");
                    if(code == 1){
                        JSONObject data = response.getJSONObject("data");
                        data = data.getJSONObject("sales");
                        int last_page = data.getInt("last_page");
                        int current_page = data.getInt("current_page");
                        JSONArray jsonArray = data.getJSONArray("data");
                        ArrayList<DealBean> list = DealBean.getDealListFromJSONObject(jsonArray);
                        mPage = current_page;
                        mTotalPage = last_page;
                        mDealList.addAll(list);
                        if(current_page == last_page){
                            mAccountListAdapter.setLoadStatus(FooterViewHolder.STATUS_LOAD_FULL, false);
                        }else{
                            mAccountListAdapter.setLoadStatus(FooterViewHolder.STATUS_LOAD_DONE, false);
                        }
                        mAccountListAdapter.notifyDataSetChanged();
                    }else{
                        String msg = response.getString("msg");
                        Snackbar.make(mWrapper, msg, Snackbar.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mRefresh.setRefreshing(false);
                mLoading = false;
                Log.e(AccountActivity.class.getSimpleName(), "** 信息 >> " + error.toString());
                Snackbar.make(mWrapper, error.toString(), Snackbar.LENGTH_LONG).show();
            }
        });
        mRequestQueue.add(request);
    }

    private void loadMore(){
        if(mLoading) return;
        if(mTotalPage == mPage) return;// 已经是最后一页
        mLoading = true;
        mAccountListAdapter.setLoadStatus(FooterViewHolder.STATUS_LOADING, false);
        HashMap<String, String> map = new HashMap<>();
        map.put("page", String.valueOf(mPage+1)); // 下一页
        JSONObject jsonObject = new JSONObject(map);
        MyUserJsonObjectRequest request = new MyUserJsonObjectRequest(Request.Method.POST, Params.URL_ACCOUNT_LIST, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                mLoading = false;
                Log.e(AccountActivity.class.getSimpleName(), response.toString());
                try {
                    int code = response.getInt("code");
                    if(code == 1){
                        JSONObject data = response.getJSONObject("data");
                        data = data.getJSONObject("sales");
                        int last_page = data.getInt("last_page");
                        int current_page = data.getInt("current_page");
                        JSONArray jsonArray = data.getJSONArray("data");
                        ArrayList<DealBean> list = DealBean.getDealListFromJSONObject(jsonArray);
                        mPage = current_page;
                        mTotalPage = last_page;
                        mDealList.addAll(list);
                        if(current_page == last_page){
                            mAccountListAdapter.setLoadStatus(FooterViewHolder.STATUS_LOAD_FULL, false);
                        }else{
                            mAccountListAdapter.setLoadStatus(FooterViewHolder.STATUS_LOAD_DONE, false);
                        }
                        mAccountListAdapter.notifyDataSetChanged();// 更新数据, 包括更新底部的UI、
                    }else{
                        String msg = response.getString("msg");
                        Snackbar.make(mWrapper, msg, Snackbar.LENGTH_LONG).show();
                        mAccountListAdapter.setLoadStatus(FooterViewHolder.STATUS_LOAD_FAIL);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(AccountActivity.class.getSimpleName(), "** 信息 >> " + error.toString());
                mLoading = false;
                Snackbar.make(mWrapper, error.toString(), Snackbar.LENGTH_LONG).show();
                mAccountListAdapter.setLoadStatus(FooterViewHolder.STATUS_LOAD_FAIL);
            }
        });
        mRequestQueue.add(request);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRequestQueue.stop();
        mRequestQueue = null;
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
}
