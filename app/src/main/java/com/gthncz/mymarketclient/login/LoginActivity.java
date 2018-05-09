package com.gthncz.mymarketclient.login;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.gthncz.mymarketclient.ClientApplication;
import com.gthncz.mymarketclient.R;
import com.gthncz.mymarketclient.beans.Params;
import com.gthncz.mymarketclient.greendao.ClientDBHelper;
import com.gthncz.mymarketclient.greendao.User;
import com.gthncz.mymarketclient.greendao.UserDao;
import com.gthncz.mymarketclient.greendao.UserToken;
import com.gthncz.mymarketclient.greendao.UserTokenDao;
import com.gthncz.mymarketclient.main.ClientActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A login screen that offers login via email or mobile or user_login/password.
 */
public class LoginActivity extends AppCompatActivity {
    // UI references.
    @BindView(R.id.toolBar_login) protected Toolbar toolbar;
    @BindView(R.id.linearLayout_wrapper) protected LinearLayout mWrapper;
    @BindView(R.id.account) protected AutoCompleteTextView mAccountView;
    @BindView(R.id.password) protected EditText mPasswordView;
    @BindView(R.id.account_sign_in_button) protected Button mLoginButton;
    @BindView(R.id.login_progress) protected View mProgressView;
    @BindView(R.id.login_form) protected View mLoginFormView;
    @BindView(R.id.button_imm_register) Button mRegisterButton;

    // network request queue
    private RequestQueue mQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        // Set up the login form.
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        mLoginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });


        mRegisterButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        // init valley request queue
        mQueue = Volley.newRequestQueue(getApplicationContext());
        mQueue.start();

        // check current user status
        checkCuurentUserStatus();
    }

    /**
     * 检查当前用户的登陆状态
     */
    protected void checkCuurentUserStatus() {
        SharedPreferences sharedPreferences = getSharedPreferences(Params.INI_NAME, MODE_PRIVATE);
        if(sharedPreferences.contains(Params.KEY_CURRENT_USER_ID)){
            long user_id = sharedPreferences.getLong(Params.KEY_CURRENT_USER_ID, 0);
            if(user_id != 0){
                List<User> users = ClientDBHelper.getInstance(this).getDaoSession().getUserDao().queryBuilder().where(UserDao.Properties.Id.eq(user_id)).limit(1).list();
                List<UserToken> tokens = ClientDBHelper.getInstance(this).getDaoSession().getUserTokenDao().queryBuilder().where(UserTokenDao.Properties.User_id.eq(user_id)).limit(1).list();
                if(users.size() > 0 && tokens.size()>0){
                    User user = users.get(0);
                    UserToken token = tokens.get(0);
                    if(System.currentTimeMillis()/1000 < token.getExpire_time()){
                        // TODO jump to next page directly
                        if(ClientApplication.DEBUG){
                            Log.i(getClass().getSimpleName(), "user in his duration, jump to next page directly !!!");
                        }
                        ClientApplication.getInstance().setUser(user);
                        ClientApplication.getInstance().setToken(token.getToken());
                        showClientMainPage();
                    }//else the token has expired, have to relogin
                }// else not login, nothing to do
            }// else not login, nothin to do
        }// else not login, nothing to do
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if(mQueue == null){
            return;
        }

        // Reset errors.
        mAccountView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String account = mAccountView.getText().toString().trim();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if (TextUtils.isEmpty(account)) {
            mAccountView.setError(getString(R.string.error_field_required));
            focusView = mAccountView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);
            submit(account, password);
        }
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * 提交会员登陆
     * @param mAccount
     * @param mPassword
     */
    protected void submit(String mAccount, String mPassword){
        HashMap<String, String> map = new HashMap<>();
        map.put("username", mAccount);
        map.put("password", mPassword);
        map.put("device_type", "android");
        JSONObject params = new JSONObject(map);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Params.URL_USER_LOGIN, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {// UI 线程中执行
                if(ClientApplication.DEBUG){
                    Log.i(getClass().getSimpleName(), response.toString());
                }
                try{
                    int code = response.getInt("code");
                    String msg = response.getString("msg");
                    if(code == 1){
                        JSONObject data = response.getJSONObject("data");
                        //存入数据库
                        (new StoreUserInfoTask(LoginActivity.this.getApplicationContext(), data)).execute((Void)null);
                    }else{
                        if(ClientApplication.DEBUG){
                            Log.i(getClass().getSimpleName(), msg);
                        }
                        showProgress(false);
                        Snackbar.make(mWrapper, msg, Snackbar.LENGTH_SHORT).show();
                    }
                }catch (JSONException e){
                    if(ClientApplication.DEBUG){
                        Log.i(getClass().getSimpleName(), e.getMessage());
                    }
                    showProgress(false);
                    Snackbar.make(mWrapper, e.getMessage(), Snackbar.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(ClientApplication.DEBUG){
                    Log.i(getClass().getSimpleName(),  "Network error!" + error.toString());
                }
                Snackbar.make(mWrapper,  "Network error!" + error.toString() , Snackbar.LENGTH_SHORT).show();
            }
        });
        mQueue.add(jsonObjectRequest);
    }

    /**
     * 存储会员信息的异步类
     */
    private class StoreUserInfoTask extends AsyncTask<Void, Void, Pair<Integer, String>> {
        private JSONObject mData;
        private Context mContext;

        public StoreUserInfoTask(Context context, JSONObject data) {
            this.mData = data;
            this.mContext = context;
        }

        @Override
        protected Pair<Integer, String> doInBackground(Void... voids) {
            ClientDBHelper helper = ClientDBHelper.getInstance(mContext);
            if(!helper.isDBInited()){
                helper.init();// init database data
            }
            try{
                JSONObject tokenObj = mData.getJSONObject("token");
                JSONObject userObj = mData.getJSONObject("user");
                User user = new User();
                user.setId(userObj.getLong("id"));
                user.setName(userObj.getString("name"));
                user.setMobile(userObj.getString("mobile"));
                user.setUser_pass(userObj.getString("user_pass"));
                user.setUser_status(userObj.getInt("user_status"));
                user.setUser_login(userObj.getString("user_login"));
                user.setUser_email(userObj.getString("user_email"));
                user.setLast_login_ip(userObj.getString("last_login_ip"));
                user.setLast_login_time(userObj.getInt("last_login_time"));
                user.setUser_activation_key(userObj.getString("user_activation_key"));
                user.setAvatar(userObj.getString("avatar"));
                user.setSex(userObj.getInt("sex"));
                user.setUser_level(userObj.getInt("user_level"));
                user.setMore(userObj.getString("more"));

                UserToken userToken = new UserToken();
                userToken.setToken(tokenObj.getString("token"));
                userToken.setUser_id(tokenObj.getInt("user_id"));
                userToken.setCreate_time(tokenObj.getInt("create_time"));
                userToken.setExpire_time(tokenObj.getInt("expire_time"));
                userToken.setDevice_type(tokenObj.getString("device_type"));
                /*保存会员数据到数据库*/
                helper.getDaoSession().getUserDao().insertOrReplace(user);
                helper.getDaoSession().getUserTokenDao().insertOrReplace(userToken);
                /*保存当前会员ID*/
                SharedPreferences sharedPreferences = mContext.getSharedPreferences(Params.INI_NAME, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putLong(Params.KEY_CURRENT_USER_ID, user.getId());
                editor.commit();
                /*保存当前会员到Application保存*/
                ClientApplication.getInstance().setUser(user);
                ClientApplication.getInstance().setToken(userToken.getToken());
            }catch (JSONException e){
                e.printStackTrace();
                return new Pair<>(0, e.getMessage());
            }
            return new Pair<>(1, "登陆成功!");
        }

        @Override
        protected void onPostExecute(Pair<Integer, String> result) {
            super.onPostExecute(result);
            if(result.first == 1){
                Snackbar snackbar = Snackbar.make(mWrapper, result.second, Snackbar.LENGTH_LONG);
                snackbar.addCallback(new Snackbar.Callback(){
                    @Override
                    public void onDismissed(Snackbar transientBottomBar, int event) {
                        super.onDismissed(transientBottomBar, event);
                        // TODO jump to next page
                        showClientMainPage();
                    }
                });
                snackbar.show();
            }else{
                Snackbar.make(mWrapper, result.second, Snackbar.LENGTH_LONG).show();
            }
        }
    }

    private void showClientMainPage(){
        Intent intent = new Intent(this, ClientActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

