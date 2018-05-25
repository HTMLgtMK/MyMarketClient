package com.gthncz.mymarketclient.main;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gthncz.mymarketclient.R;
import com.gthncz.mymarketclient.beans.PointLogBean;
import com.gthncz.mymarketclient.beans.PointLogBean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by GT on 2018/5/18.
 */

public class PointDetailAdapter extends RecyclerView.Adapter {

    private final int TYPE_ITEM = 1;
    private final int TYPE_FOOTER = 2;

    private int mStatus;
    private FooterViewHolder.OnMyFooterViewClickedListener mFooterViewClickedListener;

    private ArrayList<PointLogBean> mPointLogs;
    private Context mContext;
    private SimpleDateFormat mSimpleDateFormat;

    public PointDetailAdapter(Context context) {
        this.mContext = context;
        mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        mStatus = FooterViewHolder.STATUS_LOAD_FULL;
    }

    public void setPointLogs(ArrayList<PointLogBean> logs){
        this.mPointLogs = logs;
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
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_point_log, parent, false);
            PointLogViewHolder viewHolder = new PointLogViewHolder(view);
            return viewHolder;
        }else if(viewType == TYPE_FOOTER){
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_footer, parent, false);
            FooterViewHolder viewHolder = new FooterViewHolder(view);
            viewHolder.setFooterViewClickedListener(mFooterViewClickedListener);
            return viewHolder;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof PointLogViewHolder && position < mPointLogs.size()){
            PointLogBean PointLogBean = mPointLogs.get(position);
            PointLogViewHolder PointLogViewHolder = (PointLogViewHolder) holder;
            PointLogViewHolder.setPointLog(PointLogBean);
        }else{
            FooterViewHolder footerViewHolder = (FooterViewHolder) holder;
            footerViewHolder.setStatus(mStatus);
        }
    }

    @Override
    public int getItemCount() {
        int size = mPointLogs == null ? 0 : mPointLogs.size();
        return size + 1;// 加上footerView
    }

    @Override
    public int getItemViewType(int position) {
        if(mPointLogs == null){
            return TYPE_FOOTER;
        }
        if(position < mPointLogs.size()){
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
        if(mPointLogs != null){
            position = mPointLogs.size();
        }
        if(notifyChange){
            notifyItemChanged(position);
        }
    }

    protected class PointLogViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.textView_item_point_log_action) protected TextView mActionView;
        @BindView(R.id.textView_item_point_log_time) protected TextView mTimeView;
        @BindView(R.id.textView_item_point_log_change) protected TextView mChangeView;

        protected PointLogBean mPointLogBean;

        public PointLogViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setPointLog(PointLogBean PointLogBean){
            this.mPointLogBean = PointLogBean;
            mActionView.setText(getActionName(PointLogBean.getAction()));
            mTimeView.setText(mSimpleDateFormat.format(new Date(PointLogBean.getCreate_time())));
            mChangeView.setText(String.format("%+d", PointLogBean.getPoint()));
        }

        /**
         * 将后台操作转换为用户可接受的操作名
         * @param action
         * @return
         */
        private String getActionName(String action){
            String t = action.substring(action.lastIndexOf('/')+1);
            if("point2balance".equals(t)){
                return "积分转余额";
            }else if("obtain".equals(t)){
                return "领取积分";
            }else if("alipayQuery".equals(t) || "wxpayQuery".equals(t) || "balancePayQuery".equals(t)){
                return "支付积分";
            }
            return "";
        }

    }
}
