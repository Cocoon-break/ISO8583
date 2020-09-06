package com.bankcard.trans.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.io.File;

public class Controller {

    private static Controller controller;
    private static Context context;

    private static final String fileName = "controller_params";

    public static synchronized Controller getInstance(Context context) {
        if (controller == null) {
            Controller.context = context.getApplicationContext();
            controller = new Controller();
        }
        return controller;
    }

    private Controller() {
        super();
        if (isParamFileExist()) {
            return;
        }

        SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        // 初始化默认参数
        editor.apply();
    }

    public int getInt(String key) {
        SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        return sp.getInt(key, 0);
    }

    public boolean getBoolean(String key) {
        SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        return sp.getBoolean(key, false);
    }

    public void setValue(String key, int value) {
        SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public String getValue(String key) {
        SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        return sp.getString(key, "");
    }

    public void setValue(String key, String value) {
        SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public void setValue(String key, boolean value) {
        SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    private boolean isParamFileExist() {
        String dir = "/data/data/" + context.getPackageName() + File.separator + "shared_prefs/" + fileName + ".xml";
        File file = new File(dir);
        if (file.exists()) {
            return true;
        }
        return false;
    }

}
