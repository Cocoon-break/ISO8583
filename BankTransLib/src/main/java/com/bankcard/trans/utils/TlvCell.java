package com.bankcard.trans.utils;

public class TlvCell {

    private byte[] tag = new byte[0];
    private byte[] value = new byte[0];

    public int setTag(int tag) {
        if (tag == 0)
            return 0;

        byte[] tmpBytes = Convert.intToBytes(tag);
        int offset = 0;
        for (int i = 0; i < tmpBytes.length; i++) {
            if (tmpBytes[i] == 0x00)
                offset++;
        }
        this.tag = new byte[tmpBytes.length - offset];
        System.arraycopy(tmpBytes, offset, this.tag, 0, tmpBytes.length - offset);
        return this.tag.length;
    }

    public int setTag(byte[] tag) {
        if (tag == null)
            return 0;
        this.tag = new byte[tag.length];
        System.arraycopy(tag, 0, this.tag, 0, this.tag.length);
        return tag.length;
    }

    public void setValue(byte[] value) {
        if (value == null)
            return;
        this.value = new byte[value.length];
        System.arraycopy(value, 0, this.value, 0, value.length);
    }

    public byte[] getTag() {
        return tag;
    }

    public byte[] getValue() {
        return value;
    }

}
