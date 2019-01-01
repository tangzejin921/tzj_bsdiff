package com.tzj.bsdiff;


import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;


/**
 * zip 压缩相关
 */
public class UtilZip {

    public static boolean unZipFolder(final String zipFileString) {
        return unZipFolder(zipFileString, new File(zipFileString).getParentFile().getAbsolutePath());
    }

    /**
     * 解压zip到指定的路径
     *
     * @param zipFileString ZIP的名称
     * @param outPathString 要解压缩路径
     * @throws Exception
     */
    public static boolean unZipFolder(String zipFileString, String outPathString) {
        ZipInputStream inZip = null;
        try {
            inZip = new ZipInputStream(new FileInputStream(zipFileString));
            ZipEntry zipEntry;
            String szName = "";
            while ((zipEntry = inZip.getNextEntry()) != null) {
                szName = zipEntry.getName();
                if (zipEntry.isDirectory()) {
                    //获取部件的文件夹名
                    szName = szName.substring(0, szName.length() - 1);
                    File folder = new File(outPathString + File.separator + szName);
                    folder.mkdirs();
                } else {
                    File file = new File(outPathString + File.separator + szName);
                    if (!file.exists()) {
                        file.getParentFile().mkdirs();
                        file.createNewFile();
                    }
                    // 获取文件的输出流
                    FileOutputStream out = new FileOutputStream(file);
                    int len;
                    byte[] buffer = new byte[1024];
                    // 读取（字节）字节到缓冲区
                    while ((len = inZip.read(buffer)) != -1) {
                        // 从缓冲区（0）位置写入（字节）字节
                        out.write(buffer, 0, len);
                        out.flush();
                    }
                    out.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (inZip != null) {
                    inZip.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    public static void unZipFolder(String zipFileString, String outPathString, String szName) throws Exception {
        ZipInputStream inZip = new ZipInputStream(new FileInputStream(zipFileString));
        ZipEntry zipEntry;
        while ((zipEntry = inZip.getNextEntry()) != null) {
            //szName = zipEntry.getName();
            if (zipEntry.isDirectory()) {
                //获取部件的文件夹名
                szName = szName.substring(0, szName.length() - 1);
                File folder = new File(outPathString + File.separator + szName);
                folder.mkdirs();
            } else {
                File file = new File(outPathString + File.separator + szName);
                if (!file.exists()) {
                    file.getParentFile().mkdirs();
                    file.createNewFile();
                }
                // 获取文件的输出流
                FileOutputStream out = new FileOutputStream(file);
                int len;
                byte[] buffer = new byte[1024];
                // 读取（字节）字节到缓冲区
                while ((len = inZip.read(buffer)) != -1) {
                    // 从缓冲区（0）位置写入（字节）字节
                    out.write(buffer, 0, len);
                    out.flush();
                }
                out.close();
            }
        }
        inZip.close();
    }

    /**
     * 压缩文件和文件夹
     *
     * @param srcFileString
     */
    public static boolean zipFolder(String srcFileString) {
        return zipFolder(null,srcFileString, srcFileString + ".zip");
    }
    /**
     * 压缩文件和文件夹
     *
     * @param srcFileString
     */
    public static boolean zipFolder(Context ctx, String srcFileString) {
        return zipFolder(ctx,srcFileString, srcFileString + ".zip");
    }
    /**
     * 压缩文件和文件夹
     *
     * @param srcFileString 要压缩的文件或文件夹
     * @param zipFileString 解压完成的Zip路径
     * @throws Exception
     */
    public static boolean zipFolder(Context ctx, String srcFileString, String zipFileString) {
        ZipOutputStream outZip = null;
        try {
            //创建ZIP
            outZip = new ZipOutputStream(new FileOutputStream(zipFileString));
            //创建文件
            AssetsFile file = new AssetsFile(ctx,srcFileString);
            if (file.isDirectory()){
                return zipFiles(ctx,file+File.separator, "", outZip);
            }else{
                return zipFiles(ctx,file.getParent()+File.separator, file.getName(), outZip);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                //完成和关闭
                if (outZip != null) {
                    outZip.finish();
                    outZip.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 压缩文件
     *
     * @param folderString
     * @param fileString
     * @param zipOutputSteam
     * @throws Exception
     */
    private static boolean zipFiles(Context ctx, String folderString, String fileString, ZipOutputStream zipOutputSteam) throws Exception {
        if (zipOutputSteam == null)
            return false;
        AssetsFile file = new AssetsFile(ctx,folderString + fileString);
        if (file.isFile()) {
            ZipEntry zipEntry = new ZipEntry(fileString);
            InputStream inputStream = file.open();
            zipOutputSteam.putNextEntry(zipEntry);
            int len;
            byte[] buffer = new byte[4096];
            while ((len = inputStream.read(buffer)) != -1) {
                zipOutputSteam.write(buffer, 0, len);
            }
            zipOutputSteam.closeEntry();
        } else if (file.isDirectory()){
            //文件夹
            String fileList[] = file.list();
            //没有子文件和压缩
            if (fileList == null || fileList.length <= 0) {
                if (fileString.length() != 0){
                    ZipEntry zipEntry = new ZipEntry(fileString + File.separator);
                    zipOutputSteam.putNextEntry(zipEntry);
                }
                zipOutputSteam.closeEntry();
            }else{
                //子文件和递归
                for (int i = 0; i < fileList.length; i++) {
                    if (fileString.length() == 0){
                        zipFiles(ctx,folderString, fileList[i], zipOutputSteam);
                    }else{
                        zipFiles(ctx,folderString, fileString + File.separator + fileList[i], zipOutputSteam);
                    }
                }
            }
        }else {
            throw new RuntimeException("文件："+file.getAbsolutePath()+"出错了");
        }
        return true;
    }

    /**
     * 返回zip的文件输入流
     *
     * @param zipFileString zip的名称
     * @param fileString    ZIP的文件名
     * @return InputStream
     * @throws Exception
     */
    public static InputStream upZip(String zipFileString, String fileString) throws Exception {
        ZipFile zipFile = new ZipFile(zipFileString);
        ZipEntry zipEntry = zipFile.getEntry(fileString);
        return zipFile.getInputStream(zipEntry);
    }

    /**
     * 返回ZIP中的文件列表（文件和文件夹）
     *
     * @param zipFileString  ZIP的名称
     * @param bContainFolder 是否包含文件夹
     * @param bContainFile   是否包含文件
     * @return
     * @throws Exception
     */
    public static List<File> getFileList(String zipFileString, boolean bContainFolder, boolean bContainFile) throws Exception {
        List<File> fileList = new ArrayList<File>();
        ZipInputStream inZip = new ZipInputStream(new FileInputStream(zipFileString));
        ZipEntry zipEntry;
        String szName = "";
        while ((zipEntry = inZip.getNextEntry()) != null) {
            szName = zipEntry.getName();
            if (zipEntry.isDirectory()) {
                // 获取部件的文件夹名
                szName = szName.substring(0, szName.length() - 1);
                File folder = new File(szName);
                if (bContainFolder) {
                    fileList.add(folder);
                }
            } else {
                File file = new File(szName);
                if (bContainFile) {
                    fileList.add(file);
                }
            }
        }
        inZip.close();
        return fileList;
    }

    /**
     * 删除文件/文件夹
     *
     * @param path
     * @return: void
     */
    public static boolean delFile(String path) {
        if (path == null) {
            return false;
        }
        File file = new File(path);
        if (file.isFile()) {
            return file.delete();
        }
        return file.isDirectory() && delFolder(path);
    }

    /**
     * 删除文件夹
     *
     * @param folderPath 文件夹的路径
     */
    public static boolean delFolder(String folderPath) {
        delAllFile(folderPath);
        File myFilePath = new File(folderPath);
        return myFilePath.delete();
    }
    /**
     * 删除文件
     *
     * @param path 文件的路径
     */
    public static void delAllFile(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return;
        }
        if (!file.isDirectory()) {
            return;
        }
        String[] tempList = file.list();
        if (tempList == null){
            return;
        }
        File temp;
        for (String aTempList : tempList) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + aTempList);
            } else {
                temp = new File(path + File.separator + aTempList);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                delAllFile(path + File.separator + aTempList);
                delFolder(path + File.separator + aTempList);
            }
        }
    }

}