package com.bankcard.trans.pack.base;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.util.Log;

import com.bankcard.trans.BankTransManager;
import com.bankcard.trans.device.Device;
import com.bankcard.trans.model.ETransType;
import com.bankcard.trans.model.TransData;
import com.bankcard.trans.model.TransData.EnterMode;
import com.bankcard.trans.pack.inter.IPacker;
import com.bankcard.trans.pack.inter.PackListener;
import com.bankcard.trans.utils.Utils;
import com.pax.gl.convert.IConvert;
import com.pax.gl.packer.IIso8583;
import com.pax.gl.packer.IIso8583.IIso8583Entity;
import com.pax.gl.packer.Iso8583Exception;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

@SuppressLint("DefaultLocale")
public abstract class PackIso8583 implements IPacker<TransData, byte[]> {
    private IIso8583 iso8583;
    protected IIso8583Entity entity;
    protected PackListener listener;
    private boolean isDebug = true;

    public PackIso8583(PackListener listener) {
        this.listener = listener;
        initEntity();
    }

    /**
     * 获取打包entity
     *
     * @return
     */
    private void initEntity() {
        iso8583 = BankTransManager.packer.getIso8583();
        try {
            entity = iso8583.getEntity();
            entity.loadTemplate(BankTransManager.context.getResources().getAssets().open("cup8583.xml"));
        } catch (Iso8583Exception e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
    }

    protected byte[] pack(boolean isNeedMac) {
        try {
            if (isNeedMac) {
                entity.setFieldValue("64", new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00});
            } else {
                if (entity.hasField("53"))
                    entity.resetFieldValue("53");
            }

            // for debug
            if (isDebug) {
                entity.dump();
            }

            byte[] packData = iso8583.pack();

            if (isNeedMac) {
                if (packData == null || packData.length == 0) {
                    return null;
                }

                int len = packData.length;
                byte[] calMacBuf = new byte[len - 11 - 8];// 去掉header和mac
                System.arraycopy(packData, 11, calMacBuf, 0, len - 11 - 8);
                byte[] mac = listener.onCalcMac(calMacBuf);
                if (mac == null) {
                    return null;
                }
                System.arraycopy(mac, 0, packData, len - 8, 8);
            }

            return packData;
        } catch (Iso8583Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 设置公共数据
     * <p>
     * 设置域： h,m, field 3, field 25, field 41,field 42
     *
     * @param transData
     * @return
     */
    protected int setMandatoryData(TransData transData) {
        try {
            String temp = "";
            // h
            String pHeader = transData.getTpdu() + transData.getHeader();
            entity.setFieldValue("h", pHeader);
            // m
            ETransType transType = ETransType.valueOf(transData.getTransType());
            if (transData.getIsReversal()) {
                entity.setFieldValue("m", transType.getDupMsgType());
            } else {
                entity.setFieldValue("m", transType.getMsgType());
            }

            // field 3/25 交易处理码/服务码
            if (!transType.equals(ETransType.IC_SCR_SEND)) {
                temp = transType.getProcCode();
                if (temp != null && temp.length() > 0) {
                    entity.setFieldValue("3", temp);
                    // 为后续解包比较，做准备
                    transData.setField3(temp);
                }

                temp = transType.getServiceCode();
                if (temp != null && temp.length() > 0) {
                    entity.setFieldValue("25", temp);
                }
            } else {
                // 脚本上送同执行该脚本通知交易的原始信息
                temp = transData.getOrigTransType();
                if (temp == null || temp.length() == 0)
                    return TransResult.ERR_PACK;
                ETransType origTransType = ETransType.valueOf(temp);
                entity.setFieldValue("3", origTransType.getProcCode());
                // 为后续解包比较，做准备
                transData.setField3(origTransType.getProcCode());
                entity.setFieldValue("25", ETransType.valueOf(temp).getServiceCode());
            }

            // field 41 终端号
            temp = transData.getTermID();
            if (temp != null && temp.length() > 0) {
                entity.setFieldValue("41", temp);
            }

            // field 42 商户号
            temp = transData.getMerchID();
            if (temp != null && temp.length() > 0) {
                entity.setFieldValue("42", temp);
            }

            return TransResult.SUCC;

        } catch (Exception e) {
            e.printStackTrace();
            return TransResult.ERR_PACK;
        }
    }

    /**
     * 设置field 2, 4, 11, 14, 22, 23, 26, 35,36,49,52, 53
     *
     * @param transData
     * @return
     */
    private int setCommonData(TransData transData) {
        String temp = "";
        try {
            ETransType transType = ETransType.valueOf(transData.getTransType());
            int enterMode = transData.getEnterMode();
            if (enterMode == TransData.EnterMode.MANAUL) {
                // 手工输入
                // [2]主账号,[14]有效期
                temp = transData.getPan();
                if (temp != null && temp.length() > 0) {
                    entity.setFieldValue("2", temp);
                }

                temp = transData.getExpDate();
                if (temp != null && temp.length() > 0) {
                    entity.setFieldValue("14", temp);
                }

            } else if (enterMode == TransData.EnterMode.SWIPE) {
                // 刷卡

                // [35]二磁道,[36]三磁道
                temp = transData.getTrack2();
                if (temp != null && temp.length() > 0) {
                    if (transData.getIsEncTrack()) {// 加密
                        temp = new String(listener.onEncTrack(temp.getBytes()));
                    }
                    entity.setFieldValue("35", temp);
                }

                temp = transData.getTrack3();
                if (temp != null && temp.length() > 0) {
                    if (transData.getIsEncTrack()) {// 加密
                        temp = new String(listener.onEncTrack(temp.getBytes()));
                    }
                    entity.setFieldValue("36", temp);
                }

            } else if (enterMode == TransData.EnterMode.INSERT || enterMode == TransData.EnterMode.QPBOC
                    || enterMode == TransData.EnterMode.CLSS_PBOC) {
                // [2]主账号
                temp = transData.getPan();
                if (temp != null && temp.length() > 0) {
                    entity.setFieldValue("2", temp);
                }
                // [14]有效期
                temp = transData.getExpDate();
                if (temp != null && temp.length() > 0) {
                    entity.setFieldValue("14", temp);
                }
                // [23]卡序列号
                temp = transData.getCardSerialNo();
                if (temp != null && temp.length() > 0) {
                    entity.setFieldValue("23", temp);
                }

                // 电子现金脱机交易上送，不用上送35域
                // [35]二磁道
                temp = transData.getTrack2();
                if (temp != null && temp.length() > 0) {
                    if (transData.getIsEncTrack()) {// 加密
                        temp = new String(listener.onEncTrack(temp.getBytes()));
                    }
                    entity.setFieldValue("35", temp);
                }
            }

            // field 4
            temp = transData.getAmount();
            if (temp != null && temp.length() > 0) {
                entity.setFieldValue("4", temp);
            }

            // field 11 流水号
            temp = String.valueOf(transData.getTransNo());
            if (temp != null && temp.length() > 0) {
                entity.setFieldValue("11", temp);
            }
            // field 22 服务点输入方式码
            entity.setFieldValue("22", getInputMethod(enterMode, transData.getHasPin()));

            // [26]服务点PIN获取码,[52]PIN,[53]安全控制信息
            if (transData.getHasPin()) {
                entity.setFieldValue("26", "12");
                if (transData.getIsSM()) {
                    entity.setFieldValue("52", new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00});
                } else {
                    entity.setFieldValue("52", BankTransManager.convert.strToBcd(transData.getPin(),
                            IConvert.EPaddingPosition.PADDING_LEFT));
                }
            }

            // field 53
            if (transData.getHasPin() || entity.hasField("35") || entity.hasField("36")) {
                temp = "2600000000000000";
                char[] data = temp.toCharArray();
                if (!transData.getHasPin()) {
                    data[0] = '0';
                }
                if (transData.getIsSM()) {
                    data[1] = '3';
                }
                if (transData.getIsEncTrack() && (entity.hasField("35") || entity.hasField("36"))) {
                    data[2] = '1';
                }

                temp = new String(data);
                entity.setFieldValue("53", temp);
            } else {
                if (transData.getIsSM()) { // 国密类交易必须要有53域，不需要64域时，再算mac处统一去掉53域
                    entity.setFieldValue("53", "2300000000000000");
                }
            }
            // field 49
            entity.setFieldValue("49", "156");

            return setBitDataF59(transData); // 59域数据含国密的密钥，扫码交易信息以及硬件序列号上送

        } catch (Iso8583Exception e) {

            e.printStackTrace();
        }

        return TransResult.ERR_PACK;

    }

    /**
     * 设置金融类数据
     * <p>
     * 设置域
     * <p>
     * field 2, field 4,field 14, field 22,field 23,field 26, field 35,field 36,field 49, field 52,field 53, field 55
     *
     * @param transData
     */
    protected int setFinancialData(TransData transData) {

        try {

            String temp = "";
            int ret = 0;
            ret = setMandatoryData(transData);
            if (ret != TransResult.SUCC) {
                return ret;
            }
            ret = setCommonData(transData);
            if (ret != TransResult.SUCC) {
                return ret;
            }

            // field 55
            temp = transData.getSendIccData();
            Log.d(TAG, "pack filed 55>>>" + temp);
            if (temp != null && temp.length() > 0) {
                entity.setFieldValue("55", BankTransManager.convert.strToBcd(temp, IConvert.EPaddingPosition.PADDING_LEFT));
            }

            return TransResult.SUCC;
        } catch (Exception e) {
            e.printStackTrace();
            return TransResult.ERR_PACK;
        }
    }

    /**
     * 设置撤销类交易 设置域
     * <p>
     * field 2, field 4, field 14,field 22, field 23,field 26,field 35,field 36,
     * <p>
     * field 37,field 38, field 49,field 53,field 61
     *
     * @param transData
     * @return
     */
    protected int setVoidCommonData(TransData transData) {
        try {
            String temp = "";
            int ret = 0;

            ret = setMandatoryData(transData);
            if (ret != TransResult.SUCC) {
                return ret;
            }
            ret = setCommonData(transData);
            if (ret != TransResult.SUCC) {
                return ret;
            }

            // [37]原参考号
            temp = transData.getOrigRefNo();
            if (temp != null && temp.length() > 0) {
                entity.setFieldValue("37", temp);
            }
            // [38]原授权码
            temp = transData.getOrigAuthCode();
            if (temp != null && temp.length() > 0) {
                entity.setFieldValue("38", temp);
            }

            ETransType transType = ETransType.valueOf(transData.getTransType());
            // field 61
            String f61 = "";
            temp = String.format("%06d", transData.getOrigBatchNo());
            if (temp != null && temp.length() > 0) {
                f61 += temp;
            } else {
                f61 += "000000";
            }
            temp = String.format("%06d", transData.getOrigTransNo());
            if (temp != null && temp.length() > 0) {
                f61 += temp;
            } else {
                f61 += "000000";
            }
            // 预授权撤销，预授权完成请求需要原始交易日期
            if (transType == ETransType.AUTHVOID || transType == ETransType.AUTHCMVOID) {
                temp = transData.getOrigDate();
                if (temp != null && temp.length() > 0) {
                    f61 += temp;
                } else {
                    f61 += "0000";
                }
            }
            entity.setFieldValue("61", f61);

            return TransResult.SUCC;
        } catch (Exception e) {
            e.printStackTrace();
            return TransResult.ERR_PACK;
        }
    }

    /**
     * 设置冲正公共类数据
     * <p>
     * <p>
     * 设置域
     * <p>
     * filed 2, field 4,field 11,field 14,field 22,field 23,field 38,
     * <p>
     * field 39,field 49,field 55,field 61
     *
     * @param transData
     * @return
     */
    protected int setRevCommonData(TransData transData) {
        try {
            String temp = "";
            int ret = 0;
            ret = setMandatoryData(transData);
            if (ret != TransResult.SUCC) {
                return ret;
            }

            int enterMode = transData.getEnterMode();

            ETransType transType = ETransType.valueOf(transData.getTransType());
            if (enterMode != TransData.EnterMode.SWIPE && enterMode != TransData.EnterMode.QR) {// 磁条卡交易冲正,不上送2域
                // field 2 主账号
                temp = transData.getPan();
                if (temp != null && temp.length() > 0) {
                    entity.setFieldValue("2", temp);
                }

            }
            // field 4 交易金額
            temp = transData.getAmount();
            if (temp != null && temp.length() > 0) {
                entity.setFieldValue("4", temp);
            }
            // field 11 流水号
            temp = String.valueOf(transData.getTransNo());
            if (temp != null && temp.length() > 0) {
                entity.setFieldValue("11", temp);
            }
            // field 14 有效期
            temp = transData.getExpDate();
            if (temp != null && temp.length() > 0) {
                entity.setFieldValue("14", temp);
            }
            // field 22 服务点输入方式码
            entity.setFieldValue("22", getInputMethod(enterMode, transData.getHasPin()));

            // field 23 卡片序列号
            if (enterMode == TransData.EnterMode.INSERT || enterMode == TransData.EnterMode.QPBOC
                    || enterMode == TransData.EnterMode.CLSS_PBOC) {

                temp = transData.getCardSerialNo();
                if (temp != null && temp.length() > 0) {
                    entity.setFieldValue("23", temp);
                }
            }

            // field 38
            temp = transData.getOrigAuthCode();
            if (temp != null && temp.length() > 0) {
                entity.setFieldValue("38", temp);
            }

            // filed 39
            temp = transData.getReason();
            if (temp != null && temp.length() > 0) {
                entity.setFieldValue("39", temp);
            }

            // field 49
            entity.setFieldValue("49", "156");

            // 扫码的时候26和52,53不上送
            if (transData.getHasPin()) {
                entity.setFieldValue("26", "12");
                if (transData.getIsSM()) {
                    entity.setFieldValue("52", new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00});
                } else {
                    entity.setFieldValue("52", BankTransManager.convert.strToBcd(transData.getPin(),
                            IConvert.EPaddingPosition.PADDING_LEFT));
                }
            }

            // field 53
            if (transData.getHasPin() || entity.hasField("35") || entity.hasField("36")) {
                temp = "2600000000000000";
                char[] data = temp.toCharArray();
                if (!transData.getHasPin()) {
                    data[0] = '0';
                }
                if (transData.getIsSM()) {
                    data[1] = '3';
                }
                if (transData.getIsEncTrack() && (entity.hasField("35") || entity.hasField("36"))) {
                    data[2] = '1';
                }

                temp = new String(data);
                entity.setFieldValue("53", temp);
            } else {
                if (transData.getIsSM()) { // 国密类交易必须要有53域，不需要64域时，再算mac处统一去掉53域
                    entity.setFieldValue("53", "2300000000000000");
                }
            }

            // [55]IC卡数据域if
            temp = transData.getDupIccData();
            if (temp != null && temp.length() > 0) {
                entity.setFieldValue("55", BankTransManager.convert.strToBcd(temp, IConvert.EPaddingPosition.PADDING_LEFT));
            }

            if (transType == ETransType.VOID || transType == ETransType.AUTHVOID || transType == ETransType.AUTHCMVOID
                    || transType == ETransType.AUTHCM) {

                String f61 = "";
                temp = String.format("%06d", transData.getBatchNo());
                if (temp != null && temp.length() > 0) {
                    f61 += temp;
                } else {
                    f61 += "000000";
                }
                temp = String.format("%06d", transData.getTransNo());
                if (temp != null && temp.length() > 0) {
                    f61 += temp;
                } else {
                    f61 += "000000";
                }

                if (transType == ETransType.AUTHVOID || transType == ETransType.AUTHCMVOID
                        || transType == ETransType.AUTHCM) {
                    temp = transData.getOrigDate();
                    if (temp != null && temp.length() > 0 && transData.getReason() != null
                            && !transData.getReason().equals(TransData.REASON_NO_RECV)) {
                        f61 += temp;
                    } else {
                        f61 += "0000";
                    }
                }
                entity.setFieldValue("61", f61);
            }
            return TransResult.SUCC;
        } catch (Exception e) {
            e.printStackTrace();
            return TransResult.ERR_PACK;
        }
    }

    /**
     * 检查请求和返回的关键域field4, field11, field41, field42
     *
     * @param map        解包后的map
     * @param transData  请求
     * @param isCheckAmt 是否检查field4
     * @return
     */
    protected int checkRecvData(HashMap<String, byte[]> map, TransData transData, boolean isCheckAmt) {
        String temp = "";
        byte[] data = null;
        // 交易金额
        if (isCheckAmt) {
            data = map.get("4");
            String amount = transData.getAmount();
            if ((data != null && data.length > 0) && (amount != null && amount.length() > 0)) {
                temp = new String(data);
                if (Long.parseLong(temp) != Long.parseLong(amount)) {
                    return TransResult.ERR_TRANS_AMT;
                }
            }
        }
        // 校验11域
        data = map.get("11");
        if (data != null && data.length > 0) {
            temp = new String(data);
            if (!temp.equals(String.format("%06d", transData.getTransNo()))) {
                return TransResult.ERR_TRACE_NO;
            }
        }
        // 校验终端号
        data = map.get("41");
        if (data != null && data.length > 0) {
            temp = new String(data);
            if (!temp.equals(transData.getTermID())) {
                return TransResult.ERR_TERM_ID;
            }
        }
        // 校验商户号
        data = map.get("42");
        if (data != null && data.length > 0) {
            temp = new String(data);
            if (!temp.equals(transData.getMerchID())) {
                return TransResult.ERR_MERCH_ID;
            }
        }
        return TransResult.SUCC;
    }

    // 设置 field 48
    protected int setBitDataF48(TransData transData) {
        try {
            ETransType transType = ETransType.valueOf(transData.getTransType());
            switch (transType) {
                case SETTLE:
                case BATCH_UP:
                case BATCH_UP_END:
                    entity.setFieldValue("48", transData.getField48());
                    break;
                default:
                    break;
            }
        } catch (Iso8583Exception e) {
            e.printStackTrace();
            return TransResult.ERR_PACK;
        }
        return TransResult.SUCC;
    }

    private static final String TAG = PackIso8583.class.getSimpleName();
    public static String devType = "04";

    protected int setBitDataF59(TransData transData) {
        // 得到交易类型
        ETransType transType = ETransType.valueOf(transData.getTransType());
        byte[] f59Temp = new byte[512];
        int f59Length = 0;
        if (transData.getHasPin() && (!transData.getIsReversal())) {// 设置59
            if (transData.getIsSM()) {
                String pinData = transData.getPin();
                String tagA1 = "A1" + String.format("%03d", pinData.length() / 2);
                System.arraycopy(tagA1.getBytes(), 0, f59Temp, 0, tagA1.length());
                System.arraycopy(BankTransManager.convert.strToBcd(pinData, IConvert.EPaddingPosition.PADDING_LEFT), 0,
                        f59Temp, tagA1.length(), pinData.length() / 2);
                f59Length = tagA1.length() + pinData.length() / 2;
            }
        }

        if ((transType == ETransType.SALE || transType == ETransType.AUTH)
                && (!transData.getIsReversal())) {
            String sn = BankTransManager.pedHelper.getSN();
            Log.d(TAG, "sn:" + sn);
            String tag2Value2 = BankTransManager.pedHelper.getTUSN();
            if (TextUtils.isEmpty(tag2Value2)) {
                tag2Value2 = "00000202" + sn;
            }
            Log.d(TAG, "终端系统的银联唯一序列号 :" + tag2Value2);
            String encRdmFactors = "";// 加密随机因子
            String cardNo = transData.getPan();
            encRdmFactors = cardNo.substring(cardNo.length() - 6, cardNo.length());

            String snCiphData = Utils.getEncryptedUniqueCode(tag2Value2, encRdmFactors);// 硬件序列号密文数据
            Log.d(TAG, "snCiphData = " + snCiphData);
            // String ver =
            // String.format("%08s",FinancialApplication.version.length());
            String ver = "320001  ";

            String tagA2T1 = "01" + String.format("%03d", devType.length()) + devType;
            String tagA2T2 = "02" + String.format("%03d", tag2Value2.length()) + tag2Value2;

            String tagA2T3 = "";
            String tagA2T4 = "";
            if (!TextUtils.isEmpty(snCiphData)) {
                tagA2T3 = "03" + String.format("%03d", encRdmFactors.length()) + encRdmFactors;
                tagA2T4 = "04" + String.format("%03d", snCiphData.length()) + snCiphData;
            } else {
                tagA2T2 = "02" + String.format("%03d", sn.length()) + sn;
            }
            String tagA2T5 = "05" + String.format("%03d", ver.length()) + ver;
            String tagA2 = tagA2T1 + tagA2T2 + tagA2T3 + tagA2T4 + tagA2T5;

            Log.v(TAG, " tagA2T1 = " + tagA2T1 + " tagA2T2 = " + tagA2T2 + " tagA2T3 = " + tagA2T3 + " tagA2T4 = "
                    + tagA2T4 + " tagA2T5 = " + tagA2T5);
            tagA2 = "A2" + String.format("%03d", tagA2.length()) + tagA2;
            System.arraycopy(tagA2.getBytes(), 0, f59Temp, f59Length, tagA2.length());
            f59Length += tagA2.length();
        }

        if ((transType == ETransType.SALE || transType == ETransType.AUTH) && (!transData.getIsReversal())) {
            String tagA5 = Utils.getLocationTagA5();
            if (!TextUtils.isEmpty(tagA5)) {
                System.arraycopy(tagA5.getBytes(), 0, f59Temp, f59Length, tagA5.length());
                f59Length += tagA5.length();
            }
        }

        byte[] f59 = new byte[f59Length];
        System.arraycopy(f59Temp, 0, f59, 0, f59Length);
        try {
            if (f59 != null && f59.length > 0)
                entity.setFieldValue("59", f59);
        } catch (Iso8583Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return TransResult.SUCC;
    }


    // 设置field 60
    protected int setBitDataF60(TransData transData) {
        try {
            String temp = "";
            ETransType transType = ETransType.valueOf(transData.getTransType());
            String f60 = transType.getFuncCode(); // f60.1
            f60 += String.format("%06d", transData.getBatchNo()); // f60.2

            f60 += transType.getNetCode();// f60.3

            if (transType == ETransType.SALE || transType == ETransType.VOID
                    || transType == ETransType.REFUND || transType == ETransType.AUTHVOID
                    || transType == ETransType.AUTHCM || transType == ETransType.AUTH_SETTLEMENT
                    || transType == ETransType.AUTHCMVOID || transType == ETransType.AUTH) {
                temp = "60";
                temp += "0";
                f60 += temp;
            } else if (transType == ETransType.QUERY || transType == ETransType.SETTLE_ADJUST
                    || transType == ETransType.SETTLE_ADJUST_TIP) {
                temp = "60";
                f60 += temp;
            } else if (transType == ETransType.BATCH_UP_END) {
                f60 = transData.getField60();
            } else if (transType == ETransType.IC_SCR_SEND) {
                if (transData.getEnterMode() == TransData.EnterMode.INSERT) {
                    f60 += "50";
                } else {
                    f60 += "60";
                }
            }

            entity.setFieldValue("60", f60);

        } catch (Iso8583Exception e) {
            e.printStackTrace();
            return TransResult.ERR_PACK;
        }

        return TransResult.SUCC;

    }

    /**
     * @param enterMode
     * @param hasPin
     * @return
     */
    protected String getInputMethod(int enterMode, boolean hasPin) {
        String inputMethod = "";
        switch (enterMode) {
            case TransData.EnterMode.MANAUL:
                inputMethod = "01";
                break;
            case EnterMode.SWIPE:
                inputMethod = "02";
                break;
            case EnterMode.INSERT:
                inputMethod = "05";
                break;
            case EnterMode.QPBOC:
                inputMethod = "07";
                break;
            case EnterMode.FALLBACK:
                inputMethod = "80";
                break;
            case EnterMode.PHONE:
                inputMethod = "92";
                break;
            case EnterMode.MOBILE:
                inputMethod = "96";
                break;
            case EnterMode.CLSS_PBOC:
                inputMethod = "98";
                break;
            case EnterMode.QR:
                inputMethod = "03"; // 文档上要求04，实际上银联要求03
                break;

            default:
                break;
        }

        if (hasPin) {
            inputMethod += "1";
        } else {
            inputMethod += "2";
        }

        return inputMethod;
    }

    @Override
    public int unpack(TransData transData, byte[] rsp) {
        HashMap<String, byte[]> map = null;
        try {
            map = iso8583.unpack(rsp, true);
            // 调试信息， 日志输入解包后数据
            entity.dump();
        } catch (Exception e) {
            e.printStackTrace();
            return TransResult.ERR_UNPACK;
        }

        // 报文头
        byte[] header = map.get("h");
        // TPDU不解包
        // TPDU检查
        // String rspTpdu = new String(header).substring(0, 10);
        // String reqTpdu = transData.getTpdu();
        // if (!rspTpdu.substring(2, 6).equals(reqTpdu.substring(6, 10))
        // || !rspTpdu.substring(6, 10).equals(reqTpdu.substring(2, 6))) {
        // return TransResult.ERR_UNPACK;
        // }
        transData.setHeader(new String(header).substring(10));

        ETransType transType = ETransType.valueOf(transData.getTransType());
        byte[] buff = null;
        // 检查39域应答码
        if (transType != ETransType.SETTLE) {
            buff = map.get("39");
            if (buff == null) {
                return TransResult.ERR_BAG;
            }
            transData.setResponseCode(new String(buff));

            // 联机返回61下次必须输入密码
//            if ("61".equals(transData.getResponseCode())) {
//                FinancialApplication.sysParam.set(SysParam.FORCE_INPUT_PIN, Constant.YES);
//            } else {
//                FinancialApplication.sysParam.set(SysParam.FORCE_INPUT_PIN, Constant.NO);
//            }
        }

        // 检查返回包的关键域， 包含field4
        boolean isCheckAmt = true;
        if (transType == ETransType.SETTLE || transType == ETransType.QUERY) {
            isCheckAmt = false;
        }
        int ret = checkRecvData(map, transData, isCheckAmt);
        if (ret != TransResult.SUCC) {
            return ret;
        }

        // field 2 主账号

        // field 3 交易处理码
        buff = map.get("3");
        if (buff != null && buff.length > 0) {
            String origField3 = transData.getField3();
            if (origField3 != null && origField3.length() > 0) {
                if (!origField3.equals(new String(buff))) {
                    return TransResult.ERR_PROC_CODE;
                }
            }
        }
        // field 4 交易金额
        buff = map.get("4");
        if (buff != null && buff.length > 0) {
            transData.setAmount(new String(buff));
        }

        // field 11 流水号
        buff = map.get("11");
        if (buff != null && buff.length > 0) {
            transData.setTransNo(Long.parseLong(new String(buff)));
        }

        // field 12 受卡方所在地时间
        buff = map.get("12");
        if (buff != null && buff.length > 0) {
            transData.setTime(new String(buff));
        }
        // field 13 受卡方所在地日期
        // Calendar date = Calendar.getInstance();
        // String yeardate = String.valueOf(date.get(Calendar.YEAR));
        buff = map.get("13");
        if (buff != null) {
            // yeardate = yeardate + new String(buff);
            transData.setDate(new String(buff));
        }
        // field 14 卡有效期
        buff = map.get("14");
        if (buff != null && buff.length > 0) {
            String expDate = new String(buff);
            if (!expDate.equals("0000")) {
                transData.setExpDate(expDate);
            }
        }

        // field 15清算日期
        buff = map.get("15");
        if (buff != null && buff.length > 0) {
            transData.setSettleDate(new String(buff));
        }

        // field 22

        // field 23 卡片序列号
        buff = map.get("23");
        if (buff != null && buff.length > 0) {
            transData.setCardSerialNo(new String(buff));
        }
        // field 25
        // field 26

        // field 32 受理方标识码
        buff = map.get("32");
        if (buff != null && buff.length > 0) {
            transData.setAcqCenterCode(new String(buff));
        }

        // field 35
        // field 36

        // field 37 检索参考号
        buff = map.get("37");
        if (buff != null && buff.length > 0) {
            transData.setRefNo(new String(buff));
        }

        // field 38 授权码
        buff = map.get("38");
        if (buff != null && buff.length > 0) {
            transData.setAuthCode(new String(buff));
        }

        // field 41 校验终端号
        buff = map.get("41");
        if (buff != null && buff.length > 0) {
            transData.setTermID(new String(buff));
        }

        // field 42 校验商户号
        buff = map.get("42");
        if (buff != null && buff.length > 0) {
            transData.setMerchID(new String(buff));
        }

        // field 43

        // field 44
        buff = map.get("44");
        if (buff != null && buff.length > 11) {
            String temp = new String(buff).substring(0, 11).trim();
            transData.setIsserCode(temp);
            if (buff.length > 11) {
                temp = new String(buff).substring(11).trim();
                transData.setAcqCode(temp);
            }
        }
        // field 48
        buff = map.get("48");
        if (buff != null && buff.length > 0) {
            transData.setField48(new String(buff));
        }

        // field 52

        // field 53

        // field 54
        buff = map.get("54");
        if (buff != null && buff.length >= 20) {
            String temp = new String(buff);
            transData.setBalanceFlag(temp.substring(7, 8));
            transData.setBalance(temp.substring(temp.length() - 12, temp.length()));
        }

        // field 55
        buff = map.get("55");
        if (buff != null && buff.length > 0) {
            transData.setRecvIccData(BankTransManager.convert.bcdToStr(buff));
        }
        // field 58
        // filed59域 解tlv
        buff = map.get("59");

        // field 60
        buff = map.get("60");
        if (buff != null && buff.length > 0) {
            transData.setBatchNo(Long.parseLong(new String(buff).substring(2, 8)));
        }
        // field 61
        // field 62
        buff = map.get("62");
        if (buff != null && buff.length > 0) {
            transData.setField62(BankTransManager.convert.bcdToStr(buff));
        }
        // field 63
        buff = map.get("63");
        if (buff != null && buff.length > 0) {
            try {
                // 国际组织代码
                transData.setInterOrgCode(new String(buff).substring(0, 3));
                // 63域附加信息域
                byte[] reserved = new byte[buff.length - 3];
                System.arraycopy(buff, 3, reserved, 0, reserved.length);
                transData.setReserved(new String(reserved, "GBK").trim());

                if (buff.length > 3) {
                    // 发卡行信息
                    int len = buff.length - 3 > 20 ? 20 : buff.length - 3;
                    byte[] issuerResp = new byte[20];
                    System.arraycopy(buff, 3, issuerResp, 0, len);
                    transData.setIssuerResp(new String(issuerResp, "GBK").trim());

                    if (buff.length > 23) {
                        // 中心信息
                        len = buff.length - 23 > 20 ? 20 : buff.length - 23;
                        byte[] centerResp = new byte[20];
                        System.arraycopy(buff, 23, centerResp, 0, len);
                        transData.setCenterResp(new String(centerResp, "GBK").trim());

                        if (buff.length > 43) {
                            // 收单行信息
                            len = buff.length - 43 > 20 ? 20 : buff.length - 43;
                            byte[] recvBankResp = new byte[20];
                            System.arraycopy(buff, 43, recvBankResp, 0, len);
                            transData.setRecvBankResp(new String(recvBankResp, "GBK").trim());
                        }
                    }
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else {
            // 如果内卡不返回,则设置默认值CUP
            transData.setInterOrgCode("CUP");
        }

        // field 64
        // 解包校验mac
        byte[] data = new byte[rsp.length - 11 - 8];
        System.arraycopy(rsp, 11, data, 0, data.length);
        buff = map.get("64");
        if (buff != null && buff.length > 0 && listener != null) {
            byte[] mac = listener.onCalcMac(data);
            if (!BankTransManager.gl.getUtils().isByteArrayValueSame(buff, 0, mac, 0, 8)) {
                return TransResult.ERR_MAC;
            }
        }

        return TransResult.SUCC;
    }

}
