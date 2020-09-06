package com.bankcard.trans;

import android.content.Context;
import android.os.SystemClock;
import android.util.Log;

import com.bankcard.trans.emv.EmvTags;
import com.bankcard.trans.helper.PedHelper;
import com.bankcard.trans.model.ETransType;
import com.bankcard.trans.model.TransData;
import com.bankcard.trans.pack.inter.IPacker;
import com.bankcard.trans.pack.inter.PackListenerImpl;
import com.bankcard.trans.settings.Controller;
import com.bankcard.trans.settings.SysParam;
import com.bankcard.trans.utils.Convert;
import com.bankcard.trans.utils.ExDevUtils;
import com.bankcard.trans.utils.Tlv;
import com.bankcard.trans.utils.TlvCell;
import com.bankcard.trans.utils.TrackUtils;
import com.pax.exdev.ExDevLib;
import com.pax.gl.IGL;
import com.pax.gl.commhelper.IComm;
import com.pax.gl.convert.IConvert;
import com.pax.gl.db.IDb;
import com.pax.gl.impl.GLProxy;

import java.util.Map;

/**
 * Created by SuQi on 2020/7/18.
 * Describe:银行卡交易管理类
 */
public class BankTransManager {

    private BankTransManager() {
    }

    private static BankTransManager instance = new BankTransManager();

    public static synchronized BankTransManager getInstance() {
        return instance;
    }

    private static final String TAG = "BankTransManager";

    public static Context context;
    private static IComm iComm;
    private static ExDevLib exDevLib;
    public static IGL gl;
    public static IConvert convert;
    public static com.pax.gl.packer.IPacker packer;
    public static IDb db;
    public static Controller controller;
    public static PedHelper pedHelper;

    /**
     * 初始化相关配置，在主应用Application中初始化
     */
    public void init(Context context) {
        this.context = context;
        exdevInit();
        glInit();
        controller = Controller.getInstance(context);
        pedHelper = PedHelper.getInstance();
    }

    /**
     * 打包
     *
     * @param trans
     * @return
     */
    public byte[] packData(TransData trans) {
        try {
            initTransData(trans);
            ETransType eTransType = ETransType.valueOf(trans.getTransType());
            IPacker<TransData, byte[]> packager = eTransType.getpackager(new PackListenerImpl());
            boolean readCardResult = true;
            if (ETransType.SALE == eTransType) {
                // 进入读卡流程
                readCardResult = pedHelper.readCard(60, trans.getAmount());
                Log.d(TAG, "readCardResult >>> " + readCardResult);
                if (readCardResult) {
                    String filed55 = pedHelper.getFiled55();
                    String pinBlock = pedHelper.getPinBlock();
                    Map<Integer, TlvCell> tlvCellMap = Tlv.unpack(Convert.strToBcdBytes(filed55, true));
                    saveCardInfoAndCardSeq(tlvCellMap, trans);
                    generateF55AfterARQC(tlvCellMap, trans);
                    Log.d(TAG, "filed55 >>> " + filed55);
                    Log.d(TAG, "pinBlock >>> " + pinBlock);
                }
            }
            if (readCardResult) {
                return packager.pack(trans);
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解包
     *
     * @param data
     * @return
     */
    public TransData unpackData(TransData origTrans, byte[] data) {
        ETransType eTransType = ETransType.valueOf(origTrans.getTransType());
        IPacker<TransData, byte[]> packager = eTransType.getpackager(new PackListenerImpl());
        packager.unpack(origTrans, data);
        return origTrans;
    }

    private void generateF55AfterARQC(Map<Integer, TlvCell> map, TransData transData) {
        ETransType transType = ETransType.valueOf(transData.getTransType());

        byte[] f55 = EmvTags.getF55(transType, map, false);
        if (f55 == null) {
            return;
        }
        Log.d(TAG, "sale field55:" + BankTransManager.convert.bcdToStr(f55));
        transData.setSendIccData(BankTransManager.convert.bcdToStr(f55));
        byte[] arqc = getTlv(map, 0x9f26);
        if (arqc != null && arqc.length > 0) {
            transData.setArqc(BankTransManager.convert.bcdToStr(arqc));
        }
    }

    private void saveCardInfoAndCardSeq(Map<Integer, TlvCell> map, TransData transData) {
        byte[] track2 = getTlv(map, 0x57);
        String strTrack2 = BankTransManager.convert.bcdToStr(track2);
        strTrack2 = strTrack2.split("F")[0];
        transData.setTrack2(strTrack2);
        Log.d(TAG, "strTrack2:" + strTrack2);
        // 卡号
        String pan = TrackUtils.getPan(strTrack2);
        Log.d(TAG, "pan:" + pan);
        transData.setPan(pan);
        // 有效期
        byte[] expDate = getTlv(map, 0x5f24);
        Log.d(TAG, "expDate:" + expDate);
        if (expDate != null && expDate.length > 0) {
            String temp = BankTransManager.convert.bcdToStr(expDate);
            transData.setExpDate(temp.substring(0, 4));
        }
        // 获取卡片序列号
        byte[] cardSeq = getTlv(map, 0x5f34);
        if (cardSeq != null && cardSeq.length > 0) {
            String temp = BankTransManager.convert.bcdToStr(cardSeq);
            transData.setCardSerialNo(temp.substring(0, 2));
        }
    }

    private void initTransData(TransData transData) {
        Controller controller = BankTransManager.controller;
        transData.setMerchID(controller.getValue(SysParam.MERCH_ID));
        transData.setTermID(controller.getValue(SysParam.TERMINAL_ID));
        transData.setBatchNo(controller.getInt(SysParam.BATCH_NO));
        transData.setHeader(controller.getValue(SysParam.APP_HEADER));
        transData.setTpdu(controller.getValue(SysParam.APP_TPDU));
        transData.setOper(controller.getValue(SysParam.OPERATOR));
        transData.setIsEncTrack(controller.getBoolean(SysParam.IS_TRACK_ENCRYPT));
        transData.setIsSM(controller.getBoolean(SysParam.IS_SM4));
    }


    private void glInit() {
        gl = new GLProxy(context).getGL();
        convert = gl.getConvert();
        packer = gl.getPacker();
        db = gl.getDb();
    }

    /**
     * 外置密钥键盘初始化，SP20/S300
     */
    public static void exdevInit() {
        exdevClose();
        iComm = ExDevUtils.usbInit(context);
        new Thread() {
            @Override
            public void run() {
                int connectCount = 0;// 连接次数，连接失败，尝试再连接两次
                while (true) {
                    try {
                        iComm.connect();
                        exDevLib = ExDevLib.getInstance(iComm);
                        exDevLib.setEncryptFlag(0);
                        Log.e(TAG, "connect ok >>> ");
                        break;
                    } catch (Exception e) {
                        e.printStackTrace();
                        iComm = null;
                        Log.e(TAG, "connect failed >>> ");
                        connectCount++;
                        if (connectCount > 3) {
                            break;
                        }
                        SystemClock.sleep(5000);
                    }
                }
            }
        }.start();
    }

    public static void exdevClose() {
        if (exDevLib != null) {
            exDevLib.close();
            exDevLib = null;
        }
        Log.d(TAG, "exdevClose >>> ");
    }

    /**
     * 检测密码键盘连接状态
     *
     * @return
     */
    public static boolean checkExPedState() {
        //检测状态不对时，再进行外置连接一次，如果异常报个没连接
        if (iComm == null) {
            try {
                iComm = ExDevUtils.usbInit(context);
                iComm.connect();
                exDevLib = ExDevLib.getInstance(iComm);
                exDevLib.setEncryptFlag(0);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    private byte[] getTlv(Map<Integer, TlvCell> map, int tag) {
        TlvCell c1 = map.get(tag);
        if (c1 != null) {
            return c1.getValue();
        }
        return null;
    }

}
