package com.tzj.bsdiff;

import android.content.Context;
import android.content.pm.PackageManager;

import java.io.File;

import io.flutter.util.PathUtils;
import io.flutter.view.FlutterMain;

public class FlutterUpdate {
    private int appCode;
    private String zipPath;
    private String lastFilePath;
    private String flutterTempPath;
    private int flutterCode = -1;

    /**
     * 要放在非 UI 线程
     * 1.检查有没把flutter 打包，
     * 2.检查有没有没替换成功的文件
     */
    public FlutterUpdate(Context ctx) throws Exception {
        appCode = appCode(ctx);
        zipPath = PathUtils.getDataDirectory(ctx) + "/v_" + appCode;
        ;
        flutterTempPath = zipPath + File.separator + "flutter_assets_tmp";
        lastFilePath = lastFilePath(ctx);
        String v = lastFilePath.replace(zipPath + "/v_", "").replace(".zip", "");
        flutterCode = Integer.valueOf(v);
    }

    /**
     * app 版本
     */
    private int appCode(Context mContext) {
        int versionCode = 0;
        try {
            versionCode = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    /**
     * 1.检查有没把 assets_flutter 打包成 zip
     * flutter
     * v_appVersion
     * v_flutterVersion.zip
     * 2.返回最新的 flutter zip包路径
     */
    private String lastFilePath(Context ctx) {
        int v = -1;
        String path = zipPath;
        File file = new File(zipPath);
        if (!file.exists()) {
            file.mkdirs();
            UtilZip.zipFolder(ctx,
                    "file:///android_assets/flutter_assets",
                    path = zipPath + "/v_0.zip");
        } else {
            replaceFlutter(ctx, flutterTempPath);
            String[] list = file.list();
            if (list != null) {
                for (String s : list) {
                    String replace = s.replace("v_", "").replace(".zip", "");
                    try {
                        int i = Integer.valueOf(replace);
                        if (i > v) {
                            path = zipPath + "/" + s;
                            v = i;
                        }
                    } catch (Exception e) {
//                        e.printStackTrace();
                    }
                }
            }
        }
        return path;
    }


    public int getAppCode() {
        return appCode;
    }

    public String getZipPath() {
        return zipPath;
    }

    public int getFlutterCode() {
        return flutterCode;
    }

    /**
     * 如果为.diff 结尾就合并文件
     *
     * @return 新文件的路径
     */
    public String patchFile(Context ctx, String newFilePath) {
        if (!new File(newFilePath).exists()) {
            return lastFilePath;
        }
        if (newFilePath.endsWith(".diff")) {
            String newTemp = UtilBsDiff.bsPatch(lastFilePath, newFilePath);
            UtilZip.delFile(newFilePath);
            return newTemp;
        } else if (newFilePath.endsWith(".zip")) {
            return newFilePath;
        } else {
            throw new RuntimeException("文件必须以 .diff/.zip结尾");
        }
    }

    /**
     * 解压文件
     */
    public boolean unZipFlutter(String newFile) {
        File file = new File(flutterTempPath);
        file.mkdirs();
        return UtilZip.unZipFolder(newFile, flutterTempPath);
    }

    /**
     * 替换
     */
    public boolean replaceFlutter(Context ctx, String flutterTemp) {
        //todo Flutter 文件被占用/不可替换 返回 false(替换时机的问题)
        File temp = new File(flutterTemp);
        if (temp.exists()) {
            String flutterPath = FlutterMain.findAppBundlePath(ctx);
            boolean b = UtilZip.delFile(flutterPath);
            if (b) {
                return temp.renameTo(new File(flutterPath));
            }
        }
        return false;
    }

    /**
     * 把增量包 合并+解压+替换
     */
    public boolean patchAndReplace(Context ctx, String newDiffPath) {
        //合并
        String newPath = patchFile(ctx, newDiffPath);
        //替换文件
        if (lastFilePath.equals(newPath)) {
            return false;
        }
        if (unZipFlutter(newPath)) {
            //TODO 应为没有判断时机所以放在下次打开时替换
//            return replaceFlutter(ctx,flutterTempPath);
        }
        return false;
    }

}
