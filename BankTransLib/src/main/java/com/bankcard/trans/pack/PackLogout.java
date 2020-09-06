package com.bankcard.trans.pack;

import android.text.TextUtils;

import com.bankcard.trans.model.TransData;
import com.bankcard.trans.pack.base.PackIso8583;
import com.bankcard.trans.pack.inter.PackListener;

/**
 * 签退
 */
public class PackLogout extends PackIso8583 {

    public PackLogout(PackListener listener) {
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
            temp = String.valueOf(transData.getTransNo());
            if (!TextUtils.isEmpty(temp)) {
                entity.setFieldValue("11", temp);
            }
            setBitDataF60(transData);
            return pack(false);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

}
