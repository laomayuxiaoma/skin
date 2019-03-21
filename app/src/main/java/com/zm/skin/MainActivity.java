package com.zm.skin;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.zm.skin.skin.SkinFactory;
import com.zm.skin.skin.SkinHandler;

import java.io.File;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE"};//内存读写的权限

    private TextView skin_change;

    private String[] skinPath = new String[]{"this", "skin.apk"};
    private String mCurrentSkin = skinPath[0];
    private String path = null;

    private SkinFactory mSkinFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mSkinFactory = new SkinFactory(getDelegate());
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        layoutInflater.setFactory2(mSkinFactory);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        verifyStoragePermissions(this);
        skin_change = findViewById(R.id.skin_change);
        findViewById(R.id.go_to).setOnClickListener(this);
        skin_change.setOnClickListener(this);
    }

    public static void verifyStoragePermissions(AppCompatActivity activity) {
        try {
            //检测是否有写的权限
            int permission = ActivityCompat.checkSelfPermission(activity, PERMISSIONS_STORAGE[1]);
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.skin_change:
                if (null == mCurrentSkin) {
                    path = skinPath[0];
                } else if (mCurrentSkin.equalsIgnoreCase(skinPath[0])) {
                    path = skinPath[1];
                } else if (mCurrentSkin.equalsIgnoreCase(skinPath[1])) {
                    path = skinPath[0];
                }
                changeSkin(path);
                break;
            case R.id.go_to:
                Intent intent = new Intent(this, TwoActivity.class);
                intent.putExtra("path",path);
                startActivity(intent);

                break;
        }
    }

    protected void changeSkin(String path) {
        File skinFile = new File(Environment.getExternalStorageDirectory(), path);//暂时从磁盘的根目录获取换肤包
        SkinHandler.getInstance().loadSkin(skinFile.getAbsolutePath());
        mSkinFactory.setSkin(path, this);
        mCurrentSkin = path;
    }
}
