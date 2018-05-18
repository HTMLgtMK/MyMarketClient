package com.gthncz.mymarketclient.main;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gthncz.mymarketclient.R;
import com.gthncz.mymarketclient.beans.PointStoreBean;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 积分商城列表适配器
 * Created by GT on 2018/5/18.
 */

public class PointStoreAdapter extends RecyclerView.Adapter {

    private final int TYPE_ITEM = 1;
    private final int TYPE_FOOTER = 2;

    private ArrayList<PointStoreBean> mPoints;
    private Context mContext;
    private FooterViewHolder.OnMyFooterViewClickedListener mFooterViewClickedListener;
    private OnClickedObtainListener mObtainListener;

    private int mStatus;

    public PointStoreAdapter(Context context){
        this.mContext = context;
        mStatus = FooterViewHolder.STATUS_LOAD_FULL;
    }

    public void setPoints(ArrayList<PointStoreBean> points){
        this.mPoints = points;
        notifyDataSetChanged();
    }

    public void setFooterViewClickedListener(FooterViewHolder.OnMyFooterViewClickedListener footerViewClickedListener) {
        this.mFooterViewClickedListener = footerViewClickedListener;
    }

    public void setObtainListener(OnClickedObtainListener listener){
        this.mObtainListener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == TYPE_ITEM){
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_point_store, parent, false);
            PointStoreViewHolder viewHolder = new PointStoreViewHolder(view);
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
        if(holder instanceof PointStoreViewHolder){
            PointStoreBean bean = mPoints.get(position);
            PointStoreViewHolder pointStoreViewHolder = (PointStoreViewHolder) holder;
            pointStoreViewHolder.setPoint(bean);
        }else{
            FooterViewHolder footerViewHolder = (FooterViewHolder) holder;
            footerViewHolder.setStatus(mStatus);
        }
    }

    @Override
    public int getItemCount() {
        int size = mPoints == null ? 0 : mPoints.size();
        return size + 1;// 加上footerView
    }

    @Override
    public int getItemViewType(int position) {
        if(mPoints == null){
            return TYPE_FOOTER;
        }
        if(position < mPoints.size()){
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
        if(mPoints != null){
            position = mPoints.size();
        }
        if(notifyChange){
            notifyItemChanged(position);
        }
    }

    protected class PointStoreViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.textView_item_point_store_point) protected TextView mPoint;
        protected  PointStoreBean mPointStoreBean;

        public PointStoreViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setPoint(PointStoreBean pointStoreBean) {
            this.mPointStoreBean = pointStoreBean;

            this.mPoint.setText(String.format("%d 积分", pointStoreBean.getPoint()));
        }

        @OnClick({R.id.textView_item_point_store_obtain})
        protected void onObtainClicked(){
            if(mObtainListener != null){
                mObtainListener.obtain(mPointStoreBean);
            }
        }
    }

    public interface OnClickedObtainListener {
        void obtain(PointStoreBean bean);
    }

}
