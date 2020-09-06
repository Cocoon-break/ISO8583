package com.bankcard.trans.pack;

import android.text.TextUtils;

import com.bankcard.trans.model.TransData;
import com.bankcard.trans.pack.base.PackIso8583;
import com.bankcard.trans.pack.inter.PackListener;
import com.pax.gl.packer.Iso8583Exception;

public class PackSettle extends PackIso8583 {

    public PackSettle(PackListener listener) {
        super(listener);
    }

    @Override
    public byte[] pack(TransData transData) {

        if (transData == null) {
            return null;
        }
        setMandatoryData(transData);

        try {
            // field 11
            String temp = String.valueOf(transData.getTransNo());
            if (!TextUtils.isEmpty(temp)) {
                entity.setFieldValue("11", temp);
            }

            // field 48
            setBitDataF48(transData);

            // field 49
            entity.setFieldValue("49", "156");

            setBitDataF60(transData);

            temp = transData.getOper();
            if (temp == null || temp.length() == 0) {
                temp = "01";
            }
            String f63 = temp + " ";
            entity.setFieldValue("63", f63);
        } catch (Iso8583Exception e) {

            e.printStackTrace();
        }

        return pack(false);
    }
}
