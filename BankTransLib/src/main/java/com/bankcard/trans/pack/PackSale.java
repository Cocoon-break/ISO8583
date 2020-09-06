package com.bankcard.trans.pack;

import com.bankcard.trans.model.TransData;
import com.bankcard.trans.pack.base.PackIso8583;
import com.bankcard.trans.pack.inter.PackListener;

public class PackSale extends PackIso8583 {

    public PackSale(PackListener listener) {
        super(listener);
    }

    @Override
    public byte[] pack(TransData transData) {

        if (transData == null) {
            return null;
        }
        setFinancialData(transData);
        setBitDataF60(transData);
        return pack(true);
    }

}
