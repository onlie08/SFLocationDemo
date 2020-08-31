package com.sfmap.api.location.client.bean;

public enum LbsResultCode {
    /**
     * 成功
     **/
    SUCCESS(0, "success", "成功"),
    XSS_CHECK(1, "xss no result", "查找不到结果"),
    /**
     * 系统繁忙
     */
    SERVER_BUSY(-100, "server is busy", "系统繁忙"),
    /**
     * 系统错误
     */
    SYSTEM_ERROR(-101, "system error", "系统错误"),
    RPC_TIMEOUT(-102, "rcp timeout", "RPC超时"),
    /**
     * 第三方服务不可用
     */
    THIRD_APPLICATION_NOT_AVAILABLE(-103, "the third application service is not available", "第三方服务不可用"),
    /**
     * 未知错误
     */
    UNKNOWN(-104, "unknown error", "未知异常"),
    /**
     * 未知错误
     */
    NOT_FOUND(-105, "not found", "未找到相关记录"),
    /**
     * 参数错误
     */
    PARAMS_ERROR(-106, "params error", "参数错误"),
    /**
     * 地址错误
     */
    ADDRESS_ERR(-107, "address error", "地址错误"),
    /**
     * 地址为空
     */
    ADDRESS_EMPTY(-108, "address empty", "地址为空"),
    /**
     * 详细地址与city字段的行政区划冲突 city unequal
     */
    CITY_UNEQUAL(-109, "city unequal address", "省市区冲突"),

    AC_BLACK(107, "access control in black", "IP访问限制,该IP已被列入黑名单"),

    AC_WHITE_ONLY(108, "access control in black", "IP访问限制,只允许白名单访问"),

    AC_LIMIT_VOLUME_MINUTE(109, "访问限制,访问量已超过每分钟配额", ""),

    AC_LIMIT_VOLUME_DAY(110, "访问限制,访问量已超过当天配额", ""),

    AC_LIMIT_VOLUME_MONTH(111, "访问限制,访问量已超过当月配额", ""),

    AC_LIMIT_VSPEED(112, "访问限制,服务访问过快", ""),

    AC_AK_UNDEFINE(113, "访问限制,ak未授权", ""),

    AC_IP_NOTWHITE(114, "访问限制,ip不在白名单", ""),

    AC_IP_INBLACK(115, "服务被拒绝", ""), // IP黑名单

    AC_IP_TOOFAST(116, "访问限制,ip访问过快", ""),

    TCP_UNSERVER(117, "TCP通信异常，无法建立连接", ""),

    TCP_WAIT_RES_TIMEOUT(118, "TCP通信异常，等待结果超时", ""),

    TCP_CON_TIMEOUT(119, "TCP通信异常,连接超时", ""),

    TCP_RESPONSE_ERR(120, "TCP接收返回数据时异常", ""),

    TCP_EXP_CLOSE(121, "TCP通信异常，连接非法关闭", ""),

    ERR_CODE_RESPONSE(130, "返回异常", ""),

    HTTP_RESPONSE_ERR(140, "HTTP接口数据错误", ""),

    HTTP_CONNECT_TIMEOUT(141, "HTTP接口连接超时", ""),

    HTTP_RESPONSE_TIMEOUT(142, "HTTP接口数据请求超时", ""),

    DB_ERR(150, "数据库错误", ""),

    JSON_DECODE_ERR(160, "JSON反序列化错误", ""),

    JSON_ENCODE_ERR(161, "JSON序列化错误", ""),

    URL_ENCODE_ERR(162, "URLEncode错误", ""),

    URL_DECODE_ERR(163, "URLDecode错误", ""),

    XML_DECODE_ERR(164, "XML解释错误", ""),

    XML_ENCODE_ERR(165, "XML字符串生成错误", ""),

    DATA_INSIDE_ERR(180, "内部数据源错误", ""),

    DATA_OUTSIDE_ERR(181, "外部数据源错误", ""),

    DATA_OUTSIDE_AK_ERR(182, "外部数据源授权超限", ""),

    CPP_RESPONSE_FAIL(1000, "C++接口调用失败", "");;

    /**
     * 错误码
     */
    private int err;
    /**
     * 错误信息
     */
    private String msg;
    /**
     * 错误描叙
     */
    private String desc;

    LbsResultCode(int err, String msg, String desc) {
        this.err = err;
        this.msg = msg;
        this.desc = desc;
    }

    public static LbsResultCode getByCode(int err) {
        for (LbsResultCode status : values()) {
            if (status.getErr() == err) {
                return status;
            }
        }
        return LbsResultCode.UNKNOWN;
    }

    public static int getCppCode(int cppCode) {
        return LbsResultCode.CPP_RESPONSE_FAIL.getErr() + cppCode;
    }

    public String toString() {
        return String.format("{err:\"%s\",msg:\"%s\"}", err, desc);
    }

    public int getErr() {
        return err;
    }

    public String getMsg() {
        return msg;
    }

    public String getDesc() {
        return desc;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public boolean isAuthError() {
        return this.err >= AC_BLACK.err &&
                this.err <= AC_IP_TOOFAST.err;
    }
}
