package com.bankcard.trans.model;

import android.util.Log;

import com.bankcard.trans.BankTransManager;
import com.bankcard.trans.device.Device;
import com.bankcard.trans.utils.DbUtils;
import com.pax.gl.db.DbException;
import com.pax.gl.db.IDb.AEntityBase;
import com.pax.gl.db.IDb.Column;
import com.pax.gl.db.IDb.IDao;
import com.pax.gl.db.IDb.IDbListener;
import com.pax.gl.db.IDb.Unique;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TransData extends AEntityBase implements Serializable, Cloneable {
    private static final long serialVersionUID = 1L;

    /**
     * 交易状态
     */
    public enum ETransStatus {
        /**
         * 正常
         */
        NORMAL,
        /**
         * 已撤销
         */
        VOID,
        /**
         * 已调整
         */
        ADJUST
    }

    /* 脱机上送失败原因 */

    public static class OfflineStatus {
        /**
         * 脱机上送失败
         */
        public static final int SEND_ERR_SEND = 0x01;
        /**
         * 脱机上送平台拒绝(返回码非00)
         */
        public static final int SEND_ERR_RESP = 0x02;
        /**
         * 脱机上送未知失败原因
         */
        public static final int SEND_ERR_UNKN = 0xff;
    }

    /**
     * 电子签名上送状态
     */
    public static class SignSendStatus {
        public static final int SEND_SIG_NO = 0x00; // 未上送
        public static final int SEND_SIG_SUCC = 0x01; // 上送成功
        public static final int SEND_SIG_ERR = 0X02; // 上送失败
    }

    public static class EnterMode {
        /**
         * 手工输入
         */
        public static final int MANAUL = 1;
        /**
         * 刷卡
         */
        public static final int SWIPE = 2;
        /**
         * 插卡
         */
        public static final int INSERT = 3;
        /**
         * IC卡回退
         */
        public static final int FALLBACK = 4;
        /**
         * 预约支付
         */
        public static final int PHONE = 5;
        /**
         * 非接快速支付
         */
        public static final int QPBOC = 6;
        /**
         * 非接完整PBOC
         */
        public static final int CLSS_PBOC = 7;
        /**
         * 非接读取CUPMobile
         */
        public static final int MOBILE = 8;
        /**
         * 扫码支付
         */
        public static final int QR = 9;
    }

    /**
     * 授权方式
     */
    public static class AuthMode {
        /**
         * 本系统预授权的结算
         */
        public static final String POS = "00";
        /**
         * 电话授权的结算
         */
        public static final String PHONE = "01";
        /**
         * 小额代授权的结算
         */
        public static final String SMALL_GEN_AUTH = "02";

    }

    // ============= 需要存储 ==========================
    @Column(canBeNull = true)
    private String transState; // 交易状态
    @Column
    private boolean isUpload; // 是否已批上送
    @Column
    private boolean isOffUploadState; // 是否已脱机上送,true:脱机上送成功
    @Column
    private int sendFailFlag; // 脱机上送失败类型 ：上送失败/平台拒绝
    @Column
    private int sendTimes; // 已批上送次数
    @Column
    private String transType; // 交易类型
    @Column(canBeNull = true)
    private String origTransType; // 原交易类型
    @Column(canBeNull = true)
    private String procCode; // 处理码，39域
    @Column(canBeNull = true)
    private String amount; // 交易金额
    @Column(canBeNull = true)
    private String tipAmount; // 小费金额
    @Column(canBeNull = true)
    private String balance; // 余额
    @Column(canBeNull = true)
    private String balanceFlag; // 余额标识C/D
    @Column
    @Unique
    private long transNo; // pos流水号
    @Column
    private long origTransNo; // 原pos流水号
    @Column
    private long batchNo; // 批次号
    @Column
    private long origBatchNo; // 原批次号
    @Column(canBeNull = true)
    private String pan; // 主账号

    @Column(canBeNull = true)
    private String transferPan; // 转入卡卡号

    @Column(canBeNull = true)
    private String time; // 交易时间
    @Column(canBeNull = true)
    private String date; // 交易日期
    @Column(canBeNull = true)
    private String origDate; // 原交易日期
    @Column(canBeNull = true)
    private String settleDate; // 清算日期
    @Column(canBeNull = true)
    private String expDate; // 卡有效期
    @Column(canBeNull = true)
    private int enterMode; // 输入模式
    @Column(canBeNull = true)
    private int transferEnterMode; // 转入卡的输入模式
    @Column(canBeNull = true)
    private String refNo; // 系统参考号
    @Column(canBeNull = true)
    private String origRefNo; // 原系统参考号
    @Column(canBeNull = true)
    private String authCode; // 授权码
    @Column(canBeNull = true)
    private String origAuthCode; // 原授权码
    @Column(canBeNull = true)
    private String isserCode; // 发卡行标识码
    @Column(canBeNull = true)
    private String acqCode; // 收单机构标识码
    @Column(canBeNull = true)
    private String acqCenterCode; // 受理方标识码,pos中心号(返回包时用)
    @Column(canBeNull = true)
    private String interOrgCode; // 国际组织代码
    @Column
    private boolean hasPin; // 是否有输密码
    @Column(canBeNull = true)
    private String track1; // 磁道一信息

    @Column(canBeNull = true)
    private String track2; // 磁道二数据
    @Column(canBeNull = true)
    private String track3; // 磁道三数据
    @Column
    private boolean isEncTrack; // 磁道是否加密
    @Column(canBeNull = true)
    private String reason; // 冲正原因
    @Column(canBeNull = true)
    private String reserved; // 63域附加域
    @Column(canBeNull = true)
    private String issuerResp; // 发卡方保留域
    @Column(canBeNull = true)
    private String centerResp; // 中国银联保留域
    @Column(canBeNull = true)
    private String recvBankResp;// 受理机构保留域
    @Column(canBeNull = true)
    private String scriptData; // 脚本数据
    @Column(canBeNull = true)
    private String phoneNo; // 持卡人手机号码

    @Column(canBeNull = true)
    private String authMode; // 授权方式
    @Column(canBeNull = true)
    private String authInsCode; // 授权机构代码
    @Column
    private boolean isAdjustAfterUpload; // 离线结算上送后被调整，标识为true

    // 增加扫码数据
    @Column(canBeNull = true)
    private String c2b; // 55域TagA3 扫码付C2B信息码
    @Column(canBeNull = true)
    private String c2bVoucher; // 55域 应答TagA4 扫码付付款凭证码
    @Column(canBeNull = true)
    private String origC2bVoucher; // 原付款凭证码

    // =================EMV数据=============================
    @Column
    private boolean pinFree; // 免密
    @Column
    private boolean signFree; // 免签
    @Column
    private boolean isCDCVM; // CDCVM标识

    @Column
    private boolean isOnlineTrans; // 是否为联机交易
    // 电子签名专用
    @Column(canBeNull = true)
    private byte[] signData; // signData

    @Column
    private int signSendState; // 上送状态：0，未上送；1，上送成功；2，上送失败
    @Column
    private boolean signUpload; // 1:已重上送；0：未重上送

    private String receiptElements; // 电子签名时，55域签购单信息
    // =================EMV数据=============================
    /**
     * EMV交易的执行状态
     */
    @Column(canBeNull = true)
    private byte emvResult; // EMV交易的执行状态
    @Column(canBeNull = true)
    private String cardSerialNo; // 23 域，卡片序列号
    @Column(canBeNull = true)
    private String sendIccData; // IC卡信息,55域
    @Column(canBeNull = true)
    private String dupIccData; // IC卡冲正信息,55域
    @Column(canBeNull = true)
    private String tc; // IC卡交易证书(TC值)tag9f26,(BIN)
    @Column(canBeNull = true)
    private String arqc; // 授权请求密文(ARQC)
    @Column(canBeNull = true)
    private String arpc; // 授权响应密文(ARPC)
    @Column(canBeNull = true)
    private String tvr; // 终端验证结果(TVR)值tag95
    @Column(canBeNull = true)
    private String aid; // 应用标识符AID
    @Column(canBeNull = true)
    private String emvAppLabel; // 应用标签
    @Column(canBeNull = true)
    private String emvAppName; // 应用首选名称
    @Column(canBeNull = true)
    private String tsi; // 交易状态信息(TSI)tag9B
    @Column(canBeNull = true)
    private String atc; // 应用交易计数器(ATC)值tag9f36

    @Column(canBeNull = true)
    private String orderNo; // 订单号

    // ================不需要存储=============================
    /**
     * 消息类型
     */
    private String msgID;
    /**
     * 个人密码(密文)
     */
    private String pin;
    /**
     * 安全控制信息
     */
    private String srcInfo;
    /**
     * 操作员号
     */
    private String oper;
    /**
     * 响应码
     */
    private String responseCode;
    /**
     * 相应码对应的错误信息
     */
    private String responseMsg;
    /**
     * 终端号
     */
    @Column(canBeNull = true)
    private String termID;

    /**
     * 原交易终端号
     */
    @Column(canBeNull = true)
    private String origTermID;
    /**
     * 商户号
     */
    @Column(canBeNull = true)
    private String merchID;

    private String header;
    private String tpdu;

    private boolean isReversal;
    private String field48;
    private String field60;
    private String field62;
    private boolean isSM; // 是否支持国密
    private String recvIccData;
    private String field3;
    private boolean isSupportBypass;

    // ================收费=============================
    @Column(canBeNull = true)
    private String peeType;// 缴费类型
    @Column(canBeNull = true)
    private String feeCode;// 缴费编码，方便结算查询

    public String getFeeCode() {
        return feeCode;
    }

    public void setFeeCode(String feeCode) {
        this.feeCode = feeCode;
    }

    public String getPeeType() {
        return peeType;
    }

    public void setPeeType(String peeType) {
        this.peeType = peeType;
    }

    public String getTransState() {
        return transState;
    }

    public void setTransState(String transState) {
        this.transState = transState;
    }

    public boolean getIsUpload() {
        return isUpload;
    }

    public void setIsUpload(boolean isUpload) {
        this.isUpload = isUpload;
    }

    public boolean getIsOffUploadState() {
        return isOffUploadState;
    }

    public void setIsOffUploadState(boolean isOffSend) {
        this.isOffUploadState = isOffSend;
    }

    public int getSendTimes() {
        return sendTimes;
    }

    public void setSendTimes(int sendTimes) {
        this.sendTimes = sendTimes;
    }

    public String getTransType() {
        return transType;
    }

    public void setTransType(String transType) {
        this.transType = transType;
    }

    public String getOrigTransType() {
        return origTransType;
    }

    public void setOrigTransType(String origTransType) {
        this.origTransType = origTransType;
    }

    public String getProcCode() {
        return procCode;
    }

    public void setProcCode(String procCode) {
        this.procCode = procCode;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getTipAmount() {
        return tipAmount;
    }

    public void setTipAmount(String tipAmount) {
        this.tipAmount = tipAmount;
    }

    public String getIssuerResp() {
        return issuerResp;
    }

    public void setIssuerResp(String issuerResp) {
        this.issuerResp = issuerResp;
    }

    public String getCenterResp() {
        return centerResp;
    }

    public void setCenterResp(String centerResp) {
        this.centerResp = centerResp;
    }

    public String getRecvBankResp() {
        return recvBankResp;
    }

    public void setRecvBankResp(String recvBankResp) {
        this.recvBankResp = recvBankResp;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getBalanceFlag() {
        return balanceFlag;
    }

    public void setBalanceFlag(String balanceFlag) {
        this.balanceFlag = balanceFlag;
    }

    public long getTransNo() {
        return transNo;
    }

    public void setTransNo(long transNo) {
        this.transNo = transNo;
    }

    public long getOrigTransNo() {
        return origTransNo;
    }

    public void setOrigTransNo(long origTransNo) {
        this.origTransNo = origTransNo;
    }

    public int getSendFailFlag() {
        return sendFailFlag;
    }

    public void setSendFailFlag(int sendFailFlag) {
        this.sendFailFlag = sendFailFlag;
    }

    public long getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(long batchNo) {
        this.batchNo = batchNo;
    }

    public long getOrigBatchNo() {
        return origBatchNo;
    }

    public void setOrigBatchNo(long origBatchNo) {
        this.origBatchNo = origBatchNo;
    }

    public String getPan() {
        return pan;
    }

    public void setPan(String pan) {
        this.pan = pan;
    }

    public String getTransferPan() {
        return transferPan;
    }

    public void setTransferPan(String transferPan) {
        this.transferPan = transferPan;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getOrigDate() {
        return origDate;
    }

    public void setOrigDate(String origDate) {
        this.origDate = origDate;
    }

    public String getSettleDate() {
        return settleDate;
    }

    public void setSettleDate(String settleDate) {
        this.settleDate = settleDate;
    }

    public String getExpDate() {
        return expDate;
    }

    public void setExpDate(String expDate) {
        this.expDate = expDate;
    }

    public int getEnterMode() {
        return enterMode;
    }

    public void setEnterMode(int enterMode) {
        this.enterMode = enterMode;
    }

    public int getTransferEnterMode() {
        return transferEnterMode;
    }

    public void setTransferEnterMode(int transferEnterMode) {
        this.transferEnterMode = transferEnterMode;
    }

    public String getRefNo() {
        return refNo;
    }

    public void setRefNo(String refNo) {
        this.refNo = refNo;
    }

    public String getOrigRefNo() {
        return origRefNo;
    }

    public void setOrigRefNo(String origRefNo) {
        this.origRefNo = origRefNo;
    }

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }

    public String getOrigAuthCode() {
        return origAuthCode;
    }

    public void setOrigAuthCode(String origAuthCode) {
        this.origAuthCode = origAuthCode;
    }

    public String getIsserCode() {
        return isserCode;
    }

    public void setIsserCode(String isserCode) {
        this.isserCode = isserCode;
    }

    public String getAcqCode() {
        return acqCode;
    }

    public void setAcqCode(String acqCode) {
        this.acqCode = acqCode;
    }

    public String getAcqCenterCode() {
        return acqCenterCode;
    }

    public void setAcqCenterCode(String acqCenterCode) {
        this.acqCenterCode = acqCenterCode;
    }

    public String getInterOrgCode() {
        return interOrgCode;
    }

    public void setInterOrgCode(String interOrgCode) {
        this.interOrgCode = interOrgCode;
    }

    public boolean getHasPin() {
        return hasPin;
    }

    public void setHasPin(boolean hasPin) {
        this.hasPin = hasPin;
    }

    public String getTrack1() {
        return track1;
    }

    public void setTrack1(String track1) {
        this.track1 = track1;
    }

    public String getTrack2() {
        return track2;
    }

    public void setTrack2(String track2) {
        this.track2 = track2;
    }

    public String getTrack3() {
        return track3;
    }

    public void setTrack3(String track3) {
        this.track3 = track3;
    }

    public boolean getIsEncTrack() {
        return isEncTrack;
    }

    public void setIsEncTrack(boolean isEncTrack) {
        this.isEncTrack = isEncTrack;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getAuthInsCode() {
        return authInsCode;
    }

    public void setAuthInsCode(String authInsCode) {
        this.authInsCode = authInsCode;
    }

    public byte getEmvResult() {
        return emvResult;
    }

    public void setEmvResult(byte emvResult) {
        this.emvResult = emvResult;
    }

    public String getCardSerialNo() {
        return cardSerialNo;
    }

    public void setCardSerialNo(String cardSerialNo) {
        this.cardSerialNo = cardSerialNo;
    }

    public String getSendIccData() {
        return sendIccData;
    }

    public void setSendIccData(String sendIccData) {
        this.sendIccData = sendIccData;
    }

    public String getDupIccData() {
        return dupIccData;
    }

    public void setDupIccData(String dupIccData) {
        this.dupIccData = dupIccData;
    }

    public String getTc() {
        return tc;
    }

    public void setTc(String tc) {
        this.tc = tc;
    }

    public String getArqc() {
        return arqc;
    }

    public void setArqc(String arqc) {
        this.arqc = arqc;
    }

    public String getArpc() {
        return arpc;
    }

    public void setArpc(String arpc) {
        this.arpc = arpc;
    }

    public String getTvr() {
        return tvr;
    }

    public void setTvr(String tvr) {
        this.tvr = tvr;
    }

    public String getAid() {
        return aid;
    }

    public void setAid(String aid) {
        this.aid = aid;
    }

    public String getEmvAppLabel() {
        return emvAppLabel;
    }

    public void setEmvAppLabel(String emvAppLabel) {
        this.emvAppLabel = emvAppLabel;
    }

    public String getEmvAppName() {
        return emvAppName;
    }

    public void setEmvAppName(String emvAppName) {
        this.emvAppName = emvAppName;
    }

    public String getTsi() {
        return tsi;
    }

    public void setTsi(String tsi) {
        this.tsi = tsi;
    }

    public String getAtc() {
        return atc;
    }

    public void setAtc(String atc) {
        this.atc = atc;
    }

    public String getMsgID() {
        return msgID;
    }

    public void setMsgID(String msgID) {
        this.msgID = msgID;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getSrcInfo() {
        return srcInfo;
    }

    public void setSrcInfo(String srcInfo) {
        this.srcInfo = srcInfo;
    }

    public String getOper() {
        return oper;
    }

    public void setOper(String oper) {
        this.oper = oper;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseMsg() {
        return responseMsg;
    }

    public void setResponseMsg(String responseMsg) {
        this.responseMsg = responseMsg;
    }

    public String getTermID() {
        return termID;
    }

    public void setTermID(String termID) {
        this.termID = termID;
    }

    public String getMerchID() {
        return merchID;
    }

    public void setMerchID(String merchID) {
        this.merchID = merchID;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getTpdu() {
        return tpdu;
    }

    public void setTpdu(String tpdu) {
        this.tpdu = tpdu;
    }

    public String getField48() {
        return field48;
    }

    public void setField48(String field48) {
        this.field48 = field48;
    }

    public String getField60() {
        return field60;
    }

    public void setField60(String field60) {
        this.field60 = field60;
    }

    public String getField62() {
        return field62;
    }

    public void setField62(String field62) {
        this.field62 = field62;
    }

    public String getReserved() {
        return reserved;
    }

    public void setReserved(String reserved) {
        this.reserved = reserved;
    }

    public boolean getPinFree() {
        return pinFree;
    }

    public void setPinFree(boolean pinFree) {
        this.pinFree = pinFree;
    }

    public boolean getSignFree() {
        return signFree;
    }

    public void setSignFree(boolean signFree) {
        this.signFree = signFree;
    }

    public boolean getIsCDCVM() {
        return isCDCVM;
    }

    public void setIsCDCVM(boolean isCDCVM) {
        this.isCDCVM = isCDCVM;
    }

    public byte[] getSignData() {
        return signData;
    }

    public void setSignData(byte[] signData) {
        this.signData = signData;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public boolean getIsReversal() {
        return isReversal;
    }

    public void setIsReversal(boolean isReversal) {
        this.isReversal = isReversal;
    }

    public String getRecvIccData() {
        return recvIccData;
    }

    public void setRecvIccData(String recvIccData) {
        this.recvIccData = recvIccData;
    }

    public String getScriptData() {
        return scriptData;
    }

    public void setScriptData(String scriptData) {
        this.scriptData = scriptData;
    }

    public boolean getIsOnlineTrans() {
        return isOnlineTrans;
    }

    public void setIsOnlineTrans(boolean isOnlineTrans) {
        this.isOnlineTrans = isOnlineTrans;
    }

    public boolean getIsSM() {
        return isSM;
    }

    public void setIsSM(boolean isSM) {
        this.isSM = isSM;
    }

    public TransData clone() {
        TransData obj = null;
        try {
            obj = (TransData) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return obj;
    }

    public String getOrigTermID() {
        return origTermID;
    }

    public void setOrigTermID(String origTermID) {
        this.origTermID = origTermID;
    }

    public String getReceiptElements() {
        return receiptElements;
    }

    public void setReceiptElements(String receiptElements) {
        this.receiptElements = receiptElements;
    }

    public int getSignSendState() {
        return signSendState;
    }

    public void setSignSendState(int signSendState) {
        this.signSendState = signSendState;
    }

    public boolean getSignUpload() {
        return signUpload;
    }

    public void setSignUpload(boolean signUpload) {
        this.signUpload = signUpload;
    }

    public String getAuthMode() {
        return authMode;
    }

    public void setAuthMode(String authMode) {
        this.authMode = authMode;
    }

    public String getField3() {
        return field3;
    }

    public void setField3(String field3) {
        this.field3 = field3;
    }

    public void setIsAdjustAfterUpload(boolean isAdjustAfterUpload) {
        this.isAdjustAfterUpload = isAdjustAfterUpload;
    }

    public boolean getIsAdjustAfterUpload() {
        return isAdjustAfterUpload;
    }

    public String getC2b() {
        return c2b;
    }

    public void setC2b(String c2b) {
        this.c2b = c2b;
    }

    public String getC2bVoucher() {
        return c2bVoucher;
    }

    public void setC2bVoucher(String c2bVoucher) {
        this.c2bVoucher = c2bVoucher;
    }

    public String getOrigC2bVoucher() {
        return origC2bVoucher;
    }

    public void setOrigC2bVoucher(String origC2bVoucher) {
        this.origC2bVoucher = origC2bVoucher;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    /******************************** 数据库信息定义 ******************************/

    // ********************************获取数据句柄********************************/

    /**
     * 获取交易数据库句柄
     *
     * @return
     * @throws DbException
     */
    private static IDao<TransData> getTransDao() throws DbException {
        IDao<TransData> dao = BankTransManager.db.getDb(DbInfo.VER, DbInfo.DB_NAME, DbInfo.TABLE_NAME_TRANS,
                TransData.class, new IDbListener<TransData>() {

                    @Override
                    public IDao<TransData> onUpdate(IDao<TransData> arg0, int arg1, int arg2) {
                        Log.e("=================", "===========TransData======onUpdate");
                        return updateDao(arg0);
                    }
                });

        return dao;
    }

    private static IDao<TransData> updateDao(IDao<TransData> Idao) {
        try {
            // 更新交易表
            List<TransData> data = readAllTrans();// Idao.findAll();

            Idao.dropTable();
            IDao<TransData> transDao = BankTransManager.db.getDb(DbInfo.VER, DbInfo.DB_NAME,
                    DbInfo.TABLE_NAME_TRANS, TransData.class, null);
            if (data != null && data.size() > 0) {
                Log.e("", "===============trans======size =" + data.size());
                transDao.save(data);

            } else {
                Log.e("", "===============trans======size =0");
            }

            // 更新冲正表
            data = getTradeRecordList(DbInfo.TABLE_NAME_DUP);// getDupDao().findAll();
            getDupDao().dropTable();
            IDao<TransData> dupDao = BankTransManager.db.getDb(DbInfo.VER, DbInfo.DB_NAME, DbInfo.TABLE_NAME_DUP,
                    TransData.class, null);
            if (data != null && data.size() > 0) {
                Log.e("", "===============dup======size =" + data.size());
                dupDao.save(data);
            } else {
                Log.e("", "===============dup======size =0");
            }

            // 更新脚本表
            data = getTradeRecordList(DbInfo.TABLE_NAME_SCRIPT);// getScriptDao().findAll();

            getScriptDao().dropTable();
            IDao<TransData> scriptDao = BankTransManager.db.getDb(DbInfo.VER, DbInfo.DB_NAME,
                    DbInfo.TABLE_NAME_SCRIPT, TransData.class, null);
            if (data != null && data.size() > 0) {
                Log.e("", "===============script======size =" + data.size());
                scriptDao.save(data);
            } else {
                Log.e("", "===============script======size =0");
            }

            return transDao;
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取冲正数据库句柄
     *
     * @return
     * @throws DbException
     */
    private static IDao<TransData> getDupDao() throws DbException {
        IDao<TransData> dao = BankTransManager.db.getDb(DbInfo.VER, DbInfo.DB_NAME, DbInfo.TABLE_NAME_DUP,
                TransData.class, new IDbListener<TransData>() {

                    @Override
                    public IDao<TransData> onUpdate(IDao<TransData> arg0, int arg1, int arg2) {
                        Log.e("=================", "===========TABLE_NAME_DUP======onUpdate");
                        // 在getTransDao 中已进行升级
                        return null;
                    }
                });

        return dao;
    }

    /**
     * 获取脚本数据库句柄
     *
     * @return
     * @throws DbException
     */
    private static IDao<TransData> getScriptDao() throws DbException {
        IDao<TransData> dao = BankTransManager.db.getDb(DbInfo.VER, DbInfo.DB_NAME, DbInfo.TABLE_NAME_SCRIPT,
                TransData.class, new IDbListener<TransData>() {

                    @Override
                    public IDao<TransData> onUpdate(IDao<TransData> arg0, int arg1, int arg2) {
                        Log.e("=================", "===========TABLE_NAME_SCRIPT======onUpdate");
                        // 在getTransDao 中已进行升级
                        return null;
                    }
                });

        return dao;
    }

    /******************************** 交易数据库操作 ********************************/

    /**
     * 写交易记录
     *
     * @return
     */
    public boolean saveTrans() {

        SQLiteDatabase db = null;
        try {
            IDao<TransData> dao = getTransDao();
            db = dao.getDb();
            // 开启事务,保证删除冲正和保存记录同步成功或同步失败，防止长短款情况
            db.beginTransaction();

            // 删除冲正记录
            if (isOnlineTrans) {
                DbUtils.deleteAll(DbInfo.TABLE_NAME_DUP, db);
            }

            // 保存记录
            setDate(Device.getYear() + getDate());
            DbUtils.save(this, DbInfo.TABLE_NAME_TRANS, db);

            // 事务默认是失败的，要设置成功，否则数据不会修改,上述过程如果出现异常则统一不修改
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            // 出异常则不会走setTransactionSuccessful，则回滚事务
            return false;
        } finally {
            // 提交事务
            if (db != null) {
                db.endTransaction();
                db.close();
            }
        }
        return true;

    }

    /**
     * 读指定交易记录
     *
     * @return
     */
    public static TransData readTrans(long transNo) {
        try {
            IDao<TransData> dao = getTransDao();
            String sql = "select * from trans where ";
            sql += String.format("transNo = %d", transNo);
            List<TransData> list = readTransByCondition(dao, sql);
            // List<TransData> list = dao.findByCondition(sql);
            if ((list != null) && (list.size() == 1)) {
                return list.get(0);
            }
        } catch (DbException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 读扫码指定交易记录
     *
     * @return
     */
    public static TransData readTransByVoucher(String c2bVoucher) {
        try {
            IDao<TransData> dao = getTransDao();
            String sql = "select * from trans where ";
            sql += "c2bVoucher = '" + c2bVoucher + "'";
            // List<TransData> list = dao.findByCondition(sqls);
            List<TransData> list = readTransByCondition(dao, sql);
            if ((list != null) && (list.size() == 1)) {
                return list.get(0);
            }
        } catch (DbException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 读所有交易记录
     *
     * @return
     */
    public static List<TransData> readAllTrans() {
        return getTradeRecordList(DbInfo.TABLE_NAME_TRANS);
    }

    /**
     * 读最后一笔联机（电子签名未上送）的交易记录
     *
     * @return
     */
    public static TransData readLastOnlineNoSendTrans(long currTransNo) {
        try {
            IDao<TransData> dao = getTransDao();
            String sql = "select * from trans where ";
            sql += "signData is not null and signData !=''  and isOnlineTrans = 1 and signSendState="
                    + SignSendStatus.SEND_SIG_NO + " and transNo !=" + currTransNo + " order by transNo desc limit 1";
            List<TransData> list = readTransByCondition(dao, sql);
            if (list != null && list.size() > 0) {
                return list.get(0);
            }
        } catch (DbException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 读离线未上送的电子签名 add by 170420
     *
     * @return
     */
    public static List<TransData> readOfflineNoSendTrans() {
        try {
            IDao<TransData> dao = getTransDao();
            String sql = "select * from trans where ";
            sql += "signData is not null and signData !=''  and isOnlineTrans = 0 and signSendState="
                    + SignSendStatus.SEND_SIG_NO;
            List<TransData> list = readTransByCondition(dao, sql);
            return list;
        } catch (DbException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    /***
     * 批结算前获取未成功上送的电子签名交易
     */
    public static List<TransData> readErrSignTrans() {
        try {
            IDao<TransData> dao = getTransDao();
            String sql = "select * from trans where ";
            sql += "signData is not null and signData !=''  and signUpload != 0 and signSendState="
                    + SignSendStatus.SEND_SIG_ERR;
            List<TransData> list = readTransByCondition(dao, sql);
            // List<TransData> list = dao.findByCondition(sql);
            return list;
        } catch (DbException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 读最后一笔交易记录
     *
     * @return
     */
    public static TransData readLastTrans() {
        try {
            IDao<TransData> dao = getTransDao();
            String sql = SQL_READ_LASTTRANS;
            return readOneTrans(dao, sql);
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 更新交易记录
     *
     * @return
     */
    public boolean updateTrans() {
        long transNo = getTransNo();
        try {
            IDao<TransData> dao = getTransDao();
            String sql = String.format("transNo = %d", transNo);
            List<TransData> list = dao.findByCondition(sql);
            if ((list != null) && (list.size() == 1)) {
                setId(list.get(0).getId());
            }
            dao.update(this);
            return true;
        } catch (DbException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * 读指定交易类型的交易记录
     *
     * @return
     */
    public static List<TransData> readTrans(List<ETransType> types, String addSql) {
        try {
            IDao<TransData> dao = getTransDao();
            String sql = "select * from trans where (";
            boolean flag = false;
            for (ETransType type : types) {
                if (flag) {
                    sql += String.format(" or " + "transType = '%s'", type.toString());
                } else {
                    sql += String.format("transType = '%s'", type.toString());
                    flag = true;
                }
            }
            sql += " )"; // 已撤销的交易在上层判断去掉，或者在addSql中过滤掉，添加接口的通用性
            /** 添加 */
            if (addSql != null) {
                sql = sql + " " + addSql;
            }

            List<TransData> list = readTransByCondition(dao, sql);
            return list;
        } catch (DbException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 读指定交易类型的交易记录
     *
     * @return
     */
    public static List<TransData> readTrans(List<ETransType> types) {
        return readTrans(types, null);
    }

    /**
     * 直接根据sql语句读取数据库
     */
    public static List<TransData> readTrans(String addSql) {
        try {
            IDao<TransData> dao = getTransDao();

            String sql = "select * from trans where 1 = 1";
            /** 添加 */
            if (addSql != null) {
                sql = sql + " " + addSql;
            }

            List<TransData> list = readTransByCondition(dao, sql);

            return list;
        } catch (DbException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 获取内卡借记笔数和借记总金额
     * <p>
     * 用于结算请求或者交易汇总
     *
     * @return obj[0] 笔数 obj[1] 金额
     */
    public static long[] getRmbDebitNumAndAmount() {
        SQLiteDatabase dataBase = null;
        Cursor cursor = null;
        long[] obj = new long[2];
        try {
            IDao<TransData> dao = getTransDao();

            String sql = "select count(id),sum(amount) from "
                    + DbInfo.TABLE_NAME_TRANS
                    + " where ((transType in (?,?,?,?,?,?,?,?) and transState <>?) or (transType =? and isAdjustAfterUpload =?)) and (interOrgCode is null or interOrgCode =?)";

            dataBase = dao.getDb();
            cursor = dataBase.rawQuery(sql, new String[]{"SALE", "QR_SALE", "AUTHCM", "AUTH_SETTLEMENT", "EC_SALE",
                    "SETTLE_ADJUST", "SETTLE_ADJUST_TIP", "EC_TRANSFER_LOAD", "ADJUST", "OFFLINE_SETTLE", "0", "CUP"});

            while (cursor.moveToNext()) {
                obj[0] = cursor.getLong(0);
                obj[1] = Long.parseLong(cursor.getString(1) != null ? cursor.getString(1) : "0");
            }

            return obj;

        } catch (DbException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (dataBase != null) {
                dataBase.close();
            }
        }

    }

    /**
     * 获取内卡贷记总笔数和贷记总金额
     *
     * @return obj[0] 笔数 obj[1] 金额
     */

    public static long[] getRmbCreditNumAndAmount() {
        SQLiteDatabase dataBase = null;
        Cursor cursor = null;
        long[] obj = new long[2];
        try {
            IDao<TransData> dao = getTransDao();
            String sql = "select count(id),sum(amount) from " + DbInfo.TABLE_NAME_TRANS
                    + " where transType in (?,?,?,?,?) and  (interOrgCode is null or interOrgCode =?)";
            dataBase = dao.getDb();
            cursor = dataBase.rawQuery(sql, new String[]{"VOID", "QR_VOID", "REFUND", "QR_REFUND", "AUTHCMVOID",
                    "CUP"});
            while (cursor.moveToNext()) {
                obj[0] = cursor.getLong(0);
                obj[1] = Long.parseLong(cursor.getString(1) != null ? cursor.getString(1) : "0");
            }
            return obj;

        } catch (DbException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (dataBase != null) {
                dataBase.close();
            }
        }

    }

    /**
     * 利用group by查询各类交易 string[0]:count(id) string[1]:sum(amount) string[2]:transType string[3]:transStatus
     * string[4]:interOrgCode
     */
    public static List<String[]> getTransInfoGroupByTransType() {

        SQLiteDatabase dataBase = null;
        Cursor cursor = null;
        String[] obj = null;
        List<String[]> result = new ArrayList<String[]>();
        try {
            IDao<TransData> dao = getTransDao();
            String sql = "select count(id),sum(amount),transType,transState,interOrgCode from "
                    + DbInfo.TABLE_NAME_TRANS
                    + " where isAdjustAfterUpload=  '0' group by transType ,transState,interOrgCode";

            dataBase = dao.getDb();
            cursor = dataBase.rawQuery(sql, null);

            while (cursor.moveToNext()) {
                obj = new String[5];
                obj[0] = cursor.getString(0);
                obj[1] = cursor.getString(1) != null ? cursor.getString(1) : "0";
                obj[2] = cursor.getString(2);
                obj[3] = cursor.getString(3);
                obj[4] = cursor.getString(4);

                result.add(obj);
            }

            return result;

        } catch (DbException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (cursor != null) {
                cursor.close();
            }

            if (dataBase != null) {
                dataBase.close();
            }
        }

    }

    /**
     * 利用group by查询各类交易 string[0]:count(id) string[1]:sum(amount) string[2]:transType string[3]:transStatus
     * string[4]:interOrgCode
     */
    public static List<String[]> getTransInfoGroupByTransFeeCode() {

        SQLiteDatabase dataBase = null;
        Cursor cursor = null;
        String[] obj = null;
        List<String[]> result = new ArrayList<String[]>();
        try {
            IDao<TransData> dao = getTransDao();
            String sql = "select count(id),sum(amount),transType,transState,interOrgCode,feeCode from "
                    + DbInfo.TABLE_NAME_TRANS
                    + " where isAdjustAfterUpload=  '0' group by transType ,transState,interOrgCode,feeCode";

            dataBase = dao.getDb();
            cursor = dataBase.rawQuery(sql, null);

            while (cursor.moveToNext()) {
                obj = new String[6];
                obj[0] = cursor.getString(0);
                obj[1] = cursor.getString(1) != null ? cursor.getString(1) : "0";
                obj[2] = cursor.getString(2);
                obj[3] = cursor.getString(3);
                obj[4] = cursor.getString(4);
                obj[5] = cursor.getString(5);

                result.add(obj);
            }

            return result;

        } catch (DbException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (cursor != null) {
                cursor.close();
            }

            if (dataBase != null) {
                dataBase.close();
            }
        }

    }

    /**
     * 根据交易类型计算总计(交易状态是normal的)
     * <p>
     * 用于打单
     *
     * @param transType :交易类型
     * @param isCup     ：是否是内卡
     * @return obj[0] 笔数 obj[1] 金额
     */
    public static long[] getTransNumAndAmount(String transType, ETransStatus status, boolean isCup) {
        SQLiteDatabase dataBase = null;
        Cursor cursor = null;
        long[] obj = new long[2];
        try {
            IDao<TransData> dao = getTransDao();
            String sql = "select count(id),sum(amount) from " + DbInfo.TABLE_NAME_TRANS
                    + " where (transType =? and transState =?) and isAdjustAfterUpload=?";

            if (isCup)
                sql += " and (interOrgCode is null or interOrgCode =?)";
            else {
                sql += " and (interOrgCode is not null and interOrgCode <>?)";
            }

            dataBase = dao.getDb();
            cursor = dataBase.rawQuery(sql, new String[]{transType, status.toString(), "0", "CUP"});

            while (cursor.moveToNext()) {
                obj[0] = cursor.getLong(0);
                obj[1] = Long.parseLong(cursor.getString(1) != null ? cursor.getString(1) : "0");
            }

            return obj;

        } catch (DbException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (cursor != null) {
                cursor.close();
            }

            if (dataBase != null) {
                dataBase.close();
            }
        }

    }

    /**
     * 获取外卡借记笔数和借记总金额 <br>
     * 用于结算请求或者交易汇总
     *
     * @return obj[0] 笔数 obj[1] 金额
     */

    public static long[] getFrnDebitNumAndAmount() {
        SQLiteDatabase dataBase = null;
        Cursor cursor = null;
        long[] obj = new long[2];
        try {
            IDao<TransData> dao = getTransDao();
            String sql = "select count(id),sum(amount) from "
                    + DbInfo.TABLE_NAME_TRANS
                    + " where ((transType in (?,?,?,?,?,?,?,?)  and transState <>?) or (transType =? and isAdjustAfterUpload =?)) and  (interOrgCode is not null and interOrgCode <>?)";
            dataBase = dao.getDb();
            cursor = dataBase.rawQuery(sql, new String[]{"SALE", "QR_SALE", "AUTHCM", "AUTH_SETTLEMENT", "EC_SALE",
                    "SETTLE_ADJUST", "SETTLE_ADJUST_TIP", "EC_TRANSFER_LOAD", "ADJUST", "OFFLINE_SETTLE", "0", "CUP"});
            while (cursor.moveToNext()) {
                obj[0] = cursor.getLong(0);
                obj[1] = Long.parseLong(cursor.getString(1) != null ? cursor.getString(1) : "0");
            }
            return obj;

        } catch (DbException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (dataBase != null) {
                dataBase.close();
            }
        }

    }

    /**
     * 获取外卡贷记总笔数和贷记总金额
     *
     * @return obj[0] 笔数 obj[1] 金额
     */
    public static long[] getFrnCreditNumAndAmount() {
        SQLiteDatabase dataBase = null;
        Cursor cursor = null;
        long[] obj = new long[2];
        long[] qrObj = new long[2];
        try {
            IDao<TransData> dao = getTransDao();
            String sql = "select count(id),sum(amount) from " + DbInfo.TABLE_NAME_TRANS
                    + " where transType in (?,?,?,?,?) and (interOrgCode is not null and interOrgCode <>?)";

            dataBase = dao.getDb();
            cursor = dataBase.rawQuery(sql, new String[]{"VOID", "QR_VOID", "REFUND", "QR_REFUND", "AUTHCMVOID",
                    "CUP"});
            while (cursor.moveToNext()) {
                obj[0] = cursor.getLong(0);
                obj[1] = Long.parseLong(cursor.getString(1) != null ? cursor.getString(1) : "0");
            }
            return obj;

        } catch (DbException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (dataBase != null) {
                dataBase.close();
            }
        }
    }

    /**
     * 获取交易总笔数
     *
     * @return
     */
    public static long getTransCount() {
        try {
            IDao<TransData> dao = getTransDao();
            long cnt = dao.getCount();
            return cnt;
        } catch (DbException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取数据库句柄，用于提前检测数据库更新
     *
     * @return
     */
    public static void updateDao() {
        try {
            getTransDao();
        } catch (DbException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 删除所有交易记录
     */
    public static boolean deleteAllTrans() {
        try {
            IDao<TransData> dao = getTransDao();
            dao.deleteAll();
            return true;
        } catch (DbException e) {
            e.printStackTrace();
        }
        return false;
    }

    /************************************** 冲正数据库操作 ******************************************************/

    /**
     * 写冲正记录
     */
    public boolean saveDup() {
        try {
            IDao<TransData> dao = getDupDao();
            dao.save(this);
            return true;
        } catch (DbException e) {

            e.printStackTrace();
        }

        return false;
    }

    /**
     * 读冲正记录
     *
     * @return
     */
    public static TransData readDupRecord() {
        // try {
        // IDao<TransData> dao = getDupDao();
        // return dao.findLast();
        // } catch (DbException e) {
        //
        // e.printStackTrace();
        // }
        try {
            IDao<TransData> dao = getDupDao();
            String sql = SQL_READ_DUPRECORD;
            return readOneTrans(dao, sql);
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 更新冲正记录
     *
     * @param transData
     * @return
     */
    private static boolean updateDupRecord(TransData transData) {
        try {
            long transNo = transData.getTransNo();
            IDao<TransData> dao = getDupDao();
            String sql = String.format("transNo = %d", transNo);
            List<TransData> list = dao.findByCondition(sql);
            if ((list != null) && (list.size() == 1)) {
                transData.setId(list.get(0).getId());
            }
            dao.update(transData);
            return true;
        } catch (DbException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static final String REASON_NO_RECV = "98";
    public static final String REASON_MACWRONG = "A0";
    public static final String REASON_OTHERS = "06";

    /**
     * 更新冲正原因
     *
     * @param reason
     */
    public static void updateDupReason(String reason) {
        TransData dupRecord = readDupRecord();
        if (dupRecord == null) {
            return;
        }
        dupRecord.setReason(reason);
        updateDupRecord(dupRecord);
    }

    /**
     * 更新冲正记录的原交易时间
     *
     * @param date
     */
    public static void updateDupDate(String date) {
        TransData dupRecord = readDupRecord();
        if (dupRecord == null) {
            return;
        }
        dupRecord.setOrigDate(date);
        updateDupRecord(dupRecord);
    }

    /**
     * 平台批准卡片拒绝更新55
     *
     * @param f55
     */
    public static void updateDupF55(String f55) {
        TransData dupRecord = readDupRecord();
        if (dupRecord == null) {
            return;
        }
        // dupRecord.setSendIccData(f55);
        dupRecord.setDupIccData(f55); // 在组冲正报文的55域所用的数据是DupIccData.
        updateDupRecord(dupRecord);
    }

    /**
     * 删除交易记录
     */
    public static boolean deleteDupRecord() {
        try {
            IDao<TransData> dao = getDupDao();
            dao.deleteAll();
            return true;
        } catch (DbException e) {
            e.printStackTrace();
        }
        return false;
    }

    /************************************** 脚本结果数据 *************************************/
    /**
     * 写脚本结果记录
     *
     * @return
     */
    public boolean saveScript() {
        try {
            IDao<TransData> dao = getScriptDao();
            dao.save(this);
            return true;
        } catch (DbException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * 读脚本结果记录
     *
     * @return
     */
    public static TransData readScript() {
        try {
            IDao<TransData> dao = getScriptDao();
            String sql = SQL_READ_SCRIPT;
            return readOneTrans(dao, sql);
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 删除脚本结果记录
     *
     * @return
     */
    public static boolean deleteScript() {
        try {
            IDao<TransData> dao = getScriptDao();
            dao.deleteAll();
            return true;
        } catch (DbException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 分批获取数据库记录,获取数据库指定偏移量offset中的total条记录
     *
     * @param total
     * @param offset
     * @return
     */
    public static List<TransData> readTransByLimitNo(int total, int offset) {
        try {
            IDao<TransData> dao = getTransDao();
            String sql = "select * from trans where 1=1 order by transNo desc limit " + total + " offset " + offset;
            List<TransData> list = readTransByCondition(dao, sql);

            return list;
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取所有的交易记录
     *
     * @return
     */
    public static ArrayList<TransData> getTradeRecordList(String tableName) {
        SQLiteDatabase db = null;
        ArrayList<TransData> itemlist = new ArrayList<TransData>();
        Cursor cursor = null;
        String sql;
        IDao<TransData> dao = null;
        try {
            switch (tableName) {
                case DbInfo.TABLE_NAME_TRANS:
                    dao = getTransDao();
                    break;
                case DbInfo.TABLE_NAME_DUP:
                    dao = getDupDao();
                    break;
                case DbInfo.TABLE_NAME_SCRIPT:
                    dao = getScriptDao();
                    break;
                default:
                    break;
            }
            if (dao == null) {
                return itemlist;
            }

            sql = "select * from " + tableName;

            db = dao.getDb();
            cursor = db.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                TransData item = getTransData(cursor);
                itemlist.add(item);
            }

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }

        return itemlist;
    }

    /**
     * 根据条件读取记录
     *
     * @param dao
     * @param sql
     * @return
     */
    private static List<TransData> readTransByCondition(IDao<TransData> dao, String sql) {

        SQLiteDatabase db = dao.getDb();
        Cursor cursor = db.rawQuery(sql, null);
        List<TransData> list = new ArrayList<TransData>();
        while (cursor.moveToNext()) {
            TransData transData = getTransData(cursor);
            list.add(transData);
        }
        cursor.close();
        db.close();
        return list;
    }

    /**
     * 读取指定一条记录
     *
     * @param dao
     * @param sql
     * @return
     */
    private static TransData readOneTrans(IDao<TransData> dao, String sql) {
        TransData transData = null;
        SQLiteDatabase db = dao.getDb();
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            transData = getTransData(cursor);
        }
        cursor.close();
        db.close();
        return transData;
    }

    /**
     * 根据orderNo读指定交易记录
     *
     * @return
     */
    public static TransData readTransByOrderNo(String orderNo) {
        try {
            IDao<TransData> dao = getTransDao();
            String sql = "select * from trans where ";
            sql += "orderNo = " + orderNo;
            List<TransData> list = readTransByCondition(dao, sql);
            // List<TransData> list = dao.findByCondition(sql);
            if ((list != null) && (list.size() == 1)) {
                return list.get(0);
            }
        } catch (DbException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 根据c2bVoucher/origC2bVoucher读指定交易记录(倒序)
     *
     * @return
     */
    public static List<TransData> readTransByC2bOrOrigC2b(String c2bVoucher) {
        try {
            IDao<TransData> dao = getTransDao();
            String sql = "select * from trans where ";
            sql += "c2bVoucher = '" + c2bVoucher + "' or origC2bVoucher = '" + c2bVoucher + "' order by transNo desc";
            List<TransData> list = readTransByCondition(dao, sql);
            // List<TransData> list = dao.findByCondition(sql);
            if (list != null && list.size() > 0) {
                return list;
            }
        } catch (DbException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 给对象赋值
     *
     * @param cursor
     * @return
     */
    private static TransData getTransData(Cursor cursor) {
        TransData item = new TransData();

        item.setAcqCenterCode(cursor.getString(cursor.getColumnIndex(TransDBHelper.ACQ_CENTER_CODE)));
        item.setAcqCode(cursor.getString(cursor.getColumnIndex(TransDBHelper.ACQ_CODE)));
        item.setAid(cursor.getString(cursor.getColumnIndex(TransDBHelper.AID)));
        item.setAmount(cursor.getString(cursor.getColumnIndex(TransDBHelper.AMOUNT)));
        item.setArpc(cursor.getString(cursor.getColumnIndex(TransDBHelper.ARPC)));
        item.setArqc(cursor.getString(cursor.getColumnIndex(TransDBHelper.ARQC)));
        item.setAtc(cursor.getString(cursor.getColumnIndex(TransDBHelper.ATC)));
        item.setAuthCode(cursor.getString(cursor.getColumnIndex(TransDBHelper.AUTH_CODE)));
        item.setAuthInsCode(cursor.getString(cursor.getColumnIndex(TransDBHelper.AUTH_INSCODE)));
        item.setAuthMode(cursor.getString(cursor.getColumnIndex(TransDBHelper.AUTH_MODE)));
        item.setBalance(cursor.getString(cursor.getColumnIndex(TransDBHelper.BALANCE)));
        item.setBalanceFlag(cursor.getString(cursor.getColumnIndex(TransDBHelper.BALANCE_FLAG)));
        item.setTvr(cursor.getString(cursor.getColumnIndex(TransDBHelper.TVR)));
        item.setC2b(cursor.getString(cursor.getColumnIndex(TransDBHelper.C2B)));
        item.setC2bVoucher(cursor.getString(cursor.getColumnIndex(TransDBHelper.C2B_VOUCHER)));
        item.setCardSerialNo(cursor.getString(cursor.getColumnIndex(TransDBHelper.CARD_SERIALNO)));
        item.setCenterResp(cursor.getString(cursor.getColumnIndex(TransDBHelper.CENTER_RESP)));
        item.setDate(cursor.getString(cursor.getColumnIndex(TransDBHelper.DATE)));
        item.setDupIccData(cursor.getString(cursor.getColumnIndex(TransDBHelper.DUP_ICC_DATA)));
        item.setEmvAppLabel(cursor.getString(cursor.getColumnIndex(TransDBHelper.EMV_APP_LABEL)));
        item.setEmvAppName(cursor.getString(cursor.getColumnIndex(TransDBHelper.EMV_APP_NAME)));
        item.setTsi(cursor.getString(cursor.getColumnIndex(TransDBHelper.TSI)));
        item.setTransferPan(cursor.getString(cursor.getColumnIndex(TransDBHelper.TRANSFER_PAN)));
        item.setExpDate(cursor.getString(cursor.getColumnIndex(TransDBHelper.EXP_DATE)));
        item.setTransType(cursor.getString(cursor.getColumnIndex(TransDBHelper.TRANS_TYPE)));
        item.setInterOrgCode(cursor.getString(cursor.getColumnIndex(TransDBHelper.INTER_ORG_CODE)));
        item.setTransState(cursor.getString(cursor.getColumnIndex(TransDBHelper.TRANS_STATE)));
        item.setTrack3(cursor.getString(cursor.getColumnIndex(TransDBHelper.TRACK3)));
        item.setTrack2(cursor.getString(cursor.getColumnIndex(TransDBHelper.TRACK2)));
        item.setTrack1(cursor.getString(cursor.getColumnIndex(TransDBHelper.TRACK1)));
        item.setTipAmount(cursor.getString(cursor.getColumnIndex(TransDBHelper.TIP_AMOUNT)));
        item.setTime(cursor.getString(cursor.getColumnIndex(TransDBHelper.TIME)));
        item.setIsserCode(cursor.getString(cursor.getColumnIndex(TransDBHelper.ISSER_CODE)));
        item.setIssuerResp(cursor.getString(cursor.getColumnIndex(TransDBHelper.ISSUER_RESP)));
        item.setOrigAuthCode(cursor.getString(cursor.getColumnIndex(TransDBHelper.ORIG_AUTH_CODE)));
        item.setTc(cursor.getString(cursor.getColumnIndex(TransDBHelper.TC)));
        item.setOrigC2bVoucher(cursor.getString(cursor.getColumnIndex(TransDBHelper.ORIG_C2B_VOUCHER)));
        item.setOrigDate(cursor.getString(cursor.getColumnIndex(TransDBHelper.ORIG_DATE)));
        item.setOrigRefNo(cursor.getString(cursor.getColumnIndex(TransDBHelper.ORIG_REFNO)));
        item.setOrigTermID(cursor.getString(cursor.getColumnIndex(TransDBHelper.ORIG_TERMID)));
        item.setOrigTransType(cursor.getString(cursor.getColumnIndex(TransDBHelper.ORIG_TRANS_TYPE)));
        item.setPan(cursor.getString(cursor.getColumnIndex(TransDBHelper.PAN)));
        item.setPhoneNo(cursor.getString(cursor.getColumnIndex(TransDBHelper.PHONE_NO)));
        item.setProcCode(cursor.getString(cursor.getColumnIndex(TransDBHelper.PROC_CODE)));
        item.setReason(cursor.getString(cursor.getColumnIndex(TransDBHelper.REASON)));
        item.setRecvBankResp(cursor.getString(cursor.getColumnIndex(TransDBHelper.RECV_BANK_RESP)));
        item.setRefNo(cursor.getString(cursor.getColumnIndex(TransDBHelper.REFNO)));
        item.setReserved(cursor.getString(cursor.getColumnIndex(TransDBHelper.RESERVED)));
        item.setScriptData(cursor.getString(cursor.getColumnIndex(TransDBHelper.SCRIPT_DATA)));
        item.setSettleDate(cursor.getString(cursor.getColumnIndex(TransDBHelper.SETTLE_DATE)));
        item.setSendIccData(cursor.getString(cursor.getColumnIndex(TransDBHelper.SEND_ICC_DATA)));
        item.setSendTimes(cursor.getInt(cursor.getColumnIndex(TransDBHelper.SEND_TIMES)));
        item.setBatchNo(cursor.getLong(cursor.getColumnIndex(TransDBHelper.BATCHNO)));
        item.setTransNo(cursor.getLong(cursor.getColumnIndex(TransDBHelper.TRANSNO)));
        item.setOrigBatchNo(cursor.getLong(cursor.getColumnIndex(TransDBHelper.ORIG_BATCHNO)));
        item.setOrigTransNo(cursor.getLong(cursor.getColumnIndex(TransDBHelper.ORIG_TRANSNO)));
        item.setSignUpload(cursor.getInt(cursor.getColumnIndex(TransDBHelper.SIGN_UPLOAD)) == 1);
        item.setSignSendState(cursor.getInt(cursor.getColumnIndex(TransDBHelper.SIGN_SEND_STATE)));
        item.setSignFree(cursor.getInt(cursor.getColumnIndex(TransDBHelper.SIGN_FREE)) == 1);
        item.setIsUpload(cursor.getInt(cursor.getColumnIndex(TransDBHelper.IS_UPLOAD)) == 1);
        item.setIsOffUploadState(cursor.getInt(cursor.getColumnIndex(TransDBHelper.IS_OFF_UPLOAD_STATE)) == 1);
        item.setIsEncTrack(cursor.getInt(cursor.getColumnIndex(TransDBHelper.IS_ENCTRACK)) == 1);
        item.setIsCDCVM(cursor.getInt(cursor.getColumnIndex(TransDBHelper.IS_CDCVM)) == 1);
        item.setPinFree(cursor.getInt(cursor.getColumnIndex(TransDBHelper.PIN_FREE)) == 1);
        item.setHasPin(cursor.getInt(cursor.getColumnIndex(TransDBHelper.HAS_PIN)) == 1);
        item.setTransferEnterMode(cursor.getInt(cursor.getColumnIndex(TransDBHelper.TRANSFER_ENTER_MODE)));
        item.setEnterMode(cursor.getInt(cursor.getColumnIndex(TransDBHelper.ENTER_MODE)));
        item.setIsAdjustAfterUpload(cursor.getInt(cursor.getColumnIndex(TransDBHelper.IS_ADJUST_AFTER_UPLOAD)) == 1);
        item.setIsOnlineTrans(cursor.getInt(cursor.getColumnIndex(TransDBHelper.IS_ONLINE_TRANS)) == 1);
        item.setSignData(cursor.getBlob(cursor.getColumnIndex(TransDBHelper.SIGN_DATA)));
        item.setSendFailFlag(cursor.getInt(cursor.getColumnIndex(TransDBHelper.SEND_FAIL_FLAG)));
        item.setOrderNo(cursor.getString(cursor.getColumnIndex(TransDBHelper.ORDERNO)));
        item.setPeeType(cursor.getString(cursor.getColumnIndex(TransDBHelper.PEE_TYPE)));
        item.setFeeCode(cursor.getString(cursor.getColumnIndex(TransDBHelper.FEE_CODE)));

        int termIDIndex = cursor.getColumnIndex(TransDBHelper.TERM_ID);
        if (termIDIndex >= 0) {
            item.setTermID(cursor.getString(termIDIndex));
        }

        int merchIDIndex = cursor.getColumnIndex(TransDBHelper.MERCH_ID);
        if (merchIDIndex >= 0) {
            item.setMerchID(cursor.getString(merchIDIndex));
        }

        if (cursor.getString(cursor.getColumnIndex(TransDBHelper.EMV_RESULT)) != null)
            item.setEmvResult(Byte.parseByte(cursor.getString(cursor.getColumnIndex(TransDBHelper.EMV_RESULT))));
        return item;
    }

    public boolean isSupportBypass() {
        return isSupportBypass;
    }

    public void setSupportBypass(boolean isSupportBypass) {
        this.isSupportBypass = isSupportBypass;
    }

    private static final String SQL_READ_SCRIPT = "select * from script where 1=1 order by id desc limit 1";
    private static final String SQL_READ_DUPRECORD = "select * from dup where 1=1 order by id desc limit 1";
    private static final String SQL_READ_LASTTRANS = "select * from trans where 1=1 order by id desc limit 1";

}
