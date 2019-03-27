package com.example.floatbubble;


import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.floatbubble.service.FloatWindowService;
import com.example.floatbubble.service.NotificationMonitorService;

import java.lang.reflect.Method;
import java.util.List;


public class SettingActivity extends AppCompatActivity {
    private static final String ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";
    //设置1:启用悬浮窗
    private TextView startOverlay;
    private TextView resumeService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        //初始化控件
        initView();
        //注册监听
        startOverlay.setOnClickListener((view)->{
            Toast.makeText(this,"启用悬浮窗",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, FloatWindowService.class);
            startService(intent);
        });
        resumeService.setOnClickListener((view)->{
            Toast.makeText(this,"重启通知监听",Toast.LENGTH_SHORT).show();
            toggleNotificationListenerService(this);
        });
        if (!isNotificationListenersEnabled()){
            //申请获取通知权限
            getNotificationAccessSetting(this);
        }
        if (!commonROMPermissionCheck(this)) {
            //申请获取悬浮窗权限
            //startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName())), REQUEST_CODE);
            requestAlertWindowPermission();
        }
        Intent intent = new Intent(this,NotificationMonitorService.class);
        startService(intent);
    }

    private void initView() {
        startOverlay = findViewById(R.id.setting_startOverly);
        resumeService = findViewById(R.id.setting_resumeService);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isNotificationListenersEnabled()) {
            Intent intent = new Intent(this,NotificationMonitorService.class);
            startService(intent);
        } else {
            Toast.makeText(this,"未取得获取通知权限",Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 判断是否有获取通知的权限
     * @return true or false
     */
    private boolean isNotificationListenersEnabled() {
        String pkgName = getPackageName();
        final String flat = Settings.Secure.getString(getContentResolver(),ENABLED_NOTIFICATION_LISTENERS);
        if (!TextUtils.isEmpty(flat)) {
            final String[] names = flat.split(":");
            for (int i = 0; i < names.length; i++) {
                final ComponentName componentName = ComponentName.unflattenFromString(names[i]);
                if (componentName != null) {
                    if (TextUtils.equals(pkgName, componentName.getPackageName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    /**
     * 判断 悬浮窗口权限是否打开
     * 6.0以下
     * @param context
     * @return true 允许  false禁止
     */
    public static boolean getAppOps(Context context) {
        try {
            Object object = context.getSystemService(Context.APP_OPS_SERVICE);
            if (object == null) {
                return false;
            }
            Class localClass = object.getClass();
            Class[] arrayOfClass = new Class[3];
            arrayOfClass[0] = Integer.TYPE;
            arrayOfClass[1] = Integer.TYPE;
            arrayOfClass[2] = String.class;
            Method method = localClass.getMethod("checkOp", arrayOfClass);
            if (method == null) {
                return false;
            }
            Object[] arrayOfObject1 = new Object[3];
            arrayOfObject1[0] = Integer.valueOf(24);
            arrayOfObject1[1] = Integer.valueOf(Binder.getCallingUid());
            arrayOfObject1[2] = context.getPackageName();
            int m = ((Integer) method.invoke(object, arrayOfObject1)).intValue();
            return m == AppOpsManager.MODE_ALLOWED;
        } catch (Exception ex) {

        }
        return false;
    }
    /**
     * 6.0以上判断悬浮窗权限是否打开
     *
     */
    private static final int REQUEST_CODE = 1;
    private boolean commonROMPermissionCheck(Context context) {
        Boolean result = true;
        if (Build.VERSION.SDK_INT >= 23) {
            try {
                Class clazz = Settings.class;
                Method canDrawOverlays = clazz.getDeclaredMethod("canDrawOverlays", Context.class);
                result = (Boolean) canDrawOverlays.invoke(null, context);
            } catch (Exception e) {
                Log.e("悬浮窗权限判断6.0", Log.getStackTraceString(e));
            }
        }
        return result;
    }
    //申请权限
    private void requestAlertWindowPermission() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
//处理回调
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (Settings.canDrawOverlays(this)) {
                Log.i("设置回调", "onActivityResult granted");
            }else {
                Toast.makeText(this, "授权失败",Toast.LENGTH_SHORT).show();
            }
        }
    }
    /**
     * 引导开启访问通知的权限
     */
    public static boolean getNotificationAccessSetting(Context context) {
        try {
            Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            return true;
        } catch (ActivityNotFoundException e) {
            //如果找不到再找一次
            try {
                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ComponentName cn = new ComponentName("com.android.settings","com.android.settings$NotificationAccessSettingsActivity");
                intent.setComponent(cn);
                intent.putExtra(":settings:show_fragment","NotificationAccessSettings");
                context.startActivity(intent);
                return true;
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            Toast.makeText(context,"您的手机暂不支持",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            return false;
        }

    }
    /**
     * apk被清理重启后NotificationListenerService失效的处理
     */
    public static void toggleNotificationListenerService(Context context) {
        Log.e("重启获取通知服务","toggleNotificationListenerService");
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(new ComponentName(context, NotificationMonitorService.class),PackageManager.COMPONENT_ENABLED_STATE_DISABLED,PackageManager.DONT_KILL_APP);
        pm.setComponentEnabledSetting(new ComponentName(context,NotificationMonitorService.class),PackageManager.COMPONENT_ENABLED_STATE_ENABLED,PackageManager.DONT_KILL_APP);
    }



}
