package com.bankcard.pay;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.bankcard.trans.BankTransManager;
import com.bankcard.trans.bean.PackResult;
import com.bankcard.trans.device.Device;
import com.bankcard.trans.helper.PedHelper;
import com.bankcard.trans.model.ETransType;
import com.bankcard.trans.model.TransData;
import com.bankcard.trans.pack.base.TransResult;
import com.bankcard.trans.settings.SysParam;
import com.bankcard.trans.utils.ThreadManager;

import org.json.JSONException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private TextView tvResult;
    private Handler handler = new Handler();
    public static BankTransManager bankTransManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvResult = findViewById(R.id.tvResult);
    }

    private void initData() {
        // 商户号
        BankTransManager.controller.setValue(SysParam.MERCH_ID, "999999999999999");
        // 终端号
        BankTransManager.controller.setValue(SysParam.TERMINAL_ID, "99999999");
        // 批次号，结算一次加1
        BankTransManager.controller.setValue(SysParam.BATCH_NO, 1);
        // 流水号
        BankTransManager.controller.setValue(SysParam.TRANS_NO, 1);
        // TPDU
        BankTransManager.controller.setValue(SysParam.APP_TPDU, "6009770000");
        // 报文头
        BankTransManager.controller.setValue(SysParam.APP_HEADER, "603200321301");
        // 操作员
        BankTransManager.controller.setValue(SysParam.OPERATOR, "01");
        // 是否是国密
        BankTransManager.controller.setValue(SysParam.IS_SM4, false);
        // 是否磁道加密
        BankTransManager.controller.setValue(SysParam.IS_TRACK_ENCRYPT, false);

        BankTransManager.pedHelper.addAid();
        BankTransManager.pedHelper.addCapk();
        byte[] keyValue = {(byte) 0x12, (byte) 0x034, (byte) 0x56, (byte) 0x78, (byte) 0x90, (byte) 0xab, (byte) 0xcd, (byte) 0xef, (byte) 0x11, (byte) 0x22, (byte) 0x33, (byte) 0x44, (byte) 0x55, (byte) 0x66, (byte) 0x77, (byte) 0x88};
        byte[] kcv = {(byte) 0xA5, (byte) 0x9B, (byte) 0xC7};
        Log.d(TAG, "writeTMK:" + BankTransManager.pedHelper.writeTMK(keyValue));
        Log.d(TAG, "writeTPK:" + BankTransManager.pedHelper.writeTPK(keyValue, kcv));
        Log.d(TAG, "writeTAK:" + BankTransManager.pedHelper.writeTAK(keyValue, kcv));
        Log.d(TAG, "writeTDK:" + BankTransManager.pedHelper.writeTDK(keyValue, kcv));
    }

    public void init(View view) {
        bankTransManager = BankTransManager.getInstance();
        bankTransManager.init(this);
        initData();
        tvResult.setText("初始化成功！");
    }

    public void logon(View view) throws JSONException {
        tvResult.setText("");

        Class clazz = PedHelper.class;
        try {
            Method method = clazz.getDeclaredMethod("inputOnlinePin");
            method.setAccessible(true);
            method.invoke(bankTransManager.pedHelper);

            String pinBlock = BankTransManager.pedHelper.getPinBlock();
            tvResult.setText(pinBlock);
//            requestComparisonFace(faceNo, pinBlock);

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

//        TransData transData = new TransData();
//        // 流水号，每次交易加1
//        transData.setTransNo(1);
//        // 交易日期 MMDD
//        transData.setDate(Device.getDate().substring(4));
//        // 交易时间 HHmmss
//        transData.setTime(Device.getTime());
//        // 交易类型
//        transData.setTransType(ETransType.LOGON.toString());
//
//        byte[] bytes = bankTransManager.packData(transData);
//        if (bytes != null) {
//            String data = BankTransManager.convert.bcdToStr(bytes);
//            Log.e(TAG, "logon pack data >>> " + data);
//            tvResult.setText(data);
//        }
    }

    public void sale(View view) {
//        bankTransManager.pedHelper.readCard(60, "1");
        tvResult.setText("");
        final TransData transData = new TransData();
        // 流水号，每次交易加1
        transData.setTransNo(1);
        // 交易日期 MMDD
        transData.setDate(Device.getDate().substring(4));
        // 交易时间 HHmmss
        transData.setTime(Device.getTime());
        // 交易类型
        transData.setTransType(ETransType.SALE.toString());
        // 交易金额
        transData.setAmount("1");

        ThreadManager.newInstance().createThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                PackResult result = bankTransManager.packData(transData);
                final String msg;
                if (result.getRet() == TransResult.SUCC) {
                    msg = transData.getPin();
                } else {
                    msg = "获取失败！";
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvResult.setText(msg);
                    }
                });

//                if (bytes != null) {
//                    final String data = BankTransManager.convert.bcdToStr(bytes);
//                    Log.d(TAG, "长度：" + data.length());
//                    Log.e(TAG, "sale pack data >>> " + data);
//                    handler.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            tvResult.setText(data.length());
//                        }
//                    });
//                }
            }
        });

    }

}