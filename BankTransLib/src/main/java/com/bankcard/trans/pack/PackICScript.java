package com.bankcard.trans.pack;

import com.bankcard.trans.BankTransManager;
import com.bankcard.trans.model.ETransType;
import com.bankcard.trans.model.TransData;
import com.bankcard.trans.pack.base.PackIso8583;
import com.bankcard.trans.pack.inter.PackListener;
import com.pax.gl.convert.IConvert;

/**
 * IC卡脚本结果上送
 */
public class PackICScript extends PackIso8583 {

    public PackICScript(PackListener listener) {
        super(listener);
    }

    @Override
    public byte[] pack(TransData transData) {

        try {
            String temp = "";
            if (transData == null) {
                return null;
            }

            ETransType origTransType = ETransType.valueOf(transData.getOrigTransType());

            setMandatoryData(transData);

            int enterMode = transData.getEnterMode();
            // field 2
            temp = transData.getPan();

            // field 22
            entity.setFieldValue("22", getInputMethod(enterMode, transData.getHasPin()));

            if (temp != null && temp.length() > 0) {
                if (enterMode != TransData.EnterMode.SWIPE) {
                    entity.setFieldValue("2", temp);
                }
            }
            temp = origTransType.getProcCode();
            if (temp != null && temp.length() > 0) {
                entity.setFieldValue("3", temp);
            }
            // field 4
            temp = transData.getAmount();
            if (temp != null && temp.length() > 0) {
                entity.setFieldValue("4", temp);
            }

            // field 11
            temp = String.valueOf(transData.getTransNo());
            if (temp != null && temp.length() > 0) {
                entity.setFieldValue("11", temp);
            }

            // field 23
            temp = transData.getCardSerialNo();
            if (temp != null && temp.length() > 0) {
                entity.setFieldValue("23", temp);
            }

            // field 25
            temp = ETransType.valueOf(transData.getOrigTransType()).getServiceCode();
            if (temp != null && temp.length() > 0) {
                entity.setFieldValue("25", temp);
            }

            // field 32
            temp = transData.getAcqCenterCode();
            if (temp != null && temp.length() > 0) {
                entity.setFieldValue("32", temp);
            }
            // field 37
            entity.setFieldValue("37", transData.getOrigRefNo());
            // field 38
            temp = transData.getOrigAuthCode();
            if (temp != null && temp.length() > 0) {
                entity.setFieldValue("38", temp);
            }

            // field 49
            entity.setFieldValue("49", "156");

            // field 55
            temp = transData.getScriptData();
            if (temp != null && temp.length() > 0)
                entity.setFieldValue("55", BankTransManager.convert.strToBcd(temp, IConvert.EPaddingPosition.PADDING_LEFT));

            // field 60
            setBitDataF60(transData);

            // field 61
            String f61 = "";
            // 原交易批次号
            temp = String.format("%06d", transData.getOrigBatchNo());
            f61 += temp;
            // 原POS流水号
            temp = String.format("%06d", transData.getOrigTransNo());
            f61 += temp;
            // 原交易日期
            temp = transData.getOrigDate();
            f61 += temp;
            entity.setFieldValue("61", f61);

            return pack(true);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

}
