package com.example.floatbubble.db;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;


public class FloatBubble extends RecyclerView {
    public FloatBubble(Context context) {
        super(context);
    }



    public class FloatLayoutManager extends LinearLayoutManager {

        public FloatLayoutManager(Context context) {
            super(context);
        }

        @Override
        public void onItemsAdded(RecyclerView recyclerView, int positionStart, int itemCount) {
            super.onItemsAdded(recyclerView, positionStart, itemCount);
        }

        @Override
        public void onItemsRemoved(RecyclerView recyclerView, int positionStart, int itemCount) {
            super.onItemsRemoved(recyclerView, positionStart, itemCount);
        }

        @Override
        public void onItemsMoved(RecyclerView recyclerView, int from, int to, int itemCount) {
            super.onItemsMoved(recyclerView, from, to, itemCount);
        }
    }



}
