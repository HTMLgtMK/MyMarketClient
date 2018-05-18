package com.gthncz.mymarketclient.main;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gthncz.mymarketclient.R;
import com.gthncz.mymarketclient.beans.DealBean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 首页上的交易列表适配器
 * Created by GT on 2018/5/12.
 */

public class AccountListAdapter extends RecyclerView.Adapter {

    private final int TYPE_ITEM = 1;
    private final int TYPE_FOOTER = 2;
    private ArrayList<DealBean> mDealList;
    private Context mContext;
    private int mStatus;
    private FooterViewHolder.OnMyFooterViewClickedListener mFooterViewClickedListener;

    public AccountListAdapter(Context context){
        this.mContext = context;
        mStatus = FooterViewHolder.STATUS_LOAD_FULL;
    }

    public void setDealList(ArrayList<DealBean> dealList){
        this.mDealList = dealList;
        notifyDataSetChanged();
    }

    /**
     * 为底部的布局添加点击事件回调接口, 一定要在 {@link // RecyclerView.setAdapter()} 方法前调用
     * @param mFooterViewClickedListener
     */
    public void setFooterViewClickedListener(FooterViewHolder.OnMyFooterViewClickedListener mFooterViewClickedListener) {
        this.mFooterViewClickedListener = mFooterViewClickedListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == TYPE_ITEM ){
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_account, parent, false);
            DealViewHolder viewHolder = new DealViewHolder(view);
            return viewHolder;
        }else if(viewType == TYPE_FOOTER){
            Log.e(getClass().getSimpleName(), "** 信息  >> onCreateViewHolder type: footer");
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_footer, parent, false);
            FooterViewHolder viewHolder = new FooterViewHolder(view);
            viewHolder.setFooterViewClickedListener(mFooterViewClickedListener);
            return viewHolder;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof DealViewHolder){
            DealBean dealBean = mDealList.get(position);
            DealViewHolder dealViewHolder = (DealViewHolder) holder;
            dealViewHolder.setDeal(dealBean);
        }else{
            FooterViewHolder viewHolder = (FooterViewHolder) holder;
            viewHolder.setStatus(mStatus);
        }
    }

    @Override
    public int getItemCount() {
        int size = mDealList == null ? 0 : mDealList.size();
        return size + 1;// 加上footerView
    }

    @Override
    public int getItemViewType(int position) {
        if(mDealList == null){
            return TYPE_FOOTER;
        }
        if(position < mDealList.size()){
            return TYPE_ITEM;
        }else{
            return TYPE_FOOTER;
        }
    }

    public void setLoadStatus(int loadStatus){
        this.setLoadStatus(loadStatus, true); // 默认更新UI
    }

    /**
     * 设置底部View的状态显示
     * @param loadStatus 状态
     * @param notifyChange 是否更新UI, 当前面有调用notifyDataSetChange()应为false
     */
    public void setLoadStatus(int loadStatus, boolean notifyChange){
        this.mStatus = loadStatus;
        int position = 0;
        if(mDealList != null){
            position = mDealList.size();
        }
        if(notifyChange){
            notifyItemChanged(position);
        }
    }



    public class DealViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.textView_item_account_account_id) protected TextView mDealId;
        @BindView(R.id.textView_item_account_create_time) protected TextView mCreatTime;
        @BindView(R.id.textView_item_account_payamount) protected TextView mPayAmount;
        @BindView(R.id.textView_item_account_status) protected TextView mStatus;
        public DealViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setDeal(DealBean deal){
            mDealId.setText(deal.getId());
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            mCreatTime.setText(dateFormat.format(new Date(deal.getCreate_time())));
            mPayAmount.setText(String.format("%.2f", (float)deal.getPay_amount() / 100));
            switch (deal.getStatus()){
                case 0x01:{//待付款
                    mStatus.setText( mContext.getString(R.string.pay_wait));
                    mStatus.setTextColor(ContextCompat.getColor(mContext, R.color.status_pay_wait));
                    break;
                }
                case 0x02:{//超时关闭
                    mStatus.setTextColor(ContextCompat.getColor(mContext, R.color.status_pay_expire));
                    mStatus.setText(mContext.getString(R.string.pay_expire));
                    break;
                }
                case 0x03:{//付款成功
                    mStatus.setText(mContext.getString(R.string.pay_success));
                    mStatus.setTextColor(ContextCompat.getColor(mContext, R.color.status_pay_success));
                    break;
                }
                case 0x04:{//取消
                    mStatus.setText(mContext.getString(R.string.pay_cancel));
                    mStatus.setTextColor(ContextCompat.getColor(mContext, R.color.status_pay_cancel));
                    break;
                }
            }
        }
    }

}
