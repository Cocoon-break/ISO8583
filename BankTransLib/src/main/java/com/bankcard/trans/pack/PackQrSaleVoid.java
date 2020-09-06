package com.bankcard.trans.pack;

import com.bankcard.trans.model.TransData;
import com.bankcard.trans.pack.base.PackIso8583;
import com.bankcard.trans.pack.base.TransResult;
import com.bankcard.trans.pack.inter.PackListener;

public class PackQrSaleVoid extends PackIso8583 {
    public PackQrSaleVoid(PackListener listener) {
        super(listener);
    }

    @Override
    public byte[] pack(TransData transData) {
        try {
            if (transData == null) {
                return null;
            }
            setVoidCommonData(transData);
            int ret = setBitDataF59(transData);
            if (ret != TransResult.SUCC) {
                return null;
            }
            setBitDataF60(transData);
            return pack(true);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
