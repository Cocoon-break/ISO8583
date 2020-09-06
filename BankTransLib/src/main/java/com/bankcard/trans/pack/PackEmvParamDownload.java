package com.bankcard.trans.pack;

import com.bankcard.trans.BankTransManager;
import com.bankcard.trans.model.TransData;
import com.bankcard.trans.pack.base.PackIso8583;
import com.bankcard.trans.pack.inter.PackListener;
import com.pax.gl.convert.IConvert.EPaddingPosition;

/**
 * IC卡公钥下载状态上送
 */
public class PackEmvParamDownload extends PackIso8583 {

    public PackEmvParamDownload(PackListener listener) {
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
            setBitDataF60(transData);

            temp = transData.getField62();
            if (temp != null && temp.length() > 0) {
                entity.setFieldValue("62", BankTransManager.convert.strToBcd(temp, EPaddingPosition.PADDING_LEFT));
            }

            return pack(false);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
