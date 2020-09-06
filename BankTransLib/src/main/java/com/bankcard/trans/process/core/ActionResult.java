package com.bankcard.trans.process.core;

/**
 * 返回值
 */
public class ActionResult {
    /**
     * 返回结果
     */
    int ret;
    /**
     * 返回数据
     */
    Object data;

    public ActionResult(int ret, Object data) {
        this.ret = ret;
        this.data = data;
    }

    public int getRet() {
        return ret;
    }

    public void setRet(int ret) {
        this.ret = ret;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
