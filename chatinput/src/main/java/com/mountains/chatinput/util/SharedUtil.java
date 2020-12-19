package com.mountains.chatinput.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


public class SharedUtil {

    /**
     * 存储boolean类型的键值对到SharedPreferences文件当中。
     * @param key
     *          存储的键
     * @param value
     *          存储的值
     */
    public static void save(Context context, String key, boolean value) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(
                context).edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    /**
     * 存储float类型的键值对到SharedPreferences文件当中。
     * @param key
     *          存储的键
     * @param value
     *          存储的值
     */
    public static void save(Context context,String key, float value) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(
                context).edit();
        editor.putFloat(key, value);
        editor.apply();
    }

    /**
     * 存储int类型的键值对到SharedPreferences文件当中。
     * @param key
     *          存储的键
     * @param value
     *          存储的值
     */
    public static void save(Context context,String key, int value) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(
                context).edit();
        editor.putInt(key, value);
        editor.apply();
    }

    /**
     * 存储long类型的键值对到SharedPreferences文件当中。
     * @param key
     *          存储的键
     * @param value
     *          存储的值
     */
    public static void save(Context context,String key, long value) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(
                context).edit();
        editor.putLong(key, value);
        editor.apply();
    }

    /**
     * 存储String类型的键值对到SharedPreferences文件当中。
     * @param key
     *          存储的键
     * @param value
     *          存储的值
     */
    public static void save(Context context,String key, String value) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(
                context).edit();
        editor.putString(key, value);
        editor.apply();
    }

    /**
     * 从SharedPreferences文件当中读取参数传入键相应的boolean类型的值。
     * @param key
     *          读取的键
     * @param defValue
     *          如果读取不到值，返回的默认值
     * @return boolean类型的值，如果读取不到，则返回默认值
     */
    public static boolean read(Context context,String key, boolean defValue) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(key, defValue);
    }

    /**
     * 从SharedPreferences文件当中读取参数传入键相应的float类型的值。
     * @param key
     *          读取的键
     * @param defValue
     *          如果读取不到值，返回的默认值
     * @return float类型的值，如果读取不到，则返回默认值
     */
    public static float read(Context context,String key, float defValue) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getFloat(key, defValue);
    }

    /**
     * 从SharedPreferences文件当中读取参数传入键相应的int类型的值。
     * @param key
     *          读取的键
     * @param defValue
     *          如果读取不到值，返回的默认值
     * @return int类型的值，如果读取不到，则返回默认值
     */
    public static int read(Context context,String key, int defValue) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getInt(key, defValue);
    }

    /**
     * 从SharedPreferences文件当中读取参数传入键相应的long类型的值。
     * @param key
     *          读取的键
     * @param defValue
     *          如果读取不到值，返回的默认值
     * @return long类型的值，如果读取不到，则返回默认值
     */
    public static long read(Context context,String key, long defValue) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getLong(key, defValue);
    }

    /**
     * 从SharedPreferences文件当中读取参数传入键相应的String类型的值。
     * @param key
     *          读取的键
     * @param defValue
     *          如果读取不到值，返回的默认值
     * @return String类型的值，如果读取不到，则返回默认值
     */
    public static String read(Context context,String key, String defValue) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(key, defValue);
    }

    /**
     * 判断SharedPreferences文件当中是否包含指定的键值。
     * @param key
     *          判断键是否存在
     * @return 键已存在返回true，否则返回false。
     */
    public static boolean contains(Context context,String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.contains(key);
    }

    /**
     * 清理SharedPreferences文件当中传入键所对应的值。
     * @param key
     *          想要清除的键
     */
    public static void clear(Context context,String key) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(
                context).edit();
        editor.remove(key);
        editor.apply();
    }

    /**
     * 将SharedPreferences文件中存储的所有值清除。
     */
    public static void clearAll(Context context) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(
                context).edit();
        editor.clear();
        editor.apply();
    }

}