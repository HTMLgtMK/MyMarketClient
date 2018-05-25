package com.gthncz.mymarketclient.main;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;
import com.gthncz.mymarketclient.R;
import com.gthncz.mymarketclient.beans.Params;
import com.gthncz.mymarketclient.helper.MyLocalUserHelper;
import com.gthncz.mymarketclient.helper.MyUserJsonObjectRequest;
import com.gthncz.mymarketclient.helper.PayPasswordInput;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 修改支付密码的Activity
 *
 * Created by GT on 2018/5/25.
 */

public class ModifyPayPasswordActivity extends AppCompatActivity implements PayPasswordInput.OnFinishInputListener {
    private static final String TAG = ModifyPayPasswordActivity.class.getSimpleName();

    @BindView(R.id.linearLayout_activity_modify_pay_password_wrapper) protected LinearLayout mWrapper;
    @BindView(R.id.toolbar_modify_pay_password) protected Toolbar mToolbar;
    @BindView(R.id.payPasswordInput_modify_pay_password) protected PayPasswordInput mPasswordInput;

    private String mUserToken;

    private RequestQueue mQueue;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_pay_password);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        mPasswordInput.setOnFinishInputListener(this);
        mQueue = Volley.newRequestQueue(this);
        mQueue.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mUserToken = MyLocalUserHelper.getLocalToken(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mQueue.cancelAll(TAG);
        mQueue.stop();
        mQueue = null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{
                onBackPressed();;
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void hideKeyboard(){
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm.isActive() && getCurrentFocus()!= null){
            if(getCurrentFocus().getWindowToken() != null){
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    @Override
    public void oFinishInput(String password) {
        hideKeyboard();
        (new ModifyPasswordTask(this, mUserToken, password)).execute((Void) null);
    }

    private class ModifyPasswordTask extends AsyncTask<Void, Void, Pair<Integer, String>>{

        private Context mContext;
        private String mUserToken;
        private String mPassword;
        private AlertDialog alertDialog;

        public ModifyPasswordTask(Context mContext, String mUserToken, String mPassword) {
            this.mContext = mContext;
            this.mUserToken = mUserToken;
            this.mPassword = mPassword;
        }

        @Override
        protected void onPreExecute() {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
            ProgressBar progressBar = new ProgressBar(mContext);
            progressBar.setPadding(20, 20 , 20, 20);
            builder.setView(progressBar);
            builder.setMessage(R.string.modifying);
            builder.setCancelable(false);
            alertDialog = builder.create();
            alertDialog.show();
        }

        @Override
        protected void onPostExecute(Pair<Integer, String> result) {
            if(alertDialog != null && alertDialog.isShowing()){
                alertDialog.dismiss();
            }
            if(result.first == 1){
                Snackbar snackbar = Snackbar.make(mWrapper, result.second, Snackbar.LENGTH_LONG);
                snackbar.addCallback(new Snackbar.Callback(){
                    @Override
                    public void onDismissed(Snackbar transientBottomBar, int event) {
                        super.onDismissed(transientBottomBar, event);
                        ModifyPayPasswordActivity.this.finish();
                    }
                });
                snackbar.show();
            }else{
                Snackbar.make(mWrapper, result.second, Snackbar.LENGTH_LONG).show();
                mPasswordInput.clearPassword();
            }
        }

        @Override
        protected Pair<Integer, String> doInBackground(Void... voids) {
            RequestFuture future = RequestFuture.newFuture();
            HashMap<String, String> map = new HashMap<>();
            map.put("pay_password", mPassword);
            JSONObject params = new JSONObject(map);
            MyUserJsonObjectRequest request = new MyUserJsonObjectRequest(Request.Method.POST, Params.URL_MODIFY_PAY_PASSWORD,
                    params,future, future) {
                @Override
                public String getUserToken() {
                    return mUserToken;
                }
            };
            request.addMarker(TAG);
            mQueue.add(request);
            Pair<Integer, String> pair = null;
            try {
                JSONObject jsonObj = (JSONObject) future.get();
                int code = jsonObj.getInt("code");
                String msg = jsonObj.getString("msg");
                pair = new Pair<>(code, msg);
            } catch (InterruptedException e) {
                e.printStackTrace();
                pair = new Pair<>(0, e.getMessage());
            } catch (ExecutionException e) {
                e.printStackTrace();
                pair = new Pair<>(0, e.getMessage());
            } catch (JSONException e) {
                e.printStackTrace();
                pair = new Pair<>(0, e.getMessage());
            }
            return pair;
        }

    }
}
