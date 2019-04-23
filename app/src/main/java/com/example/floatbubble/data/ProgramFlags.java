package com.example.floatbubble.data;

public class ProgramFlags {
    public final static int BUBBLE_CENTER = 0;
    public final static int BUBBLE_FLANK = 1;
    //通知过滤标志
    public final static int INVALIDINFO = 1;  //无效,不进行任何处理
    public final static int SYSTEMINFO = 2;   //系统消息,不消除
    public final static int SOCIALINFO = 3;   //社交消息,保护性处理
    public final static int COMMONINFO = 4;   //普通消息,正常处理
}
