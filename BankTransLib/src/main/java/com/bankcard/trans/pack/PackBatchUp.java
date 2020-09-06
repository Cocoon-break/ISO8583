package com.bankcard.trans.pack;

import com.bankcard.trans.model.TransData;
import com.bankcard.trans.pack.base.PackIso8583;
import com.bankcard.trans.pack.base.TransResult;
import com.bankcard.trans.pack.inter.PackListener;
import com.pax.gl.packer.Iso8583Exception;

/**
 * 批上送
 */
public class PackBatchUp extends PackIso8583 {

    public PackBatchUp(PackListener listener) {
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
            entity.setFieldValue("11", String.valueOf(transData.getTransNo()));
        } catch (Iso8583Exception e) {

            e.printStackTrace();
        }
        setBitDataF48(transData);
        setBitDataF60(transData);
        return pack(false);
    }

}
