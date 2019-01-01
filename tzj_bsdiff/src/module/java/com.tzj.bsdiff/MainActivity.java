package com.tzj.bsdiff;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;


public class MainActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void bzip2(View v){
        String sdCard = Environment.getExternalStorageDirectory().getAbsolutePath();
        int i = UtilBsDiff.zip(sdCard+"/"+"a.txt");
    }
    public void unBzip2(View v){
        String sdCard = Environment.getExternalStorageDirectory().getAbsolutePath();
        int i = UtilBsDiff.unzip(sdCard+"/"+"a.txt.bz2");
    }
    public void diff(View v){
        String sdCard = Environment.getExternalStorageDirectory().getAbsolutePath();
        String path = UtilBsDiff.bsDiff(sdCard+"/"+"a.txt",sdCard+"/b.txt");
    }
    public void patch(View v){
        String sdCard = Environment.getExternalStorageDirectory().getAbsolutePath();
        String path = UtilBsDiff.bsPatch(sdCard+"/"+"a.txt",sdCard+"/a.txt-b.txt.diff");
    }



    public void zip(View v){
        String sdCard = Environment.getExternalStorageDirectory().getAbsolutePath();
        UtilZip.zipFolder(sdCard+"/"+"a.txt");
    }

    public void unZip(View v) throws Exception {
        String sdCard = Environment.getExternalStorageDirectory().getAbsolutePath();
        UtilZip.unZipFolder(sdCard+"/a.txt.zip");
    }
}
