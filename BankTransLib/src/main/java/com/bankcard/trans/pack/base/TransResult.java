package com.bankcard.trans.pack.base;

import android.content.Context;
import android.content.res.Resources;

import com.bankcard.trans.R;


public class TransResult {
    /**
     * 交易成功
     */
    public static final int SUCC = 0;
    /**
     * 超时
     */
    public static final int ERR_TIMEOUT = -1;
    /**
     * 连接超时
     */
    public static final int ERR_CONNECT = -2;
    /**
     * 发送失败
     */
    public static final int ERR_SEND = -3;
    /**
     * 接收失败
     */
    public static final int ERR_RECV = -4;
    /**
     * 打包失败
     */
    public static final int ERR_PACK = -5;
    /**
     * 解包失败
     */
    public static final int ERR_UNPACK = -6;
    /**
     * 非法包
     */
    public static final int ERR_BAG = -7;
    /**
     * 解包mac错
     */
    public static final int ERR_MAC = -8;
    /**
     * 处理码不一致
     */
    public static final int ERR_PROC_CODE = -9;
    /**
     * 消息类型不一致
     */
    public static final int ERR_MSG = -10;
    /**
     * 交易金额不符
     */
    public static final int ERR_TRANS_AMT = -11;
    /**
     * 流水号不一致
     */
    public static final int ERR_TRACE_NO = -12;
    /**
     * 终端号不一致
     */
    public static final int ERR_TERM_ID = -13;
    /**
     * 商户号不一致
     */
    public static final int ERR_MERCH_ID = -14;
    /**
     * 无交易
     */
    public static final int ERR_NO_TRANS = -15;
    /**
     * 无原始交易
     */
    public static final int ERR_NO_ORIG_TRANS = -16;
    /**
     * 此交易已撤销
     */
    public static final int ERR_HAS_VOID = -17;
    /**
     * 此交易不可撤销
     */
    public static final int ERR_VOID_UNSUPPORT = -18;
    /**
     * 打开通讯口错误
     */
    public static final int ERR_COMM_CHANNEL = -19;
    /**
     * 失败
     */
    public static final int ERR_HOST_REJECT = -20;
    /**
     * 交易终止（终端不需要提示信息）
     */
    public static final int ERR_ABORTED = -21;
    /**
     * 预处理相关 终端未签到
     */
    public static final int ERR_NOT_LOGON = -22;
    /**
     * 预处理相关 交易笔数超限，立即结算
     */
    public static final int ERR_NEED_SETTLE_NOW = -23;
    /**
     * 预处理相关 交易笔数超限，稍后结算
     */
    public static final int ERR_NEED_SETTLE_LATER = -24;
    /**
     * 预处理相关 存储空间不足
     */
    public static final int ERR_NO_FREE_SPACE = -25;
    /**
     * 预处理相关 终端不支持该交易
     */
    public static final int ERR_NOT_SUPPORT_TRANS = -26;
    /**
     * 卡号不一致
     */
    public static final int ERR_CARD_NO = -27;
    /**
     * 密码错误
     */
    public static final int ERR_PASSWORD = -28;
    /**
     * 参数错误
     */
    public static final int ERR_PARAM = -29;

    /**
     * 终端批上送未完成
     */
    public static final int ERR_BATCH_UP_NOT_COMPLETED = -31;
    /**
     * 金额超限
     */
    public static final int ERR_AMOUNT = -33;
    /**
     * 平台批准卡片拒绝
     */
    public static final int ERR_CARD_DENIED = -34;
    /**
     * 纯电子现金联机拒绝
     */
    public static final int ERR_PURE_CARD_CAN_NOT_ONLINE = -35;
    /**
     * 此交易不可调整
     */
    public static final int ERR_ADJUST_UNSUPPORT = -36;
    /**
     * 预授权类交易不能联机
     */
    public static final int ERR_AUTH_TRANS_CAN_NOT_USE_PURE_CARD = -37;
    /**
     * 无有效交易
     */
    public static final int ERR_NO_VALID_TRANS = -38;
    /**
     * 工作密钥长度错误
     */
    public static final int ERR_TWK_LENGTH = -39;
    /**
     * 还有交易流水，先结算
     */
    public static final int ERR_HAVE_TRANS = -40;
    /**
     * 二维码有*，提示无效的二维码
     */
    public static final int ERR_SCAN_CODE = -41;
    /**
     * 凡是扫描带*都统一提示未激活
     */
    public static final int ERR_NO_ACTIVED = -42;

    /**
     * 存储至数据库时失败
     */
    public static final int ERR_SAVE_FAIL = -43;
    /**
     * 底座超时
     */
    public static final int EXPED_TIMEOUT = -44;

    /***************** 聚合支付 **********************/
    /**
     * 支付失败
     */
    public static final int ERR_PAY_RESULT = -45;

    /**
     * 关闭订单
     */
    public static final int ORDER_CLOSED = -54;

    /**
     * 需要查询
     */
    public static final int NEED_QUERY = -55;
    /**
     * 签名校验错
     */
    public static final int ERR_SIGN = -56;

    /**
     * 未支付(刷卡支付，用户退出输密界面会返回-直接报错提示，扫码支付订单查询会返回-需要订单查询)
     */
    public static final int NOT_PAY = -57;
    /**
     * 取消订单
     */
    public static final int ERR_ShOW_CANEL_DIALOG = -58;
    /**
     * 取消订单
     */
    public static final int ERR_ORDER_CANCEL = -59;

    /**
     * 订单需做撤单处理
     */
    public static final int ORDER_REVERSE = -60;

    /**
     * 订单查询结果为已关闭
     */
    public static final int ORDER_RUSULT_CLOSED = -61;

    /**
     * 取消订单
     */
    public static final int ORDER_RUSULT_REFUND = -62;

    /**
     * 取消订单
     */
    public static final int ORDER_RUSULT_REVERSE = -63;

    /**
     * 聚会支付业务结果错误
     */
    public static int ERR_RESULT_CODE = -64;
    /**
     * http地址错误
     */
    public static final int ERR_HTTP_ADDRESS = -65;

    /**
     * 状态错误
     */
    public static final int ERR_STATUES = -66;
    /**
     * 用户支付中
     */
    public static final int ORDER_USER_PAYING = -67;
    /**
     * 聚合支付通讯错误
     */
    public static final int ERR_HTTP_COMM = -68;

    /**
     * 取消绑定查询
     */
    public static final int ERR_CANCEL_BIND_QUERY = -69;

    /**
     * 生成公私钥失败
     */
    public static final int ERR_GENERAT_KEY = -70;

    /**
     * 绑定查询失败
     */
    public static final int ERR_BIND_STATE = -71;

    /**
     * 退款失败
     */
    public static final int REFUND_FAIL = -72;

    /**
     * 退款处理中
     */
    public static final int REFUND_PROCESSING = -73;

    /**
     * 退款不确定
     */
    public static final int REFUND_NOTSURE = -74;

    /**
     * 绑定查询失败
     */
    public static final int REFUND_CHANGE = -75;

    public static String getMessage(Context context, int ret) {
        String message = "";
        Resources resource = context.getResources();
        switch (ret) {
            case SUCC:
                message = resource.getString(R.string.trans_succ);
                break;
            case ERR_TIMEOUT:
                message = resource.getString(R.string.err_timeout);
                break;
            case ERR_CONNECT:
                message = resource.getString(R.string.err_connect);
                break;
            case ERR_SEND:
                message = resource.getString(R.string.err_send);
                break;
            case ERR_RECV:
                message = resource.getString(R.string.err_recv);
                break;
            case ERR_PACK:
                message = resource.getString(R.string.err_pack);
                break;
            case ERR_UNPACK:
                message = resource.getString(R.string.err_unpack);
                break;
            case ERR_BAG:
                message = resource.getString(R.string.err_bag);
                break;
            case ERR_MAC:
                message = resource.getString(R.string.err_mac);
                break;
            case ERR_PROC_CODE:
                message = resource.getString(R.string.err_proc_code);
                break;
            case ERR_MSG:
                message = resource.getString(R.string.err_msg);
                break;
            case ERR_TRANS_AMT:
                message = resource.getString(R.string.err_trans_amt);
                break;
            case ERR_TRACE_NO:
                message = resource.getString(R.string.err_trace_no);
                break;
            case ERR_TERM_ID:
                message = resource.getString(R.string.err_term_id);
                break;
            case ERR_MERCH_ID:
                message = resource.getString(R.string.err_merch_id);
                break;
            case ERR_NO_TRANS:
                message = resource.getString(R.string.err_no_trans);
                break;
            case ERR_NO_ORIG_TRANS:
                message = resource.getString(R.string.err_no_orig_trans);
                break;
            case ERR_HAS_VOID:
                message = resource.getString(R.string.err_has_void);
                break;
            case ERR_VOID_UNSUPPORT:
                message = resource.getString(R.string.err_void_unsupport);
                break;
            case ERR_COMM_CHANNEL:
                message = resource.getString(R.string.err_comm_channel);
                break;
            case ERR_HOST_REJECT:
                message = resource.getString(R.string.err_host_reject);
                break;
            case ERR_NOT_LOGON:
                message = resource.getString(R.string.err_not_logon);
                break;
            case ERR_NEED_SETTLE_NOW:
                message = resource.getString(R.string.err_need_settle_now);
                break;
            case ERR_NEED_SETTLE_LATER:
                message = resource.getString(R.string.err_need_settle_later);
                break;
            case ERR_NO_FREE_SPACE:
                message = resource.getString(R.string.err_no_free_space);
                break;
            case ERR_NOT_SUPPORT_TRANS:
                message = resource.getString(R.string.err_not_support_trans);
                break;
            case ERR_BATCH_UP_NOT_COMPLETED:
                message = resource.getString(R.string.err_batch_up_break_need_continue);
                break;
            case ERR_CARD_NO:
                message = resource.getString(R.string.err_original_cardno);
                break;
            case ERR_PASSWORD:
                message = resource.getString(R.string.err_manager_password);
                break;
            case ERR_PARAM:
                message = resource.getString(R.string.err_param);
                break;
            case ERR_AMOUNT:
                message = resource.getString(R.string.err_amount);
                break;
            case ERR_CARD_DENIED:
                message = resource.getString(R.string.err_card_denied);
                break;
            case ERR_PURE_CARD_CAN_NOT_ONLINE:
                message = resource.getString(R.string.emv_err_pure_card_can_not_online);
                break;
            case ERR_ADJUST_UNSUPPORT:
                message = resource.getString(R.string.err_adjust_unsupport);
                break;
            case ERR_AUTH_TRANS_CAN_NOT_USE_PURE_CARD:
                message = resource.getString(R.string.emv_err_auth_trans_can_not_use_pure_card);
                break;
            case ERR_NO_VALID_TRANS:
                message = resource.getString(R.string.err_no_valid_trans);
                break;
            case ERR_TWK_LENGTH:
                message = resource.getString(R.string.err_twk_length);
                break;
            case ERR_HAVE_TRANS:
                message = resource.getString(R.string.set_settle);
                break;
            case ERR_SCAN_CODE:
                message = resource.getString(R.string.err_scan_code);
                break;
            case ERR_NO_ACTIVED:
                message = resource.getString(R.string.err_no_actived);
                break;
            case ERR_SAVE_FAIL:
                message = resource.getString(R.string.err_save_fail);
                break;
            case EXPED_TIMEOUT:
                message = resource.getString(R.string.err_exped_timeout);
                break;
            case ORDER_RUSULT_REFUND:
                message = resource.getString(R.string.order_refund);
                break;
            case NOT_PAY:
                message = resource.getString(R.string.order_not_pay);
                break;
            case ORDER_RUSULT_CLOSED:
                message = resource.getString(R.string.order_closed);
                break;
            case ORDER_RUSULT_REVERSE:
                message = resource.getString(R.string.order_void);
                break;
            case ERR_PAY_RESULT:
                message = resource.getString(R.string.order_pay_fail);
                break;
            case ORDER_USER_PAYING:
                message = resource.getString(R.string.order_user_paying);
                break;
            case ERR_HTTP_COMM:
                message = resource.getString(R.string.err_comm_http);
                break;
            case ERR_SIGN:
                message = resource.getString(R.string.sign_err);
                break;
            case ERR_CANCEL_BIND_QUERY:
                message = resource.getString(R.string.cancle_bind_query);
                break;
            case ERR_GENERAT_KEY:
                message = resource.getString(R.string.err_generate_key);
                break;
            case ERR_BIND_STATE:
                message = resource.getString(R.string.err_bind_state);
                break;
            case REFUND_FAIL:
                message = resource.getString(R.string.refund_fail);
                break;
            case REFUND_CHANGE:
                message = resource.getString(R.string.refund_change);
                break;
            case REFUND_NOTSURE:
                message = resource.getString(R.string.refund_notsure);
                break;
            case REFUND_PROCESSING:
                message = resource.getString(R.string.refund_processing);
                break;
            default:
                message = resource.getString(R.string.err_undefine) + "[" + ret + "]";
                break;
        }
        return message;
    }
}
