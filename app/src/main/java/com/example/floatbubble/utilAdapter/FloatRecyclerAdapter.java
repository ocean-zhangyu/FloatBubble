package com.example.floatbubble.utilAdapter;

import android.app.PendingIntent;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.example.floatbubble.R;
import com.example.floatbubble.data.LabelFlags;
import com.example.floatbubble.entity.NewNotification;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.floatbubble.R.*;

public class FloatRecyclerAdapter extends RecyclerView.Adapter<FloatRecyclerAdapter.ViewHolder> {

        private List<NewNotification> floatNotiList;
        private Context mContext;

        public FloatRecyclerAdapter(List<NewNotification> list) {
            floatNotiList = list;
        }
        public void setList(List<NewNotification> list1) {
            floatNotiList = list1;
        }
        public List<NewNotification> getList() {
            return floatNotiList;
        }
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (mContext == null) {
                mContext = parent.getContext();
            }
            View view = LayoutInflater.from(mContext).inflate(layout.floatnotilist_item,parent,false);

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            //findView
            holder.mItem = floatNotiList.get(position);
            NewNotification notification = holder.mItem;
            //根据标签设置颜色
            switch (notification.getLabel()) {
                case LabelFlags.NEWS:
                    holder.mLinearLayoutView.setBackgroundResource(color.colorNews);
                    break;
                case LabelFlags.COMMUNICATION:
                    holder.mLinearLayoutView.setBackgroundResource(color.colorCommunication);
                    break;
                case LabelFlags.REMINDER:
                    holder.mLinearLayoutView.setBackgroundResource(color.colorReminder);
                    break;
                case LabelFlags.ADVERTISEMENT:
                    holder.mLinearLayoutView.setBackgroundResource(color.colorAd);
                    break;
                case LabelFlags.OTHERS:
                    holder.mLinearLayoutView.setBackgroundResource(color.colorOthers);
                    break;
                    default:
                        holder.mLinearLayoutView.setBackgroundResource(color.colorReminder);
                        break;
            }
            holder.mSendAppView.setImageIcon(notification.getNotification().getSmallIcon());
            holder.mSendTitleView.setText(notification.getTitle());
            holder.mSendContentView.setText(notification.getContent());

            holder.mView.setOnClickListener(v -> {
                //int position1 = holder.getAdapterPosition();
                try {
                    notification.getNotification().contentIntent.send();
                } catch (PendingIntent.CanceledException e) {
                    e.printStackTrace();
                } finally {
                    removeItem(position);
                }
            });
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
        floatNotiList.remove(position);
        //删除动画
        notifyItemRemoved(position);
        notifyDataSetChanged();
    }
        @Override
        public int getItemCount() {
            return floatNotiList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private final CardView mView;
            private CircleImageView mSendAppView;
            private LinearLayout mLinearLayoutView;
            private TextView mSendTitleView;
            private TextView mSendContentView;
            private NewNotification mItem;

            private ViewHolder(View itemView) {
                super(itemView);
                mView = (CardView) itemView;
                mLinearLayoutView = mView.findViewById(id.set_background);
                mSendAppView = mView.findViewById(id.app_icon);
                mSendTitleView = mView.findViewById(id.send_title);
                mSendContentView = mView.findViewById(id.send_content);
            }
        }


}
