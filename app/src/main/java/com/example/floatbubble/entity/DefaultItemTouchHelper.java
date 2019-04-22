package com.example.floatbubble.entity;

import android.support.v7.widget.helper.ItemTouchHelper;

import com.example.floatbubble.InterfaceBin.OnItemTouchCallbackListener;

public class DefaultItemTouchHelper extends YolandaItemTouchHelper {
    private DefaultItemTouchHelpCallback itemTouchHelpCallback;

    public DefaultItemTouchHelper(OnItemTouchCallbackListener listener) {
        super(new DefaultItemTouchHelpCallback(listener));
        itemTouchHelpCallback = (DefaultItemTouchHelpCallback) getCallback();
    }

    /**
     * 设置是否可以被拖拽
     * @param canDrag
     */
    public void setCanDrag(boolean canDrag) {
        itemTouchHelpCallback.setCanDrag(canDrag);
    }

    /**
     * 设置是否可以滑动
     * @param canSwipe
     */
    public void setCanSwipe(boolean canSwipe) {
        itemTouchHelpCallback.setCanSwipe(canSwipe);
    }
}
class YolandaItemTouchHelper extends ItemTouchHelper {
    ItemTouchHelper.Callback callback;
    public YolandaItemTouchHelper(ItemTouchHelper.Callback callback) {
        super(callback);
        this.callback = callback;
    }

    public Callback getCallback() {
        return callback;
    }
}
