package com.caih.cloud.iscs.common;

/**
 * Created by zengheng on 2018/7/23.
 */
public class Constants {

    /**
     * 商户号
     */
    public static final String DEAL_CUSID = "287611072992874";
    /**
     * 应用ID
     */
    public static final String DEAL_APPID = "00009648";
    /**
     * 应用密钥
     */
    public static final String DEAL_APPKEY = "gx_shenhe";
    /**
     * secret
     */
    public static final String DEAL_SECRET = "6b929648fda44002ba68552c00628020";
    /**
     * 测试环境交易退款URL
     */
//    public static final String DEAL_APIURL_TEST_REFUND = "http://xtest.allinpaygx.com/wx/cash/externalorder/refund";
    public static final String DEAL_APIURL_TEST_REFUND = "https://test.allinpaygd.com/apiweb/unitorder/refund";
    /**
     * 生产环境交易退款URL
     */
    public static final String DEAL_APIURL_PRD_REFUND = "https://vsp.allinpay.com/apiweb/unitorder/refund";

    /**
     * 通联返回码：请求成功
     */
    public static final String DEAL_SUCCESS = "SUCCESS";
    /**
     * 通联返回码：交易成功
     */
    public static final String DEAL_SUCCESS_0000 = "0000";
    /**
     * 通联返回码：交易不存在
     */
    public static final String DEAL_SUCCESS_1001 = "1001";
    /**
     * 通联返回码：交易处理中
     */
    public static final String DEAL_SUCCESS_2008 = "2008";
    /**
     * 通联返回码：交易处理中
     */
    public static final String DEAL_SUCCESS_2000 = "2000";

    /**
     * 通联返回码：请求或前端处理失败，具体看retmsg
     */
    public static final String DEAL_FAIL = "FAIL";

    /**
     * 通联返回码：请求或前端处理失败，具体看retmsg
     */
    public static final String DEAL_FAIL_TX = "FAIL4TX";
    /**
     * 通联返回码：3开头的错误码代表交易失败，具体看retmsg
     */
    public static final String DEAL_FAIL_3 = "3";
    /**
     * 通联返回码：请求参数错误
     */
    public static final String DEAL_PARAMERR = "PARAMERR";

    /**
     * 通联返回码：签名或者api权限不足
     */
    public static final String DEAL_SIGNAUTHERR = "SIGNAUTHERR";

    /**
     * 通联返回码：系统异常，对于实时类交易（例如被扫交易），建议进行查询
     */
    public static final String DEAL_SYSTEMERR = "SYSTEMERR";

    /**
     * 提现类型
     */
    public static final String EXTRACT_STATE = "extract_state";

    /**
     * 超管类型
     */
    public static final int ADMIN_TYPE = 1;


    /**
     * 平台类型
     */
    public static final int PLATFORM_TYPE = 2;

    /**
     * 商家类型
     */
    public static final int STORE_TYPE = 3;


    /**
     * 成功类型
     */
    public static final int SUCCESS_TYPE = 1;

    /**
     * 同步成功
     */
    public static final String STORE_SUBMIT_SUCCESS = "同步成功";

    /**
     * 同步失败
     */
    public static final String STORE_SUBMIT_FAIL = "同步失败";

    /**
     * 查询失败
     */
    public static final String QUERY_FAIL = "查询失败";

    /**
     * 查询成功
     */
    public static final String QUERY_SUCCESS = "查询成功";

    public static final String SIGN_FAIL = "数据验签失败";

    public static final String SPLIT_EMPTY_STRING = "";

    public static final String SPLIT_BALNK_COMMA = ",";

    public static final String QUERY_FAIL_CONTAIN_SAME_STORE_NO = "已包含相同商户编号(storeNo)数据";

    public static final String QUERY_FAIL_CONTAIN_SAME_ACC_NO = "已包含相同账户编码(accNo)数据";

    public static final String QUERY_FAIL_CONTAIN_SAME_CUSID = "已包含相同通联收银宝商户号(cusid)数据";

    public static final String QUERY_FAIL_CONTAIN_SAME_APPID = "已包含相同通联收银宝APPID(appid)数据";

    public static final String QUERY_FAIL_STORE_NAME_GREATER_THAN_50 = "商户名称长度大于50";

    public static final String QUERY_FAIL_USER_NAME_GREATER_THAN_20 = "联系人名称长度大于20";

    public static final String QUERY_FAIL_PHONE = "手机号不合法";

    public static final String QUERY_FAIL_SERVICE = "服务器异常, 保存数据失败";

    /**
     * 商户表store_no字段名
     */
    public static final String COLUMN_NAME_STORE_NO = "store_no";

    /**
     * 商户表acc_no字段名
     */
    public static final String COLUMN_NAME_ACC_NO = "acc_no";

    /**
     * 商户表cusid字段名
     */
    public static final String COLUMN_NAME_CUSID = "cusid";

    /**
     * 商户表appid字段名
     */
    public static final String COLUMN_NAME_APPID = "appid";

    public static final int LENGTH_50 = 50;

    public static final int LENGTH_20 = 20;

    public static final int LENGTH_0 = 0;

    public static final String LOCK_PREFIX = "lock_";

}
