package com.gthncz.mymarketclient.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.gthncz.mymarketclient.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 应用设置Activity
 * Created by GT on 2018/5/11.
 * PreferenceActivity继承于ListActivity, 而添加preference xml resource的方法已经弃用了，
 * 布局文件要基于ListView，且id一定是android.R.id.list,否则会报错: java.lang.RuntimeException:
 * Your content must have a ListView whose id attribute is 'android.R.id.list'
 * <p>
 * 如果只有一个PrefereceFragment, 而外部使用的是PreferenceActivity, 按照文档，可以直接显示该fragment,
 * 具体方法为:
 * 1. 启动Activity是添加参数:
 *  Intent intent = new Intent(ClientActivity.this, SettingActivity.class);
 *  intent.putExtra(PreferenceActivity.EXTRA_NO_HEADERS, true);
 *  intent.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT, SettingFragment.class.getName());
 *  startActivity(intent);
 * 2. Activity内部重载函数 isValidFragment():
 *  @Override
 *  proteted void isValidFragment(String fragmentName){
 *      if(fragmentName.equal(SettingFragment.class.getName())){
 *          return true;
 *      }
 *      return super.isValidFragment(fragment);
 *  }
 * </p>
 * <p>
 * 现在的API推荐使用PreferenceFragment，在里面可以使用addPreferencesFromResource方法.
 * 而启动PreferenceFragment的Activity不需要一定是PreferenceActivity.
 * </p>
 *
 * <p>
 *  谷歌官方推荐PreferenceActibity和PreferenceFragment结合使用，但是要怎么自定义布局呢？
 *  参考:
 *  https://chromium.googlesource.com/android_tools/+/7200281446186c7192cb02f54dc2b38e02d705e5/sdk/extras/android/support/samples/Support7Demos/src/com/example/android/supportv7/app/AppCompatPreferenceActivity.java#6
 * </p>
 */

public class SettingActivity extends AppCompatActivity {

    @BindView(R.id.toolbar_setting) protected Toolbar mToolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        SettingFragment settingFragment = new SettingFragment();
        getFragmentManager()
                .beginTransaction()
                .add(R.id.frameLayout_layout_frame, settingFragment)
                .commit();
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
