package com.bankcard.trans.utils;

import java.util.HashMap;
import java.util.Map;


public class Tlv {

    private static String TAG = "CocoaBean.tools";

    public static byte[] pack(Map<Integer, TlvCell> tlvCellList) {
        byte[] result = new byte[0];
        if (tlvCellList == null)
            return result;

        TlvCell tlvCell;
        byte[] cell;
        for (Integer key : tlvCellList.keySet()) {
            tlvCell = tlvCellList.get(key);
            cell = mergeTlv(tlvCell.getTag(), tlvCell.getValue());
            result = Bytes.mergeBytes(result, cell);
        }

        return result;
    }

    public static Map<Integer, TlvCell> unpack(byte[] data) {
        Map<Integer, TlvCell> tlvCellList = new HashMap<Integer, TlvCell>();
        int offset = 0;

        TlvCell tlvCell;
        while (offset < data.length) {
            tlvCell = new TlvCell();

            // 载入tag
            tlvCell.setTag(getTagFromPackage(data, offset));
            offset += tlvCell.getTag().length;
            // Debug.printBytes("Tag: ", tlvCell.getTag());
            if (offset > data.length) {
                System.out.println("input data error");
                break;
            }

            // 获取长度 0-len的长度, 1-value的长度
            int len[] = getLengthFromPackage(data, offset);
            offset += len[0];
            // Debug.printString("LEN: ", len[1] + "");

            if ((offset + len[1]) > data.length) {
                System.out.println("input data error");
                break;
            }

            // 载入value
            tlvCell.setValue(Bytes.memcpy(data, offset, len[1]));
            offset += len[1];
            // Debug.printBytes("VAL: ", tlvCell.getValue());

            tlvCellList.put(Convert.bytesToInt(tlvCell.getTag(), false, 16), tlvCell);
        }

        return tlvCellList;
    }

    private static byte[] mergeTlv(byte[] tag, byte[] value) {
        byte[] len = getTlvValueLen(value.length);
        byte[] array = new byte[tag.length + len.length + value.length];

        System.arraycopy(tag, 0, array, 0, tag.length);
        System.arraycopy(len, 0, array, tag.length, len.length);
        System.arraycopy(value, 0, array, (tag.length + len.length), value.length);

        return array;
    }

    private static byte[] getTlvValueLen(int valueLength) {
        int offset = 0;
        byte[] lengthTmp = Convert.intToBytes(valueLength);

        for (int i = 0; i < lengthTmp.length; i++) {
            if (lengthTmp[i] == 0x00)
                offset++;
        }
        byte[] result = new byte[lengthTmp.length - offset];
        System.arraycopy(lengthTmp, offset, result, 0, lengthTmp.length - offset);

        return result;
    }

    private static byte[] getTagFromPackage(byte[] data, int offset) {
        byte[] result;
        int len = 0;

        if (data[offset] == 0x00)
            return new byte[0];

        if ((data[offset] & 0x1f) == 0x1f) {
            if ((data[offset + 1] & 0x80) == 0x80)
                len = 3;
            else
                len = 2;
        } else
            len = 1;

        result = new byte[len];
        System.arraycopy(data, offset, result, 0, len);
        return result;
    }

    private static int[] getLengthFromPackage(byte[] data, int offset) {
        int[] result = new int[2];
        if ((data[offset] & 0x80) == 0) {
            result[0] = 1;
            result[1] = data[offset];
        } else {
            int length = data[offset] & 0x7f;
            offset++;
            int len = 0;
            int i = 0;
            while (i < length) {
                len <<= 8;
                len += (data[offset] & 0xff);

                i++;
                offset++;
            }

            result[0] = length + 1;
            result[1] = len;
        }
        return result;
    }

}
