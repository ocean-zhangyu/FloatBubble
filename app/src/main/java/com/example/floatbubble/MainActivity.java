package com.example.floatbubble;



import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.floatbubble.Util.AppUtil;
import com.example.floatbubble.Util.LogUtil;
import com.example.floatbubble.entity.dbTable.Keywords;
import com.example.floatbubble.entity.dbTable.PkgNames;
import com.example.floatbubble.entity.dbTable.SocialApps;
import com.example.floatbubble.service.NotificationMonitorService;

import org.litepal.tablemanager.Connector;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_NOTIPERMISSION = 0;
    private ViewPager mViewPager;
    private RadioGroup mTabRadioGroup;

    private List<Fragment> mFragments;
    private FragmentPagerAdapter mAdapter;
    //第一页碎片,"今日"
    AllNotifications allNotiFragment;

    //通知监听服务
    private NotificationMonitorService nMService;
    //绑定
    private NotificationMonitorService.NmBinder nmBinder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //生成数据库
        Connector.getDatabase();
        //保存数据
        SharedPreferences preferences = getSharedPreferences("data",MODE_PRIVATE);
        //是否初次启动
        boolean isFirst = preferences.getBoolean("isFirst", true);
        if (isFirst) {
            //初始化过滤数据库
            initInflateDB();
        }
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //初始化各控件
        initView();

        LogUtil.d("MainActivity","onCreate 活动");
        if (!AppUtil.isNotificationListenerEnabled(this)) {
            //如果服务未启动
            Intent openSettings = new Intent(this, SettingActivity.class);
            startActivityForResult(openSettings,REQUEST_NOTIPERMISSION);
        } else {
            //如果服务已启动,绑定服务以获取实时通知
            Intent intent = new Intent(this, NotificationMonitorService.class);
            LogUtil.d("主活动","绑定活动");
            bindService(intent, connection, BIND_ADJUST_WITH_ACTIVITY);
            LogUtil.d("主活动", "设置监听");
        }
    }

    private void initInflateDB() {
        Keywords keyword = new Keywords("正在其他应用的上层显示内容");
        keyword.save();
        Keywords keyword1 = new Keywords("输入法");
        keyword1.save();
        Keywords keyword2 = new Keywords("正在运行");
        keyword2.save();
        PkgNames pkgName = new PkgNames("android");
        pkgName.save();
        SocialApps qq = new SocialApps("com.tencent.mobileqq");
        qq.save();
        SocialApps wechat = new SocialApps("com.tencent.mm");
        wechat.save();
        SharedPreferences.Editor editor = getSharedPreferences("data",MODE_PRIVATE).edit();
        editor.putBoolean("isFirst", false);
        editor.apply();
    }

    @Override
    protected void onStart() {
        super.onStart();
        LogUtil.d("MainActivity","onStart 活动");
        if (!AppUtil.isNotificationListenerEnabled(this)) {
            //如果服务未启动
            Intent openSettings = new Intent(this, SettingActivity.class);
            startActivityForResult(openSettings,REQUEST_NOTIPERMISSION);
        } else {
            //如果服务已启动,绑定服务以获取实时通知
            Intent intent = new Intent(this, NotificationMonitorService.class);
            LogUtil.d("主活动","绑定活动");
            bindService(intent, connection, BIND_AUTO_CREATE);
            LogUtil.d("主活动", "设置监听");
        }
        //注册监听
        if (nMService != null) {
            //有新通知到来,该信息由通知监听服务发送
            nMService.setNotificationComeListener(() -> {
                //更新界面
                allNotiFragment.refreshView();
            });
        }
        //TODO 从存储的内容里读取数据
        allNotiFragment.refreshAllLayout();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case REQUEST_NOTIPERMISSION:
                break;
                default:
                    break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search_bar:
                Toast.makeText(this,"搜索功能还在建设中...",Toast.LENGTH_SHORT).show();
                break;
            case R.id.setting:
                Intent intent = new Intent(this, SettingActivity.class);
                startActivity(intent);

                break;
                default:
                    break;
        }
        return true;
    }

    private void initView() {
        //初始化控件,设置viewPager碎片
        mViewPager = findViewById(R.id.fragment_vp);
        mTabRadioGroup = findViewById(R.id.tabs_rg);
        // init fragment
        mFragments = new ArrayList<>(4);
        allNotiFragment = AllNotifications.newInstance("今日");
        mFragments.add(allNotiFragment);
        mFragments.add(ItemFragment.newInstance(2));
        mFragments.add(Instructions.newInstance("使用说明","xx"));
        mFragments.add(MyFragment.newInstance("我的","xxx"));
        // init view pager
        mAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), mFragments);
        mViewPager.setAdapter(mAdapter);
        // register listener
        mViewPager.addOnPageChangeListener(mPageChangeListener);
        mTabRadioGroup.setOnCheckedChangeListener(mOnCheckedChangeListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mViewPager.removeOnPageChangeListener(mPageChangeListener);
        //解绑服务
        if (nMService != null) {
            unbindService(connection);
        }
    }

    //碎片移动监听器
    private ViewPager.OnPageChangeListener mPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            RadioButton radioButton = (RadioButton) mTabRadioGroup.getChildAt(position);
            radioButton.setChecked(true);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    //底栏按钮监听器
    private RadioGroup.OnCheckedChangeListener mOnCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            for (int i = 0; i < group.getChildCount(); i++) {
                if (group.getChildAt(i).getId() == checkedId) {
                    mViewPager.setCurrentItem(i);
                    return;
                }
            }
        }
    };

    //绑定服务的connection
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            nmBinder = (NotificationMonitorService.NmBinder) binder;
            //前面的是Binder,可以用来操纵服务,调用服务的方法
            nMService = nmBinder.getNmService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }

        @Override
        public void onBindingDied(ComponentName name) {
            unbindService(connection);
        }
    };

    //控制碎片的适配器
    private class MyFragmentPagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> mList;

        public MyFragmentPagerAdapter(FragmentManager fm, List<Fragment> list) {
            super(fm);
            this.mList = list;
        }

        @Override
        public Fragment getItem(int position) {
            return this.mList == null ? null : this.mList.get(position);
        }

        @Override
        public int getCount() {
            return this.mList == null ? 0 : this.mList.size();
        }

    }

}
