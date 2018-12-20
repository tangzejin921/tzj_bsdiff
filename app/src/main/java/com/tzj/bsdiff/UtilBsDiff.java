package com.tzj.bsdiff;

/**
 * sdCard  的读写问题
 */
public class UtilBsDiff {
    static {
        System.loadLibrary("bsdiff_4.3");
    }

    /**
     * 压缩（只能单个文件压缩）
     * 压缩后的文件路径为原文件 + .bz2
     *
     * @param fileName 文件路径（不能为文件夹）
     * @return 0 成功
     */
    public static native int zip(String fileName);

    /**
     * 解压
     *
     * @param fileName 文件路径（不能为文件夹）
     * @return 0 成功
     */
    public static native int unzip(String fileName);

    /**
     * 差分文件
     * @param oldFielName
     * @param newFileName
     * @return 生成差分文件的路径
     */
    public static native String bsDiff(String oldFielName,String newFileName);
    /**
     * 合并文件
     * @param oldFielName
     * @param diffFileName
     * @return 生成新文件的路径
     */
    public static native String bsPatch(String oldFielName,String diffFileName);

}
