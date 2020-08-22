package com.hikvision.auto.router.base.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

public interface IData {

    String PATH = "data";

    /**
     * 查询接口
     * @param uri 见路由表
     * @param projection
     * @param selection
     * @param selectionArgs
     * @return
     */
     Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs);

    /**
     * 插入接口
     * @param uri
     * @param values
     * @return
     */
     void insert(Uri uri, ContentValues values);

    /**
     * 删除接口
     * @param uri
     * @param selection
     * @param selectionArgs
     * @return
     */
     int delete(Uri uri,String selection, String[] selectionArgs);

    /**
     * 更新接口
     * @param uri
     * @param values
     * @param selection
     * @param selectionArgs
     * @return
     */
     int update(Uri uri,ContentValues values,String selection, String[] selectionArgs);

    /**
     * 插入或更新
     * @param uri
     * @param values
     * @param selection
     * @param selectionArgs
     * @return
     */
     void insertOrUpdate(Uri uri, ContentValues values, String selection, String[] selectionArgs);


}
