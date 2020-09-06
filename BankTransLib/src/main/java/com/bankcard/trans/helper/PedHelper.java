package com.bankcard.trans.helper;

import android.text.TextUtils;
import android.util.Log;

import com.bankcard.trans.BankTransManager;
import com.bankcard.trans.constants.Constants;
import com.bankcard.trans.settings.SysParam;
import com.bankcard.trans.utils.Convert;
import com.bankcard.trans.utils.Tool;
import com.pax.exdev.ExPed;
import com.pax.exdev.ExPp;
import com.pax.exdev.ExQpboc;

/**
 * Created by SuQi on 2020/7/21.
 * Describe:
 */
public class PedHelper {

    private static final String TAG = "PedHelper";
    private static final int RET_SUCC = 0;// 成功
    private static final int RET_NO_INPUT_PIN = -305;// 没有输入密码，点确定

    private static PedHelper instance;
    private String filed55;
    private String pinBlock;
    private int transAmount;
    private int readCardFlag;// 刷卡标识

    private PedHelper() {
    }

    public static PedHelper getInstance() {
        if (instance == null) {
            instance = new PedHelper();
        }
        return instance;
    }

    public boolean readCard(int timeout, String amount) {
        this.transAmount = Integer.parseInt(amount);
        if (!open()) {
            return false;
        }
        // 1.打开读卡器
        byte readType = 0;
        readType |= 1;// MAG 刷卡
        readType |= 2;// ICC 插卡
        readType |= 4;// PICC 非接
        int ret = ExQpboc.openCard(readType);
        Log.d(TAG, "ExQpboc.openCard >>> ret:" + ret);
        if (ret != RET_SUCC) {
            return false;
        }

        boolean result = false;
        ExPp.scrCls();
        ExPp.scrPrint((byte) 0, (byte) 0, "PLS SWING CARD");
        ExPed.setAsteriskLayout(48, 40, 32, 0xFFFFFFFF, (char) 0);

        // 2.开始检卡
        byte[] cardType = new byte[1];
        ret = ExQpboc.checkCard2(timeout, cardType);
        Log.d(TAG, "ExQpboc.checkCard2 >>> ret:" + ret);
        if (ret != RET_SUCC) {
            return false;
        }

        // 检卡成功
        // 3.处理读卡数据
        Log.d(TAG, "cardType >>> ret:" + cardType[0]);
        switch (cardType[0]) {// 当前的读卡类型
            case 1:// 非接
                readCardFlag = 1;
                if (getCardData()) {
                    // 非接输密
                    result = inputOnlinePin();
                }
                break;
            case 2:// 插卡
                readCardFlag = 2;
                result = insertCardProcess();
                break;
        }

        // 4.关闭读卡器
        ExQpboc.closeCard(cardType[0]);
        srcClear();
        close();
        return result;
    }

    /**
     * 插卡流程
     */
    private boolean insertCardProcess() {
        // 1.应用选择
        if (!selectApp()) {
            return false;
        }

        // >>> 1.首先判断卡号是否有值，卡号有值，说明是单应用，直接走下一步「设置流程」
        // >>> 2.当卡号没有值时，说明是多应用，应用结果不为空的时候，说应用有值
        // >>> 3.当是多应用的情况时，需要弹框提示用户选择使用的应用，用户选择之后走「应用选择结果步骤」
        if (!TextUtils.isEmpty(cardNo)) {
            // 2.设置流程
            return setFlowPath();
        } else {
            // 多应用时，用户选择后，填入用户选择的应用，此处为了测试方便，默认用户选择了第一个应用
            byte[] cardNo = new byte[64];
            int ret = ExQpboc.selectAppResult(app1, cardNo);
            Log.d(TAG, "selectAppResult return " + ret);
            if (ret == RET_SUCC) {
                this.cardNo = new String(cardNo).trim();
                Log.d(TAG, "selectAppResult >>> cardNo:" + this.cardNo);
                // 2.设置流程
                return setFlowPath();
            } else {
                return false;
            }
        }
    }

    private boolean isSimpleFlow;// 简易流程标识
    private String cardNo;// 卡号
    byte[] app1;// 应用1
    byte[] app2;// 应用2
    byte[] app3;// 应用3

    /**
     * 应用选择
     */
    private boolean selectApp() {
        app1 = new byte[64];
        app2 = new byte[64];
        app3 = new byte[64];
        byte[] cardNo = new byte[64];
        int amount = 1;
        int ret = ExQpboc.selectApp(transAmount, 0, app1, app2, app3, cardNo);
        Log.d(TAG, "selectApp return " + ret);
        if (ret == RET_SUCC) {
            // >>> 1.首先判断卡号是否有值，卡号有值，说明是单应用，直接走下一步「输密码步骤」
            // >>> 2.当卡号没有值时，说明是多应用，应用结果不为空的时候，说应用有值
            // >>> 3.当是多应用的情况时，需要弹框提示用户选择使用的应用，用户选择之后走「应用选择结果步骤」
            String appName1 = new String(app1).trim();
            String appName2 = new String(app2).trim();
            String appName3 = new String(app3).trim();
            this.cardNo = new String(cardNo).trim();
            Log.d(TAG, "selectApp >>> appName1:" + appName1);
            Log.d(TAG, "selectApp >>> appName2:" + appName2);
            Log.d(TAG, "selectApp >>> appName3:" + appName3);
            Log.d(TAG, "selectApp >>> cardNo:" + this.cardNo);
            return true;
        }
        return false;
    }

    /**
     * 设置流程
     * 用于设置简易流程或标准流程
     * 简易流程：插卡后，直接获取emv数据
     * 标准流程：插卡后，需要输入PIN，然后获取emv数据
     */
    private boolean setFlowPath() {
        int flow = 2;// 2表示简易流程
        if (!isSimpleFlow) {
            flow = 1;// 1表示标准流程
        }
        // 设置流程
        byte[] stepType = new byte[1];// 标准流程下一步执行步骤的类型，1:联机PIN 2:脱机PIN 3:证件信息
        byte[] emvData = new byte[2048];// 简易流程的emv数据
        byte[] certNo = new byte[1];// 证件序号
        byte[] certData = new byte[2048];// 证件信息
        int ret = ExQpboc.setFlowPath(flow, stepType, emvData, certNo, certData);
        Log.d(TAG, "ExQpboc.setFlowPath >>> ret:" + ret);
        if (isSimpleFlow) {
            // 简易流程
            if (ret > 0) {
                // 直接获取EMV数据
                filed55 = Tool.bcd2Str(emvData, 0, ret);
                Log.d(TAG, "insertCard simplePath >>> getEmvData return " + ret + " data is " + filed55);
                return true;
            }
        } else {
            // 标准流程
            if (ret == RET_SUCC) {
                // 根据返回的下一步要执行步骤，进行操作
                // 1:联机PIN 2:脱机PIN 3:证件信息
                Log.d(TAG, "insertCard normalPath >>> stepType:" + stepType[0]);
                switch (stepType[0]) {
                    case 1:// 联机PIN
                        if (inputOnlinePin()) {
                            // 3.获取EMV数据
                            if (getInsertEmvData()) {
                                // 4.获取二次授权和脚本处理结果，联机结束后调用
                                //getAuthAndScriptResult();
                                //TODO 请求联机
                                return true;
                            }
                        }
                        break;
                }
            }
        }

        return false;
    }

    /**
     * 获取插卡55域数据
     *
     * @return
     */
    private boolean getInsertEmvData() {
        byte[] sReadBuf = new byte[4096];
        int ret = ExQpboc.getEmvData(sReadBuf);
        Log.d(TAG, "ExQpboc.getEmvData >>> ret:" + ret);
        if (ret > 0) {
            filed55 = Tool.bcd2Str(sReadBuf, 0, ret);
            Log.d(TAG, "getInsertEmvData >>> " + filed55);
            return true;
        }
        return true;
    }

    /**
     * 联机后调用
     * 获取脚本数据和二次授权结果
     */
    private void getAuthAndScriptResult() {
        String responseCode = "A00";// 联机结果，成功标识A，响应码00
        String authCode = "000000";// 授权码，6位，不存在传入000000
//        String respFiled55 = "910A71FD612B99311AB33030";// 联机返回的55域信息
        String respFiled55 = "910A71FD612B99311AB3303072289F1804AABBCCDD86098424000004AABBCCDD86098418000004AABBCCDD86098416000004AABBCCDD";// 联机返回的55域信息 有脚本
        byte[] authResult = new byte[2];// 二次授权结果 A:成功 B:失败
        byte[] scriptData = new byte[2048];// 处理后的脚本结果，可能为空
        int ret = ExQpboc.getAuthAndScriptResult(responseCode, authCode, respFiled55, authResult, scriptData);
        Log.d(TAG, "scriptNotice return " + ret);
        if (ret >= 0) {
            Log.d(TAG, "authResult is " + new String(authResult));
            Log.d(TAG, "scriptData is " + Tool.bcd2Str(scriptData, 0, ret));
        }
    }

    /**
     * 输入联机密码
     */
    private boolean inputOnlinePin() {
        ExPed.setInterval(1000);
        ExPp.scrCls();
        ExPp.scrPrint((byte) 0, (byte) 0, "PLS INPUT PIN:");
        ExPed.setAsteriskLayout(48, 40, 32, 0xFFFFFFFF, (char) 0);
        ExPp.beef((byte) 6, 100);
        int ret;
        boolean result = false;
        boolean isSm4 = BankTransManager.controller.getBoolean(SysParam.IS_SM4);
        if (isSm4) {// 国密
            byte[] szDataIn = {0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38,
                    0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38};
            byte[] sPinBlock = new byte[16];
            ret = ExPed.getPinBlockSM4(Constants.INDEX_TPK, "0,4,6", szDataIn, sPinBlock, 0, 60 * 1000);
            Log.d(TAG, "ExPed.getPinBlockSM4 >>> ret:" + ret);
            if (ret == RET_SUCC) {
                pinBlock = Tool.bcd2Str(sPinBlock);
                Log.d(TAG, "getPinBlockSM4 >>> " + pinBlock);
                result = true;
            } else if (ret == RET_NO_INPUT_PIN) {
                pinBlock = "";
                Log.d(TAG, "getPinBlock >>> RET_NO_INPUT_PIN");
                result = true;
            }
        } else {// 3DES
            byte[] dataIn = {0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30};
            byte[] dataOut = new byte[16];
            ret = ExPed.getPinBlock(Constants.INDEX_TPK, dataIn, "0,4,6", 0, 60 * 1000, dataOut);
            Log.d(TAG, "ExPed.getPinBlock >>> ret:" + ret);
            if (ret == RET_SUCC) {
                pinBlock = Tool.bcd2Str(dataOut);
                Log.d(TAG, "getPinBlock >>> " + pinBlock);
                result = true;
            } else if (ret == RET_NO_INPUT_PIN) {
                pinBlock = "";
                Log.d(TAG, "getPinBlock >>> RET_NO_INPUT_PIN");
                result = true;
            }
        }
        srcClear();
        return result;
    }

    /**
     * 获取非接数据
     */
    public boolean getCardData() {
        byte[] sReadBuf = new byte[4096];
        int ret = ExQpboc.getCardData(1, 0, sReadBuf);
        Log.d(TAG, "getCardData >>> ret:" + ret);
        if (ret > 0) {
            ExPp.beef((byte) 6, 100);
            filed55 = Tool.bcd2Str(sReadBuf, 0, ret);
            Log.d(TAG, "filed55 >>> " + filed55);
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取MAC
     *
     * @param data
     * @return
     */
    public byte[] calcMac(byte[] data) {
        if (open()) {
            byte[] tmpbuf = new byte[8];
            boolean isSM4 = BankTransManager.controller.getBoolean(SysParam.IS_SM4);
            if (isSM4) {
                tmpbuf = new byte[16];
            }
            int len;
            byte[] dataIn = new byte[data.length + tmpbuf.length];
            len = data.length / tmpbuf.length + 1;

            System.arraycopy(data, 0, dataIn, 0, data.length);

            for (int i = 0; i < len; i++) {
                for (int k = 0; k < tmpbuf.length; k++) {
                    tmpbuf[k] ^= dataIn[i * tmpbuf.length + k];
                }
            }

            String beforeCalcMacData = Convert.bcdBytesToStr(tmpbuf);
            byte[] macOut = new byte[16];

            int ret;
            if (isSM4) {
                byte[] initVector = new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                        0x00, 0x00, 0x00, 0x00};
                ret = ExPed.getMacSM(Constants.INDEX_TAK, initVector, beforeCalcMacData.getBytes(), beforeCalcMacData.getBytes().length, macOut, (byte) 0x00);
                Log.d(TAG, "ExPed.getMacSM >>> ret:" + ret);

            } else {
                ret = ExPed.getMac(Constants.INDEX_TAK, beforeCalcMacData.getBytes(), beforeCalcMacData.getBytes().length, macOut, (byte) 0x00);
                Log.d(TAG, "ExPed.getMac >>> ret:" + ret);
            }
            close();
            if (ret == RET_SUCC) {
                byte[] mac = Convert.bcdBytesToStr(macOut).substring(0, 8).getBytes();
                Log.d(TAG, "mac >>> " + Convert.bcdBytesToStr(macOut).substring(0, 8));
                return mac;
            }

        }
        return null;
    }

    /**
     * 计算DES
     *
     * @param data
     * @return
     */
    public byte[] calcDes(byte[] data) {
        if (open()) {
            int result;
            byte[] dataOut;
            boolean isSM4 = BankTransManager.controller.getBoolean(SysParam.IS_SM4);
            if (isSM4) {
                dataOut = new byte[data.length];
                result = ExPed.sm4(Constants.INDEX_TDK, null, data, data.length, dataOut, 0x01);
                Log.d(TAG, "ExPed.sm4 >>> ret:" + result);
            } else {
                dataOut = new byte[data.length];
                result = ExPed.des(Constants.INDEX_TDK, null, data, data.length, dataOut, (byte) 0x01);
                Log.d(TAG, "ExPed.des >>> ret:" + result);
            }
            close();
            if (result == RET_SUCC) {
                Log.d(TAG, "calcDes >>> " + Convert.bcdBytesToStr(dataOut));
                return dataOut;
            }

        }
        return null;
    }

    private byte[] getFilledDesData(byte[] data) {
        double dataLength = data.length;
        double length = Math.ceil(dataLength / 16);
        int filledLength = (int) (length * 16);
        return Convert.fillData(filledLength, data, 0);
    }

    /**
     * 写入密钥
     *
     * @param scrKeyType 源密钥类型
     * @param scrKeyIdx  源密钥索引
     * @param dstKeyIdx  目标密钥索引
     * @param dstKeyType 目标密钥了您先给
     * @param keyValue   密钥值
     * @param keyKcv     校验值
     * @return
     */
    private boolean pedWriteKey(byte scrKeyType, byte scrKeyIdx, byte dstKeyIdx, byte dstKeyType, byte[] keyValue, byte[] keyKcv) {
        if (!open()) {
            return false;
        }
        byte[] keyBlock = new byte[184];
        keyBlock[0] = 0x03;
        keyBlock[1] = scrKeyType;//ScrKeyType PED_TMK
        keyBlock[2] = scrKeyIdx;//ScrKeyIdx
        keyBlock[3] = dstKeyIdx;//DstKeyIdx
        keyBlock[11] = dstKeyType; //DstKeyType PED_TMK
        keyBlock[12] = 16;        //DstKeyLen
        System.arraycopy(keyValue, 0, keyBlock, 13, keyValue.length);
        if (keyKcv != null) {
            keyBlock[37] = 0x01;  //KcvMode
            //128 bytes KcvData
            System.arraycopy(keyKcv, 0, keyBlock, 166, keyKcv.length);
        } else {
            keyBlock[37] = 0x00;
        }
        int ret = ExPed.writeKey(keyBlock);
        close();
        return ret == RET_SUCC;
    }

    /**
     * 明文写入主密钥
     *
     * @param value
     * @return
     */
    public boolean writeTMK(byte[] value) {
        boolean isSM4 = BankTransManager.controller.getBoolean(SysParam.IS_SM4);
        boolean result;
        if (!isSM4) {
            result = pedWriteKey((byte) ExPed.PED_TMK, (byte) 0x00, Constants.INDEX_TMK, (byte) ExPed.PED_TMK, value, null);
        } else {
            result = pedWriteKey((byte) ExPed.PED_SM4_TMK, (byte) 0x00, Constants.INDEX_TMK, (byte) ExPed.PED_SM4_TMK, value, null);
        }
        return result;
    }

    /**
     * 密文写入TPK
     *
     * @param value
     * @return
     */
    public boolean writeTPK(byte[] value, byte[] kcv) {
        boolean isSM4 = BankTransManager.controller.getBoolean(SysParam.IS_SM4);
        boolean result;
        if (!isSM4) {
            result = pedWriteKey((byte) ExPed.PED_TMK, Constants.INDEX_TMK, Constants.INDEX_TPK, (byte) ExPed.PED_TPK, value, kcv);
        } else {
            result = pedWriteKey((byte) ExPed.PED_SM4_TMK, Constants.INDEX_TMK, Constants.INDEX_TPK, (byte) ExPed.PED_SM4_TPK, value, kcv);
        }
        return result;
    }

    /**
     * 密文写入TAK
     *
     * @param value
     * @return
     */
    public boolean writeTAK(byte[] value, byte[] kcv) {
        boolean isSM4 = BankTransManager.controller.getBoolean(SysParam.IS_SM4);
        boolean result;
        if (!isSM4) {
            result = pedWriteKey((byte) ExPed.PED_TMK, Constants.INDEX_TMK, Constants.INDEX_TAK, (byte) ExPed.PED_TAK, value, kcv);
        } else {
            result = pedWriteKey((byte) ExPed.PED_SM4_TMK, Constants.INDEX_TMK, Constants.INDEX_TAK, (byte) ExPed.PED_SM4_TAK, value, kcv);
        }
        return result;
    }

    /**
     * 密文写入TDK
     *
     * @param value
     * @return
     */
    public boolean writeTDK(byte[] value, byte[] kcv) {
        boolean isSM4 = BankTransManager.controller.getBoolean(SysParam.IS_SM4);
        boolean result;
        if (!isSM4) {
            result = pedWriteKey((byte) ExPed.PED_TMK, Constants.INDEX_TMK, Constants.INDEX_TDK, (byte) ExPed.PED_TDK, value, kcv);
        } else {
            result = pedWriteKey((byte) ExPed.PED_SM4_TMK, Constants.INDEX_TMK, Constants.INDEX_TDK, (byte) ExPed.PED_SM4_TDK, value, kcv);
        }
        return result;
    }

    /**
     * 获取55域
     *
     * @return
     */
    public String getFiled55() {
        return filed55;
    }

    /**
     * 获取密码密文
     *
     * @return
     */
    public String getPinBlock() {
        return pinBlock;
    }

    public int getReadCardFlag() {
        return readCardFlag;
    }

    public boolean open() {
        if (BankTransManager.checkExPedState()) {
            int ret = ExPed.open();
            Log.d(TAG, "open >>> ret:" + ret);
            return ret == RET_SUCC;
        }
        return false;
    }

    public boolean close() {
        int ret = ExPed.close();
        Log.d(TAG, "close >>> ret:" + ret);
        return ret == RET_SUCC;
    }

    public void srcClear() {
        ExPp.scrCls();
        ExPp.scrPrint((byte) 0, (byte) 0, "   WELCOME!   ");
        ExPp.scrPrint((byte) 1, (byte) 0, "PAX TECHNOLOGY");
    }

    public String getSN() {
        return ExPp.readSN();
    }

    public String getTUSN() {
        return ExPp.readTusn();
    }

    public byte[] idKeyCalc(byte[] data) {
        byte[] dataOut = new byte[1024];
        byte[] initVector = new byte[] { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00 };
//        ExPed.idKeyCalc((byte)1, initVector, data, data.length, dataOut, 0);
        return null;
    }

    /**
     * 添加AID
     */
    public void addAid() {
        ExQpboc.addAidCapk(1, "9F0607A0000000031010DF0101009F08020140DF1105D84000A800DF1205D84004F800DF130500100000009F1B0400000000DF150400000000DF160199DF170199DF14039F3704DF180101");
        ExQpboc.addAidCapk(1, "9F0607A0000000032010DF0101009F08020140DF1105D84000A800DF1205D84004F800DF130500100000009F1B0400000000DF150400000000DF160199DF170199DF14039F3704DF180100");
        ExQpboc.addAidCapk(1, "9F0607A0000000033010DF0101009F08020140DF1105D84000A800DF1205D84004F800DF130500100000009F1B0400000000DF150400000000DF160199DF170199DF14039F3704DF180100");
        ExQpboc.addAidCapk(1, "9F0607A0000000041010DF0101009F08020002DF1105FC5080A000DF1205F85080F800DF130504000000009F1B0400000000DF150400000000DF160199DF170199DF14039F3704DF180101");
        ExQpboc.addAidCapk(1, "9F0607A0000000043060DF0101009F08020002DF1105FC5058A000DF1205F85058F800DF130504000000009F1B0400000000DF150400000000DF160199DF170199DF14039F3704DF180101");
        ExQpboc.addAidCapk(1, "9F0607A0000000651010DF0101009F08020200DF1105FC6024A800DF1205FC60ACF800DF130500100000009F1B0400000000DF150400000000DF160199DF170199DF14039F3704DF180100");
        ExQpboc.addAidCapk(1, "9F0608A000000333010101DF0101009F08020020DF1105D84000A800DF1205D84004F800DF130500100000009F1B0400000000DF150400000000DF160199DF170199DF14039F3704DF1801019F7B06000000100000DF1906000000100000DF2006000000100000DF2106000000100000");
        ExQpboc.addAidCapk(1, "9F0608A000000333010102DF0101009F08020020DF1105D84000A800DF1205D84004F800DF130500100000009F1B0400000000DF150400000000DF160199DF170199DF14039F3704DF1801019F7B06000000100000DF1906000000100000DF2006000000100000DF2106000000100000");
        ExQpboc.addAidCapk(1, "9F0608A000000333010103DF0101009F08020020DF1105D84000A800DF1205D84004F800DF130500100000009F1B0400000000DF150400000000DF160199DF170199DF14039F3704DF1801019F7B06000000100000DF1906000000100000DF2006000000100000DF2106000000100000");
        ExQpboc.addAidCapk(1, "9F0608A000000333010106DF0101009F08020020DF1105D84000A800DF1205D84004F800DF130500100000009F1B0400000000DF150400000000DF160199DF170199DF14039F3704DF1801019F7B06000000100000DF1906000000100000DF2006000000100000DF2106000000100000");
    }

    /**
     * 添加CAPK
     */
    public void addCapk() {
        ExQpboc.addAidCapk(0, "9F0605A0000000659F220109DF05083230303931323331DF060101DF070101DF028180B72A8FEF5B27F2B550398FDCC256F714BAD497FF56094B7408328CB626AA6F0E6A9DF8388EB9887BC930170BCC1213E90FC070D52C8DCD0FF9E10FAD36801FE93FC998A721705091F18BC7C98241CADC15A2B9DA7FB963142C0AB640D5D0135E77EBAE95AF1B4FEFADCF9C012366BDDA0455C1564A68810D7127676D493890BDDF040103DF03144410C6D51C2F83ADFD92528FA6E38A32DF048D0A");
        ExQpboc.addAidCapk(0, "9F0605A0000000659F220110DF05083230313231323331DF060101DF070101DF02819099B63464EE0B4957E4FD23BF923D12B61469B8FFF8814346B2ED6A780F8988EA9CF0433BC1E655F05EFA66D0C98098F25B659D7A25B8478A36E489760D071F54CDF7416948ED733D816349DA2AADDA227EE45936203CBF628CD033AABA5E5A6E4AE37FBACB4611B4113ED427529C636F6C3304F8ABDD6D9AD660516AE87F7F2DDF1D2FA44C164727E56BBC9BA23C0285DF040103DF0314C75E5210CBE6E8F0594A0F1911B07418CADB5BAB");
        ExQpboc.addAidCapk(0, "9F0605A0000000659F220112DF05083230313431323331DF060101DF070101DF0281B0ADF05CD4C5B490B087C3467B0F3043750438848461288BFEFD6198DD576DC3AD7A7CFA07DBA128C247A8EAB30DC3A30B02FCD7F1C8167965463626FEFF8AB1AA61A4B9AEF09EE12B009842A1ABA01ADB4A2B170668781EC92B60F605FD12B2B2A6F1FE734BE510F60DC5D189E401451B62B4E06851EC20EBFF4522AACC2E9CDC89BC5D8CDE5D633CFD77220FF6BBD4A9B441473CC3C6FEFC8D13E57C3DE97E1269FA19F655215B23563ED1D1860D8681DF040103DF0314874B379B7F607DC1CAF87A19E400B6A9E25163E8");
        ExQpboc.addAidCapk(0, "9F0605A0000000659F220114DF05083230313631323331DF060101DF070101DF0281F8AEED55B9EE00E1ECEB045F61D2DA9A66AB637B43FB5CDBDB22A2FBB25BE061E937E38244EE5132F530144A3F268907D8FD648863F5A96FED7E42089E93457ADC0E1BC89C58A0DB72675FBC47FEE9FF33C16ADE6D341936B06B6A6F5EF6F66A4EDD981DF75DA8399C3053F430ECA342437C23AF423A211AC9F58EAF09B0F837DE9D86C7109DB1646561AA5AF0289AF5514AC64BC2D9D36A179BB8A7971E2BFA03A9E4B847FD3D63524D43A0E8003547B94A8A75E519DF3177D0A60BC0B4BAB1EA59A2CBB4D2D62354E926E9C7D3BE4181E81BA60F8285A896D17DA8C3242481B6C405769A39D547C74ED9FF95A70A796046B5EFF36682DC29DF040103DF0314C0D15F6CD957E491DB56DCDD1CA87A03EBE06B7B");
        ExQpboc.addAidCapk(0, "9F0605A0000003339F220101DF05083230303931323331DF060101DF070101DF028180BBE9066D2517511D239C7BFA77884144AE20C7372F515147E8CE6537C54C0A6A4D45F8CA4D290870CDA59F1344EF71D17D3F35D92F3F06778D0D511EC2A7DC4FFEADF4FB1253CE37A7B2B5A3741227BEF72524DA7A2B7B1CB426BEE27BC513B0CB11AB99BC1BC61DF5AC6CC4D831D0848788CD74F6D543AD37C5A2B4C5D5A93BDF040103DF0314E881E390675D44C2DD81234DCE29C3F5AB2297A0");
        ExQpboc.addAidCapk(0, "9F0605A0000003339F220102DF05083230323131323331DF060101DF070101DF028190A3767ABD1B6AA69D7F3FBF28C092DE9ED1E658BA5F0909AF7A1CCD907373B7210FDEB16287BA8E78E1529F443976FD27F991EC67D95E5F4E96B127CAB2396A94D6E45CDA44CA4C4867570D6B07542F8D4BF9FF97975DB9891515E66F525D2B3CBEB6D662BFB6C3F338E93B02142BFC44173A3764C56AADD202075B26DC2F9F7D7AE74BD7D00FD05EE430032663D27A57DF040103DF031403BB335A8549A03B87AB089D006F60852E4B8060");
        ExQpboc.addAidCapk(0, "9F0605A0000003339F220103DF05083230323431323331DF060101DF070101DF0281B0B0627DEE87864F9C18C13B9A1F025448BF13C58380C91F4CEBA9F9BCB214FF8414E9B59D6ABA10F941C7331768F47B2127907D857FA39AAF8CE02045DD01619D689EE731C551159BE7EB2D51A372FF56B556E5CB2FDE36E23073A44CA215D6C26CA68847B388E39520E0026E62294B557D6470440CA0AEFC9438C923AEC9B2098D6D3A1AF5E8B1DE36F4B53040109D89B77CAFAF70C26C601ABDF59EEC0FDC8A99089140CD2E817E335175B03B7AA33DDF040103DF031487F0CD7C0E86F38F89A66F8C47071A8B88586F26");
        ExQpboc.addAidCapk(0, "9F0605A0000003339F220104DF05083230323531323331DF060101DF070101DF0281F8BC853E6B5365E89E7EE9317C94B02D0ABB0DBD91C05A224A2554AA29ED9FCB9D86EB9CCBB322A57811F86188AAC7351C72BD9EF196C5A01ACEF7A4EB0D2AD63D9E6AC2E7836547CB1595C68BCBAFD0F6728760F3A7CA7B97301B7E0220184EFC4F653008D93CE098C0D93B45201096D1ADFF4CF1F9FC02AF759DA27CD6DFD6D789B099F16F378B6100334E63F3D35F3251A5EC78693731F5233519CDB380F5AB8C0F02728E91D469ABD0EAE0D93B1CC66CE127B29C7D77441A49D09FCA5D6D9762FC74C31BB506C8BAE3C79AD6C2578775B95956B5370D1D0519E37906B384736233251E8F09AD79DFBE2C6ABFADAC8E4D8624318C27DAF1DF040103DF0314F527081CF371DD7E1FD4FA414A665036E0F5E6E5");
        ExQpboc.addAidCapk(0, "9F0605A0000000039F220101DF05083230303931323331DF060101DF070101DF028180C696034213D7D8546984579D1D0F0EA519CFF8DEFFC429354CF3A871A6F7183F1228DA5C7470C055387100CB935A712C4E2864DF5D64BA93FE7E63E71F25B1E5F5298575EBE1C63AA617706917911DC2A75AC28B251C7EF40F2365912490B939BCA2124A30A28F54402C34AECA331AB67E1E79B285DD5771B5D9FF79EA630B75DF040103DF0314D34A6A776011C7E7CE3AEC5F03AD2F8CFC5503CC");
        ExQpboc.addAidCapk(0, "9F0605A0000000039F220107DF05083230313231323331DF060101DF070101DF028190A89F25A56FA6DA258C8CA8B40427D927B4A1EB4D7EA326BBB12F97DED70AE5E4480FC9C5E8A972177110A1CC318D06D2F8F5C4844AC5FA79A4DC470BB11ED635699C17081B90F1B984F12E92C1C529276D8AF8EC7F28492097D8CD5BECEA16FE4088F6CFAB4A1B42328A1B996F9278B0B7E3311CA5EF856C2F888474B83612A82E4E00D0CD4069A6783140433D50725FDF040103DF0314B4BC56CC4E88324932CBC643D6898F6FE593B172");
        ExQpboc.addAidCapk(0, "9F0605A0000000049F220103DF05083230303931323331DF060101DF070101DF028180C2490747FE17EB0584C88D47B1602704150ADC88C5B998BD59CE043EDEBF0FFEE3093AC7956AD3B6AD4554C6DE19A178D6DA295BE15D5220645E3C8131666FA4BE5B84FE131EA44B039307638B9E74A8C42564F892A64DF1CB15712B736E3374F1BBB6819371602D8970E97B900793C7C2A89A4A1649A59BE680574DD0B60145DF040103DF03145ADDF21D09278661141179CBEFF272EA384B13BB");
        ExQpboc.addAidCapk(0, "9F0605A0000000039F220108DF05083230313431323331DF060101DF070101DF0281B0D9FD6ED75D51D0E30664BD157023EAA1FFA871E4DA65672B863D255E81E137A51DE4F72BCC9E44ACE12127F87E263D3AF9DD9CF35CA4A7B01E907000BA85D24954C2FCA3074825DDD4C0C8F186CB020F683E02F2DEAD3969133F06F7845166ACEB57CA0FC2603445469811D293BFEFBAFAB57631B3DD91E796BF850A25012F1AE38F05AA5C4D6D03B1DC2E568612785938BBC9B3CD3A910C1DA55A5A9218ACE0F7A21287752682F15832A678D6E1ED0BDF040103DF031420D213126955DE205ADC2FD2822BD22DE21CF9A8");
        ExQpboc.addAidCapk(0, "9F0605A0000000049F220104DF05083230313231323331DF060101DF070101DF028190A6DA428387A502D7DDFB7A74D3F412BE762627197B25435B7A81716A700157DDD06F7CC99D6CA28C2470527E2C03616B9C59217357C2674F583B3BA5C7DCF2838692D023E3562420B4615C439CA97C44DC9A249CFCE7B3BFB22F68228C3AF13329AA4A613CF8DD853502373D62E49AB256D2BC17120E54AEDCED6D96A4287ACC5C04677D4A5A320DB8BEE2F775E5FEC5DF040103DF0314381A035DA58B482EE2AF75F4C3F2CA469BA4AA6C");
        ExQpboc.addAidCapk(0, "9F0605A0000000039F220109DF05083230313631323331DF060101DF070101DF0281F89D912248DE0A4E39C1A7DDE3F6D2588992C1A4095AFBD1824D1BA74847F2BC4926D2EFD904B4B54954CD189A54C5D1179654F8F9B0D2AB5F0357EB642FEDA95D3912C6576945FAB897E7062CAA44A4AA06B8FE6E3DBA18AF6AE3738E30429EE9BE03427C9D64F695FA8CAB4BFE376853EA34AD1D76BFCAD15908C077FFE6DC5521ECEF5D278A96E26F57359FFAEDA19434B937F1AD999DC5C41EB11935B44C18100E857F431A4A5A6BB65114F174C2D7B59FDF237D6BB1DD0916E644D709DED56481477C75D95CDD68254615F7740EC07F330AC5D67BCD75BF23D28A140826C026DBDE971A37CD3EF9B8DF644AC385010501EFC6509D7A41DF040103DF03141FF80A40173F52D7D27E0F26A146A1C8CCB29046");
        ExQpboc.addAidCapk(0, "9F0605A0000000049F220105DF05083230313431323331DF060101DF070101DF0281B0B8048ABC30C90D976336543E3FD7091C8FE4800DF820ED55E7E94813ED00555B573FECA3D84AF6131A651D66CFF4284FB13B635EDD0EE40176D8BF04B7FD1C7BACF9AC7327DFAA8AA72D10DB3B8E70B2DDD811CB4196525EA386ACC33C0D9D4575916469C4E4F53E8E1C912CC618CB22DDE7C3568E90022E6BBA770202E4522A2DD623D180E215BD1D1507FE3DC90CA310D27B3EFCCD8F83DE3052CAD1E48938C68D095AAC91B5F37E28BB49EC7ED597DF040103DF0314EBFA0D5D06D8CE702DA3EAE890701D45E274C845");
        ExQpboc.addAidCapk(0, "9F0605A0000000049F220106DF05083230313631323331DF060101DF070101DF0281F8CB26FC830B43785B2BCE37C81ED334622F9622F4C89AAE641046B2353433883F307FB7C974162DA72F7A4EC75D9D657336865B8D3023D3D645667625C9A07A6B7A137CF0C64198AE38FC238006FB2603F41F4F3BB9DA1347270F2F5D8C606E420958C5F7D50A71DE30142F70DE468889B5E3A08695B938A50FC980393A9CBCE44AD2D64F630BB33AD3F5F5FD495D31F37818C1D94071342E07F1BEC2194F6035BA5DED3936500EB82DFDA6E8AFB655B1EF3D0D7EBF86B66DD9F29F6B1D324FE8B26CE38AB2013DD13F611E7A594D675C4432350EA244CC34F3873CBA06592987A1D7E852ADC22EF5A2EE28132031E48F74037E3B34AB747FDF040103DF0314F910A1504D5FFB793D94F3B500765E1ABCAD72D9");
    }


}
