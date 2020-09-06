package com.bankcard.trans.communicate;


public abstract class ACommunicate {
    /**
     * 建立连接
     *
     * @return
     */
    public abstract int onConnect();

    /**
     * 发送数据
     *
     * @param data
     * @return
     */
    public abstract int onSend(byte[] data);

    /**
     * 接收数据
     *
     * @return
     */
    public abstract CommResponse onRecv();

    /**
     * 关闭连接
     */
    public abstract void onClose();

}
