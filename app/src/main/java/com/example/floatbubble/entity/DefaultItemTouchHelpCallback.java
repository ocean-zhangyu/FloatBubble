package com.example.floatbubble.entity;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.example.floatbubble.InterfaceBin.OnItemTouchCallbackListener;

public class DefaultItemTouchHelpCallback extends ItemTouchHelper.Callback {
    /**
     * Item操作的回调
     */
    private OnItemTouchCallbackListener onItemTouchCallbackListener;
    /**
     * 是否可以拖拽
     */
    private boolean isCanDrag = false;

    private boolean isCanSwipe = false;
    public DefaultItemTouchHelpCallback() {

    }
    public DefaultItemTouchHelpCallback(OnItemTouchCallbackListener listener) {
        this.onItemTouchCallbackListener = listener;
    }

    /**
     * 设置Item的回调,去更新UI和数据源
     * @param listener
     */
    public void setOnItemTouchCallbackListener(OnItemTouchCallbackListener listener) {
        this.onItemTouchCallbackListener = listener;
    }

    public void setCanDrag(boolean canDrag) {
        isCanDrag = canDrag;
    }

    public void setCanSwipe(boolean canSwipe) {
        isCanSwipe = canSwipe;
    }

    /**
     * 当Item被长按的时候是否可以被拖拽
     * @return
     */
    @Override
    public boolean isLongPressDragEnabled() {
        return isCanDrag;
    }

    /**
     * Item是否可以被滑动,(H:左右滑动,V:上下滑动)
     * @return
     */
    @Override
    public boolean isItemViewSwipeEnabled() {
        return isCanSwipe;
    }

    /**
     * 当用户拖拽或者滑动Item的时候需要我们告诉系统滑动或者拖拽的方向
     * @param recyclerView
     * @param viewHolder
     * @return
     */
    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            //flag如果是0,相当于这个功能被关闭
            int dragFlag = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT |ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            int swipeFlag = 0;
            return  makeMovementFlags(dragFlag, swipeFlag);
        } else if (layoutManager instanceof LinearLayoutManager) {
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
            int orientation = linearLayoutManager.getOrientation();

            int dragFlag = 0;
            int swipeFlag = 0;
            //水平布局
            if (orientation == LinearLayoutManager.HORIZONTAL) {
                swipeFlag = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                dragFlag = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
            } else if (orientation == LinearLayoutManager.VERTICAL) {
                //垂直布局
                dragFlag = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                swipeFlag = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
            }
            return makeMovementFlags(dragFlag,swipeFlag);
        }
        return  0;
    }

    /**
     * 当Item被拖拽的时候被回调
     * @param recyclerView
     * @param viewHolder  被拖拽的viewholder
     * @param target 目的地viewHolder
     * @return
     */
    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        if (onItemTouchCallbackListener != null) {
            return onItemTouchCallbackListener.onMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        }
        return false;
    }

    /**
     *
     * @param viewHolder
     * @param direction
     */
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        if (onItemTouchCallbackListener != null) {
            onItemTouchCallbackListener.onSwipe(viewHolder.getAdapterPosition());
        }
    }
}
