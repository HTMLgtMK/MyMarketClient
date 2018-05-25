package com.gthncz.mymarketclient.main;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.gthncz.mymarketclient.R;
import com.gthncz.mymarketclient.beans.Params;
import com.gthncz.mymarketclient.greendao.ClientDBHelper;
import com.gthncz.mymarketclient.greendao.User;
import com.gthncz.mymarketclient.helper.MyLocalUserHelper;
import com.gthncz.mymarketclient.helper.MyUserImageRequest;
import com.gthncz.mymarketclient.helper.SquareImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 头像Activity
 * Created by GT on 2018/5/21.
 */

public class AvatarActivity extends AppCompatActivity {

    @BindView(R.id.linearLayout_activity_avatar) protected LinearLayout mWrapper;
    @BindView(R.id.toolbar_activity_avatar) protected Toolbar mToolbar;
    @BindView(R.id.imageView_activity_avatar) protected SquareImageView mAvatarView;

    private final int REQ_CODE_PICK_PICTURE = 0X01;
    private final int REQ_CODE_CROP_PICTURE = 0x02;

    private final int REQ_CODE_REQUEST_READ_PERMISSION = 0X03;

    private User mUser;
    private String mToken;
    private int mWidth;
    private int mHeight;

    private final String TEMP_FILE_NAME = "avatar.png";
    private File tempFile;

    private RequestQueue mQueue;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avatar);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);

        mQueue = Volley.newRequestQueue(this);
        mQueue.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mWidth = mAvatarView.getWidth();
        mHeight = mAvatarView.getHeight();
        // 加载头像信息
        mUser = MyLocalUserHelper.getLocalUser(this);
        mToken = MyLocalUserHelper.getLocalToken(this);
        loadAvatar();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_avatar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{
                onBackPressed();
                return true;
            }
            case R.id.menu_activity_avatar_save_avatar:{
                saveAvatar();
                return true;
            }
            case R.id.menu_activity_avatar_change_avatar:{
                pickPicture();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private  void loadAvatar(){
        String avatar = null;
        if (mUser != null) {
            avatar = mUser.getAvatar();
        }
        if(avatar != null){
            MyUserImageRequest imageRequest = new MyUserImageRequest(Params.URL_USER_AVATAR, new Response.Listener<Bitmap>() {
                @Override
                public void onResponse(Bitmap response) {
                    mAvatarView.setImageBitmap(response);
                }
            }, mWidth, mHeight, ImageView.ScaleType.CENTER_CROP, Bitmap.Config.ARGB_8888, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(AvatarActivity.this.getClass().getSimpleName(), "** 信息 >> loadAvatar Error:" + error.toString());
                    Snackbar.make(mWrapper, error.toString(), Snackbar.LENGTH_LONG).show();
                    mAvatarView.setImageResource(R.mipmap.ic_launcher);
                }
            }) {
                @Override
                public String getUserToken() {
                    return mToken;
                }
            };
            mQueue.add(imageRequest);
        }else{
            mAvatarView.setImageResource(R.mipmap.ic_launcher);
        }
    }

    private void pickPicture() {
        int permission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if(permission != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)){
                Snackbar snackbar = Snackbar.make(mWrapper, R.string.permission_read_external_storage_rational, Snackbar.LENGTH_INDEFINITE);
                snackbar.setAction(R.string.button_ok, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ActivityCompat.requestPermissions(AvatarActivity.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQ_CODE_REQUEST_READ_PERMISSION);
                    }
                });
                snackbar.show();
            }else{
                ActivityCompat.requestPermissions(AvatarActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQ_CODE_REQUEST_READ_PERMISSION);
            }
            return;
        }
        //  调用系统相册，获取一张图片
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, REQ_CODE_PICK_PICTURE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQ_CODE_REQUEST_READ_PERMISSION && grantResults.length > 0){
            int grant = grantResults[0];
            if(grant== PackageManager.PERMISSION_GRANTED){
                pickPicture();
            } // else showErrorMsgAndExit
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void cropPicture(Uri uri){
        // 调用系统相册裁剪图片
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", true); // 设置可裁剪
        // 设置图片宽高比
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // 设置外部ImageView的宽高
        intent.putExtra("outputX", mWidth);
        intent.putExtra("outputY", mHeight);
        intent.putExtra("scale", true); // 设置放缩
        // 设置是否返回图片
        intent.putExtra("return-data", false); // 由于直接传递图片会消耗大量资源，因此不直接传递图片, 返回uri
//         intent.putExtra(MediaStore.EXTRA_OUTPUT, uri); // 覆盖原图
        intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
        intent.putExtra("noFaceDetection", true);
        startActivityForResult(intent, REQ_CODE_CROP_PICTURE);
    }

    private void saveAvatar(){

    }

    protected void uploadAvatar(Uri uri){
        ContentResolver resolver = getContentResolver();
        Cursor cursor = resolver.query(uri, new String[]{MediaStore.Images.Media.DATA},
                null, null, null);
        if(!cursor.moveToFirst()){
            Snackbar.make(mWrapper, "没有找到裁剪后的图片!", Snackbar.LENGTH_LONG).show();
            cursor.close();
            return;
        }
        int  columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        String path = cursor.getString(columnIndex);
        cursor.close(); // 关闭游标

        Log.e(getClass().getSimpleName(), "** 信息 >> filePath:" + path);
        (new UploadAvatarTask(AvatarActivity.this)).execute(new String[]{path});
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQ_CODE_PICK_PICTURE){
            if(resultCode == Activity.RESULT_OK){
                Uri uri = data.getData();
                cropPicture(uri);
            }
        }else if(requestCode == REQ_CODE_CROP_PICTURE){
            if(resultCode == Activity.RESULT_OK){
                Log.e(getClass().getSimpleName(), "** 信息 >> "+data.toString());
//                Uri uri =  data.getParcelableExtra(MediaStore.EXTRA_OUTPUT);
                Uri uri = data.getData();
                uploadAvatar(uri);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private class UploadAvatarTask extends AsyncTask<String, Long, UploadResponseBean>{

        private AlertDialog dialog;
        private TextView tv_progress;
        private Context context;
        private String token;

        public UploadAvatarTask(Context context) {
            this.context = context;
            token = MyLocalUserHelper.getLocalToken(context);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            AlertDialog.Builder builder = new AlertDialog.Builder(context, android.app.AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
            LinearLayout linearLayout = new LinearLayout(context);
            linearLayout.setGravity(Gravity.CENTER_HORIZONTAL);
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            linearLayout.setPadding(10,10,10,10);
            ProgressBar progressBar = new ProgressBar(context);
            tv_progress = new TextView(context);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0, 10, 0, 0);
            tv_progress.setLayoutParams(layoutParams);
            linearLayout.addView(progressBar);
            linearLayout.addView(tv_progress);
            builder.setView(linearLayout);
            builder.setCancelable(false);
            dialog = builder.create();
            dialog.show();
        }

        @Override
        protected UploadResponseBean doInBackground(String... strings) {
            UploadResponseBean responseBean = new UploadResponseBean();
            String  path = strings[0];
            File file = new File(path);
            long totalSize = file.length();// 文件总大小

            Log.e(getClass().getSimpleName(), "** 信息 >> totalSize : "+ totalSize);

            long offset = 0;                // 已经传送的长度
            int len = 0;                   // 本次读取的长度
            long time = System.currentTimeMillis();
            byte[] buffer = new byte[512]; // 输入缓冲
            HttpURLConnection connection = null;
            FileInputStream fis = null;
            OutputStream os = null;
            DataOutputStream dos = null;
            InputStream is = null;
            BufferedReader reader = null;
            String boundary = getBoundary(); // 传送边界
            final String CRLF = "\r\n";     // 换行
            try {
                fis = new FileInputStream(file);
                URL url = new URL(Params.URL_USER_AVATAR_UPLOAD);
                connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setConnectTimeout(3*1000);
                connection.setReadTimeout(5*1000);

                connection.setRequestMethod("POST");
                connection.setRequestProperty("Connection","keep-alive");
                connection.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);
                connection.setRequestProperty("Cache-Control", "no-cache");
                connection.setRequestProperty("XX-Token", token);
                connection.setRequestProperty("XX-Device-Type", "android");

                connection.connect();
                os = connection.getOutputStream();
                dos = new DataOutputStream(os);
                // ------------------------------
                // 传送表单数据
                // ------------------------------
                String line = "--" + boundary + CRLF;
                dos.writeBytes(line);
                line = "Content-Disposition:form-data; name=\"file\"; filename=\""+file.getName()+CRLF;
                dos.writeBytes(line);
                line = "Content-Type:"+getMimeType(path)+CRLF;
                dos.writeBytes(line);
                dos.writeBytes(CRLF); // 再写一行
                // ------------------------------
                // 开始写文件数据
                // ------------------------------
                while((len = fis.read(buffer)) != -1){
                    offset += len;
                    dos.write(buffer, 0, len);
                    if(System.currentTimeMillis() - time > 5*100){
                        publishProgress(offset, totalSize);
                        time = System.currentTimeMillis();
                    }
                }
                dos.writeBytes(CRLF);
                line = "--"+boundary+CRLF;
                dos.writeBytes(line);
                dos.flush();
                // ------------------------------
                // 开始处理响应数据
                // ------------------------------
                String json = null;
                int responseCode = connection.getResponseCode();
                if(responseCode == 200){
                    is = connection.getInputStream(); // java.io.FileNotFoundException: http://192.168.137.1:8888/api/user/Upload/uploadAvatar ?
                    reader = new BufferedReader(new InputStreamReader(is));
                    char[] buf = new char[512];
                    StringBuilder builder = new StringBuilder();
                    while((len = reader.read(buf))!=-1){
                        builder.append(buf, 0, len);
                    }
                    // 开始解析数据
                    json = builder.toString();
                }else{
                    json = "{\"code\":0, \"msg\":\"response state code : "+ responseCode + " \"}";
                }
                Log.e(getClass().getSimpleName(), "** 信息 >>upload response : "+json);
                // ------------------------------
                // 解析数据
                // ------------------------------
                parseJson(json, responseBean);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                responseBean.setCode(0);
                responseBean.setMsg(e.getMessage());
            } catch (MalformedURLException e) {
                e.printStackTrace();
                responseBean.setCode(0);
                responseBean.setMsg(e.getMessage());
            } catch (IOException e) {
                e.printStackTrace();
                responseBean.setCode(0);
                responseBean.setMsg(e.getMessage());
            } catch (JSONException e) {
                e.printStackTrace();
                responseBean.setCode(0);
                responseBean.setMsg(e.getMessage());
            } finally {
                try {
                    if(fis != null){
                        fis.close();
                    }
                    if(os != null){
                        os.close();
                    }
                    if(dos != null ){
                        dos.close();
                    }
                    if(is != null){
                        is.close();
                    }
                    if(reader != null){
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return responseBean;
        }

        @Override
        protected void onProgressUpdate(Long... values) {
            String progress = String.format("上传中: %.2fMB/%.2fMB",
                    Float.valueOf(values[0])/1048576, Float.valueOf(values[1])/1048576);
            tv_progress.setText(progress);
        }

        @Override
        protected void onPostExecute(UploadResponseBean uploadResponseBean) {
            if(dialog != null && dialog.isShowing()){
                dialog.dismiss();
            }
            Snackbar.make(mWrapper, uploadResponseBean.getMsg(), Snackbar.LENGTH_LONG).show();
            if(uploadResponseBean.getCode() == 1){
                mUser.setAvatar(uploadResponseBean.getUrl());
                ClientDBHelper.getInstance(context).getDaoSession().getUserDao().update(mUser);
                // 设置图片
                loadAvatar();
            }
        }

        /*获取边界*/
        private String getBoundary(){
            String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
            StringBuilder builder = new StringBuilder();
            for(int i=0;i<16;++i){
                int pos = (int) (Math.random()*(26+26+10));
                builder.append(str.charAt(pos));
            }
            return builder.toString();
        }

        private String getMimeType(String path){
            String ext = MimeTypeMap.getFileExtensionFromUrl(path);
            return MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext);
        }

        private void parseJson(String json, UploadResponseBean responseBean) throws JSONException {
            JSONObject jsonObj = new JSONObject(json);
            int code = jsonObj.getInt("code");
            String msg = jsonObj.getString("msg");
            responseBean.setCode(code);
            responseBean.setMsg(msg);
            if(code == 1){
                JSONObject data = jsonObj.getJSONObject("data");
                String url1 = data.getString("url");
                String filename = data.getString("filename");
                responseBean.setFilename(filename);
                responseBean.setUrl(url1);
            }

        }
    }


    private class UploadResponseBean{
        private int code;
        private String msg;
        private String url;
        private String filename;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getFilename() {
            return filename;
        }

        public void setFilename(String filename) {
            this.filename = filename;
        }
    }

}
