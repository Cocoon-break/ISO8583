package com.bankcard.trans.model;

/**
 * 交易记录数据库信息, 当表结构有变化时， {@link DbInfo#VER} 加1
 * 
 */
public class DbInfo {
    /**
     * 版本号
     */
    public static final int VER = 1;
    /**
     * 交易记录数据库名称
     */
    public static final String DB_NAME = "BankTransRecord.db";
    /**
     * 交易记录表名
     */
    public static final String TABLE_NAME_TRANS = "trans";
    /**
     * 冲正表名
     */
    public static final String TABLE_NAME_DUP = "dup";
    /**
     * 脚本结果表名
     */
    public static final String TABLE_NAME_SCRIPT = "script";

}