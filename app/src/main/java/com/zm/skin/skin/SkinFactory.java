package com.zm.skin.skin;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.app.AppCompatDelegate;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.zm.skin.R;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author zhangming
 * @Date 2019/3/20 15:55
 * @Description: 自定义view生成工厂类
 */
public class SkinFactory implements LayoutInflater.Factory2 {

    private AppCompatDelegate mDelegate;//系统原有委托类
    private List<SkinView> mSkinViews = new ArrayList<>();//用于保存需要换肤的view

    public SkinFactory(AppCompatDelegate mDelegate) {
        this.mDelegate = mDelegate;
    }

    @Override

    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        //调用系统的方法生成View
        View view = mDelegate.createView(parent, name, context, attrs);

        if (view == null) {//万一系统创建出来是空，那么我们来补救
            mConstructorArgs[0] = context;
            try {
                if (-1 == name.indexOf('.')) {//不包含. 说明不带包名，那么我们帮他加上包名
                    view = createViewByPrefix(context, name, prefixs, attrs);
                } else {//包含. 说明 是权限定名的view name，
                    view = createViewByPrefix(context, name, null, attrs);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Log.e("TTTTTTTT",view +"  |||  " +name);

        getSkinView(context, attrs, view);
        return view;
    }

    static final Class<?>[] mConstructorSignature = new Class[]{Context.class, AttributeSet.class};//
    final Object[] mConstructorArgs = new Object[2];//View的构造函数的2个"实"参对象
    private static final HashMap<String, Constructor<? extends View>> sConstructorMap = new HashMap<String, Constructor<? extends View>>();//用映射，将View的反射构造函数都存起来
    static final String[] prefixs = new String[]{//安卓里面控件的包名，就这么3种,这个变量是为了下面代码里，反射创建类的class而预备的
            "android.widget.",
            "android.view.",
            "android.webkit."
    };

    /**
     * 反射创建View
     *
     * @param context
     * @param name
     * @param prefixs
     * @param attrs
     * @return
     */
    private final View createViewByPrefix(Context context, String name, String[] prefixs, AttributeSet attrs) {

        Constructor<? extends View> constructor = sConstructorMap.get(name);
        Class<? extends View> clazz = null;

        if (constructor == null) {
            try {
                if (prefixs != null && prefixs.length > 0) {
                    for (String prefix : prefixs) {
                        clazz = context.getClassLoader().loadClass(
                                prefix != null ? (prefix + name) : name).asSubclass(View.class);//控件
                        if (clazz != null) break;
                    }
                } else {
                    if (clazz == null) {
                        clazz = context.getClassLoader().loadClass(name).asSubclass(View.class);
                    }
                }
                if (clazz == null) {
                    return null;
                }
                constructor = clazz.getConstructor(mConstructorSignature);//拿到 构造方法，
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            constructor.setAccessible(true);//
            sConstructorMap.put(name, constructor);//然后缓存起来，下次再用，就直接从内存中去取
        }
        Object[] args = mConstructorArgs;
        args[1] = attrs;
        try {
            //通过反射创建View对象
            final View view = constructor.newInstance(args);//执行构造函数，拿到View对象
            return view;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //获取需要换肤的view
    private void getSkinView(Context context, AttributeSet attrs, View view) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.Skinable);
        boolean isSupportSkin = array.getBoolean(R.styleable.Skinable_isSupportSkin, false);
        if (isSupportSkin) {//支持换肤
            HashMap<String, String> attrMap = new HashMap<>();
            String attrName;
            String attrValue;
            for (int i = 0; i < attrs.getAttributeCount(); i++) {
                attrName = attrs.getAttributeName(i);
                attrValue = attrs.getAttributeValue(i);
                attrMap.put(attrName, attrValue);
            }
            SkinView skinView = new SkinView(view, attrMap);
            mSkinViews.add(skinView);
            Log.e("TTTTTYYY",view+"     "+attrMap);
        }
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        return null;
    }

    //设置肤色
    public void setSkin(String path, Context mContext) {
        for (SkinView view : mSkinViews) {
            view.changeSkin(path, mContext);
        }
    }

    //换肤实体类
    static class SkinView {
        View view;//保存view
        HashMap<String, String> attrsMap;//保存view的属性

        public SkinView(View view, HashMap<String, String> attrsMap) {
            this.view = view;
            this.attrsMap = attrsMap;
        }

        /**
         * 真正的换肤操作
         */
        public void changeSkin(String path, Context mContext) {
            if (!TextUtils.isEmpty(attrsMap.get("background"))) {//属性名,例如，这个background，text，textColor....
                int bgId = Integer.parseInt(attrsMap.get("background").substring(1));//属性值，R.id.XXX ，int类型，
                // 这个值，在app的一次运行中，不会发生变化
                String attrType = view.getResources().getResourceTypeName(bgId); // 属性类别：比如 drawable ,color
                if (TextUtils.equals(attrType, "mipmap")) {//区分drawable和color
                    view.setBackground(SkinHandler.getInstance().getDrawable(path, mContext, bgId));//加载外部资源管理器，拿到外部资源的drawable
                } else if (TextUtils.equals(attrType, "color")) {
                    view.setBackgroundColor(SkinHandler.getInstance().getColor(path, mContext, bgId));
                }
            }

            if (view instanceof TextView) {
                if (!TextUtils.isEmpty(attrsMap.get("textColor"))) {
                    int textColorId = Integer.parseInt(attrsMap.get("textColor").substring(1));
                    ((TextView) view).setTextColor(SkinHandler.getInstance().getColor(path, mContext, textColorId));
                }
            }
        }

    }
}
