package com.bankcard.trans.pack.inter;

/**
 * 打包模块监听器
 */
public interface PackListener {
    /**
     * 计算mac
     *
     * @param data
     * @return mac值
     */
    byte[] onCalcMac(byte[] data);

    /**
     * 磁道加密
     *
     * @param track
     * @return
     */
    byte[] onEncTrack(byte[] track);
}
