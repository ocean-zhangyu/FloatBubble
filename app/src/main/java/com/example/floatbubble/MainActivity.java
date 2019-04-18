package com.example.floatbubble;



import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
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
import com.example.floatbubble.service.NotificationMonitorService;

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

    private NotificationMonitorService.NmBinder nmBinder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initView();
        Log.d("MainActivity","onCreate 活动");
        if (!AppUtil.isNotificationListenerEnabled(this)) {
            //未绑定服务
            Intent openSettings = new Intent(this, SettingActivity.class);
            startActivityForResult(openSettings,REQUEST_NOTIPERMISSION);
        } else {
            Intent intent = new Intent(this, NotificationMonitorService.class);
            //startActivity(intent);
            Log.d("主活动","绑定活动");
            bindService(intent, connection, BIND_AUTO_CREATE);
            Log.d("主活动", "设置监听");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (nMService != null) {
            //有新通知到来,该信息由通知监听服务发送
            nMService.setNotificationComeListener(() -> {
                //更新界面
                allNotiFragment.refreshView();
            });
        }
        Log.d("MainActivity","onStart 活动");
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
