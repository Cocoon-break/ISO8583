package com.bankcard.trans.communicate;

public class CommResponse {
    int retCode;
    byte[] data;

    public int getRetCode() {
        return retCode;
    }

    public CommResponse(int retCode, byte[] data) {
        this.retCode = retCode;
        this.data = data;
    }

    public void setRetCode(int retCode) {
        this.retCode = retCode;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
