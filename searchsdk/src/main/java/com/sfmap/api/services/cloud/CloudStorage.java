package com.sfmap.api.services.cloud;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.sfmap.api.services.core.AppInfo;
import com.sfmap.api.services.core.SearchException;
import com.sfmap.api.services.core.MessageManager;

/**
 * 云图数据存储的入口
 */
public class CloudStorage {

    /**
     * 对应的Context。
     */
    private Context context;
    /**
     * 云图数据存储回调接口。
     */
    private OnCloudStorageListener storageListener;
    /**
     * 子线程通信。
     */
    private Handler handler;

    /**
     * 云图数据存储的构造方法。
     * @param context 对应的Context。
     */
    public CloudStorage(Context context) {
        this.context = context;
        handler = MessageManager.getInstance();
    }


    /**
     * 设置云图数据存储回调监听。
     * @param storageListener 云图存储回调监听。
     */
    public void setClounStorageListener(OnCloudStorageListener storageListener) {
        this.storageListener = storageListener;
    }

    /**
     * 向数据集中添加单条或多条数据。
     * @param dataSetId 数据集id。
     * @param items 数据。
     * @return 返回操作结果对象。
     * @throws SearchException 此接口在网络连接出现问题的情况下，会抛出SearchException。
     */
    public CloudStorageResult add(int dataSetId,CloudItem[] items) throws SearchException {
        CloudStorageAddServerHandler handler = new CloudStorageAddServerHandler(
                context, new Add(dataSetId,items), AppInfo.getProxy(context), null);
        return handler.GetData();
    }

    /**
     * 添加数据集。
     * @param item 数据集。
     * @return 返回操作结果对象。
     * @throws SearchException 此接口在网络连接出现问题的情况下，会抛出SearchException。
     */
    public CloudStorageResult addSet(CloudDatasetItem item) throws SearchException {
        CloudStorageAddSetServerHandler handler = new CloudStorageAddSetServerHandler(
                context, new Add(item), AppInfo.getProxy(context), null);
        return handler.GetData();
    }

    /**
     * 删除数据集。
     * @param dataSetId 数据集id。
     * @return 返回操作结果对象。
     * @throws SearchException 此接口在网络连接出现问题的情况下，会抛出SearchException。
     */
    public CloudStorageResult delSet(int dataSetId) throws SearchException {
        CloudStorageDelSetServerHandler handler = new CloudStorageDelSetServerHandler(
                context, new Del(dataSetId), AppInfo.getProxy(context), null);
        return handler.GetData();
    }


    /**
     * 删除数据集中单条或多条数据
     * @param dataIds 数据id。
     * @param dataSetId 数据集id。
     * @return 返回操作结果对象。
     * @throws SearchException 此接口在网络连接出现问题的情况下，会抛出SearchException。
     */
    public CloudStorageResult del(int dataSetId,int[] dataIds) throws SearchException {
        CloudStorageDelServerHandler handler = new CloudStorageDelServerHandler(
                context, new Del(dataSetId,dataIds), AppInfo.getProxy(context), null);
        return handler.GetData();
    }


    /**
     * 更新数据集中单条或多条数据
     * @param dataSetId 数据集id。
     * @param dataIds 数据id。
     * @param items 数据。
     * @return 返回操作结果对象
     * @throws SearchException 此接口在网络连接出现问题的情况下，会抛出SearchException。
     */
    public CloudStorageResult updata(int dataSetId,int[] dataIds,CloudItem[] items) throws SearchException {
        CloudStorageUpdataServerHandler handler = new CloudStorageUpdataServerHandler(
                context, new Updata(dataSetId,dataIds,items), AppInfo.getProxy(context), null);
        return handler.GetData();
    }

    /**
     * 向数据集中添加单条或多条数据的异步接口。
     * @param dataSetId 数据集id。
     * @param items 数据。
     * @return 返回操作结果对象。
     * @throws SearchException 此接口在网络连接出现问题的情况下，会抛出SearchException。
     */
    public void addAysn(final int dataSetId, final CloudItem[] items){
        new Thread(new Runnable() {
            public void run() {
                Message localMessage = MessageManager.getInstance()
                        .obtainMessage();
                MessageManager.CloudStorageWrapper locale = new MessageManager.CloudStorageWrapper();
                locale.listener = CloudStorage.this.storageListener;
                try {
                    localMessage.arg1 = MessageManager.MESSAGE_TYPE_CLOUDSTORAGE_ADD;
                    localMessage.arg2 = 0;
                    locale.result = CloudStorage.this.add(dataSetId,items);
                } catch (SearchException localSearchException) {
                    localMessage.arg2 = localSearchException.getErrorCode();
                } finally {
                    localMessage.obj = locale;
                    if (null != CloudStorage.this.handler)
                        CloudStorage.this.handler.sendMessage(localMessage);
                }
            }
        }).start();
    }


    /**
     * 添加数据集的异步接口。
     * @param item 数据集。
     * @return 返回操作结果对象。
     * @throws SearchException 此接口在网络连接出现问题的情况下，会抛出SearchException。
     */
    public void addSetAysn(final CloudDatasetItem item){
        new Thread(new Runnable() {
            public void run() {
                Message localMessage = MessageManager.getInstance()
                        .obtainMessage();
                MessageManager.CloudStorageWrapper locale = new MessageManager.CloudStorageWrapper();
                locale.listener = CloudStorage.this.storageListener;
                try {
                    localMessage.arg1 = MessageManager.MESSAGE_TYPE_CLOUDSTORAGE_ADDSET;
                    localMessage.arg2 = 0;
                    locale.result = CloudStorage.this.addSet(item);
                } catch (SearchException localSearchException) {
                    localMessage.arg2 = localSearchException.getErrorCode();
                } finally {
                    localMessage.obj = locale;
                    if (null != CloudStorage.this.handler)
                        CloudStorage.this.handler.sendMessage(localMessage);
                }
            }
        }).start();
    }


    /**
     * 删除数据集中单条或多条数据的异步接口
     * @param dataIds 数据id。
     * @param dataSetId 数据集id。
     * @return 返回操作结果对象。
     * @throws SearchException 此接口在网络连接出现问题的情况下，会抛出SearchException。
     */
    public void delAysn(final int dataSetId, final int[] dataIds){
        new Thread(new Runnable() {
            public void run() {
                Message localMessage = MessageManager.getInstance()
                        .obtainMessage();
                MessageManager.CloudStorageWrapper locale = new MessageManager.CloudStorageWrapper();
                locale.listener = CloudStorage.this.storageListener;
                try {
                    localMessage.arg1 = MessageManager.MESSAGE_TYPE_CLOUDSTORAGE_DEL;
                    localMessage.arg2 = 0;
                    locale.result = CloudStorage.this.del(dataSetId,dataIds);
                } catch (SearchException localSearchException) {
                    localMessage.arg2 = localSearchException.getErrorCode();
                } finally {
                    localMessage.obj = locale;
                    if (null != CloudStorage.this.handler)
                        CloudStorage.this.handler.sendMessage(localMessage);
                }
            }
        }).start();
    }

    /**
     * 删除数据集的异步接口。
     * @param dataSetId 数据集id。
     * @return 返回操作结果对象。
     * @throws SearchException 此接口在网络连接出现问题的情况下，会抛出SearchException。
     */
    public void delSetAysn(final int dataSetId){
        new Thread(new Runnable() {
            public void run() {
                Message localMessage = MessageManager.getInstance()
                        .obtainMessage();
                MessageManager.CloudStorageWrapper locale = new MessageManager.CloudStorageWrapper();
                locale.listener = CloudStorage.this.storageListener;
                try {
                    localMessage.arg1 = MessageManager.MESSAGE_TYPE_CLOUDSTORAGE_DELSET;
                    localMessage.arg2 = 0;
                    locale.result = CloudStorage.this.delSet(dataSetId);
                } catch (SearchException localSearchException) {
                    localMessage.arg2 = localSearchException.getErrorCode();
                } finally {
                    localMessage.obj = locale;
                    if (null != CloudStorage.this.handler)
                        CloudStorage.this.handler.sendMessage(localMessage);
                }
            }
        }).start();
    }

    /**
     * 更新数据集中单条或多条数据的异步接口
     * @param dataSetId 数据集id。
     * @param dataIds 数据id。
     * @param items 数据。
     * @return 返回操作结果对象
     * @throws SearchException 此接口在网络连接出现问题的情况下，会抛出SearchException。
     */
    public void updataAysn(final int dataSetId, final int[] dataIds, final CloudItem[] items){
        new Thread(new Runnable() {
            public void run() {
                Message localMessage = MessageManager.getInstance()
                        .obtainMessage();
                MessageManager.CloudStorageWrapper locale = new MessageManager.CloudStorageWrapper();
                locale.listener = CloudStorage.this.storageListener;
                try {
                    localMessage.arg1 = MessageManager.MESSAGE_TYPE_CLOUDSTORAGE_UPDATA;
                    localMessage.arg2 = 0;
                    locale.result = CloudStorage.this.updata(dataSetId,dataIds,items);
                } catch (SearchException localSearchException) {
                    localMessage.arg2 = localSearchException.getErrorCode();
                } finally {
                    localMessage.obj = locale;
                    if (null != CloudStorage.this.handler)
                        CloudStorage.this.handler.sendMessage(localMessage);
                }
            }
        }).start();
    }

    /**
     * 此类定义了云存储添加数据需要信息
     */
     class Add{
        /**
         * 数据集id
         */
        private int dataSetId;
        /**
         * 数据信息.
         */
        private CloudItem[] dataItems;
        /**
         * 数据集信息。
         */
        private CloudDatasetItem datasetItem;

        /**
         * 添加数据的构造方法。
         * @param dataSetId 数据集id。
         * @param dataItems 数据。
         */
        Add(int dataSetId,CloudItem[] dataItems){
            this.dataItems=dataItems;
            this.dataSetId=dataSetId;
        }

        /**
         * 添加数据集的构造方法。
         * @param datasetItem 数据集。
         */
        Add(CloudDatasetItem datasetItem){
            this.datasetItem=datasetItem;
        }

        /**
         * 获取数据集id。
         * @return 数据集id。
         */
        public int getDataSetId(){
            return this.dataSetId;
        }

        /**
         * 获取数据集信息。
         * @return 数据集。
         */
        public CloudDatasetItem getDatasetItem(){
            return this.datasetItem;
        }

        /**
         * 获取数据信息。
         * @return  数据信息。
         */
        public CloudItem[] getDataItems(){
            return this.dataItems;
        }
    }

    /**
     * 此类定义了删除数据需要的信息
     */
     class Del{
        /**
         * 数据集id。
         */
        private int dataSetId;
        /**
         * 数据id.
         */
        private int[] dataIds;

        /**
         * 删除数据集的构造方法。
         * @param dataSetId 数据集id
         */
        Del(int dataSetId){
            this.dataSetId=dataSetId;
        }

        /**
         * 删除数据集中数据的构造方法
         * @param dataSetId 数据集id。
         * @param dataIds 数据id.
         */
        Del(int dataSetId,int[] dataIds){
            this.dataSetId=dataSetId;
            this.dataIds=dataIds;
        }

        /**
         * 获取数据集id.
         * @return 数据集id.
         */
        public int getDataSetId(){
            return this.dataSetId;
        }

        /**
         * 获取数据id.
         * @return 数据id.
         */
        public int[] getDataIds(){
            return this.dataIds;
        }
    }

    /**
     * 此类定义了更新数据集中数据需要的信息
     */
    class Updata{
        /**
         * 数据集id.
         */
        private int dataSetId;
        /**
         * 数据id.
         */
        private int[] dataIds;
        /**
         * 数据
         */
        private CloudItem[] cloudItems;

        /**
         * 更新数据集中数据的构造方法
         * @param dataSetId 数据集id.
         * @param dataIds 数据id.
         * @param cloudItems 数据。
         */
        Updata(int dataSetId,int[] dataIds,CloudItem[] cloudItems){
            this.dataIds=dataIds;
            this.dataSetId=dataSetId;
            this.cloudItems=cloudItems;
        }

        /**
         * 获取数据集id.
         * @return 数据集id.
         */
        public int getDataSetId(){
            return this.dataSetId;
        }

        /**
         * 获取数据id
         * @return 数据id。
         */
        public int[] getDataIds(){
            return this.dataIds;
        }

        /**
         * 获取数据信息。
         * @return 数据信息。
         */
        public CloudItem[] getCloudItems(){
            return this.cloudItems;
        }
    }

    /**
     * 云图存储的异步处理回调接口
     */
    public static interface OnCloudStorageListener{
        /**
         * 异步向数据集中添加数据时回调的方法。
         * @param dataSetId 数据集id.
         * @param dataId 添加成功后的数据id数据.
         * @param errorCode  错误码 。0为操作成功,其他为失败
         */
        public void onAdd(int dataSetId,int[] dataId,int errorCode);
        /**
         * 异步向数据集时回调的方法。
         * @param dataSetId 添加成功后的数据集id.
         * @param errorCode  错误码 。0为操作成功,其他为失败
         */
        public void onAddSet(int dataSetId,int errorCode);
        /**
         * 异步更新数据集中数据时回调的方法。
         * @param dataSetId 更新成功后的数据集id.
         * @param dataId 更新后的数据id数组
         * @param errorCode  错误码 。0为操作成功,其他为失败
         */
        public void onUpdata(int dataSetId,int[] dataId,int errorCode);
        /**
         * 异步删除数据集中数据时回调的方法。
         * @param dataSetId 数据集id.
         * @param dataId 删除的数据id数组
         * @param errorCode  错误码 。0为操作成功,其他为失败
         */
        public void onDel(int dataSetId,int[] dataId,int errorCode);
        /**
         * 异步删除数据集中数据时回调的方法。
         * @param dataSetId 删除的数据集id.
         * @param errorCode  错误码 。0为操作成功,其他为失败
         */
        public void onDelSet(int dataSetId,int errorCode);
    }

}
