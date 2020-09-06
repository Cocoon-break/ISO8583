package com.bankcard.trans.model;

import com.bankcard.trans.BankTransManager;
import com.bankcard.trans.R;
import com.bankcard.trans.pack.PackAuth;
import com.bankcard.trans.pack.PackAuthCM;
import com.bankcard.trans.pack.PackAuthCMAdv;
import com.bankcard.trans.pack.PackAuthCMVoid;
import com.bankcard.trans.pack.PackAuthVoid;
import com.bankcard.trans.pack.PackBalance;
import com.bankcard.trans.pack.PackBatchUp;
import com.bankcard.trans.pack.PackBatchUpNotice;
import com.bankcard.trans.pack.PackBinDownload;
import com.bankcard.trans.pack.PackBlackDownload;
import com.bankcard.trans.pack.PackDownloadParam;
import com.bankcard.trans.pack.PackEcho;
import com.bankcard.trans.pack.PackEmvParamDownload;
import com.bankcard.trans.pack.PackEmvParamQuery;
import com.bankcard.trans.pack.PackICScript;
import com.bankcard.trans.pack.PackIcTcBat;
import com.bankcard.trans.pack.PackLogon;
import com.bankcard.trans.pack.PackLogout;
import com.bankcard.trans.pack.PackQrRefund;
import com.bankcard.trans.pack.PackQrSale;
import com.bankcard.trans.pack.PackQrSaleVoid;
import com.bankcard.trans.pack.PackRefund;
import com.bankcard.trans.pack.PackReveral;
import com.bankcard.trans.pack.PackSale;
import com.bankcard.trans.pack.PackSaleVoid;
import com.bankcard.trans.pack.PackSettle;
import com.bankcard.trans.pack.PackSettleAdjust;
import com.bankcard.trans.pack.PackSignatureUpload;
import com.bankcard.trans.pack.base.PackIso8583;
import com.bankcard.trans.pack.inter.PackListener;

public enum ETransType {

    /************************************************ 管理类 ****************************************************/
    LOGON("0800", "", "", "", "00", "001", BankTransManager.context.getString(R.string.pos_logon), false, false, false) {
        @Override
        public PackIso8583 getpackager(PackListener listener) {
            return new PackLogon(listener);
        }

        @Override
        public PackIso8583 getDupPackager(PackListener listener) {
            return null;
        }
    },

    LOGOUT("0820", "", "", "", "00", "002", BankTransManager.context.getString(R.string.pos_logout), false, false,
            false) {
        @Override
        public PackIso8583 getpackager(PackListener listener) {
            return new PackLogout(listener);
        }

        @Override
        public PackIso8583 getDupPackager(PackListener listener) {
            return null;
        }
    },

    /**
     * IC卡公钥下载状态上送
     */
    EMV_MON_CA("0820", "", "", "", "00", "372", BankTransManager.context.getString(R.string.emv_mon_ca), false, false,
            false) {
        @Override
        public PackIso8583 getpackager(PackListener listener) {
            return new PackEmvParamQuery(listener);
        }

        @Override
        public PackIso8583 getDupPackager(PackListener listener) {
            return null;
        }
    },
    /**
     * IC卡公钥下载
     */
    EMV_CA_DOWN("0800", "", "", "", "00", "370", BankTransManager.context.getString(R.string.emv_ca_down), false,
            false, false) {
        @Override
        public PackIso8583 getpackager(PackListener listener) {
            return new PackEmvParamDownload(listener);
        }

        @Override
        public PackIso8583 getDupPackager(PackListener listener) {
            return null;
        }
    },
    /**
     * IC卡公钥下载结束
     */
    EMV_CA_DOWN_END("0800", "", "", "", "00", "371", BankTransManager.context.getString(R.string.emv_ca_down_end),
            false, false, false) {
        @Override
        public PackIso8583 getpackager(PackListener listener) {
            return new PackEmvParamDownload(listener);
        }

        @Override
        public PackIso8583 getDupPackager(PackListener listener) {

            return null;
        }
    },
    /**
     * IC卡参数下载状态上送
     */
    EMV_MON_PARAM("0820", "", "", "", "00", "382", BankTransManager.context.getString(R.string.emv_mon_param), false,
            false, false) {
        @Override
        public PackIso8583 getpackager(PackListener listener) {

            return new PackEmvParamQuery(listener);
        }

        @Override
        public PackIso8583 getDupPackager(PackListener listener) {

            return null;
        }
    },
    /**
     * IC卡参数下载
     */
    EMV_PARAM_DOWN("0800", "", "", "", "00", "380", BankTransManager.context.getString(R.string.emv_param_down),
            false, false, false) {
        @Override
        public PackIso8583 getpackager(PackListener listener) {

            return new PackEmvParamDownload(listener);
        }

        @Override
        public PackIso8583 getDupPackager(PackListener listener) {

            return null;
        }
    },
    /**
     * 回响功能
     */
    ECHO("0820", "", "", "", "00", "301", BankTransManager.context.getString(R.string.echo), false, false, false) {
        @Override
        public PackIso8583 getpackager(PackListener listener) {

            return new PackEcho(listener);
        }

        @Override
        public PackIso8583 getDupPackager(PackListener listener) {

            return null;
        }
    },
    /**
     * 参数传递
     */
    PARAM_TRANSMIT("0800", "", "", "", "00", "360", BankTransManager.context.getString(R.string.param_transmit),
            false, false, false) {
        @Override
        public PackIso8583 getpackager(PackListener listener) {

            return new PackEcho(listener);
        }

        @Override
        public PackIso8583 getDupPackager(PackListener listener) {

            return null;
        }
    },
    /**
     * IC卡参数下载结束
     */
    EMV_PARAM_DOWN_END("0800", "", "", "", "00", "381", BankTransManager.context
            .getString(R.string.emv_param_down_end), false, false, false) {
        @Override
        public PackIso8583 getpackager(PackListener listener) {

            return new PackEmvParamDownload(listener);
        }

        @Override
        public PackIso8583 getDupPackager(PackListener listener) {

            return null;
        }

    },
    /**
     * 黑名单下载
     */
    BLACK_DOWN("0800", "", "", "", "00", "390", BankTransManager.context.getString(R.string.black_down), false, false,
            false) {
        @Override
        public PackIso8583 getpackager(PackListener listener) {

            return new PackBlackDownload(listener);
        }

        @Override
        public PackIso8583 getDupPackager(PackListener listener) {

            return null;
        }
    },

    /**
     * 黑名单下载结束
     */
    BLACK_DOWN_END("0800", "", "", "", "00", "391", BankTransManager.context.getString(R.string.black_down_end),
            false, false, false) {
        @Override
        public PackIso8583 getpackager(PackListener listener) {

            return new PackEmvParamDownload(listener);
        }

        @Override
        public PackIso8583 getDupPackager(PackListener listener) {

            return null;
        }
    },
    /**
     * IC卡脚本结果上送
     */
    IC_SCR_SEND("0620", "", "", "", "00", "951", BankTransManager.context.getString(R.string.ic_scr_send), false,
            false, false) {
        @Override
        public PackIso8583 getpackager(PackListener listener) {

            return new PackICScript(listener);
        }

        @Override
        public PackIso8583 getDupPackager(PackListener listener) {

            return null;
        }
    },
    /**
     * 磁条卡参数下载
     */
    DOWNLOAD_PARAM("0800", "", "", "", "00", "360", BankTransManager.context.getString(R.string.download_param),
            false, false, false) {
        @Override
        public PackIso8583 getpackager(PackListener listener) {

            return new PackDownloadParam(listener);
        }

        @Override
        public PackIso8583 getDupPackager(PackListener listener) {

            return null;
        }
    },

    /**
     * 非接参数下载
     */
    PICC_DOWNLOAD_PARAM("0800", "", "", "", "00", "394", BankTransManager.context
            .getString(R.string.picc_download_param), false, false, false) {
        @Override
        public PackIso8583 getpackager(PackListener listener) {

            return new PackDownloadParam(listener);
        }

        @Override
        public PackIso8583 getDupPackager(PackListener listener) {

            return null;
        }
    },
    /**
     * BIN B
     */
    BIN_B_DOWNLOAD("0800", "", "", "", "00", "396", BankTransManager.context.getString(R.string.bin_b_download),
            false, false, false) {
        @Override
        public PackIso8583 getpackager(PackListener listener) {

            return new PackBinDownload(listener);
        }

        @Override
        public PackIso8583 getDupPackager(PackListener listener) {

            return null;
        }
    },
    /**
     * BIN B
     */
    BIN_B_DOWNLOAD_END("0800", "", "", "", "00", "397", BankTransManager.context
            .getString(R.string.bin_b_download_end), false, false, false) {
        @Override
        public PackIso8583 getpackager(PackListener listener) {

            return new PackBinDownload(listener);
        }

        @Override
        public PackIso8583 getDupPackager(PackListener listener) {

            return null;
        }
    },
    /**
     * BIN C
     */
    BIN_C_DOWNLOAD("0800", "", "", "", "00", "398", BankTransManager.context.getString(R.string.bin_c_download),
            false, false, false) {
        @Override
        public PackIso8583 getpackager(PackListener listener) {

            return new PackBinDownload(listener);
        }

        @Override
        public PackIso8583 getDupPackager(PackListener listener) {

            return null;
        }
    },
    /**
     * BIN C
     */
    BIN_C_DOWNLOAD_END("0800", "", "", "", "00", "399", BankTransManager.context
            .getString(R.string.bin_c_download_end), false, false, false) {
        @Override
        public PackIso8583 getpackager(PackListener listener) {

            return new PackBinDownload(listener);
        }

        @Override
        public PackIso8583 getDupPackager(PackListener listener) {

            return null;
        }
    },
    /**
     * 非接参数下载结束
     */
    PICC_DOWNLOAD_PARAM_END("0800", "", "", "", "00", "395", BankTransManager.context
            .getString(R.string.picc_download_param_end), false, false, false) {
        @Override
        public PackIso8583 getpackager(PackListener listener) {

            return new PackDownloadParam(listener);
        }

        @Override
        public PackIso8583 getDupPackager(PackListener listener) {

            return null;
        }
    },

    SETTLE("0500", "", "", "", "00", "201", BankTransManager.context.getString(R.string.settle), true, true, true) {
        @Override
        public PackIso8583 getpackager(PackListener listener) {
            return new PackSettle(listener);
        }

        @Override
        public PackIso8583 getDupPackager(PackListener listener) {

            return null;
        }

    },

    BATCH_UP("0320", "", "", "", "00", "201", BankTransManager.context.getString(R.string.batch_up), false, false,
            false) {
        @Override
        public PackIso8583 getpackager(PackListener listener) {
            return new PackBatchUp(listener);
        }

        @Override
        public PackIso8583 getDupPackager(PackListener listener) {
            return null;
        }

    },
    /**
     * 通知类交易披上送，包括 退货、预授权完成通知、离线结算、结算调整、结算调整小费
     */

    NOTICE_TRANS_BAT("0320", "", "200000", "00", "25", "000", BankTransManager.context.getString(R.string.batch_up),
            false, false, false) {
        @Override
        public PackIso8583 getpackager(PackListener listener) {
            return new PackBatchUpNotice(listener);
        }

        @Override
        public PackIso8583 getDupPackager(PackListener listener) {
            return null;
        }
    },

    IC_TC_BAT("0320", "", "", "", "00", "203", BankTransManager.context.getString(R.string.batch_up), false, false,
            false) {
        @Override
        public PackIso8583 getpackager(PackListener listener) {
            return new PackIcTcBat(listener);
        }

        @Override
        public PackIso8583 getDupPackager(PackListener listener) {
            return null;
        }

    },
    IC_FAIL_BAT("0320", "", "", "", "00", "204", BankTransManager.context.getString(R.string.batch_up), false, false,
            false) {
        @Override
        public PackIso8583 getpackager(PackListener listener) {
            return new PackIcTcBat(listener);
        }

        @Override
        public PackIso8583 getDupPackager(PackListener listener) {
            return null;
        }

    },
    BATCH_UP_END("0320", "", "", "", "00", "207", BankTransManager.context.getString(R.string.batch_up_end), false,
            false, false) {
        @Override
        public PackIso8583 getpackager(PackListener listener) {
            return new PackBatchUp(listener);
        }

        @Override
        public PackIso8583 getDupPackager(PackListener listener) {
            return null;
        }

    },
    /************************************************ 交易类 ****************************************************/

    SALE("0200", "0400", "000000", "00", "22", "000", BankTransManager.context.getString(R.string.sale_trans), true,
            true, true) {
        @Override
        public PackIso8583 getpackager(PackListener listener) {

            return new PackSale(listener);
        }

        @Override
        public PackIso8583 getDupPackager(PackListener listener) {

            return new PackReveral(listener);
        }
    },
    QUERY("0200", "", "310000", "00", "01", "000", BankTransManager.context.getString(R.string.trans_balance), true,
            true, true) {
        @Override
        public PackIso8583 getpackager(PackListener listener) {
            return new PackBalance(listener);
        }

        @Override
        public PackIso8583 getDupPackager(PackListener listener) {
            return null;
        }
    },
    /**********************************************************************************************************/
    VOID("0200", "0400", "200000", "00", "23", "000", BankTransManager.context.getString(R.string.void_trans), true,
            true, true) {
        @Override
        public PackIso8583 getpackager(PackListener listener) {
            return new PackSaleVoid(listener);
        }

        @Override
        public PackIso8583 getDupPackager(PackListener listener) {
            return new PackReveral(listener);
        }

    },
    /**********************************************************************************************************/
    REFUND("0220", "0420", "200000", "00", "25", "000", BankTransManager.context.getString(R.string.trans_refund),
            true, true, true) {
        @Override
        public PackIso8583 getpackager(PackListener listener) {
            return new PackRefund(listener);
        }

        @Override
        public PackIso8583 getDupPackager(PackListener listener) {
            return null;
        }

    },
    /**********************************************************************************************************/
    AUTH("0100", "0400", "030000", "06", "10", "000", BankTransManager.context.getString(R.string.auth_trans), true,
            true, true) {
        @Override
        public PackIso8583 getpackager(PackListener listener) {

            return new PackAuth(listener);
        }

        @Override
        public PackIso8583 getDupPackager(PackListener listener) {

            return new PackReveral(listener);
        }

    },
    /************************************************************************************************************/
    AUTHCM("0200", "0400", "000000", "06", "20", "000", BankTransManager.context.getString(R.string.auth_cm), true,
            true, true) {
        @Override
        public PackIso8583 getpackager(PackListener listener) {
            return new PackAuthCM(listener);
        }

        @Override
        public PackIso8583 getDupPackager(PackListener listener) {
            return new PackReveral(listener);
        }

    },
    /***************************************************************************************************************/
    AUTHCMVOID("0200", "0400", "200000", "06", "21", "000", BankTransManager.context
            .getString(R.string.auth_cm_void_all), true, true, true) {
        @Override
        public PackIso8583 getpackager(PackListener listener) {

            return new PackAuthCMVoid(listener);
        }

        @Override
        public PackIso8583 getDupPackager(PackListener listener) {

            return new PackReveral(listener);
        }

    },
    /************************************************************************************************************/
    AUTH_SETTLEMENT("0220", "", "000000", "06", "24", "000", BankTransManager.context
            .getString(R.string.auth_cm_adv_all), true, true, true) {
        @Override
        public PackIso8583 getpackager(PackListener listener) {
            return new PackAuthCMAdv(listener);
        }

        @Override
        public PackIso8583 getDupPackager(PackListener listener) {
            return null;
        }

    },
    /************************************************************************************************************/
    AUTHVOID("0100", "0400", "200000", "06", "11", "000", BankTransManager.context.getString(R.string.auth_void),
            true, true, true) {
        @Override
        public PackIso8583 getpackager(PackListener listener) {
            return new PackAuthVoid(listener);
        }

        @Override
        public PackIso8583 getDupPackager(PackListener listener) {
            return new PackReveral(listener);
        }

    },

    READCARDNO("", "", "000000", "", "", "", BankTransManager.context.getString(R.string.trans_readcard), false,
            false, false) {
        @Override
        public PackIso8583 getpackager(PackListener listener) {
            return null;
        }

        @Override
        public PackIso8583 getDupPackager(PackListener listener) {
            return null;
        }

    },
    /*************************************************************************************/
    SIG_SEND("0820", "", "", "", "07", "800", BankTransManager.context.getString(R.string.sign_send), true, true, true) {
        @Override
        public PackIso8583 getpackager(PackListener listener) {
            return new PackSignatureUpload(listener);
        }

        @Override
        public PackIso8583 getDupPackager(PackListener listener) {
            return null;
        }
    },

    SETTLE_ADJUST("0220", "", "000000", "00", "32", "000", BankTransManager.context.getString(R.string.settle_adjust),
            true, true, true) {
        @Override
        public PackIso8583 getpackager(PackListener listener) {
            return new PackSettleAdjust(listener);
        }

        @Override
        public PackIso8583 getDupPackager(PackListener listener) {
            return null;
        }
    },
    SETTLE_ADJUST_TIP("0220", "", "000000", "00", "34", "000", BankTransManager.context
            .getString(R.string.settle_adjust), true, true, true) {
        @Override
        public PackIso8583 getpackager(PackListener listener) {
            return new PackSettleAdjust(listener);
        }

        @Override
        public PackIso8583 getDupPackager(PackListener listener) {
            return null;
        }
    },

    ;

    /**
     * @param msgType       ：消息类型码
     * @param dupMsgType    :冲正消息类型码
     * @param procCode      : 处理码
     * @param serviceCode   ：服务码
     * @param funcCode      ：功能码
     * @param netCode       ：网络管理码
     * @param transName     :交易名称
     * @param isDupSend     ：是否冲正上送
     * @param isScriptSend  ：是否脚本结果上送
     * @param isOfflineSend ：是否脱机交易上送
     */
    private ETransType(String msgType, String dupMsgType, String procCode, String serviceCode, String funcCode,
                       String netCode, String transName, boolean isDupSend, boolean isScriptSend, boolean isOfflineSend) {
        this.msgType = msgType;
        this.dupMsgType = dupMsgType;
        this.procCode = procCode;
        this.serviceCode = serviceCode;
        this.funcCode = funcCode;
        this.netCode = netCode;
        this.transName = transName;
        this.isDupSend = isDupSend;
        this.isScriptSend = isScriptSend;
        this.isOfflineSend = isOfflineSend;
    }

    private String msgType;
    private String dupMsgType;
    private String procCode;
    private String serviceCode;
    private String funcCode;
    private String netCode;
    private String transName;
    private boolean isDupSend;
    private boolean isScriptSend;
    private boolean isOfflineSend;

    public String getMsgType() {
        return msgType;
    }

    public String getDupMsgType() {
        return dupMsgType;
    }

    public String getProcCode() {
        return procCode;
    }

    public String getServiceCode() {
        return serviceCode;
    }

    public String getFuncCode() {
        return funcCode;
    }

    public String getNetCode() {
        return netCode;
    }

    public String getTransName() {
        return transName;
    }

    public boolean isDupSend() {
        return isDupSend;
    }

    public boolean isScriptSend() {
        return isScriptSend;
    }

    public boolean isOfflineSend() {
        return isOfflineSend;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public void setDupMsgType(String dupMsgType) {
        this.dupMsgType = dupMsgType;
    }

    public void setProcCode(String procCode) {
        this.procCode = procCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    public void setFuncCode(String funcCode) {
        this.funcCode = funcCode;
    }

    public void setNetCode(String netCode) {
        this.netCode = netCode;
    }

    public void setTransName(String transName) {
        this.transName = transName;
    }

    public void setDupSend(boolean isDupSend) {
        this.isDupSend = isDupSend;
    }

    public void setScriptSend(boolean isScriptSend) {
        this.isScriptSend = isScriptSend;
    }

    public void setOfflineSend(boolean isOfflineSend) {
        this.isOfflineSend = isOfflineSend;
    }

    public abstract PackIso8583 getpackager(PackListener listener);

    public abstract PackIso8583 getDupPackager(PackListener listener);

}