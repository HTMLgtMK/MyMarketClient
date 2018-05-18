package com.gthncz.mymarketclient.main;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.gthncz.mymarketclient.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 列表底部ViewHolder
 * Created by GT on 2018/5/18.
 */

public class FooterViewHolder extends RecyclerView.ViewHolder {

    public final static int STATUS_LOADING = 0x01;
    public final static int STATUS_LOAD_DONE =0x02;
    public final static int STATUS_LOAD_FAIL = 0x03;
    public final static int STATUS_LOAD_FULL = 0x04;
    /*当前状态*/
    private int mStatus;

    private OnMyFooterViewClickedListener footerViewClickedListener;

    @BindView(R.id.linearLayout_item_footer_load_fail) protected LinearLayout mLoadFail;
    @BindView(R.id.linearLayout_item_footer_load_full) protected LinearLayout mLoadFull;
    @BindView(R.id.linearLayout_item_footer_loading) protected LinearLayout mLoading;
    @BindView(R.id.linearLayout_item_footer_load_done) protected LinearLayout mLoadDone;

    public FooterViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        mStatus = STATUS_LOAD_FULL;
    }

    public void setFooterViewClickedListener(OnMyFooterViewClickedListener footerViewClickedListener) {
        this.footerViewClickedListener = footerViewClickedListener;
    }

    @OnClick({R.id.linearLayout_item_footer_load_fail})
    protected void onLoadFail(){
        Log.e(getClass().getSimpleName(), "** 信息 >> loadFail被点击!");
        setStatus(STATUS_LOADING);// 修改当前状态为loading, 会导致onCreateViewHolder重新调用
        if(footerViewClickedListener != null){
            footerViewClickedListener.onClickedLoadFail();
        }
    }

    public void setStatus(int status){
        if(status == mStatus){
            return; // 状态相同，不需要重复设置
        }
        mStatus = status;
        mLoadFull.setVisibility(LinearLayout.INVISIBLE);
        mLoadDone.setVisibility(LinearLayout.INVISIBLE);
        mLoading.setVisibility(LinearLayout.INVISIBLE);
        mLoadFail.setVisibility(LinearLayout.INVISIBLE);
        switch (status){
            case STATUS_LOADING:{
                mLoading.setVisibility(LinearLayout.VISIBLE);
                break;
            }
            case STATUS_LOAD_DONE:{
                mLoadDone.setVisibility(LinearLayout.VISIBLE);
                break;
            }
            case STATUS_LOAD_FAIL:{
                mLoadFail.setVisibility(LinearLayout.VISIBLE);
                break;
            }
            case STATUS_LOAD_FULL:{
                mLoadFull.setVisibility(LinearLayout.VISIBLE);
                break;
            }
        }
    }

    public interface  OnMyFooterViewClickedListener{
        void onClickedLoadFail();
    }
}