package com.tzj.bsdiff;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MainActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void zip(View v){
        String sdCard = getCacheDir().getAbsolutePath();
        int i = UtilBsDiff.zip(sdCard+"/"+"a.txt");
    }
    public void unZip(View v){
        String sdCard = getCacheDir().getAbsolutePath();
        int i = UtilBsDiff.unzip(sdCard+"/"+"a.txt.bz2");
    }
    public void diff(View v){
        String sdCard = getCacheDir().getAbsolutePath();
        String path = UtilBsDiff.bsDiff(sdCard+"/"+"a.txt",sdCard+"/b.txt");
        Log.e("test",path);
    }
    public void patch(View v){
        String sdCard = getCacheDir().getAbsolutePath();
        String path = UtilBsDiff.bsPatch(sdCard+"/"+"a.txt",sdCard+"/a.txt_b.txt.diff");
        Log.e("test",path);
    }


}
