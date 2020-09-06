package com.bankcard.trans.pack;

import com.bankcard.trans.model.TransData;
import com.bankcard.trans.pack.base.PackIso8583;
import com.bankcard.trans.pack.inter.PackListener;

/**
 * Bin下载
 */
public class PackBinDownload extends PackIso8583 {

    public PackBinDownload(PackListener listener) {
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
            String temp = transData.getField62();
            if (temp != null && temp.length() > 0) {
                entity.setFieldValue("62", temp);
            }
            return pack(false);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
