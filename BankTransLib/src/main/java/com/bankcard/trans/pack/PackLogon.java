package com.bankcard.trans.pack;


import com.bankcard.trans.BankTransManager;
import com.bankcard.trans.device.Device;
import com.bankcard.trans.model.TransData;
import com.bankcard.trans.pack.base.PackIso8583;
import com.bankcard.trans.pack.inter.PackListener;

/**
 * 签到
 */
public class PackLogon extends PackIso8583 {

    public PackLogon(PackListener listener) {
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
            temp = String.valueOf(transData.getTransNo());
            if (temp != null && temp.length() > 0) {
                entity.setFieldValue("11", temp);
            }

            setBitDataF60(transData);
            String sn = BankTransManager.pedHelper.getSN();
            if (sn != null) {
                String len = String.format("%02d", 4 + sn.length());
                // 1234为随便写的认证编号，以前每款设备过认证时银联会分配一个
                entity.setFieldValue("62", "Sequence No" + len + "1234" + sn);
            }

            temp = transData.getOper();
            if (temp == null || temp.length() == 0) {
                temp = "01";
            }
            String f63 = temp + " ";
            entity.setFieldValue("63", f63);
            return pack(false);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

}
