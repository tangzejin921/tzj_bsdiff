package com.tzj.bsdiff;

import android.app.ActivityManager;
import android.content.Context;
import android.os.AsyncTask;

import io.flutter.view.FlutterMain;

public class UtilFlutterUpdate {
    /**
     * 要放在 FlutterMain.startInitialization(this);
     * 之后调用，因为用到了 FlutterMain.isRunningPrecompiledCode()
     */
    public static AsyncTask update(final Context ctx, final Callback callback){
        AsyncTask asyncTask = null;
        if (!ctx.getPackageName().equals(getProcessName(ctx))
                || !FlutterMain.isRunningPrecompiledCode()){
            asyncTask = new AsyncTask() {
                @Override
                protected Object doInBackground(Object[] objects) {
                    return null;
                }
            };
        }else{
            asyncTask = new AsyncTask() {

                @Override
                protected Object doInBackground(Object[] objects) {
                    try {
                        FlutterUpdate update = new FlutterUpdate(ctx);
                        //下载版本
                        callback.download(ctx,update);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            };
        }
        return asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
    }
    /**
     * 得到当前进程名
     */
    public static String getProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager.getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return "";
    }


    public interface Callback{
        /**
         * AsyncTask 线程中 下载 Flutter 文件
         *
         * 下载的文件必须放在这里 {@link FlutterUpdate#getZipPath()} ，并且名称规定如下
         * zip:
         *  v_<flutterCode>.zip
         * diff:
         *  v_<flutterCode>.zip-v_<newFlutterCode>.zip.diff
         *
         * 下载完成请调用 {@link FlutterUpdate#patchAndReplace(Context, String)}
         */
        void download(Context ctx,FlutterUpdate update);
    }

}
