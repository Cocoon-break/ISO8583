package com.bankcard.trans.utils;

import android.content.Context;
import android.util.Log;

import com.pax.gl.commhelper.IComm;
import com.pax.gl.commhelper.ICommUsbHost;
import com.pax.gl.commhelper.impl.PaxGLComm;

/**
 * Created by SuQi on 2020/7/18.
 * Describe:
 */
public class ExDevUtils {

    private static final String TAG = "exdev";

    public static IComm usbInit(Context context) {
        ICommUsbHost usbHost = PaxGLComm.getInstance(context).createUsbHost();
        for (ICommUsbHost.IUsbDeviceInfo deviceInfo : usbHost.getPeerDevice()) {
            if (deviceInfo.isPaxDevice()) {
                Log.i(TAG, "dName: " + deviceInfo.getDevice().getDeviceName());
                Log.i(TAG, "vid: " + deviceInfo.getDevice().getVendorId());
                Log.i(TAG, "pid: " + deviceInfo.getDevice().getProductId());
                //Log.i(TAG, "pName: " + deviceInfo.getDevice().getProductName());

                /* 连接百富的设备，后面2个参数 可取任意值，内部会忽略这两个参数，自动匹配对应的规则
                 * 如果连接的是其非百富的标准的usb设备，则需要从中选择合适的interface 和 传输类型
                 * 如果拔插过设备则需要重新getPeerDevice，重新调用setUsbDevice
                 */
                usbHost.setUsbDevice(deviceInfo.getDevice(), null, 0);
                usbHost.setPaxSpecialDevice(true);
                break;
            }
        }
        return usbHost;
    }
}
