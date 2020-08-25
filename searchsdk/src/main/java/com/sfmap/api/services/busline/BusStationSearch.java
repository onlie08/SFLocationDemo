package com.sfmap.api.services.busline;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.sfmap.api.services.core.AppInfo;
import com.sfmap.api.services.core.SearchException;
import com.sfmap.api.services.core.MessageManager;

/**
 * 本类为公交站点搜索的“入口”类，定义此类，开始搜索。在类BusStationSearch中，使用BusStationQuery类设定搜索参数。
 */
public class BusStationSearch {
	/**
	 * 公交站点查询异步处理回调接口。
	 */
	private OnBusStationSearchListener busStationSearchListener;
	/**
	 * 公交站点查询条件。
	 */
	private BusStationQuery busStationQuery;
	/**
	 * 当前 Activity上下文。
	 */
	private Context context;
	private Handler handler = null;

	/**
	 * BusStationSearch构造函数。
	 */
	public BusStationSearch() {
		AppInfo.getSystemAk(context);
		handler = MessageManager.getInstance();
	}

	/**
	 * BusStationSearch构造函数。
	 * 
	 * @param act
	 *            当前 Activity。
	 * @param query
	 *            公交站点查询条件。
	 */
	public BusStationSearch(android.content.Context act, BusStationQuery query) {
		this.busStationQuery = query;
		this.context = act.getApplicationContext();
		AppInfo.getSystemAk(context);
		handler = MessageManager.getInstance();

	}

	public void setOnBusStationSearchListener(
			OnBusStationSearchListener onBusStationSearchListener) {
		this.busStationSearchListener = onBusStationSearchListener;
	}

	/**
	 * 公交站点的异步处理调用。
	 */
	public void searchBusStationAsyn() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				Message localMessage = MessageManager.getInstance()
						.obtainMessage();

				try {
					localMessage.arg1 = MessageManager.MESSAGE_TYPE_BUSSTATIONE;
					localMessage.arg2 = 0;
					MessageManager.BusStationWrapper locale = new MessageManager.BusStationWrapper();
					localMessage.obj = locale;
					locale.listener = BusStationSearch.this.busStationSearchListener;
					locale.result = BusStationSearch.this.searchBusStation();
				} catch (SearchException localSearchException) {
					localMessage.arg2 = localSearchException.getErrorCode();
				} finally {
					if (null != BusStationSearch.this.handler)
						BusStationSearch.this.handler.sendMessage(localMessage);
				}

			}
		}).start();

	}

	/**
	 * 搜索公交站点。
	 * 
	 * @return 公交站点搜索结果。
	 * @throws SearchException 搜索异常。
	 */
	public BusStationResult searchBusStation() throws SearchException {
		BusStationServerHandler handler = new BusStationServerHandler(context,
				getQuery(), AppInfo.getProxy(context), null);
		return handler.GetData();

	}

	/**
	 * 设置查询条件。
	 * 
	 * @param query
	 *            -新的查询条件。
	 */
	public void setQuery(BusStationQuery query) {
		this.busStationQuery = query;
	}

	/**
	 * 返回查询条件。
	 * 
	 * @return 返回查询条件。
	 */
	public BusStationQuery getQuery() {
		return busStationQuery;

	}

	/**
	 * 此接口定义了公交站点查询异步处理回调接口。
	 */
	public static abstract interface OnBusStationSearchListener {
        /***
         *
         * @param busStationPagedResult  - 公交站点的搜索结果。
         * @param resultID - 返回结果成功或者失败的响应码。0为成功，其他为失败。
         */
		public abstract void onBusStationSearched(
				BusStationResult busStationPagedResult, int resultID);
	}

}
