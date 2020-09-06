package com.bankcard.trans.pack;

import com.bankcard.trans.model.TransData;
import com.bankcard.trans.pack.base.PackIso8583;
import com.bankcard.trans.pack.base.TransResult;
import com.bankcard.trans.pack.inter.PackListener;

public class PackQrSale extends PackIso8583 {

    public PackQrSale(PackListener listener) {
        super(listener);
    }

    @Override
    public byte[] pack(TransData transData) {

        if (transData == null) {
            return null;
        }
        setFinancialData(transData);
        // 处理59域数据
        int ret = setBitDataF59(transData);
        if (ret != TransResult.SUCC) {
            return null;
        }
        // 扫码的60域不用处理
        setBitDataF60(transData);

        return pack(true);
    }

}
