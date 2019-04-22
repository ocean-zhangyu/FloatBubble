package com.example.floatbubble.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.floatbubble.InterfaceBin.OnItemTouchCallbackListener;
import com.example.floatbubble.R;
import com.example.floatbubble.Util.LogUtil;
import com.example.floatbubble.data.ProgramFlags;
import com.example.floatbubble.entity.DefaultItemTouchHelper;
import com.example.floatbubble.entity.InterestedPool;
import com.example.floatbubble.entity.NewNotification;
import com.example.floatbubble.utilAdapter.FloatRecyclerAdapter;

import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FloatWindowService extends Service {

    //气泡使用的布局参数
    WindowManager.LayoutParams layoutParams;

    //通知面板使用的布局参数
    WindowManager.LayoutParams layoutParamsPanel;

    //窗口管理器,用于管理悬浮窗显示
    WindowManager windowManager;

    //悬浮窗view
    CircleImageView coreFloatBubble;

    //通知列表view
    RecyclerView notiListView;
    FloatRecyclerAdapter adapter;
    //OnItemTouchCallbackListener onItemTouchCallbackListener;

    //气泡位置标志
    int flag = ProgramFlags.BUBBLE_FLANK;

    //气泡原来的位置
    int bubbleX;
    int bubbleY;

    //屏幕大小
    Point size = new Point();

    //通知面板是否开启
    boolean isDisplay = false;
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

    /**
     * 初始化window
     */
    private void showFloatingWindow() {
        if (Settings.canDrawOverlays(this)) {

            //获取WindowManager服务
            windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
            if (windowManager != null) {
                windowManager.getDefaultDisplay().getSize(size);
            }

            //新建悬浮窗控件
            coreFloatBubble = new CircleImageView(getApplicationContext());

            //新建控件,设置recyclerView需要设置的
            notiListView = (RecyclerView)LayoutInflater.from(getApplicationContext()).inflate(R.layout.float_notilist,null);
            DefaultItemTouchHelper itemTouchHelper = new DefaultItemTouchHelper(new NotiListTouchListener());
            itemTouchHelper.setCanDrag(false);
            itemTouchHelper.setCanSwipe(true);
            itemTouchHelper.attachToRecyclerView(notiListView);
            notiListView.setLayoutManager(new LinearLayoutManager(this));
            List < NewNotification > list = InterestedPool.getNotiPoolInstance().getNewNotificationList();
            //展示面板时再装载到list
            adapter = new FloatRecyclerAdapter(list);
            notiListView.setOnTouchListener((v, event) -> {
                LogUtil.i("悬浮窗口", "onTouch");
                /*
                int x = (int) event.getX();
                int y = (int) event.getY();
                Rect rect = new Rect();
                notiListView.getGlobalVisibleRect(rect);
                if (!rect.contains(x, y)) {
                    LogUtil.i("悬浮窗口", "hide ");
                */
                //收起通知列表
                windowManager.removeView(notiListView);
                //收起气泡
                layoutParams.x = bubbleX;
                layoutParams.y = bubbleY;
                windowManager.updateViewLayout(coreFloatBubble,layoutParams);
                flag = ProgramFlags.BUBBLE_FLANK;
                isDisplay = false;
                //}
                return false;
            });
            initNotiListPanel();

            showFloatBubble(coreFloatBubble);

            //加载图标
            //coreFloatBubble.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.user_profile));
            Glide.with(this).load(R.drawable.user_profile).into(coreFloatBubble);
            coreFloatBubble.setFocusableInTouchMode(true);

            //设置监听事件
            coreFloatBubble.setOnClickListener(new BubbleOnClickListener());
            coreFloatBubble.setOnLongClickListener(v -> {
                //长按事件
                return false;
            });
            coreFloatBubble.setOnTouchListener(new BubbleOnTouchListener());
        }
    }



    /**
     * 展示气泡面板,从开始即运行
     * @param view
     */
    private void showFloatBubble(View view) {
        //设置LayoutParams
        layoutParams = new WindowManager.LayoutParams();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        //FLAG_FULL_SCREEN, FLAG_LAYOUT_IN_SCREEN
        layoutParams.gravity = Gravity.START;
        layoutParams.format = PixelFormat.RGBA_8888;
        layoutParams.width = 90;
        layoutParams.height = 90;
        layoutParams.y = 300;
        //将控件添加到WindowManager中

        windowManager.addView(view, layoutParams);
    }

    /**
     * 初始化通知面板
     */
    private void initNotiListPanel() {
        //新建LayoutParamsPanel
        layoutParamsPanel = new WindowManager.LayoutParams();
        //根据SDK设置type,8.0以上不同
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParamsPanel.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layoutParamsPanel.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        //设置LayoutParamsPanel
        layoutParamsPanel.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        //| WindowManager.LayoutParams.FLAG_DIM_BEHIND ;
        //FLAG_FULL_SCREEN, FLAG_LAYOUT_IN_SCREEN
        layoutParamsPanel.format = PixelFormat.RGBA_8888;
        //包容通知,已经限定宽度
        layoutParamsPanel.width = WindowManager.LayoutParams.MATCH_PARENT;
        //限定高度为气泡以上的内容区域
        layoutParamsPanel.height = size.y - 300;
        layoutParamsPanel.x = 0;
        layoutParamsPanel.y = 10;
        //将控件添加到WindowManager中
    }

    /**
     * 获取最新的兴趣池,展示通知面板
     */
    private void showNotiListPanel() {

        List<NewNotification> list = InterestedPool.getNotiPoolInstance().getNewNotificationList();
        adapter.setList(list);
        notiListView.setAdapter(adapter);

        windowManager.addView(notiListView, layoutParamsPanel);
        isDisplay = true;
    }

    /**
     * 点击监听接口
     */
    private  class BubbleOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            //点击事件
            Toast.makeText(getApplicationContext(),"点击",Toast.LENGTH_SHORT).show();
            //TODO 打开通知列表面板
            switch (flag) {
                case ProgramFlags.BUBBLE_FLANK:
                    //改变气泡位置,使其位于底部中央
                    bubbleX = layoutParams.x;
                    bubbleY = layoutParams.y;
                    layoutParams.x = (size.x/2) - (coreFloatBubble.getWidth() / 2);
                    layoutParams.y = (size.y) - 30 ;
                    windowManager.updateViewLayout(coreFloatBubble, layoutParams);
                    //展示通知面板
                    showNotiListPanel();
                    flag = ProgramFlags.BUBBLE_CENTER;
                    break;
                case ProgramFlags.BUBBLE_CENTER:
                    //清除所有通知,回到原来的位置
                    //TODO 点击外部隐藏,重复,需判断面板是否开启
                    if (isDisplay) {
                        windowManager.removeView(notiListView);
                    }
                    //TODO 清空兴趣池,获取暂存兴趣池
                    layoutParams.x = bubbleX;
                    layoutParams.y = bubbleY;
                    windowManager.updateViewLayout(coreFloatBubble,layoutParams);
                    flag = ProgramFlags.BUBBLE_FLANK;
                    break;
                    default:
                        break;
            }
        }
    }


    /**
     * 悬浮球触摸监听接口
     */
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
                    break;
                case MotionEvent.ACTION_UP:
                    float endX = event.getRawX();
                    float endY = event.getRawY();
                    //判断松手时view的横坐标是靠近屏幕哪侧
                    if (endX < size.x / 2) {
                        endX = 0;
                    } else {
                        endX = size.x - 90;
                    }
                    layoutParams.x = (int) endX;
                    windowManager.updateViewLayout(coreFloatBubble,layoutParams);
                    //如果初始落点与松手落点的坐标差值超过6个像素，则拦截该点击事件
                    //否则继续传递，将事件交给OnClickListener函数处理
                    if (Math.abs(endX - tempX) > 2 && Math.abs(endY - tempY) > 2) {
                        Log.d("onTouch","传递点击");
                       return true;//拦截
                    }
                    break;
                    default:
                        break;
            }
            return false;
        }
    }


    /**
     * 通知列表监听接口
     */
    private class NotiListTouchListener implements OnItemTouchCallbackListener {

        @Override
        public boolean onMove(int srcPosition, int targetPosition) {
            if (adapter.getList() != null) {
                //更换数据源中的数据Item的位置
                Collections.swap(adapter.getList(),srcPosition, targetPosition);
                adapter.notifyItemMoved(srcPosition, targetPosition);
                return true;
            }
            return false;
        }

        @Override
        public void onSwipe(int adapterPosition) {
            //滑动时删除,并从数据源删除,并刷新这个Item
            //TODO 暂时存储该元素,灌注到兴趣池
            if (adapter.getList() != null) {
                adapter.getList().remove(adapterPosition);
                adapter.notifyItemRemoved(adapterPosition);
            }
        }
    }

}
