package com.bankcard.trans.pack;

import android.text.TextUtils;

import com.bankcard.trans.model.TransData;
import com.bankcard.trans.pack.base.PackIso8583;
import com.bankcard.trans.pack.base.TransResult;
import com.bankcard.trans.pack.inter.PackListener;

public class PackQrRefund extends PackIso8583 {

    public PackQrRefund(PackListener listener) {
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

            temp = transData.getOrigRefNo();
            if (temp != null && temp.length() > 0) {
                entity.setFieldValue("37", temp);
            } else {
                entity.setFieldValue("37", "000000000000");
            }
            int ret = setBitDataF59(transData);
            if (ret != TransResult.SUCC) {
                return null;
            }
            setBitDataF60(transData);

            // field 61 原交易信息
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

            // field 63
            temp = transData.getInterOrgCode();
            if (temp == null || temp.length() == 0) {
                temp = "000";
            }
            entity.setFieldValue("63", temp);
            return pack(true);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
