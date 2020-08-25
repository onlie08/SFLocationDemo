package com.sfmap.api.services.cloud;

/**
 * 云图数据存储网络请求的结果类
 */

public class CloudStorageResult {
    /**
     * 服务返回信息
     */
    private String message;
    /**
     * 数据的id
     */
    private int[] ids;
    /**
     * 数据集id
     */
    private int  datasetId;
    /**
     * 结果状态码。0,成功;其他失败;
     */
    private String status;

    /**
     * 请求结果状态码。
     * @return "0",成功;其他失败。
     */
    public String getStatus() {
        return status;
    }

     void setStatus(String status) {
        this.status = status;
    }

    /**
     * 获取服务返回的message。
     * @return 服务返回的message。
     */
    public String getMessage() {
        return message;
    }

     void setMessage(String message) {
        this.message = message;
    }

    /**
     * 获取数据id
     * @return 数据id.
     */
    public int[] getId() {
        return ids;
    }

     void setId(int[] ids) {
        this.ids = ids;
    }

    /**
     * 获取数据集id.
     * @return 数据集id.
     */
    public int getDatasetId() {
        return datasetId;
    }

     void setDatasetId(int datasetId) {
        this.datasetId = datasetId;
    }
}
