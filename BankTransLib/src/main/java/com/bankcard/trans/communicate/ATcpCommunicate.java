package com.bankcard.trans.communicate;

import com.bankcard.trans.BankTransManager;
import com.bankcard.trans.pack.base.TransResult;
import com.bankcard.trans.settings.SysParam;
import com.pax.gl.comm.IComm;

public abstract class ATcpCommunicate extends ACommunicate {

    protected IComm client;
    protected String hostIp;
    protected int hostPort;

    /**
     * 设置TCP通讯的相关参数
     *
     * @return
     */
    public int setTcpCommParam() {
        return TransResult.SUCC;
    }

    /**
     * 获取主机地址
     *
     * @return
     */
    protected String getMainHostIp() {
        return BankTransManager.controller.getValue(SysParam.HOST_IP);
    }

    /**
     * 获取主机端口
     *
     * @return
     */
    protected int getMainHostPort() {
        String hostPort = BankTransManager.controller.getValue(SysParam.HOST_PORT);
        if (hostPort == null || hostPort.length() == 0) {
            hostPort = "0";
        }
        return Integer.parseInt(hostPort);
    }

}
