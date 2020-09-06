package com.bankcard.trans.pack.inter;

import com.bankcard.trans.BankTransManager;
import com.bankcard.trans.settings.SysParam;
import com.pax.gl.convert.IConvert;

import java.util.Arrays;

public class PackListenerImpl implements PackListener {

    public PackListenerImpl() {
    }

    @Override
    public byte[] onCalcMac(byte[] data) {
        return BankTransManager.pedHelper.calcMac(data);
    }

    @Override
    public byte[] onEncTrack(byte[] track) {
        byte[] block = null;
        String trackStr = new String();
        int len = track.length;
        int isDouble = 0;
        if (len % 2 > 0) {
            isDouble = 1;
            trackStr = new String(track) + "0";
        } else {
            trackStr = new String(track);
        }
        boolean isSM4 = BankTransManager.controller.getBoolean(SysParam.IS_SM4);
        byte[] trackData = new byte[8];
        if (isSM4) {
            trackData = new byte[16];
        }
        Arrays.fill(trackData, (byte) 0xff);
        byte[] bTrack = BankTransManager.convert.strToBcd(trackStr, IConvert.EPaddingPosition.PADDING_LEFT);
        if (bTrack.length - 1 < trackData.length) {
            System.arraycopy(bTrack, 0, trackData, 0, bTrack.length - 1);
        } else {
            System.arraycopy(bTrack, bTrack.length - trackData.length - 1, trackData, 0, trackData.length);
        }
        block = BankTransManager.pedHelper.calcDes(trackData);

        if (bTrack.length - 1 < trackData.length) {
            byte[] data = new byte[trackData.length + 1];
            System.arraycopy(block, 0, data, 0, trackData.length);
            System.arraycopy(bTrack, bTrack.length - 1, data, trackData.length, 1);

            if (isDouble == 1) {
                return BankTransManager.convert.bcdToStr(data).substring(0, BankTransManager.convert.bcdToStr(data).length() - 1).getBytes();
            }
            return BankTransManager.convert.bcdToStr(data).getBytes();
        } else {
            System.arraycopy(block, 0, bTrack, bTrack.length - block.length - 1, block.length);
            return BankTransManager.convert.bcdToStr(bTrack).substring(0, len).getBytes();
        }
    }
}
