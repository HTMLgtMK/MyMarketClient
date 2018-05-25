package com.gthncz.mymarketclient.main;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gthncz.mymarketclient.R;
import com.gthncz.mymarketclient.beans.DiscountStoreBean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GT on 2018/5/19.
 */

public class DiscountStoreAdapter extends RecyclerView.Adapter {

    private final int TYPE_FOOTER = 0X01;
    private final int TYPE_ITEM_ALL = 0X02;
    private final int TYPE_ITEM_VIP = 0X03;

    private Context mContext;
    private int mStatus;

    private ArrayList<DiscountStoreBean> mDiscounts;
    private SimpleDateFormat mSimpleDateFormat;

    private FooterViewHolder.OnMyFooterViewClickedListener mFooterViewClickedListener;
    private OnClickObtainListener mObtainListener;

    public DiscountStoreAdapter(Context context) {
        this.mContext = context;
        mSimpleDateFormat = new SimpleDateFormat("MM-dd HH:mm");
        mStatus = FooterViewHolder.STATUS_LOAD_FULL;
    }

    public void setDiscounts(ArrayList<DiscountStoreBean> discounts){
        this.mDiscounts = discounts;
        notifyDataSetChanged();
    }

    public void setObtainListener(OnClickObtainListener listener) {
        this.mObtainListener = listener;
    }

    public void setFooterViewClickedListener(FooterViewHolder.OnMyFooterViewClickedListener listener){
        this.mFooterViewClickedListener = listener;
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
        if(notifyChange){
            int position = 0;
            if(mDiscounts != null){
                position = mDiscounts.size();
            }
            notifyItemChanged(position);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM_ALL || viewType == TYPE_ITEM_VIP){
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_discountstore, parent, false);
            DiscountStoreViewHolder viewHolder = new DiscountStoreViewHolder(view);
            return viewHolder;
        }else{
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_footer, parent, false);
            FooterViewHolder viewHolder = new FooterViewHolder(view);
            viewHolder.setStatus(mStatus);
            viewHolder.setFooterViewClickedListener(mFooterViewClickedListener);
            return viewHolder;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof DiscountStoreViewHolder){
            DiscountStoreBean bean = mDiscounts.get(position);
            DiscountStoreViewHolder viewHolder = (DiscountStoreViewHolder) holder;
            viewHolder.setDiscountStoreBean(bean);
        }else{
            FooterViewHolder viewHolder = (FooterViewHolder) holder;
            viewHolder.setStatus(mStatus);
        }
    }

    @Override
    public int getItemCount() {
        int size = mDiscounts == null ? 0 : mDiscounts.size();
        return size + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if(mDiscounts == null) return TYPE_FOOTER;
        if(position < mDiscounts.size()){
            DiscountStoreBean bean = mDiscounts.get(position);
            switch (bean.getOpen()){
                case 1:{
                    return TYPE_ITEM_ALL;
                }
                case 2:{
                    return TYPE_ITEM_VIP;
                }
            }
        }
        return TYPE_FOOTER;
    }

    protected class DiscountStoreViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.textView_item_discountstore_discount_name_rest) protected TextView mDiscountNameRestView;
        @BindView(R.id.textView_item_discountstore_discount_extend) protected TextView mDiscountExtendView;
        @BindView(R.id.textView_item_discountstore_discount_coin) protected TextView mDiscountCoinView;
        @BindView(R.id.textView_item_discountstore_expire_duration) protected TextView mDiscountExpireDurationView;
        @BindView(R.id.button_item_discountstore_obtain) protected Button mDiscountObtainView;
        @BindView(R.id.textView_item_discountstore_possessed) protected TextView mDiscountPossessedView;
        @BindView(R.id.frameLayout_item_discountstore_operation) protected FrameLayout mOperationView;
        @BindView(R.id.textView_item_discountstore_discount_vip) protected TextView mVIPView;

        protected DiscountStoreBean mDiscountStoreBean;



        public DiscountStoreViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setDiscountStoreBean(DiscountStoreBean bean){
            this.mDiscountStoreBean = bean;
            String nameRest = String.format("%s (%d 张)" , bean.getName(), bean.getRest());
            mDiscountNameRestView.setText(nameRest);
            if(bean.getExtent() - 1.0f < 0f) {
                mDiscountExtendView.setText(String.format("%.1f折", bean.getExtent()*10));
            }else{
                mDiscountExtendView.setText("无打折");
            }
            if(bean.getCoin() == 0){
                mDiscountCoinView.setText("无立减");
            }else{
                mDiscountCoinView.setText(String.format("减%.2f元", ((float)-bean.getCoin())/100));
            }
            if(bean.getOpen() == 1){ // open to all
                mVIPView.setText(mContext.getString(R.string.all));
                mOperationView.setVisibility(FrameLayout.INVISIBLE);
            }else if(bean.getOpen() == 2){// open to vip only
                mVIPView.setText(mContext.getString(R.string.vip));
                mOperationView.setVisibility(FrameLayout.VISIBLE);
                if(bean.isPossess()){
                    mDiscountPossessedView.setVisibility(TextView.VISIBLE);
                    mDiscountObtainView.setVisibility(Button.INVISIBLE);
                }else{
                    mDiscountPossessedView.setVisibility(TextView.INVISIBLE);
                    mDiscountObtainView.setVisibility(Button.VISIBLE);
                }
            }
            String expireDuration = String.format("有效期: %s - %s",
                    mSimpleDateFormat.format(new Date(bean.getCreate_time())),
                    mSimpleDateFormat.format(new Date(bean.getExpire_time())));
            mDiscountExpireDurationView.setText(expireDuration);
        }

        @OnClick({R.id.button_item_discountstore_obtain})
        protected void clickedObtain(){
            if(mObtainListener != null){
                mObtainListener.onObtain(mDiscountStoreBean);
            }
        }
    }

    public interface OnClickObtainListener{
        void onObtain(DiscountStoreBean bean);
    }

}
