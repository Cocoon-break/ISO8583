package com.bankcard.trans.process.action;

import android.content.Context;

import com.bankcard.trans.model.ETransType;
import com.bankcard.trans.process.component.Component;
import com.bankcard.trans.process.core.AAction;
import com.bankcard.trans.process.core.ActionResult;
import com.bankcard.trans.utils.ThreadManager;

/**
 * 交易预处理
 */
public class ActionTransPreDeal extends AAction {

    public ActionTransPreDeal(ActionStartListener listener) {
        super(listener);

    }

    private Context context;
    private ETransType transType;

    /**
     * 设置action运行时参数
     *
     * @param context
     * @param transType
     */
    public void setParam(Context context, ETransType transType) {
        this.context = context;
        this.transType = transType;
    }

    @Override
    protected void process() {
        ThreadManager.newInstance().createThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                // 执行交易预处理
                int ret = Component.transPreDeal(context, transType);
                setResult(new ActionResult(ret, null));
            }
        });
    }

    @Override
    public void setResult(ActionResult result) {
        super.setResult(result);
        context = null;
    }

}
