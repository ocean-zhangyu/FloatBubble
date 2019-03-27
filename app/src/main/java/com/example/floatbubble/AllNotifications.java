package com.example.floatbubble;




import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;

import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.floatbubble.db.NewNotification;
import com.example.floatbubble.db.NotificationPool;
import com.example.floatbubble.utilAdapter.MyItemRecyclerViewAdapter;

import java.util.List;


/**
 *
 */
public class AllNotifications extends Fragment {

    RecyclerView recyclerView;

    private static final String ARG_PARAM1 = "param1";


    private String mParam1;

    //recyclerView adapter;
    private MyItemRecyclerViewAdapter myItemRecyclerViewAdapter;

    //下拉刷新
    private SwipeRefreshLayout refreshLayout;


    public AllNotifications() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static AllNotifications newInstance(String param1) {
        AllNotifications fragment = new AllNotifications();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.allnotification_list, container, false);
        if (view instanceof SwipeRefreshLayout) {
            Context context = view.getContext();
            recyclerView = view.findViewById(R.id.noti_list);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            NotificationPool np = NotificationPool.getNotiPoolInstance();
            List<NewNotification> list = np.getNewNotificationList();
            myItemRecyclerViewAdapter = new MyItemRecyclerViewAdapter(list);
            recyclerView.setAdapter(myItemRecyclerViewAdapter);
        }
        refreshLayout = view.findViewById(R.id.notification_refresh);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshAllLayout();
            }
        });
        return view;
    }

    public void refreshAllLayout() {
        if (myItemRecyclerViewAdapter != null) {
            myItemRecyclerViewAdapter.notifyDataSetChanged();
            refreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onFragmentEvent(String test) {
        //传值给Activities
    }
    public void refreshView() {
        //更新通知列表
       // ((MyItemRecyclerViewAdapter) recyclerView.getAdapter()).addItem(0);
        myItemRecyclerViewAdapter.addItem(0);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }



}
