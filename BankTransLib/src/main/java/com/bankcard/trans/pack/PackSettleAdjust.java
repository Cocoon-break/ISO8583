package com.bankcard.trans.pack;

import com.bankcard.trans.model.TransData;
import com.bankcard.trans.pack.base.PackIso8583;
import com.bankcard.trans.pack.inter.PackListener;
import com.pax.gl.packer.Iso8583Exception;

public class PackSettleAdjust extends PackIso8583 {

    public PackSettleAdjust(PackListener listener) {
        super(listener);
    }

    @Override
    public byte[] pack(TransData transData) {
        if (transData == null) {
            return null;
        }
        setFinancialData(transData);
        setBitDataF60(transData);
        try {
            // field 37
            String temp = transData.getOrigRefNo();
            if (temp != null && temp.length() > 0) {
                entity.setFieldValue("37", temp);
            }

            // field 38
            temp = transData.getAuthCode();
            if (temp != null && temp.length() > 0) {
                entity.setFieldValue("38", temp);
            }

            // field 48
            temp = transData.getTipAmount();
            if (temp != null && temp.length() > 0) {
                entity.setFieldValue("48", String.format("%012d", Long.parseLong(temp)));
            }

            // field 61
            temp = "";
            // 原批次号
            long origBatchNo = transData.getOrigBatchNo();
            // 原流水号
            long origTransNo = transData.getOrigTransNo();
            // 原交易日期
            String origDate = transData.getOrigDate();
            // 原授权方式
            String authMode = transData.getAuthMode();
            // 原授权机构
            String authInsCode = transData.getAuthInsCode();

            temp = String.format("%06d", origBatchNo);
            temp += String.format("%06d", origTransNo);
            if (origDate != null && origDate.length() == 4) {
                temp += origDate;
            } else {
                temp += "0000";
            }

            if (authMode != null && authMode.length() == 2) {
                temp += authMode;
            } else {
                temp += "00";
            }

            if (authInsCode != null && authInsCode.length() > 0) {
                temp += authInsCode;
            }

            entity.setFieldValue("61", temp);
            entity.setFieldValue("63", transData.getInterOrgCode());

        } catch (Iso8583Exception e) {
            e.printStackTrace();
        }

        return pack(true);
    }
}
