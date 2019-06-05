package com.ks.freecoupon.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;

/**
 * Date:2019/3/22
 * Author：康少
 * Description：Android平台上一个轻量级的存储辅助类
 */
public class SharedPreferencesUtils {
    private static final String FILE_NAME = "FreeCoupon_cache";


    /**
     * 保存数据的方法，我们需要拿到保存数据的具体类型，然后根据类型调用不同的保存方法
     * @param context context
     * @param key 关键词
     * @param object 对象
     */
    public static void setParam(Context context , String key, Object object){

        String type = object.getClass().getSimpleName();
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        if("String".equals(type)){
            editor.putString(key, (String)object);
        }
        else if("Integer".equals(type)){
            editor.putInt(key, (Integer)object);
        }
        else if("Boolean".equals(type)){
            editor.putBoolean(key, (Boolean)object);
        }
        else if("Float".equals(type)){
            editor.putFloat(key, (Float)object);
        }
        else if("Long".equals(type)){
            editor.putLong(key, (Long)object);
        }

        editor.apply();
    }


    /**
     * 得到保存数据的方法，我们根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取值
     *
     *          重要！！！！！：defaultObject不能设为null
     *
     * @param context context
     * @param key 关键词
     * @param defaultObject 默认值
     * @return 对象
     */
    public static Object getParam(Context context , String key, Object defaultObject){
        String type = defaultObject.getClass().getSimpleName();
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);

        if("String".equals(type)){
            return sp.getString(key, (String)defaultObject);
        }
        else if("Integer".equals(type)){
            return sp.getInt(key, (Integer)defaultObject);
        }
        else if("Boolean".equals(type)){
            return sp.getBoolean(key, (Boolean)defaultObject);
        }
        else if("Float".equals(type)){
            return sp.getFloat(key, (Float)defaultObject);
        }
        else if("Long".equals(type)){
            return sp.getLong(key, (Long)defaultObject);
        }

        return null;
    }

    /**
     * 删除固定key
     * @param context context
     * @param key 关键词
     */
    public static void removeKey(Context context, String key){
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        editor.apply();
    }

    /**
     * 删除所有缓存
     * @param context context
     */
    public static void removeAll(Context context){
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.apply();
    }

    /**
     * 存放List<String>集合数据</>
     * @param mContext  context
     * @param list 集合
     */
    public static void setArrayList(Context mContext, String key, List<String> list) {
        SharedPreferences sp = mContext.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor mEdit1= sp.edit();
        //保存该key已经存在的list集合，并添加新的list数据，组成一个新的list集合保存下来
        List<String> listnew = new ArrayList<>();
        int size = sp.getInt(key+"_size", 0);
        for (int j = 0; j < size; j++) {
            if (sp.getString(key + "_" + j, null) != null) {
                listnew.add(sp.getString(key+"_" + j, null));
            }
        }
        for (int k = 0; k < listnew.size(); k++) {
            mEdit1.remove(key + "_" + k);
            mEdit1.putString(key + "_" + k, listnew.get(k));
        }
        mEdit1.putInt(key + "_size", list.size() + listnew.size());
        int count = 0;
        for (int i = listnew.size(); i < list.size() + listnew.size(); i++) {
            mEdit1.remove(key + "_" + i);
            mEdit1.putString(key + "_" + i, list.get(count));
            count++;
        }
        mEdit1.apply();
    }

    /**
     * 获取List<String>集合数据</>
     * @param mContext context
     * @param key 关键词
     * @return 集合
     */
    public static List<String> getArrayList(Context mContext, String key) {
        SharedPreferences mSharedPreference1 = mContext.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        List<String> list = new ArrayList<>();
        int size = mSharedPreference1.getInt(key+"_size", 0);
        for(int i=0;i<size;i++) {
            if (mSharedPreference1.getString(key+"_" + i, null) != null) {
                list.add(mSharedPreference1.getString(key+"_" + i, null));
            }
        }
        return list;
    }

    /**
     * 删除ArrayList
     * @param mContext context
     * @param key 关键词
     */
    public static void removeAllArrayList(Context mContext, String key) {
        SharedPreferences sp = mContext.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor mEdit1= sp.edit();
        int size = sp.getInt(key+"_size", 0);
        if (size > 0) {
            for (int k = 0; k < size; k++) {
                mEdit1.remove(key + "_" + k);
            }
            mEdit1.apply();
        }
    }

    /**
     * 删除List中指定的Item
     */
    public static void removeListItemByKeyword(Context mContext, String key, String keyword) {
        SharedPreferences sp = mContext.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor mEdit1= sp.edit();
        int size = sp.getInt(key+"_size", 0);
        if (size > 0) {
            //检索匹配的keyword并删除
            List<String> list1 = SharedPreferencesUtils.getArrayList(mContext,key);
            for (int i = 0; i < list1.size(); i++) {
                if (list1.get(i).equals(keyword)) {
                    mEdit1.remove(key + "_" + i);
                    mEdit1.apply();
                }
            }
            //重新排序
            List<String> list = getArrayList(mContext,key);
            removeAllArrayList(mContext,key);
            setArrayList(mContext,key,list);
        }
    }
}
