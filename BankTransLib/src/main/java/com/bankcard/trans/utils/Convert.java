package com.bankcard.trans.utils;

import java.io.UnsupportedEncodingException;

public class Convert {

    public static long bytesToLong(byte[] bytes, boolean bytesIsHex, int radix) {
        long result = 0;
        try {
            if (bytesIsHex)
                result = Long.parseLong(hexBytesToStr(bytes), radix);
            else
                result = Long.parseLong(bcdBytesToStr(bytes), radix);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return result;
    }
    public static String hexBytesToStr(byte[] bytes) {
        try {
            return new String(bytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }
    public static int bytesToInt(byte[] bytes, boolean bytesIsHex, int radix) {
        return (int) bytesToLong(bytes, bytesIsHex, radix);
    }

    public static byte[] intToBytes(long num) {
        byte[] tmp = new byte[4];
        for (int i = 0; i < 4; i++) {
            tmp[i] = (byte) (num >>> (24 - i * 8));
        }
        return tmp;
    }
    public static String bytes2String(byte[] source) {
        String result = "";

        try {
            if(source.length > 0) {
                result = new String(source, "UTF-8");
            }
        } catch (UnsupportedEncodingException var3) {
            var3.printStackTrace();
        }

        return result;
    }
    public static byte[] string2Bytes(String source) {
        byte[] result = new byte[0];

        try {
            if(source != null) {
                result = source.getBytes("UTF-8");
            }
        } catch (UnsupportedEncodingException var3) {
            var3.printStackTrace();
        }

        return result;
    }
    public static byte[] fillData(int dataLength, byte[] source, int offset) {
        byte[] result = new byte[dataLength];
        if(offset >= 0) {
            System.arraycopy(source, 0, result, offset, source.length);
        }

        return result;
    }
    public static String bcdBytesToStr(byte[] bytes) {
        if (bytes == null)
            return "";

        char c[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (int i = 0; i < bytes.length; i++)
            sb.append(c[(bytes[i] & 0xf0) >>> 4]).append(c[bytes[i] & 0x0f]);

        return sb.toString();
    }

    public static byte[] strToBcdBytes(String str, boolean isPaddingLeft) {
        if (str == null)
            return new byte[0];
        str = str.toUpperCase();
        int mod = str.length() % 2;
        if (mod != 0) {
            if (!isPaddingLeft)
                str = str + "0";
            else
                str = "0" + str;
        }

        int len = str.length() / 2;
        byte[] result = new byte[len];
        char[] achar = str.toCharArray();

        for (int i = 0; i < len; i++) {
            int pos = i * 2;
            result[i] = (byte) (charToByte(achar[pos]) << 4 | charToByte(achar[pos + 1]));
        }
        return result;
    }

    private static byte charToByte(char c) {
        byte b = (byte) "0123456789ABCDEF".indexOf(c);
        return b;
    }
}
