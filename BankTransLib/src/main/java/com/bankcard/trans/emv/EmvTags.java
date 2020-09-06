package com.bankcard.trans.emv;

import android.text.TextUtils;

import com.bankcard.trans.BankTransManager;
import com.bankcard.trans.model.ETransType;
import com.bankcard.trans.utils.Convert;
import com.bankcard.trans.utils.Tlv;
import com.bankcard.trans.utils.TlvCell;
import com.pax.gl.packer.ITlv;
import com.pax.gl.packer.TlvException;

import java.util.Map;

public class EmvTags {

    /**
     * 消费55域EMV标签
     */
    public static final int[] TAGS_SALE = {0x9F26, 0x9F27, 0x9F10, 0x9F37, 0x9F36, 0x95, 0x9A, 0x9C, 0x9F02, 0x5F2A,
            0x82, 0x9F1A, 0x9F03, 0x9F33, 0x9F34, 0x9F35, 0x9F1E, 0x84, 0x9F09, 0x9F41, 0x9F63};
    /**
     * 查余额55域EMV标签
     */
    public static final int[] TAGS_QUE = {0x9F26, 0x9F27, 0x9F10, 0x9F37, 0x9F36, 0x95, 0x9A, 0x9C, 0x9F02, 0x5F2A,
            0x82, 0x9F1A, 0x9F03, 0x9F33, 0x9F34, 0x9F35, 0x9F1E, 0x84, 0x9F09, 0x9F41, 0x9F63};
    /**
     * 脱机消费（PBOC）55域EMV标签
     */
    public static final int[] TAGS_PBOC_OFFLINE = {0x9F26, 0x9F27, 0x9F10, 0x9F37, 0x9F36, 0x95, 0x9A, 0x9C, 0x9F02,
            0x5F2A, 0x82, 0x9F1A, 0x9F03, 0x9F33, 0x9F1E, 0x84, 0x9F09, 0x9F41, 0x9F34, 0x9F35, 0x9F63, 0x8A};
    /**
     * 脱机消费（EC）55域EMV标签
     */
    public static final int[] TAGS_EC_OFFLINE = {0x9F26, 0x9F27, 0x9F10, 0x9F37, 0x9F36, 0x95, 0x9A, 0x9C, 0x9F02,
            0x5F2A, 0x82, 0x9F1A, 0x9F03, 0x9F33, 0x9F1E, 0x84, 0x9F09, 0x9F41, 0x9F34, 0x9F35, 0x9F63, 0x9F74, 0x8A};
    /**
     * 预授权55域EMV标签
     */
    public static final int[] TAGS_AUTH = {0x9F26, 0x9F27, 0x9F10, 0x9F37, 0x9F36, 0x95, 0x9A, 0x9C, 0x9F02, 0x5F2A,
            0x82, 0x9F1A, 0x9F03, 0x9F33, 0x9F34, 0x9F35, 0x9F1E, 0x84, 0x9F09, 0x9F41, 0x9F63};

    /**
     * 冲正
     */
    public static final int[] TAGS_DUP = {0x95, 0x9F10, 0x9F1E, 0xDF31};

    /**
     * 交易承兑但卡片拒绝时发起的冲正
     */
    public static final int[] TAGS_POSACCPDUP = {0x95, 0x9F10, 0x9F1E, 0x9F36, 0xDF31};

    /**
     * 脚本结果上送
     */
    public static final int[] TAGS_SCRIPT = {0x9F33, 0x95, 0x9F37, 0x9F1E, 0x9F10, 0x9F26, 0x9F36, 0x82, 0xDF31,
            0x9F1A, 0x9A};

    /**
     * 指定账户圈存55域EMV标签
     */
    public static final int[] TAGS_ECLOAD = {0x9F26, 0x9F27, 0x9F10, 0x9F37, 0x9F36, 0x95, 0x9A, 0x9C, 0x9F02, 0x5F2A,
            0x82, 0x9F1A, 0x9F03, 0x9F33, 0x9F34, 0x9F35, 0x9F1E, 0x84, 0x9F09, 0x9F41, 0x9F63};
    /**
     * 指定账户圈存冲正
     */
    public static final int[] TAGS_ECLOAD_REV = {0x95, 0x9F1E, 0x9F10, 0x9F36, 0xDF31};

    public static final int[] TAGS_NECLOAD = {0x9F26, 0x9F27, 0x9F10, 0x9F37, 0x9F36, 0x95, 0x9A, 0x9C, 0x9F02,
            0x5F2A, 0x82, 0x9F1A, 0x9F03, 0x9F33, 0x9F34, 0x9F35, 0x9F1E, 0x84, 0x9F09, 0x9F41, 0x9F63};

    /**
     * 电子现金现金充值55域EMV标签
     */
    public static final int[] TAGS_EC_CASH_LOAD = {0x9F26, 0x9F27, 0x9F10, 0x9F37, 0x9F36, 0x95, 0x9A, 0x9C, 0x9F02,
            0x5F2A, 0x82, 0x9F1A, 0x9F03, 0x9F33, 0x9F34, 0x9F35, 0x9F1E, 0x84, 0x9F09, 0x9F41, 0x9F63};
    /**
     * 电子现金现金充值撤销55域EMV标签
     */
    public static final int[] TAGS_EC_CASH_LOAD_VOID = {0x9F1A, 0x9F03, 0x9F33, 0x9F34, 0x9F35, 0x9F1E, 0x84, 0x9F09,
            0x9F41, 0x9F63, 0x9F26, 0x9F27, 0x9F10, 0x9F37, 0x9F36, 0x95, 0x9A, 0x9C, 0x9F02, 0x5F2A, 0x82};

    private EmvTags() {
    }

    /**
     * 根据交易类型获取55域TLV数据
     *
     * @param transType
     * @param isDup
     * @return
     */
    public static byte[] getF55(ETransType transType, Map<Integer, TlvCell> map, boolean isDup) {
        switch (transType) {
            case SALE:
                if (isDup) {
                    return getValueList(TAGS_DUP, map);
                }
                return getValueList(TAGS_SALE, map);
            case QUERY:
                return getValueList(TAGS_QUE, map);
            case AUTH:
                if (isDup) {
                    return getValueList(TAGS_DUP, map);
                }
                return getValueList(TAGS_AUTH, map);
            case IC_SCR_SEND:
                return getValueList(TAGS_SCRIPT, map);
            default:
                break;
        }
        return null;
    }

    private static byte[] getValueList(int[] tags, Map<Integer, TlvCell> map) {
        if (tags == null || tags.length == 0) {
            return null;
        }

        ITlv tlv = BankTransManager.packer.getTlv();
        ITlv.ITlvDataObjList tlvList = tlv.createTlvDataObjectList();
        for (int tag : tags) {
            try {
                byte[] value = getTlv(map, tag);
                if (value == null || value.length == 0) {
                    if (tag == 0x9f03) {
                        value = new byte[6];
                    } else {
                        continue;
                    }
                }
                ITlv.ITlvDataObj obj = tlv.createTlvDataObject();
                obj.setTag(tag);
                obj.setValue(value);
                tlvList.addDataObj(obj);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
        }

        try {
            return tlv.pack(tlvList);
        } catch (TlvException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static byte[] getTlv(String filed55, int tag) {
        if (TextUtils.isEmpty(filed55)) {
            return null;
        }
        Map map = Tlv.unpack(Convert.strToBcdBytes(filed55, true));
        TlvCell c1 = (TlvCell) map.get(tag);
        return c1.getValue();
    }

    private static byte[] getTlv(Map<Integer, TlvCell> map, int tag) {
        TlvCell c1 = map.get(tag);
        if (c1 != null) {
            return c1.getValue();
        }
        return null;
    }

}
