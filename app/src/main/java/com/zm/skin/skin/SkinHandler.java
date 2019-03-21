package com.zm.skin.skin;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.io.File;
import java.lang.reflect.Method;

/**
 * @author zhangming
 * @Date 2019/3/20 17:26
 * @Description: 资源处理类
 */
public class SkinHandler {
    private final static SkinHandler instance = new SkinHandler();
    private Resources mOutResource;// 资源管理器
    private Context mContext;//上下文
    private String mOutPkgName;// 外部资源包的packageName

    private SkinHandler() {

    }

    public static SkinHandler getInstance() {
        return instance;
    }

    public void init(Context context) {
        mContext = context;
    }

    public void loadSkin(String path) {
        File file = new File(path);
        if (!file.exists() || "this".equalsIgnoreCase(path)) {
            return;
        }
        PackageManager manager = mContext.getPackageManager();
        PackageInfo packageArchiveInfo = manager.getPackageArchiveInfo(path, PackageManager.GET_ACTIVITIES);
        mOutPkgName = packageArchiveInfo.packageName;
        AssetManager assetManager;
        try {
            assetManager = AssetManager.class.newInstance();//通过反射获取
            Method addAssetPath = assetManager.getClass().getMethod("addAssetPath", String.class);
            addAssetPath.invoke(assetManager, path);
            mOutResource = new Resources(assetManager,
                    mContext.getResources().getDisplayMetrics(),
                    mContext.getResources().getConfiguration());
            //最终得到一个外部资源包
        } catch (Exception e) {
            Log.e("log_load", e.getMessage() + "");
        }
    }


    /**
     * 提供外部资源包里面的颜色
     *
     * @param resId
     * @return
     */
    public int getColor(String path, Context mContext, int resId) {
        if (mOutResource == null || "this".equalsIgnoreCase(path)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return mContext.getColor(resId);
            } else {
                return mContext.getResources().getColor(resId);
            }
        }
        String resName = mOutResource.getResourceEntryName(resId);
        int outResId = mOutResource.getIdentifier(resName, "color", mOutPkgName);
        if (outResId == 0) {
            return resId;
        }
        return mOutResource.getColor(outResId);
    }

    /**
     * 提供外部资源包里的图片资源
     *
     * @param resId
     * @return
     */
    public Drawable getDrawable(String path, Context mContext, int resId) {//获取图片
        if (mOutResource == null || "this".equalsIgnoreCase(path)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                return mContext.getDrawable(resId);
            } else {
                return mContext.getResources().getDrawable(resId);
            }
        }
        String resName = mOutResource.getResourceEntryName(resId);
        int outResId = mOutResource.getIdentifier(resName, "mipmap", mOutPkgName);
        if (outResId == 0) {
            return ContextCompat.getDrawable(mContext, resId);
        }
        return mOutResource.getDrawable(outResId);
    }

}
