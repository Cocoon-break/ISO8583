package com.bankcard.trans.pack;

import com.bankcard.trans.BankTransManager;
import com.bankcard.trans.model.TransData;
import com.bankcard.trans.pack.base.PackIso8583;
import com.bankcard.trans.pack.base.TransResult;
import com.bankcard.trans.pack.inter.PackListener;
import com.pax.gl.convert.IConvert.EPaddingPosition;
import com.pax.gl.packer.Iso8583Exception;

public class PackIcTcBat extends PackIso8583 {

    public PackIcTcBat(PackListener listener) {
        super(listener);
    }

    @Override
    public byte[] pack(TransData transData) {

        if (transData == null) {
            return null;
        }
        int ret = setMandatoryData(transData);
        if (ret != TransResult.SUCC) {
            return null;
        }
        try {
            entity.setFieldValue("2", transData.getPan());
            entity.setFieldValue("4", transData.getAmount());
            entity.setFieldValue("11", String.valueOf(transData.getTransNo()));
            entity.setFieldValue("22", getInputMethod(transData.getEnterMode(), transData.getHasPin()));
            entity.setFieldValue("23", transData.getCardSerialNo());
            String temp = transData.getSendIccData();
            if (temp != null) {
                entity.setFieldValue("55", BankTransManager.convert.strToBcd(temp, EPaddingPosition.PADDING_LEFT));
            }
            entity.setFieldValue("60", transData.getField60());
            entity.setFieldValue("62", transData.getField62());
        } catch (Iso8583Exception e) {
            e.printStackTrace();
            return null;
        }

        return pack(false);
    }

}
