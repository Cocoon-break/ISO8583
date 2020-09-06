package com.bankcard.trans.pack;

import com.bankcard.trans.model.TransData;
import com.bankcard.trans.pack.base.PackIso8583;
import com.bankcard.trans.pack.inter.PackListener;

public class PackAuthCMVoid extends PackIso8583 {

    public PackAuthCMVoid(PackListener listener) {
        super(listener);
    }

    @Override
    public byte[] pack(TransData transData) {

        try {

            if (transData == null) {
                return null;
            }

            setVoidCommonData(transData);
            setBitDataF60(transData);
            return pack(true);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

}
