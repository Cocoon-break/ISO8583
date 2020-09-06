/*
 * ===========================================================================================
 * = COPYRIGHT
 *          PAX Computer Technology(Shenzhen) CO., LTD PROPRIETARY INFORMATION
 *   This software is supplied under the terms of a license agreement or nondisclosure
 *   agreement with PAX Computer Technology(Shenzhen) CO., LTD and may not be copied or
 *   disclosed except in accordance with the terms in that agreement.
 *     Copyright (C) YYYY-? PAX Computer Technology(Shenzhen) CO., LTD All rights reserved.
 * Description: // Detail description about the function of this module,
 *             // interfaces with the other modules, and dependencies.
 * Revision History:
 * Date	                 Author	                Action
 * 2020/01/18  	         Alex           	    Create
 * ===========================================================================================
 */

package com.bankcard.trans.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class Tool {
    /**
     * bcd to string.
     * 0x01 0x02->"0102"
     *
     * @param bcdByteArray
     * @return
     */
    public static String bcd2Str(final byte[] bcdByteArray) {
        return bcd2Str(bcdByteArray, 0, bcdByteArray.length);
    }

    /**
     * bcd to string.
     * 0x01 0x02->"0102"
     *
     * @param bcdByteArray
     * @param offset
     * @param len
     * @return
     */
    public static String bcd2Str(final byte[] bcdByteArray, final int offset, final int len) {
        final char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        StringBuilder strBuilder = new StringBuilder(len * 2);
        for (int i = 0; i < len; ++i) {
            strBuilder.append(hexDigits[(bcdByteArray[i + offset] & 0xf0) >>> 4]);
            strBuilder.append(hexDigits[bcdByteArray[i + offset] & 0x0f]);
        }
        return strBuilder.toString();
    }

    /**
     * string to bcd.
     * "0102"->0x01 0x02
     *
     * @param asc
     * @return
     */
    public static byte[] str2Bcd(final String asc) {
        String tmpStr = asc;
        if (tmpStr.length() % 2 != 0) {
            tmpStr = "0" + asc;
        }
        int len = tmpStr.length() / 2;
        byte[] bbt = new byte[len];
        for (int p = 0; p < len; ++p) {
            bbt[p] = (byte) ((getCharValue(tmpStr.getBytes()[2 * p]) << 4) +
                    getCharValue(tmpStr.getBytes()[2 * p + 1]));
        }

        return bbt;
    }

    /**
     * Convert hex string to byte[]
     *
     * @param hexString the hex string
     * @return byte[]
     */
    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    /**
     * Convert byte array to string, stop if \0 found.
     *
     * @param buf
     * @return string
     */
    public static String byteArray2String(byte[] buf) {
        return byteArray2String(buf, 0, buf.length);
    }

    /**
     * @param buf
     * @param offset
     * @param len
     * @return
     */
    public static String byteArray2String(byte[] buf, int offset, int len) {
        int count = len;
        for (int i = 0; i < len; ++i) {
            if (buf[offset + i] == 0x00) {
                count = i;
                break;
            }
        }
        return new String(buf, offset, count);
    }

    /**
     * byte array to string
     *
     * @param data   byte arrray
     * @param offset convert start offset
     * @param len    convert length
     * @return string
     */
    public static String bytesToStr(byte[] data, int offset, int len) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) {
            sb.append(String.format("%c", data[offset + i]));
        }
        return sb.toString();
    }

    /**
     * Call unit method
     *
     * @param classObject   the object from XXX.class(XXX is class name)
     * @param classInstance the instance object from the class's construct function.
     * @param methodName    method name.
     */
    public static void callUnitMethod(Class classObject, Object classInstance, String methodName) {
        Method[] methods = classObject.getMethods();
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                try {
                    method.invoke(classInstance);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
                return;
            }
        }

    }

    private static int getCharValue(byte srcByte) {
        int ret;
        if (srcByte >= 'a' && srcByte <= 'z') {
            ret = srcByte - 'a' + 10;
        } else if (srcByte >= 'A' && srcByte <= 'Z') {
            ret = srcByte - 'A' + 10;
        } else {
            ret = srcByte - '0';
        }
        return ret;
    }

    /**
     * Convert char to byte
     *
     * @param c char
     * @return byte
     */
    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    public static boolean memcmp(byte[] buf1, int start1, byte[] buf2, int start2, int ascLen) {
        int i;
        for (i = 0; i < ascLen && i < (buf1.length - start1) && i < (buf2.length - start2); i++) {
            if (buf1[i + start1] < buf2[i + start2]) {
                return false;
            } else if (buf1[i + start1] > buf2[i + start2]) {
                return false;
            }
        }

        if ((i == ascLen) || (buf1.length == buf2.length)) {
            return true;
        } else if (buf1.length > buf2.length) {
            return false;
        } else {
            return false;
        }
    }
}
