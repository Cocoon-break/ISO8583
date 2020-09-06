package com.bankcard.trans.pack;

import com.bankcard.trans.model.TransData;
import com.bankcard.trans.pack.base.PackIso8583;
import com.bankcard.trans.pack.inter.PackListener;

/**
 * 回响功能
 */
public class PackEcho extends PackIso8583 {

    public PackEcho(PackListener listener) {
        super(listener);
    }

    @Override
    public byte[] pack(TransData transData) {

        try {
            if (transData == null) {
                return null;
            }

            setMandatoryData(transData);
            setBitDataF60(transData);

            return pack(false);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

}
