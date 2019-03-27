package com.example.floatbubble.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.floatbubble.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class FloatWindowService extends Service {

    WindowManager.LayoutParams layoutParams;
    WindowManager windowManager;
    //悬浮窗view
    CircleImageView coreFloatBubble;

    Point size = new Point();
    public FloatWindowService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        showFloatingWindow();
        return super.onStartCommand(intent, flags, startId);

    }

    private void showFloatingWindow() {
        if (Settings.canDrawOverlays(this)) {
            //获取WindowManager服务
            windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
            windowManager.getDefaultDisplay().getSize(size);

            //新建悬浮窗控件
            coreFloatBubble = new CircleImageView(getApplicationContext());

            //设置LayoutParames
            layoutParams = new WindowManager.LayoutParams();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            } else {
                layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
            }
            layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE| WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
            //FLAG_FULL_SCREEN, FLAG_LAYOUT_IN_SCREEN
            layoutParams.gravity = Gravity.LEFT;
            layoutParams.format = PixelFormat.RGBA_8888;
            layoutParams.width = 90;
            layoutParams.height = 90;
            layoutParams.x = 300;
            layoutParams.y = 300;
            //将控件添加到WindowManager中

            windowManager.addView(coreFloatBubble, layoutParams);

            //加载图标
            //coreFloatBubble.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.user_profile));
            Glide.with(this).load(R.drawable.user_profile).into(coreFloatBubble);
            coreFloatBubble.setFocusableInTouchMode(true);
            //设置监听事件
            coreFloatBubble.setOnTouchListener(new BubbleOnTouchListener());
            coreFloatBubble.setOnClickListener((v)->{
                //点击事件
                Toast.makeText(getApplicationContext(),"点击",Toast.LENGTH_SHORT).show();
            });
            coreFloatBubble.setOnLongClickListener(v -> {
                //长按事件
                return false;
            });
        }
    }

    //监听接口
    private class BubbleOnTouchListener implements View.OnTouchListener{

        private float startX;
        private float startY;
        private float tempX;
        private float tempY;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    startX =  event.getRawX();
                    startY =  event.getRawY();

                    tempX = event.getRawX();
                    tempY = event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    float movedX = event.getRawX() - startX;
                    float movedY = event.getRawY() - startY;
                    //计算偏移量
                    layoutParams.x += movedX;
                    layoutParams.y += movedY;

                    //更新位置
                    windowManager.updateViewLayout(v,layoutParams);
                    startX = event.getRawX();
                    startY = event.getRawY();
                    break;
                case MotionEvent.ACTION_UP:
                    //判断松手时view的横坐标是靠近屏幕哪侧
                    float endX = event.getRawX();
                    float endY = event.getRawY();
                    if (endX < size.x / 2) {
                        endX = 0;
                    } else {
                        endX = size.x - 90;
                    }
                    layoutParams.x = (int) endX;
                    windowManager.updateViewLayout(coreFloatBubble,layoutParams);
                    //如果初始落点与松手落点的坐标差值超过6个像素，则拦截该点击事件
                    //否则继续传递，将事件交给OnClickListener函数处理
                    if (Math.abs(endX - tempX) > 6 && Math.abs(endY - tempY) > 6) {
                        return true;//拦截
                    }
                    break;
                    default:
                        break;
            }
            return false;
        }


        //onClick

    }
}
