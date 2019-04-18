package com.example.floatbubble.InterfaceBin;

public interface OnItemTouchCallbackListener {
    /**
     * 当两个Item位置互换的时候
     * @param srcPosition
     * @param targetPosition
     * @return 处理返回true,未处理返回false
     */
    boolean onMove(int srcPosition, int targetPosition);

    /**
     * 当某个元素被滑动删除的时候
     * @param adapterPosition
     */
    void onSwipe(int adapterPosition);
}
