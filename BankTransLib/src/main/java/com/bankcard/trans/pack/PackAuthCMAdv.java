package com.bankcard.trans.pack;

import android.text.TextUtils;

import com.bankcard.trans.model.TransData;
import com.bankcard.trans.pack.base.PackIso8583;
import com.bankcard.trans.pack.inter.PackListener;

public class PackAuthCMAdv extends PackIso8583 {

    public PackAuthCMAdv(PackListener listener) {
        super(listener);
    }

    @Override
    public byte[] pack(TransData transData) {

        try {
            String temp = "";
            if (transData == null) {
                return null;
            }

            setFinancialData(transData);

            // field 38 同原交易
            temp = transData.getOrigAuthCode();
            if (temp != null && temp.length() > 0) {
                entity.setFieldValue("38", temp);
            }

            setBitDataF60(transData);
            // field 61
            String f61 = "";
            temp = String.format("%06d", transData.getOrigBatchNo());
            if (!TextUtils.isEmpty(temp)) {
                f61 += temp;
            } else {
                f61 += "000000";
            }
            temp = String.format("%06d", transData.getOrigTransNo());
            if (!TextUtils.isEmpty(temp)) {
                f61 += temp;
            } else {
                f61 += "000000";
            }
            temp = transData.getOrigDate();
            if (!TextUtils.isEmpty(temp)) {
                f61 += temp;
            } else {
                f61 += "0000";
            }
            entity.setFieldValue("61", f61);

            return pack(true);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

}