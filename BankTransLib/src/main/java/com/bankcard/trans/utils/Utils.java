package com.bankcard.trans.utils;

import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.bankcard.trans.BankTransManager;
import com.bankcard.trans.device.Device;

/**
 * Created by SuQi on 2020/7/18.
 * Describe:
 */
public class Utils {

    /**
     * 获取硬件上送序列号
     *
     * @param uniqueCode
     * @param randomFactor
     * @return
     */
    public static String getEncryptedUniqueCode(String uniqueCode, String randomFactor) {
        String result = "";
        try {
            String data = uniqueCode + randomFactor;
            byte[] xor1 = formatData(data);
            byte[] lowXor1 = new byte[16];
            byte[] highXor1 = new byte[16];
            System.arraycopy(xor1, 0, lowXor1, 0, 16);
            System.arraycopy(xor1, 16, highXor1, 0, 16);
            byte[] retSoft1 =BankTransManager.pedHelper.idKeyCalc(xor1);
            byte[] xor2 = new byte[16];
            for (int j = 0; j < 16; j++) {
                xor2[j] = (byte) (highXor1[j] ^ retSoft1[j]);
            }
            byte[] retSoft2 = BankTransManager.pedHelper.idKeyCalc(xor2);
            result = BankTransManager.convert.bcdToStr(retSoft2).substring(0, 8);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        return result;
    }

    private static byte[] formatData(String datain) {
        byte[] data;
        int mod = datain.length() / 16;
        if (datain.length() % 16 != 0) {
            mod += 1;
            data = new byte[datain.length() + 16 - datain.length() % 16];
        } else {
            data = new byte[datain.length()];
        }
        byte[] temp = datain.getBytes();
        System.arraycopy(temp, 0, data, 0, datain.length());

        byte[] buf = new byte[16];
        for (int i = 0; i < mod; i++) {
            for (int k = 0; k < 16; k++) {
                buf[k] = (byte) (buf[k] ^ data[i * 16 + k]);
            }
        }
        return BankTransManager.convert.bcdToStr(buf).getBytes();
    }

    public static String getLocationTagA5() {
        String tagA5T1 = "";// 经度
        String tagA5T2 = "";// 纬度
        String tagA5T5 = "";// 坐标系

        String tagA5T3 = "";// MCC
        String tagA5T4 = "";// MNC

        // 1, CDMA 电信
        String tagA5T15 = "";// SID
        String tagA5T16 = "";// NID
        String tagA5T17 = "";// BID
        String tagA5T18 = "";// SIG

        // 2, GSM 移动，联通
        String tagA5T6 = "";// LAC
        String tagA5T7 = "";// CID
        String tagA5T8 = "";// SIG

        // MNC（00、02、04、07-移动 01、06、09-联通 03、05、11-电信）

        StringBuilder sb = new StringBuilder();
        // 1,GPS/WIFI定位
        // 定位信息 消费和预授权需要上送tagA5 必填信息: 经度 纬度 坐标系 (高德地图返回的是 GCJ02)
//        if (FinancialApplication.locationInfo != null) {
//            String longitude = "0";
//            String latitude = "0";
//            String type = "GCJ02";
//            // 经度
//            double longitudeDouble = FinancialApplication.locationInfo.getLongitude();
//            longitude = String.valueOf(longitudeDouble);
//            if (longitudeDouble >= 0) {
//                longitude = "+" + longitude;
//            }
//            if (longitude.length() > 10) {
//                longitude = longitude.substring(0, 10);
//            }
//            // 纬度
//            double latitudeDouble = FinancialApplication.locationInfo.getLatitude();
//            latitude = String.valueOf(latitudeDouble);
//
//            if (latitudeDouble >= 0) {
//                latitude = "+" + latitude;
//            }
//            if (latitude.length() > 10) {
//                latitude = latitude.substring(0, 10);
//            }
//
//            tagA5T1 = getTlv(sb, "01", longitude);
//            tagA5T2 = getTlv(sb, "02", latitude);
//            tagA5T5 = getTlv(sb, "05", type);
//        }
//
//        // 2,基站信息 定位和基站信息都有就都上送
//        BaseStationInfo baseStationInfo = FinancialApplication.baseStationInfo;
//        if (baseStationInfo != null) {
//
//            // 共有 MCC MNC SIG
//            String mcc = baseStationInfo.getMcc() + "";
//            String mnc = String.format("%02d", baseStationInfo.getMnc());
//            String sig = baseStationInfo.getSig();
//
//            tagA5T3 = getTlv(sb, "03", mcc);
//            tagA5T4 = getTlv(sb, "04", mnc);
//
//            if (baseStationInfo.getPhoneType() == TelephonyManager.PHONE_TYPE_CDMA) {
//                // 1，CDMA 必选 NID BID SID
//                String sid = baseStationInfo.getSID();
//                String nid = baseStationInfo.getNID();
//                String bid = baseStationInfo.getBID();
//
//                tagA5T15 = getTlv(sb, "15", sid);// SID
//                tagA5T16 = getTlv(sb, "16", nid);// NID
//                tagA5T17 = getTlv(sb, "17", bid);// BID
//                tagA5T18 = getTlv(sb, "18", sig);// SIG
//
//            } else {
//                // 2，GSM 必选LAC CID
//
//                String lac = baseStationInfo.getLac();
//                String cid = baseStationInfo.getCid();
//
//                tagA5T6 = getTlv(sb, "06", lac);// LAC
//                tagA5T7 = getTlv(sb, "07", cid);// CID
//                tagA5T8 = getTlv(sb, "08", sig);// SIG
//            }
//
//        }
//
//        // A5 value整合
//        sb.delete(0, sb.length());
//        sb.append(tagA5T1);
//        sb.append(tagA5T2);
//        sb.append(tagA5T3);
//        sb.append(tagA5T4);
//        sb.append(tagA5T5);
//        sb.append(tagA5T6);
//        sb.append(tagA5T7);
//        sb.append(tagA5T8);
//        sb.append(tagA5T15);
//        sb.append(tagA5T16);
//        sb.append(tagA5T17);
//        sb.append(tagA5T18);
//        String tagA5 = sb.toString();
//
//        if (!TextUtils.isEmpty(tagA5)) {
//            // A5 TLV
//            tagA5 = getTlv(sb, "A5", tagA5);
//
//        }
//        return tagA5;
        return null;
    }

    private static String getTlv(StringBuilder sb, String tag, String value) {
        sb.delete(0, sb.length());
        sb.append(tag);
        sb.append(String.format("%03d", value.length()));
        sb.append(value);
        return sb.toString();
    }
}
