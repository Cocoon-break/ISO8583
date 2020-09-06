package com.bankcard.trans.utils;

public class Bytes {

    public static byte[] memcpy(byte[] a, int begin, int length) {
        byte[] array = new byte[length];
        System.arraycopy(a, begin, array, 0, length);
        return array;
    }

    public static byte[] mergeBytes(byte[] bytes1, byte[] bytes2) {
        byte[] array = new byte[bytes1.length + bytes2.length];

        System.arraycopy(bytes1, 0, array, 0, bytes1.length);
        System.arraycopy(bytes2, 0, array, bytes1.length, bytes2.length);

        return array;
    }

    public static byte[] fillData(int dataLength, byte[] source, int offset) {
        byte[] result = new byte[dataLength];
        if (offset >= 0)
            System.arraycopy(source, 0, result, offset, source.length);
        return result;
    }

    public static byte[] fillData(int dataLength, byte[] source, int offset, byte fillByte) {
        byte[] result = new byte[dataLength];
        for (int i = 0; i < dataLength; i++) {
            result[i] = fillByte;
        }
        if (offset >= 0)
            System.arraycopy(source, 0, result, offset, source.length);
        return result;
    }

}
