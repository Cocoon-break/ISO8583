package com.bankcard.trans.pack;

import android.text.TextUtils;

import com.bankcard.trans.BankTransManager;
import com.bankcard.trans.model.TransData;
import com.bankcard.trans.pack.base.PackIso8583;
import com.bankcard.trans.pack.inter.PackListener;
import com.pax.gl.convert.IConvert;

public class PackSignatureUpload extends PackIso8583 {

    public PackSignatureUpload(PackListener listener) {
        super(listener);
    }

    @Override
    public byte[] pack(TransData transData) {

        try {
            String temp = "";
            if (transData == null) {
                return null;
            }

            setMandatoryData(transData);

            // field 2
            temp = transData.getPan();
            if (temp != null && temp.length() > 0) {
                entity.setFieldValue("2", temp);
            }
            // field 4
            temp = transData.getAmount();
            if (temp != null && temp.length() > 0) {
                entity.setFieldValue("4", temp);
            }

            // field 11 流水号
            temp = String.valueOf(transData.getTransNo());
            if (!TextUtils.isEmpty(temp)) {
                entity.setFieldValue("11", temp);
            }

            // field 15 清算日期
            temp = transData.getSettleDate();
            if (temp != null && temp.length() > 0) {
                entity.setFieldValue("15", temp);
            }

            // field 37 检索参考号
            temp = transData.getRefNo();
            if (temp != null && temp.length() > 0) {
                entity.setFieldValue("37", temp);
            }

            // field 55
            temp = transData.getReceiptElements();
            if (temp != null && temp.length() > 0) {
                entity.setFieldValue("55", BankTransManager.convert.strToBcd(temp, IConvert.EPaddingPosition.PADDING_LEFT));
            }
            // field 60
            setBitDataF60(transData);

            // field 62
            byte[] sign = transData.getSignData();
            if (sign != null) {
                entity.setFieldValue("62", sign);
            }

            return pack(true);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
