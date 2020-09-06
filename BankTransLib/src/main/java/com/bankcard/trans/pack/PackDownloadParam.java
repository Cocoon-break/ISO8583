package com.bankcard.trans.pack;

import com.bankcard.trans.model.TransData;
import com.bankcard.trans.pack.base.PackIso8583;
import com.bankcard.trans.pack.inter.PackListener;

/**
 * 参数下载
 */
public class PackDownloadParam extends PackIso8583 {

    public PackDownloadParam(PackListener listener) {
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
