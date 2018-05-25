package com.gthncz.mymarketclient.payment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Pair;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;
import com.gthncz.mymarketclient.R;
import com.gthncz.mymarketclient.beans.Params;
import com.gthncz.mymarketclient.greendao.User;
import com.gthncz.mymarketclient.helper.DigestHelper;
import com.gthncz.mymarketclient.helper.MyLocalUserHelper;
import com.gthncz.mymarketclient.helper.MyUserJsonObjectRequest;
import com.gthncz.mymarketclient.helper.PayPasswordInput;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 余额支付控制器
 *
 * Created by GT on 2018/5/24.
 */

public class BPayActivity extends AppCompatActivity {

    @BindView(R.id.toolbar_payment) protected Toolbar mToolbar;
    @BindView(R.id.viewFlipper_payment_togglePage) protected ViewFlipper mViewFlipper;
    @BindView(R.id.textView_payment_subject) protected TextView mSubjectView;
    @BindView(R.id.textView_payment_id) protected TextView mPayIdView;
    @BindView(R.id.textView_payment_pay_amount) protected TextView mPayAmountView;
    @BindView(R.id.textView_payment_time_start) protected TextView mTimeStartView;
    @BindView(R.id.payPasswordInput_payment_password) protected PayPasswordInput mPasswordInput;

    private String mToken; /* 支付请求token */
    private User mUser;
    private String mUserToken;

    private RequestQueue mQueue;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bpay);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);

        mQueue = Volley.newRequestQueue(this);
        mQueue.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mUser = MyLocalUserHelper.getLocalUser(this);
        mUserToken = MyLocalUserHelper.getLocalToken(this);
        Intent intent = getIntent();
        mToken = intent.getStringExtra("token");
        if(mToken == null){
            showMessageDialogAndExit("Token参数为空!");
        }else{
            // 开始检查交易状态
            checkPaymentStatus();
        }
    }

    private void checkPaymentStatus() {
        (new CheckPaymentStatusTask(mToken, mUserToken)).execute((Void) null);
    }

    private void initPasswordInputPage(PaymentBean bean) {
        if(bean.getCode() == 0) return;
        mPayAmountView.setText(String.format("%.2f", Float.valueOf(bean.getPayAmount())/100));
        mSubjectView.setText(bean.getSubject());
        mPayIdView.setText(bean.getId());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        mTimeStartView.setText(simpleDateFormat.format(new Date(bean.getTimeStart())));
        mPasswordInput.setOnFinishInputListener(new PayPasswordInput.OnFinishInputListener() {
            @Override
            public void oFinishInput(String password) {
                startBalancePay(password);
            }
        });
    }

    private void startBalancePay(String password){
        (new BalancePayTask(this, mToken, mUserToken, password)).execute((Void) null);
    }


    private void showMessageDialogAndExit(String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        builder.setMessage(msg);
        builder.setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                BPayActivity.this.finish();
            }
        });
        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();
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

    private class BalancePayTask extends AsyncTask<Void,Void,Pair<Integer, String>>{

        private Context mContext;
        private String mToken;
        private String mUserToken;
        private String mPayPassword;
        private AlertDialog dialog;

        public BalancePayTask(Context context, String mToken, String mUserToken, String mPayPassword) {
            this.mContext = context;
            this.mToken = mToken;
            this.mUserToken = mUserToken;
            this.mPayPassword = mPayPassword;
        }

        @Override
        protected void onPreExecute() {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
            ProgressBar progressBar = new ProgressBar(mContext);
            progressBar.setPadding(20, 20, 20, 20);
            builder.setView(progressBar);
            builder.setMessage(mContext.getString(R.string.paying));
            builder.setCancelable(false);
            dialog = builder.create();
            dialog.show();
        }

        @Override
        protected Pair<Integer, String> doInBackground(Void... voids) {
            RequestFuture future = RequestFuture.newFuture();
            HashMap<String, String> map = new HashMap<>();
            String postPassword = null;
            try { // md5(sha1()) 加密
                postPassword = DigestHelper.md5(DigestHelper.sha1(mPayPassword));
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            map.put("token", mToken);
            map.put("pay_password", postPassword);
            JSONObject params = new JSONObject(map);
            MyUserJsonObjectRequest request = new MyUserJsonObjectRequest(Request.Method.POST, Params.URL_BALANCE_PAY,
                    params, future, future) {
                @Override
                public String getUserToken() {
                    return mUserToken;
                }
            };
            mQueue.add(request);
            Pair<Integer, String> result = null;
            try {
                JSONObject jsonObj = (JSONObject) future.get();
                int code = jsonObj.getInt("code");
                String msg = jsonObj.getString("msg");
                result = new Pair<>(code, msg);
            } catch (InterruptedException e) {
                e.printStackTrace();
                result = new Pair<>(0, e.getMessage());
            } catch (ExecutionException e) {
                e.printStackTrace();
                result = new Pair<>(0, e.getMessage());
            } catch (JSONException e) {
                e.printStackTrace();
                result = new Pair<>(0, e.getMessage());
            }
            return result;
        }

        @Override
        protected void onPostExecute(Pair<Integer, String> result) {
            if(dialog !=null && dialog.isShowing()){
                dialog.dismiss();
            }
            if (result.first == 1){
                showMessageDialogAndExit(result.second);
            }else{
                Toast.makeText(mContext, result.second, Toast.LENGTH_LONG).show();
                mPasswordInput.clearPassword();
            }
        }
    }

    /** 检查交易状态任务  */
    private class CheckPaymentStatusTask extends AsyncTask<Void, Void, PaymentBean> {

        private String mToken;
        private String mUserToken;

        public CheckPaymentStatusTask(String mToken, String mUserToken) {
            this.mToken = mToken;
            this.mUserToken = mUserToken;
        }

        @Override
        protected PaymentBean doInBackground(Void... voids) {
            RequestFuture future = RequestFuture.newFuture();
            HashMap<String, String> map = new HashMap<>();
            map.put("token", mToken);
            JSONObject params = new JSONObject(map);
            MyUserJsonObjectRequest request = new MyUserJsonObjectRequest(Request.Method.POST,
                    Params.URL_CHECK_BALANCE_PAY_STATUS, params, future, future) {
                @Override
                public String getUserToken() {
                    return mUserToken;
                }
            };
            mQueue.add(request);
            PaymentBean bean = new PaymentBean();
            try {
                JSONObject jsonObject = (JSONObject) future.get();
                Log.e(BPayActivity.class.getSimpleName(), jsonObject.toString());
               bean.fillContent(jsonObject);
            } catch (InterruptedException e) {
                e.printStackTrace();
                bean.setCode(0);
                bean.setMsg(e.getMessage());
            } catch (ExecutionException e) {
                e.printStackTrace();
                bean.setCode(0);
                bean.setMsg(e.getMessage());
            } catch (JSONException e) {
                e.printStackTrace();
                bean.setCode(0);
                bean.setMsg(e.getMessage());
            }
            return bean;
        }

        @Override
        protected void onPostExecute(PaymentBean bean) {
            if(bean.getCode() == 1){
                int status = bean.getStatus();
                switch (status){
                    case 3:{
                        showMessageDialogAndExit("交易已经成功!");
                        break;
                    }
                    case 4:{
                        showMessageDialogAndExit("交易已经关闭");
                        break;
                    }
                    case 5:{
                        showMessageDialogAndExit("交易已经取消");
                        break;
                    }
                    case 1: // 用户未扫码, 不可能出现的情形
                    case 2:{ // 等待用户付款
                        mViewFlipper.showNext();
                        initPasswordInputPage(bean);
                        break;
                    }
                }
            }else{//检查结果出现错误
                showMessageDialogAndExit(bean.getMsg());
            }
        }
    }

    private class PaymentBean {
        // 必须返回的值
        private int code;
        private String msg;
        // 请求成功时返回的值
        private String id;
        private long userId;
        private String token;
        private String outTradeNO;
        private int totalAmount;
        private int discountAmount;
        private int payAmount;
        private long timeStart;
        private long timeExpire;
        private String subject;
        private String goodsDetail;
        private int storeId;
        private int terminalId;
        private int status;
        private long modifyTime;

        public void setCode(int code) {
            this.code = code;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public void fillContent(JSONObject jsonObj) throws JSONException {
            code = jsonObj.getInt("code");
            msg = jsonObj.getString("msg");
            if(code == 1){
                JSONObject data = jsonObj.getJSONObject("data");
                id = data.getString("id");
                userId = data.getLong("user_id");
                token = data.getString("token");
                outTradeNO = data.getString("out_trade_no");
                totalAmount = data.getInt("total_amount");
                discountAmount = data.getInt("discount_amount");
                payAmount = data.getInt("pay_amount");
                timeStart = data.getLong("time_start") * 1000;
                timeExpire = data.getLong("time_expire") * 1000;
                subject = data.getString("subject");
                goodsDetail = data.getString("goods_detail");
                storeId = data.getInt("store_id");
                terminalId = data.getInt("terminal_id");
                status = data.getInt("status");
                modifyTime = data.getLong("modify_time") * 1000;
            }
        }

        public int getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }

        public String getId() {
            return id;
        }

        public long getUserId() {
            return userId;
        }

        public String getToken() {
            return token;
        }

        public String getOutTradeNO() {
            return outTradeNO;
        }

        public int getTotalAmount() {
            return totalAmount;
        }

        public int getDiscountAmount() {
            return discountAmount;
        }

        public int getPayAmount() {
            return payAmount;
        }

        public long getTimeStart() {
            return timeStart;
        }

        public long getTimeExpire() {
            return timeExpire;
        }

        public String getSubject() {
            return subject;
        }

        public String getGoodsDetail() {
            return goodsDetail;
        }

        public int getStoreId() {
            return storeId;
        }

        public int getTerminalId() {
            return terminalId;
        }

        public int getStatus() {
            return status;
        }

        public long getModifyTime() {
            return modifyTime;
        }
    }
}
