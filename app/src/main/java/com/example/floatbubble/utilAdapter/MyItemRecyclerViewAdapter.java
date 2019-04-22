package com.example.floatbubble.utilAdapter;

import android.app.PendingIntent;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.floatbubble.R;
import com.example.floatbubble.entity.NewNotification;



import java.util.List;

/**
 *
 * TODO: Replace the implementation with code for your data type.
 */
public class MyItemRecyclerViewAdapter extends RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder> {


    private  List<NewNotification> mNotiList;
    private Context mContext;

    public MyItemRecyclerViewAdapter(List<NewNotification> newNotifications) {
        mNotiList = newNotifications;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.allnotifications_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mNotiList.get(position);
        NewNotification notification = holder.mItem;
        holder.mIconView.setImageIcon(notification.getNotification().getSmallIcon());
        holder.mTimeView.setText(notification.getTime());
        holder.mTitleView.setText(notification.getTitle());
        holder.mContentView.setText(notification.getContent());

        holder.mView.setOnClickListener(v -> {
            //int position1 = holder.getAdapterPosition();
            //NewNotification notification1 = mNotiList.get(position1);
            try {
                notification.getNotification().contentIntent.send(1);
            } catch (PendingIntent.CanceledException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mNotiList.size();
    }

    /**
     * 添加头部数据
     * @param position
     */
    public void addItem(int position) {
        //添加动画
        notifyItemInserted(position);
    }

    /**
     * 删除某个数据
     * //TODO 删除通知的功能
     */
    public void removeItem(int position) {
        mNotiList.remove(position);
        //删除动画
        notifyItemRemoved(position);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final CardView mView;
        private ImageView mIconView;
        private TextView mTimeView;
        private TextView mTitleView;
        private TextView mContentView;
        private NewNotification mItem;

        private ViewHolder(View view) {
            super(view);
            mView = (CardView) view;
            mIconView = view.findViewById(R.id.notification_icon);
            mTimeView = view.findViewById(R.id.notification_time);
            mTitleView = view.findViewById(R.id.notification_title);
            mContentView = view.findViewById(R.id.notification_content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
