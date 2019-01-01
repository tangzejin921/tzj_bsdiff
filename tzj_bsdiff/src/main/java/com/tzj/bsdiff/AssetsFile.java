package com.tzj.bsdiff;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class AssetsFile {
    private String absolutePath;
    private File file;
    private AssetManager assets;

    /**
     * 传 Context 那么打开的是 assets
     */
    public AssetsFile(Context ctx, String s) {
        file = new File(absolutePath = s);
        if (ctx != null) {
            assets = ctx.getAssets();
        }
    }

    public boolean isFile() {
        try {
            if (assets.list(getPath()).length == 0) {
                InputStream is = assets.open(getPath());
                int available = is.available();
                is.close();
                if (available > 0) {
                    return true;
                }
            }
            return file.isFile();
        } catch (Exception e) {
//            e.printStackTrace();
            return file.isFile();
        }
    }

    public boolean isDirectory() {
        try {
            return assets.list(getPath()).length > 0;
        } catch (Exception e) {
//            e.printStackTrace();
            return file.isDirectory();
        }
    }

    public String[] list() {
        try {
            return assets.list(getPath());
        } catch (Exception e) {
//            e.printStackTrace();
            return file.list();
        }
    }

    public String getAbsolutePath() {
        return absolutePath;
    }

    public String getParent() {
        return file.getParent();
    }

    public String getName() {
        return file.getName();
    }

    public InputStream open() throws IOException {
        if (assets != null) {
            return assets.open(getPath());
        } else {
            return new FileInputStream(file);
        }
    }

    public String getPath() {
        if (assets != null) {
            return absolutePath.replaceFirst("file:///android_assets/", "");
        }
        return file.getPath();
    }

    @Override
    public String toString() {
        return absolutePath;
    }
}
