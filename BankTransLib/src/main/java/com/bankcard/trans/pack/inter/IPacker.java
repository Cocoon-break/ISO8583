package com.bankcard.trans.pack.inter;

/**
 * 打包模块抽象接口
 */
public interface IPacker<T, O> {
    /**
     * 打包接口
     *
     * @return
     */
    public O pack(T t);

    /**
     * 解包接口
     *
     * @return
     */
    public int unpack(T t, O o);
}
