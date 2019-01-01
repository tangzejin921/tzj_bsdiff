package com.tzj.bsdiff;

import android.content.Context;
import android.content.pm.PackageManager;

import java.io.File;

import io.flutter.util.PathUtils;
import io.flutter.view.FlutterMain;

public class UtilUpdate {
    private static final String tempName = "flutter_assets_tmp";
    /**
     * 1.检查有没把 assets_flutter 打包成 zip
     * flutter
     *  v_appVersion
     *      v_flutterVersion.zip
     * 2.返回最大的 flutter 版本
     */
    public static int flutterCode(Context ctx) {
        try {
            String v=lastFilePath(ctx).replace(zipPath(ctx)+"/v_","").replace(".zip","");
            return Integer.valueOf(v);
        }catch (Exception e){
            e.printStackTrace();
        }
        return -1;
    }
    /**
     * 1.检查有没把 assets_flutter 打包成 zip
     * flutter
     *  v_appVersion
     *      v_flutterVersion.zip
     * 2.返回最新的 flutter zip包路径
     */
    public static String lastFilePath(Context ctx) {
        int v = -1;
        String zip = zipPath(ctx);
        String path = zip;
        File file = new File(zip);
        if (!file.exists()) {
            file.mkdirs();
            //TODO 不存在时没判断
            UtilZip.zipFolder(ctx,
                    "file:///android_assets/flutter_assets",
                    path = zip+ "/v_0.zip");
        } else {
            File temp = new File(file, tempName);
            if (temp.exists()){
                replace(ctx,temp.getAbsolutePath());
            }
            String[] list = file.list();
            if (list != null){
                for (String s : list) {
                    String replace = s.replace("v_", "").replace(".zip", "");
                    try {
                        int i = Integer.valueOf(replace);
                        if (i > v){
                            path = zip+"/"+s;
                            v = i;
                        }
                    }catch (Exception e){
//                        e.printStackTrace();
                    }
                }
            }
        }
        return path;
    }
    /**
     * zip 存放的路径
     */
    public static String zipPath(Context ctx){
        return PathUtils.getDataDirectory(ctx) + "/v_"+appCode(ctx);
    }
    /**
     * app 版本
     */
    public static int appCode(Context mContext) {
        int versionCode = 0;
        try {
            versionCode = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    /**
     * 新文件的路径
     */
    public static String patchFile(Context ctx, String newFilePath){
        if (newFilePath.endsWith(".diff")){
            String oldFile = lastFilePath(ctx);
            if (new File(newFilePath).exists()){
                oldFile = UtilBsDiff.bsPatch(oldFile,newFilePath);
                UtilZip.delFile(newFilePath);
            }
            return oldFile;
        }else if (newFilePath.endsWith(".zip")){
            return newFilePath;
        }else{
            throw new RuntimeException("文件必须以 .diff/.zip结尾");
        }
    }

    /**
     * 替换文件
     */
    public static boolean copyFlutter(Context ctx){
        String newFile = lastFilePath(ctx);
        String dataDirectory = zipPath(ctx)+File.separator+tempName;
        File file = new File(dataDirectory);
        file.mkdirs();
        if (UtilZip.unZipFolder(newFile,dataDirectory)){
            return replace(ctx,dataDirectory);
        }
        return false;
    }

    /**
     * 替换
     */
    private static boolean replace(Context ctx, String flutterTemp){
        File temp = new File(flutterTemp);
        if (temp.exists()){
            String flutterPath = FlutterMain.findAppBundlePath(ctx);
            boolean b = UtilZip.delFile(flutterPath);
            if (b){
                return temp.renameTo(new File(flutterPath));
            }
        }
        return false;
    }
}
