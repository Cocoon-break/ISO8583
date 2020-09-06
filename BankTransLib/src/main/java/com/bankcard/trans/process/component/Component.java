package com.bankcard.trans.process.component;

import android.content.Context;

import com.bankcard.trans.model.ETransType;
import com.bankcard.trans.pack.base.TransResult;

public class Component {
    private static final String TAG = Component.class.getSimpleName();

    /**
     * 交易预处理，检查是否签到， 是否需要结束， 是否继续批上送， 是否支持该交易， 是否需要参数下载
     *
     * @param context
     * @param transType
     * @return
     */
    public static int transPreDeal(final Context context, ETransType transType) {
        if (!IsNeedPreDeal(transType)) {
            return TransResult.SUCC;
        }
        // 检测电量状态，暂不处理，后续再确定需不需要 fix me
        // 判断终端签到状态
        if (!isLogon()) {
            return TransResult.ERR_NOT_LOGON;
        }
        return TransResult.SUCC;
    }

    /**
     * 根据交易类型、冲正标识确认当前交易是否预处理
     *
     * @param transType
     * @return true:需要预处理 false:不需要预处理
     * 备注：签到，签退，结算，参数下发，公钥下载，冲正类不需要预处理,新增交易类型时，需修改添加交易类型判断
     */
    private static boolean IsNeedPreDeal(ETransType transType) {
        if (transType == ETransType.LOGON
                || transType == ETransType.LOGOUT
                // 上送交易
                // 批上送类
                // 参数下载类
                || transType == ETransType.EMV_MON_CA || transType == ETransType.EMV_CA_DOWN
                || transType == ETransType.EMV_CA_DOWN_END || transType == ETransType.EMV_MON_PARAM
                || transType == ETransType.EMV_PARAM_DOWN || transType == ETransType.BLACK_DOWN
                || transType == ETransType.BLACK_DOWN_END
                // 结算
                || transType == ETransType.SETTLE
            // 冲正类交易
        ) {
            return false;
        }
        return true;
    }

    /**
     * 判断终端是否签到
     *
     * @return true：已签到 false：未签到
     */
    private static boolean isLogon() {
        return true;
    }



}
