package com.sfmap.api.services.help;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.sfmap.api.services.core.AppInfo;
import com.sfmap.api.services.core.SearchException;
import com.sfmap.api.services.core.MessageManager;

import java.util.List;

/**
 * 此类定义了输入提示的关键字。
 */
public class Inputtips {
    /**
     * 当前 Activity上下文。
     */
    private Context mContext;
    /**
     * 输入提示回调的监听接口。
     */
    private InputtipsListener inputtipsListener;
    private InputtipsQuery inputtipsQuery;
    private Handler handler = null;

    /**
     * 根据给定的参数构造一个Inputtips的新对象。
     *
     * @param context  当前 Activity。
     * @param listener 输入提示回调的监听接口。
     */
    public Inputtips(Context context, Inputtips.InputtipsListener listener) {

        this.mContext = context.getApplicationContext();
        this.inputtipsListener = listener;
        this.handler = MessageManager.getInstance();
        AppInfo.getSystemAk(context);

    }

    /**
     * 根据给定的参数构造一个Inputtips的新对象。
     *
     * @param context 当前 Activity。
     * @param query   查询条件。
     */
    public Inputtips(android.content.Context context, InputtipsQuery query) {
        this.mContext = context.getApplicationContext();
        this.inputtipsQuery = query;
        this.handler = MessageManager.getInstance();
        AppInfo.getSystemAk(context);
    }

    /**
     * 设置提示查询条件。
     *
     * @param query 提示查询条件。
     */
    public void setQuery(InputtipsQuery query) {
        this.inputtipsQuery = query;
    }

    /**
     * 返回提示查询条件。
     *
     * @return 提示查询条件。
     */
    public InputtipsQuery getQuery() {
        return inputtipsQuery;

    }

    /**
     * 设置提示查询监听。
     *
     * @param listener 提示类查询监听。
     */
    public void setInputtipsListener(Inputtips.InputtipsListener listener) {
        this.inputtipsListener = listener;

    }

    /**
     * 查询输入提示的异步接口。
     */
    public void requestInputtipsAsyn(final boolean isSearchNearBy, final double latitude,final double longitude) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message localMessage = MessageManager.getInstance()
                        .obtainMessage();

                try {
                    localMessage.arg1 = MessageManager.MESSAGE_TYPE_INPUTTIPS;
                    localMessage.arg2 = 0;
                    MessageManager.InputtipsWrapper locale = new MessageManager.InputtipsWrapper();
                    localMessage.obj = locale;
                    locale.listener = Inputtips.this.inputtipsListener;
                    locale.result = Inputtips.this.requestInputtips(isSearchNearBy,latitude,longitude);
                } catch (SearchException localSearchException) {
                    localMessage.arg2 = localSearchException.getErrorCode();

                } finally {
                    if (null != Inputtips.this.handler)
                        Inputtips.this.handler.sendMessage(localMessage);
                }

            }
        }).start();

    }

    /**
     * 查询输入提示的同步接口。
     *
     * @return 输入提示接口回调的提示列表。
     * @throws SearchException 搜索异常。
     */
    public List<Tip> requestInputtips(boolean isSearchNearBy, double latitude, double longitude) throws SearchException {
        if (getQuery().getKeyword().equals("") || getQuery().getKeyword() == null && getQuery().getAdcode().equals("") || getQuery().getAdcode() == null) {
            throw new SearchException(SearchException.ERROR_REQUEST1);
        }
        InputtipsServerHandler handler = new InputtipsServerHandler(mContext,
                getQuery(), AppInfo.getProxy(mContext), null,isSearchNearBy,latitude,longitude);
        return handler.GetData();
    }

    /**
     * 输入提示回调的监听接口。
     */
    public static abstract interface InputtipsListener {
        /**
         * @param paramList - 输入提示接口回调的提示列表。
         * @param paramInt  - 返回结果成功或者失败的响应码。0为成功，其他为失败。
         */
        public abstract void onGetInputtips(List<Tip> paramList, int paramInt);
    }

}
