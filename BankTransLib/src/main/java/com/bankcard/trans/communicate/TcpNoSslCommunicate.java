package com.bankcard.trans.communicate;

import com.bankcard.trans.BankTransManager;
import com.bankcard.trans.pack.base.TransResult;
import com.bankcard.trans.settings.SysParam;
import com.pax.gl.comm.CommException;
import com.pax.gl.comm.ICommHelper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class TcpNoSslCommunicate extends ATcpCommunicate {

    @Override
    public int onConnect() {
        int ret = setTcpCommParam();
        if (ret != TransResult.SUCC) {
            return ret;
        }
        int timeout = Integer.parseInt(BankTransManager.controller.getValue(SysParam.COMM_TIMEOUT)) * 1000;
        // 启用主通讯地址
        ret = TransResult.ERR_CONNECT;
        hostIp = getMainHostIp();
        hostPort = getMainHostPort();
        ret = connectNoSLL(hostIp, hostPort, timeout);
        if (ret != TransResult.ERR_CONNECT) {
            return ret;
        }
        ret = connectNoSLL(hostIp, hostPort, timeout);
        return ret;
    }

    @Override
    public int onSend(byte[] data) {
        try {
            client.send(data);
            return TransResult.SUCC;
        } catch (CommException e) {

            e.printStackTrace();
        }
        return TransResult.ERR_SEND;
    }

    @Override
    public CommResponse onRecv() {
        try {
            byte[] lenBuf = client.recv(2);
            if (lenBuf == null || lenBuf.length != 2) {
                return new CommResponse(TransResult.ERR_RECV, null);
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int len = (((lenBuf[0] << 8) & 0xff00) | (lenBuf[1] & 0xff));
            byte[] rsp = client.recv(len);
            if (rsp == null || rsp.length != len) {
                return new CommResponse(TransResult.ERR_RECV, null);
            }
            baos.write(rsp);
            rsp = baos.toByteArray();
            return new CommResponse(TransResult.SUCC, rsp);
        } catch (IOException e) {

            e.printStackTrace();
        } catch (CommException e) {

            e.printStackTrace();
        }

        return new CommResponse(TransResult.ERR_RECV, null);
    }

    @Override
    public void onClose() {
        try {
            client.disconnect();
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    private int connectNoSLL(String hostIp, int port, int timeout) {
        if (hostIp == null || hostIp.length() == 0 || hostIp.equals("0.0.0.0")) {
            return TransResult.ERR_CONNECT;
        }

        ICommHelper commHelper = BankTransManager.gl.getCommHelper();
        client = commHelper.createTcpClient(hostIp, port);
        client.setConnectTimeout(timeout);
        client.setRecvTimeout(timeout);
        try {
            client.connect();
            return TransResult.SUCC;
        } catch (CommException e) {

            e.printStackTrace();
        }
        return TransResult.ERR_CONNECT;
    }
}
