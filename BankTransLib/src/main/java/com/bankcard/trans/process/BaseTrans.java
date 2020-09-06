package com.bankcard.trans.process;

import android.content.Context;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;

import com.bankcard.trans.model.ETransType;
import com.bankcard.trans.model.TransData;
import com.bankcard.trans.pack.base.TransResult;
import com.bankcard.trans.process.action.ActionTransPreDeal;
import com.bankcard.trans.process.core.AAction;
import com.bankcard.trans.process.core.ATransaction;
import com.bankcard.trans.process.core.ActionResult;
import com.bankcard.trans.process.core.TransContext;

/**
 * Created by SuQi on 2020/7/18.
 * Describe:
 */
public abstract class BaseTrans extends ATransaction {

    private static final String TAG = BaseTrans.class.getSimpleName();

    protected Context context;
    protected Handler handler;
    // 当前交易类型
    protected ETransType transType;
    protected TransData transData;

    /**
     * 交易监听器
     */
    protected TransEndListener transListener;

    public BaseTrans(Context context, Handler handler, ETransType transType, TransEndListener transListener) {
        super();
        this.context = context;
        this.handler = handler;
        this.transType = transType;
        this.transListener = transListener;
    }

    /**
     * 交易结果提示
     */
    protected void transEnd(final ActionResult result) {
        Log.i(TAG, transType.toString() + " TRANS--END--");

        new Thread(new Runnable() {
            @Override
            public void run() {
//                TransProcessListenerImpl listenerImpl = new TransProcessListenerImpl(TransContext.getInstance().getCurrentContext());
//                TransOnline.downLoadCheck(true, false, listenerImpl);
//                if (listenerImpl != null) {
//                    listenerImpl.onHideProgress();
//                }
                TransContext.getInstance().setCurrentAction(null);
                if (transListener != null) {
                    // 防止第三方自动签到后无法做交易,将标志位及时设置为false
                    setTransRunning(false);
                    transListener.onEnd(result);
                }
                // 睡眠保证页面已经关闭,防止快速点击签到崩溃
                SystemClock.sleep(200);
                setTransRunning(false);
            }
        });
    }

    /**
     * 重写父类的execute， 增加交易是否已运行检查和交易预处理
     */
    @Override
    public synchronized void execute(TransData trans) {
        Log.i(TAG, transType.toString() + " TRANS--START--");
        if (isTransRunning()) {
            return;
        }

        setTransRunning(true);
        // 初始化transData
        this.transData = trans;
        // 设置当前context
        TransContext.getInstance().setCurrentContext(context);
        ActionTransPreDeal preDealAction = new ActionTransPreDeal(new AAction.ActionStartListener() {
            @Override
            public void onStart(AAction action) {
                ((ActionTransPreDeal) action).setParam(getCurrentContext(), transType);
            }
        });
        preDealAction.setEndListener(new AAction.ActionEndListener() {
            @Override
            public void onEnd(AAction action, ActionResult result) {
                if (result.getRet() != TransResult.SUCC) {
                    transEnd(result);
                    return;
                }
                transData.setTransType(transType.toString());
                exe();
            }
        });
        preDealAction.execute();
    }

    /**
     * 执行父类的execute方法
     */
    private void exe() {
        super.execute(transData);
    }

    protected Context getCurrentContext() {
        return TransContext.getInstance().getCurrentContext();
    }

    /**
     * 交易是否已执行， 此标准是全局性的， 真的所有交易， 如果某个交易中间需要插入其他交易时， 自己控制此状态。
     */
    private static boolean isTransRunning = false;

    /**
     * 获取交易执行状态
     *
     * @return
     */
    public static boolean isTransRunning() {
        return isTransRunning;
    }

    /**
     * 设置交易执行状态
     *
     * @param isTransRunning
     */
    public static void setTransRunning(boolean isTransRunning) {
        BaseTrans.isTransRunning = isTransRunning;
    }

    @Override
    protected void bind(String state, AAction action) {
        super.bind(state, action);
        if (action != null) {
            action.setEndListener(new AAction.ActionEndListener() {
                @Override
                public void onEnd(AAction action, final ActionResult result) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Log.i(TAG, transType.toString() + " ACTION--" + currentState + "--end");
                                onActionResult(currentState, result);
                            } catch (Exception e) {
                                e.printStackTrace();
                                transEnd(new ActionResult(TransResult.ERR_ABORTED, null));
                            }

                        }
                    });
                }
            });
        }
    }

    private String currentState;

    /**
     * action结果处理
     *
     * @param currentState ：当前State
     * @param result       ：当前Action执行的结果
     */
    public abstract void onActionResult(String currentState, ActionResult result);

    @Override
    public void gotoState(String state) {
        this.currentState = state;
        Log.i(TAG, transType.toString() + " ACTION--" + currentState + "--start");
        super.gotoState(state);
    }

}
